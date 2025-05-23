package com.wobbz.framework.processor

import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Processes @SettingsKey annotations to validate their usage.
 * This processor validates that @SettingsKey is used correctly on fields
 * and that the key values are properly formatted.
 */
@AutoService(Processor::class)
@SupportedAnnotationTypes("com.wobbz.framework.processor.SettingsKey")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
class SettingsKeyProcessor : AbstractProcessor() {
    
    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // We don't need to generate code for @SettingsKey
        // It's used at runtime for binding, so just validate the usage
        
        for (element in roundEnv.getElementsAnnotatedWith(SettingsKey::class.java)) {
            if (element.kind != ElementKind.FIELD) {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "@SettingsKey can only be applied to fields",
                    element
                )
                continue
            }
            
            val annotation = element.getAnnotation(SettingsKey::class.java)
            val key = annotation.value
            
            // Validate key format
            if (key.isEmpty()) {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "@SettingsKey value cannot be empty",
                    element
                )
                continue
            }
            
            // Check key naming convention
            if (!isValidKeyName(key)) {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.WARNING,
                    "@SettingsKey value '$key' should follow snake_case naming convention",
                    element
                )
            }
            
            // Validate field type compatibility
            validateFieldType(element)
        }
        
        return true
    }
    
    /**
     * Validates that the key follows proper naming conventions.
     */
    private fun isValidKeyName(key: String): Boolean {
        // Check for snake_case format
        val snakeCaseRegex = Regex("^[a-z][a-z0-9_]*[a-z0-9]$")
        return key.matches(snakeCaseRegex) || key.matches(Regex("^[a-z][a-z0-9]*$"))
    }
    
    /**
     * Validates that the field type is supported by the settings system.
     */
    private fun validateFieldType(element: javax.lang.model.element.Element) {
        val fieldType = element.asType().toString()
        
        val supportedTypes = setOf(
            "boolean", "java.lang.Boolean",
            "int", "java.lang.Integer",
            "long", "java.lang.Long", 
            "float", "java.lang.Float",
            "double", "java.lang.Double",
            "java.lang.String",
            "java.util.Set<java.lang.String>",
            "java.util.List<java.lang.String>"
        )
        
        if (fieldType !in supportedTypes) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.WARNING,
                "@SettingsKey field type '$fieldType' may not be supported by the settings system. " +
                "Supported types are: ${supportedTypes.joinToString()}",
                element
            )
        }
    }
} 