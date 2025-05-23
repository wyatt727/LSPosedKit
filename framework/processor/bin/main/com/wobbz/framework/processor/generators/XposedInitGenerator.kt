package com.wobbz.framework.processor.generators

import com.wobbz.framework.processor.ModuleMetadata
import javax.annotation.processing.ProcessingEnvironment
import javax.tools.StandardLocation
import java.io.IOException

/**
 * Generator for xposed_init files that specify the module entry point.
 */
class XposedInitGenerator {
    
    fun generate(metadata: ModuleMetadata, processingEnv: ProcessingEnvironment) {
        try {
            val filer = processingEnv.filer
            
            // Create the xposed_init file in assets
            val fileObject = filer.createResource(
                StandardLocation.CLASS_OUTPUT,
                "",
                "assets/xposed_init"
            )
            
            fileObject.openWriter().use { writer ->
                writer.write(metadata.generateXposedInit())
            }
            
            processingEnv.messager.printMessage(
                javax.tools.Diagnostic.Kind.NOTE,
                "Generated xposed_init for module: ${metadata.id}"
            )
            
        } catch (e: IOException) {
            processingEnv.messager.printMessage(
                javax.tools.Diagnostic.Kind.ERROR,
                "Failed to generate xposed_init: ${e.message}"
            )
        }
    }
} 