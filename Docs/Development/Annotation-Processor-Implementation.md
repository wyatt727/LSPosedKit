# Annotation Processor Implementation Guide

This document provides detailed implementation guidelines for creating the annotation processor component of LSPosedKit. The annotation processor is responsible for generating necessary files like `module.prop`, `xposed_init`, and module metadata based on annotations in the module code.

## Directory Structure

Create the following files in the `framework/processor/src/main/java/com/wobbz/framework/processor` directory:

```
framework/processor/src/main/java/com/wobbz/framework/processor/
├── XposedPlugin.kt
├── HotReloadable.kt
├── SettingsKey.kt
├── XposedPluginProcessor.kt
├── ModuleMetadata.kt
├── generators/
│   ├── ModulePropGenerator.kt
│   ├── XposedInitGenerator.kt
│   └── ModuleInfoGenerator.kt
└── utils/
    ├── ProcessorUtils.kt
    └── FileUtils.kt
```

## Gradle Configuration

First, configure the `framework/processor/build.gradle` file to include the necessary annotation processing dependencies:

```groovy
plugins {
    id 'java-library'
    id 'org.jetbrains.kotlin.jvm'
    id 'kotlin-kapt'
}

java {
    sourceCompatibility = rootProject.ext.javaVersion
    targetCompatibility = rootProject.ext.javaVersion
}

dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib:${rootProject.ext.kotlinVersion}"
    
    // Annotation processing
    implementation 'com.google.auto.service:auto-service-annotations:1.0.1'
    kapt 'com.google.auto.service:auto-service:1.0.1'
    
    // JSON processing
    implementation 'com.squareup.moshi:moshi:1.14.0'
    implementation 'com.squareup.moshi:moshi-kotlin:1.14.0'
    
    // For file generation
    implementation 'com.squareup:kotlinpoet:1.12.0'
}
```

## Annotation Definitions

### XposedPlugin.kt

This annotation defines the module metadata:

```kotlin
package com.wobbz.framework.processor

import kotlin.annotation.AnnotationRetention
import kotlin.annotation.AnnotationTarget

/**
 * Annotation for the main module class that provides metadata for the module.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class XposedPlugin(
    /**
     * The module ID. This should be a unique, kebab-case identifier.
     */
    val id: String,
    
    /**
     * The human-readable name of the module.
     */
    val name: String,
    
    /**
     * The module version in SemVer format (e.g., "1.0.0").
     */
    val version: String,
    
    /**
     * Target package scopes for the module.
     * Use ["*"] to target all packages.
     */
    val scope: Array<String>,
    
    /**
     * Description of the module.
     */
    val description: String,
    
    /**
     * Author of the module (optional).
     */
    val author: String = ""
)
```

### HotReloadable.kt

This annotation marks modules that support hot-reload:

```kotlin
package com.wobbz.framework.processor

import kotlin.annotation.AnnotationRetention
import kotlin.annotation.AnnotationTarget

/**
 * Annotation to mark a module as supporting hot-reload functionality.
 * Classes with this annotation must implement IHotReloadable interface.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class HotReloadable
```

### SettingsKey.kt

This annotation is used for binding settings to fields:

```kotlin
package com.wobbz.framework.processor

import kotlin.annotation.AnnotationRetention
import kotlin.annotation.AnnotationTarget

/**
 * Annotation for binding settings keys to fields or properties.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class SettingsKey(
    /**
     * The settings key to bind to this field or property.
     */
    val value: String
)
```

## Annotation Processor Implementation

### XposedPluginProcessor.kt

This is the main annotation processor that will generate the necessary files:

```kotlin
package com.wobbz.framework.processor

import com.google.auto.service.AutoService
import com.wobbz.framework.processor.generators.ModuleInfoGenerator
import com.wobbz.framework.processor.generators.ModulePropGenerator
import com.wobbz.framework.processor.generators.XposedInitGenerator
import com.wobbz.framework.processor.utils.ProcessorUtils
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedAnnotationTypes(
    "com.wobbz.framework.processor.XposedPlugin",
    "com.wobbz.framework.processor.HotReloadable",
    "com.wobbz.framework.processor.SettingsKey"
)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
class XposedPluginProcessor : AbstractProcessor() {
    
    private lateinit var messager: Messager
    private lateinit var filer: Filer
    private var processed = false
    
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager
        filer = processingEnv.filer
    }
    
    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // Skip if already processed
        if (processed) {
            return true
        }
        
        // Find all classes with @XposedPlugin annotation
        val xposedPluginElements = roundEnv.getElementsAnnotatedWith(XposedPlugin::class.java)
        
        if (xposedPluginElements.isEmpty()) {
            return false
        }
        
        // Validate there's only one main module class
        if (xposedPluginElements.size > 1) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Only one class can be annotated with @XposedPlugin"
            )
            return true
        }
        
        // Get the main module class
        val moduleElement = xposedPluginElements.first()
        
        if (moduleElement.kind != ElementKind.CLASS) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "@XposedPlugin can only be applied to classes"
            )
            return true
        }
        
        try {
            // Extract module metadata from annotation
            val moduleMetadata = extractModuleMetadata(moduleElement)
            
            // Check if module supports hot-reload
            val supportsHotReload = moduleElement.getAnnotation(HotReloadable::class.java) != null
            
            // Generate required files
            generateFiles(moduleMetadata, moduleElement as TypeElement, supportsHotReload)
            
            processed = true
            return true
        } catch (e: Exception) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Error processing annotations: ${e.message}"
            )
            e.printStackTrace()
            return true
        }
    }
    
    private fun extractModuleMetadata(element: Element): ModuleMetadata {
        val annotation = element.getAnnotation(XposedPlugin::class.java)
        return ModuleMetadata(
            id = annotation.id,
            name = annotation.name,
            version = annotation.version,
            scope = annotation.scope.toList(),
            description = annotation.description,
            author = annotation.author,
            mainClass = (element as TypeElement).qualifiedName.toString()
        )
    }
    
    private fun generateFiles(
        metadata: ModuleMetadata,
        moduleElement: TypeElement,
        supportsHotReload: Boolean
    ) {
        // Create output directory for generated files
        val outputDir = ProcessorUtils.getOutputDirectory(processingEnv)
        
        // Generate module.prop
        ModulePropGenerator.generate(metadata, outputDir)
        
        // Generate xposed_init
        XposedInitGenerator.generate(metadata, outputDir)
        
        // Generate module-info.json
        ModuleInfoGenerator.generate(metadata, supportsHotReload, outputDir)
        
        messager.printMessage(
            Diagnostic.Kind.NOTE,
            "Generated files for module ${metadata.id}"
        )
    }
}
```

### ModuleMetadata.kt

This class holds the metadata extracted from the XposedPlugin annotation:

```kotlin
package com.wobbz.framework.processor

/**
 * Data class containing module metadata.
 */
data class ModuleMetadata(
    val id: String,
    val name: String,
    val version: String,
    val scope: List<String>,
    val description: String,
    val author: String,
    val mainClass: String
)
```

## Generators

### ModulePropGenerator.kt

Generates the `module.prop` file required by LSPosed:

```kotlin
package com.wobbz.framework.processor.generators

import com.wobbz.framework.processor.ModuleMetadata
import com.wobbz.framework.processor.utils.FileUtils
import java.io.File

/**
 * Generates the module.prop file required by LSPosed.
 */
object ModulePropGenerator {
    
    fun generate(metadata: ModuleMetadata, outputDir: File) {
        val moduleDir = File(outputDir, "assets")
        moduleDir.mkdirs()
        
        val modulePropContent = buildString {
            appendLine("id=${metadata.id}")
            appendLine("name=${metadata.name}")
            appendLine("version=${metadata.version}")
            appendLine("versionCode=${convertVersionToCode(metadata.version)}")
            appendLine("author=${metadata.author}")
            appendLine("description=${metadata.description}")
        }
        
        val modulePropFile = File(moduleDir, "module.prop")
        FileUtils.writeFile(modulePropFile, modulePropContent)
    }
    
    /**
     * Converts a SemVer version string to a numeric version code.
     * For example, "1.2.3" becomes 10203.
     */
    private fun convertVersionToCode(version: String): Int {
        val parts = version.split(".")
        val major = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0
        
        return major * 10000 + minor * 100 + patch
    }
}
```

### XposedInitGenerator.kt

Generates the `xposed_init` file required by LSPosed:

```kotlin
package com.wobbz.framework.processor.generators

import com.wobbz.framework.processor.ModuleMetadata
import com.wobbz.framework.processor.utils.FileUtils
import java.io.File

/**
 * Generates the xposed_init file required by LSPosed.
 */
object XposedInitGenerator {
    
    fun generate(metadata: ModuleMetadata, outputDir: File) {
        val assetsDir = File(outputDir, "assets")
        assetsDir.mkdirs()
        
        val xposedInitContent = metadata.mainClass
        
        val xposedInitFile = File(assetsDir, "xposed_init")
        FileUtils.writeFile(xposedInitFile, xposedInitContent)
    }
}
```

### ModuleInfoGenerator.kt

Generates the `module-info.json` file with extended metadata:

```kotlin
package com.wobbz.framework.processor.generators

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wobbz.framework.processor.ModuleMetadata
import com.wobbz.framework.processor.utils.FileUtils
import java.io.File

/**
 * Generates the module-info.json file with extended metadata.
 */
object ModuleInfoGenerator {
    
    fun generate(metadata: ModuleMetadata, supportsHotReload: Boolean, outputDir: File) {
        val assetsDir = File(outputDir, "assets")
        assetsDir.mkdirs()
        
        val moduleInfo = mapOf(
            "id" to metadata.id,
            "name" to metadata.name,
            "version" to metadata.version,
            "description" to metadata.description,
            "author" to metadata.author,
            "scope" to metadata.scope,
            "mainClass" to metadata.mainClass,
            "features" to listOf<String>(), // Module declares features it offers
            "capabilities" to mapOf(
                "hotReload" to supportsHotReload
            ),
            "providedServices" to mapOf<String, String>(), // Maps service interface name to its version, e.g., "com.example.IService" : "1.2.0"
            "serviceDependencies" to listOf<Map<String, String>>() // List of maps, e.g., [{"service": "com.example.IOtherService", "version": "^1.0"}]
        )
        
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        
        val jsonAdapter = moshi.adapter(Map::class.java)
        val moduleInfoContent = jsonAdapter.indent("  ").toJson(moduleInfo)
        
        val moduleInfoFile = File(assetsDir, "module-info.json")
        FileUtils.writeFile(moduleInfoFile, moduleInfoContent)
    }
}
```

## Utility Classes

### ProcessorUtils.kt

Utility functions for the annotation processor:

```kotlin
package com.wobbz.framework.processor.utils

import java.io.File
import javax.annotation.processing.ProcessingEnvironment
import javax.tools.StandardLocation

/**
 * Utilities for annotation processors.
 */
object ProcessorUtils {
    
    /**
     * Gets the output directory for generated files.
     */
    fun getOutputDirectory(processingEnv: ProcessingEnvironment): File {
        val filer = processingEnv.filer
        val packageElement = filer.getResource(
            StandardLocation.CLASS_OUTPUT,
            "",
            "dummy"
        )
        val outputDirPath = packageElement.toUri().path
        
        // Remove the dummy file from the path
        val outputPath = outputDirPath.substring(0, outputDirPath.lastIndexOf('/'))
        
        return File(outputPath)
    }
}
```

### FileUtils.kt

Utility functions for file operations:

```kotlin
package com.wobbz.framework.processor.utils

import java.io.File

/**
 * Utilities for file operations.
 */
object FileUtils {
    
    /**
     * Writes content to a file.
     */
    fun writeFile(file: File, content: String) {
        file.parentFile?.mkdirs()
        file.writeText(content)
    }
}
```

## Integration with Module Projects

To use the annotation processor in a module project, add it as a kapt dependency in the module's build.gradle:

```groovy
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
    id 'kotlin-kapt'
}

dependencies {
    implementation project(':framework')
    kapt project(':framework:processor')
}
```

## Testing

To test the annotation processor, create a simple module class with the required annotations:

```kotlin
package com.example.testmodule

import com.wobbz.framework.core.IModulePlugin
import com.wobbz.framework.core.PackageLoadedParam
import com.wobbz.framework.processor.XposedPlugin
import com.wobbz.framework.processor.HotReloadable

@XposedPlugin(
    id = "test-module",
    name = "Test Module",
    version = "1.0.0",
    scope = ["com.android.settings"],
    description = "A test module for LSPosedKit",
    author = "Test Author"
)
@HotReloadable
class TestModule : IModulePlugin {
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Test implementation
    }
}
```

Then build the project and verify that the annotation processor generates the following files:

- `assets/module.prop`
- `assets/xposed_init`
- `assets/module-info.json`

## Next Steps

After implementing the annotation processor, proceed to:

1. Implement the hot-reload system
2. Create the settings management system
3. Build the FeatureManager service bus
4. Implement sample modules to test the entire system 