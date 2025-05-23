package com.wobbz.framework.processor

import com.google.auto.service.AutoService
import com.wobbz.framework.processor.validation.SchemaValidator
import org.json.JSONObject
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.tools.FileObject
import javax.tools.StandardLocation

/**
 * Validates settings.json schema files during compilation.
 * This processor finds modules with @XposedPlugin annotation and validates
 * their settings.json files if present.
 */
@AutoService(Processor::class)
@SupportedAnnotationTypes("com.wobbz.framework.processor.XposedPlugin")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
class SettingsSchemaProcessor : AbstractProcessor() {
    
    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // Find all modules with @XposedPlugin annotation
        for (element in roundEnv.getElementsAnnotatedWith(XposedPlugin::class.java)) {
            if (element.kind != ElementKind.CLASS) continue
            
            val moduleElement = element as TypeElement
            val moduleName = moduleElement.simpleName.toString()
            
            // Check if the module has a settings.json file
            // Note: During annotation processing, assets may not be available yet
            // So we'll be more lenient about settings validation
            try {
                val settingsFile = getSettingsFile(moduleElement)
                if (settingsFile != null) {
                    validateSettingsSchema(settingsFile, moduleName, moduleElement)
                } else {
                    // Settings file not found during annotation processing - this is normal
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.NOTE,
                        "settings.json not found during annotation processing for module '$moduleName' - this is normal",
                        element
                    )
                }
            } catch (e: Exception) {
                // Make this a warning instead of error since assets may not be available during annotation processing
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.WARNING,
                    "Could not validate settings.json for module '$moduleName': ${e.message}",
                    element
                )
            }
        }
        
        return true
    }
    
    /**
     * Gets the settings.json file for a module.
     */
    private fun getSettingsFile(moduleElement: TypeElement): FileObject? {
        return try {
            // Try to get the settings.json from the module's source assets
            // During annotation processing, we need to look in the source directory
            processingEnv.filer.getResource(
                StandardLocation.SOURCE_PATH,
                "",
                "assets/settings.json"
            )
        } catch (e: Exception) {
            // Try alternative locations
            try {
                processingEnv.filer.getResource(
                    StandardLocation.CLASS_PATH,
                    "",
                    "assets/settings.json"
                )
            } catch (e2: Exception) {
                // File doesn't exist, which is fine - not all modules have settings
                null
            }
        }
    }
    
    /**
     * Validates a settings schema file.
     */
    private fun validateSettingsSchema(file: FileObject, moduleName: String, moduleElement: TypeElement) {
        try {
            val content = file.openInputStream().use { it.reader().readText() }
            
            // Use our SchemaValidator to validate the content
            val result = SchemaValidator.validateSchema(content)
            
            // Report errors through the annotation processor
            if (!result.isValid) {
                result.errors.forEach { error ->
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Settings schema validation error for module '$moduleName': $error",
                        moduleElement
                    )
                }
            }
            
            // Report warnings
            result.warnings.forEach { warning ->
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.WARNING,
                    "Settings schema warning for module '$moduleName': $warning",
                    moduleElement
                )
            }
            
            // Additional module-specific validation
            validateModuleSpecificSchema(content, moduleName, moduleElement)
            
        } catch (e: Exception) {
            // During annotation processing, this is often normal since assets may not be available
            processingEnv.messager.printMessage(
                Diagnostic.Kind.WARNING,
                "Error reading or parsing settings.json for module '$moduleName': ${e.message}",
                moduleElement
            )
        }
    }
    
    /**
     * Performs module-specific validation that's beyond general schema validation.
     */
    private fun validateModuleSpecificSchema(content: String, moduleName: String, moduleElement: TypeElement) {
        try {
            val jsonObject = JSONObject(content)
            
            // Check if settings keys in the schema match any @SettingsKey annotations in the module
            validateSettingsKeysConsistency(jsonObject, moduleElement)
            
            // Check for module-specific constraints
            validateModuleConstraints(jsonObject, moduleName, moduleElement)
            
        } catch (e: Exception) {
            // Already handled in validateSettingsSchema
        }
    }
    
    /**
     * Validates that @SettingsKey annotations in the module match the schema.
     */
    private fun validateSettingsKeysConsistency(schema: JSONObject, moduleElement: TypeElement) {
        val properties = schema.optJSONObject("properties") ?: return
        val schemaKeys = properties.keys().asSequence().toSet()
        
        // Find all @SettingsKey annotations in the module
        val annotatedKeys = mutableSetOf<String>()
        
        // This is a simplified version - in a real implementation, we'd need to
        // traverse the entire module's class hierarchy to find all @SettingsKey annotations
        // For now, we'll just note this for future enhancement
        
        // Check for keys in schema but not used in code
        val unusedKeys = schemaKeys - annotatedKeys
        if (unusedKeys.isNotEmpty()) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.WARNING,
                "Settings schema contains keys that are not used in code: ${unusedKeys.joinToString()}",
                moduleElement
            )
        }
    }
    
    /**
     * Validates module-specific constraints.
     */
    private fun validateModuleConstraints(schema: JSONObject, moduleName: String, moduleElement: TypeElement) {
        // Check for common anti-patterns
        val properties = schema.optJSONObject("properties") ?: return
        
        // Warn about potential security issues
        for (key in properties.keys()) {
            val property = properties.optJSONObject(key) ?: continue
            val keyLower = key.toLowerCase()
            
            // Check for potentially sensitive settings
            if (keyLower.contains("password") || keyLower.contains("secret") || keyLower.contains("key")) {
                if (property.optString("type") == "string") {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.WARNING,
                        "Setting '$key' appears to be sensitive data. Consider using secure storage mechanisms.",
                        moduleElement
                    )
                }
            }
            
            // Check for overly broad permissions
            if (keyLower.contains("enable_all") || keyLower.contains("global")) {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.WARNING,
                    "Setting '$key' appears to grant broad permissions. Ensure this is intended.",
                    moduleElement
                )
            }
        }
        
        // Check for reasonable defaults
        validateDefaults(properties, moduleElement)
    }
    
    /**
     * Validates that default values are reasonable.
     */
    private fun validateDefaults(properties: JSONObject, moduleElement: TypeElement) {
        for (key in properties.keys()) {
            val property = properties.optJSONObject(key) ?: continue
            
            if (property.has("default")) {
                val default = property.get("default")
                val type = property.optString("type")
                
                // Check for potentially problematic defaults
                when (type) {
                    "boolean" -> {
                        if (default == true) {
                            val keyLower = key.toLowerCase()
                            if (keyLower.contains("enable") || keyLower.contains("active")) {
                                processingEnv.messager.printMessage(
                                    Diagnostic.Kind.NOTE,
                                    "Setting '$key' defaults to enabled. Ensure this is the desired behavior.",
                                    moduleElement
                                )
                            }
                        }
                    }
                    "array" -> {
                        // Check if array defaults are empty
                        if (default.toString() == "[]") {
                            processingEnv.messager.printMessage(
                                Diagnostic.Kind.NOTE,
                                "Setting '$key' defaults to empty array. Consider if this provides good user experience.",
                                moduleElement
                            )
                        }
                    }
                }
            } else {
                // No default provided
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.WARNING,
                    "Setting '$key' has no default value. This may cause issues if the user hasn't configured it.",
                    moduleElement
                )
            }
        }
    }
} 