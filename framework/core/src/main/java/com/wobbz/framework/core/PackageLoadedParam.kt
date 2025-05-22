package com.wobbz.framework.core

/**
 * Wrapper for package loading parameters.
 * Contains information about the loaded package and provides access to the XposedInterface.
 */
data class PackageLoadedParam(
    /**
     * The name of the package that was loaded.
     */
    val packageName: String,
    
    /**
     * The class loader for the loaded package.
     */
    val classLoader: ClassLoader,
    
    /**
     * The XposedInterface instance for this package.
     */
    val xposed: XposedInterface,
    
    /**
     * Indicates whether this is a system app.
     */
    val isSystemApp: Boolean = false,
    
    /**
     * The application info for the loaded package.
     */
    val appInfo: android.content.pm.ApplicationInfo? = null
) 