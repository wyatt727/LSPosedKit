package com.wobbz.framework.processor

import com.google.auto.service.AutoService
import com.wobbz.framework.processor.generators.ModuleInfoGenerator
import com.wobbz.framework.processor.generators.ModulePropGenerator
import com.wobbz.framework.processor.generators.XposedInitGenerator
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Main annotation processor that processes @XposedPlugin annotations and generates all required files.
 */
@AutoService(Processor::class)
@SupportedAnnotationTypes("com.wobbz.framework.processor.XposedPlugin")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
class XposedPluginProcessor : AbstractProcessor() {
    
    private val modulePropGenerator = ModulePropGenerator()
    private val xposedInitGenerator = XposedInitGenerator()
    private val moduleInfoGenerator = ModuleInfoGenerator()
    
    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // Only process if annotations are present
        if (annotations.isEmpty()) {
            return false
        }
        
        // Find all classes annotated with @XposedPlugin
        val annotatedElements = roundEnv.getElementsAnnotatedWith(XposedPlugin::class.java)
        
        if (annotatedElements.isEmpty()) {
            return false
        }
        
        // Process each annotated class
        for (element in annotatedElements) {
            if (element.kind != ElementKind.CLASS) {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "@XposedPlugin can only be applied to classes",
                    element
                )
                continue
            }
            
            val classElement = element as TypeElement
            val annotation = classElement.getAnnotation(XposedPlugin::class.java)
            
            try {
                // Extract metadata from annotation
                val metadata = extractMetadata(classElement, annotation)
                
                // Validate metadata
                val validationErrors = metadata.validate()
                if (validationErrors.isNotEmpty()) {
                    for (error in validationErrors) {
                        processingEnv.messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            "Module validation error: $error",
                            element
                        )
                    }
                    continue
                }
                
                // Generate files
                generateFiles(metadata)
                
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "Successfully processed module: ${metadata.id}",
                    element
                )
                
            } catch (e: Exception) {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Failed to process @XposedPlugin annotation: ${e.message}",
                    element
                )
            }
        }
        
        return true
    }
    
    /**
     * Extracts metadata from the @XposedPlugin annotation and class element.
     */
    private fun extractMetadata(classElement: TypeElement, annotation: XposedPlugin): ModuleMetadata {
        val className = classElement.simpleName.toString()
        val packageName = processingEnv.elementUtils.getPackageOf(classElement).qualifiedName.toString()
        
        // Check if class has @HotReloadable annotation
        val isHotReloadable = classElement.getAnnotation(HotReloadable::class.java) != null
        
                 return ModuleMetadata(
             id = annotation.id,
             name = annotation.name,
             version = annotation.version,
             description = annotation.description,
             author = annotation.author,
             scope = annotation.scope.toList(),
             minApiVersion = if (annotation.minApi == -1) 31 else annotation.minApi, // Default to minSdk
             maxApiVersion = if (annotation.maxApi == -1) 35 else annotation.maxApi,  // Default to targetSdk
             className = className,
             packageName = packageName,
             isHotReloadable = isHotReloadable
         )
    }
    
    /**
     * Generates all required files using the metadata.
     */
    private fun generateFiles(metadata: ModuleMetadata) {
        try {
            // Generate module.prop
            modulePropGenerator.generate(metadata, processingEnv)
            
            // Generate xposed_init
            xposedInitGenerator.generate(metadata, processingEnv)
            
            // Generate module-info.json
            moduleInfoGenerator.generate(metadata, processingEnv)
            
        } catch (e: Exception) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Failed to generate files for module ${metadata.id}: ${e.message}"
            )
        }
    }
} 