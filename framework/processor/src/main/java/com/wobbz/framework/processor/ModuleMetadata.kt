package com.wobbz.framework.processor

/**
 * Represents the metadata extracted from @XposedPlugin annotation.
 * This data is used to generate module.prop, xposed_init, and module-info.json files.
 */
data class ModuleMetadata(
    val id: String,
    val name: String,
    val version: String,
    val description: String,
    val author: String,
    val scope: List<String>,
    val minApiVersion: Int,
    val maxApiVersion: Int,
    val className: String,
    val packageName: String,
    val isHotReloadable: Boolean = false
) {
    
    /**
     * Generates the content for module.prop file.
     */
    fun generateModuleProp(): String {
        return buildString {
            appendLine("id=$id")
            appendLine("name=$name")
            appendLine("version=$version")
            appendLine("versionCode=1")
            appendLine("author=$author")
            appendLine("description=$description")
            appendLine("minMagiskVersion=20400")
        }
    }
    
    /**
     * Generates the content for xposed_init file.
     */
    fun generateXposedInit(): String {
        return "$packageName.$className"
    }
    
    /**
     * Generates the content for module-info.json file.
     */
    fun generateModuleInfo(): String {
        val scopeArray = scope.joinToString(",") { "\"$it\"" }
        
        return buildString {
            appendLine("{")
            appendLine("  \"id\": \"$id\",")
            appendLine("  \"name\": \"$name\",")
            appendLine("  \"version\": \"$version\",")
            appendLine("  \"description\": \"$description\",")
            appendLine("  \"author\": \"$author\",")
            appendLine("  \"packageName\": \"$packageName\",")
            appendLine("  \"className\": \"$className\",")
            appendLine("  \"scope\": [$scopeArray],")
            appendLine("  \"minApiVersion\": $minApiVersion,")
            appendLine("  \"maxApiVersion\": $maxApiVersion,")
            appendLine("  \"capabilities\": {")
            appendLine("    \"hotReload\": $isHotReloadable")
            appendLine("  }")
            appendLine("}")
        }
    }
    
    /**
     * Validates the metadata for correctness.
     */
    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        
        if (id.isBlank()) {
            errors.add("Module ID cannot be blank")
        } else if (!id.matches(Regex("[a-z0-9-_]+"))) {
            errors.add("Module ID must contain only lowercase letters, numbers, hyphens, and underscores")
        }
        
        if (name.isBlank()) {
            errors.add("Module name cannot be blank")
        }
        
        if (version.isBlank()) {
            errors.add("Module version cannot be blank")
        } else if (!version.matches(Regex("\\d+\\.\\d+\\.\\d+(-[a-zA-Z0-9.-]+)?"))) {
            errors.add("Module version must follow semantic versioning (e.g., 1.0.0)")
        }
        
        if (description.isBlank()) {
            errors.add("Module description cannot be blank")
        }
        
        if (author.isBlank()) {
            errors.add("Module author cannot be blank")
        }
        
        if (scope.isEmpty()) {
            errors.add("Module scope cannot be empty")
        }
        
        if (className.isBlank()) {
            errors.add("Module class name cannot be blank")
        }
        
        if (packageName.isBlank()) {
            errors.add("Module package name cannot be blank")
        }
        
        if (minApiVersion < 1) {
            errors.add("Minimum API version must be greater than 0")
        }
        
        if (maxApiVersion < minApiVersion) {
            errors.add("Maximum API version must be greater than or equal to minimum API version")
        }
        
        return errors
    }
} 