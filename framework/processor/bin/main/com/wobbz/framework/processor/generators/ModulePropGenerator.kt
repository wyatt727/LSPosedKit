package com.wobbz.framework.processor.generators

import com.wobbz.framework.processor.ModuleMetadata
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.tools.StandardLocation
import java.io.IOException

/**
 * Generator for module.prop files used by LSPosed Manager.
 */
class ModulePropGenerator {
    
    fun generate(metadata: ModuleMetadata, processingEnv: ProcessingEnvironment) {
        try {
            val filer = processingEnv.filer
            
            // Create the module.prop file in assets
            val fileObject = filer.createResource(
                StandardLocation.CLASS_OUTPUT,
                "",
                "assets/module.prop"
            )
            
            fileObject.openWriter().use { writer ->
                writer.write(metadata.generateModuleProp())
            }
            
            processingEnv.messager.printMessage(
                javax.tools.Diagnostic.Kind.NOTE,
                "Generated module.prop for module: ${metadata.id}"
            )
            
        } catch (e: IOException) {
            processingEnv.messager.printMessage(
                javax.tools.Diagnostic.Kind.ERROR,
                "Failed to generate module.prop: ${e.message}"
            )
        }
    }
} 