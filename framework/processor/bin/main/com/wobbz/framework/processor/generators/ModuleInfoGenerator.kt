package com.wobbz.framework.processor.generators

import com.wobbz.framework.processor.ModuleMetadata
import javax.annotation.processing.ProcessingEnvironment
import javax.tools.StandardLocation
import java.io.IOException

/**
 * Generator for module-info.json files with extended module metadata.
 */
class ModuleInfoGenerator {
    
    fun generate(metadata: ModuleMetadata, processingEnv: ProcessingEnvironment) {
        try {
            val filer = processingEnv.filer
            
            // Create the module-info.json file in assets
            val fileObject = filer.createResource(
                StandardLocation.CLASS_OUTPUT,
                "",
                "assets/module-info.json"
            )
            
            fileObject.openWriter().use { writer ->
                writer.write(metadata.generateModuleInfo())
            }
            
            processingEnv.messager.printMessage(
                javax.tools.Diagnostic.Kind.NOTE,
                "Generated module-info.json for module: ${metadata.id}"
            )
            
        } catch (e: IOException) {
            processingEnv.messager.printMessage(
                javax.tools.Diagnostic.Kind.ERROR,
                "Failed to generate module-info.json: ${e.message}"
            )
        }
    }
} 