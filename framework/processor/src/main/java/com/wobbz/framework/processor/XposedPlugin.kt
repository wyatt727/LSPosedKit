package com.wobbz.framework.processor

/**
 * Annotation for marking LSPosedKit module classes.
 * This annotation provides metadata about the module and is processed at compile time
 * to generate module.prop, xposed_init, and module-info.json files.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class XposedPlugin(
    /**
     * The unique identifier for this module.
     * Must be unique across all modules.
     */
    val id: String,
    
    /**
     * The human-readable name of the module.
     */
    val name: String,
    
    /**
     * The version of the module in semver format (e.g., "1.0.0").
     */
    val version: String,
    
    /**
     * A brief description of what the module does.
     */
    val description: String,
    
    /**
     * The author or organization that created the module.
     */
    val author: String = "",
    
    /**
     * Array of package names that this module should be applied to.
     * Use ["*"] to apply to all packages.
     */
    val scope: Array<String>,
    
    /**
     * Minimum Android API level required for this module.
     * Defaults to the project's minSdk.
     */
    val minApi: Int = -1,
    
    /**
     * Maximum Android API level supported by this module.
     * Defaults to no maximum.
     */
    val maxApi: Int = -1
) 