package com.wobbz.framework.core

import android.content.Context

/**
 * Primary interface that all LSPosedKit modules must implement.
 * This interface defines the lifecycle methods for module initialization and package loading.
 */
interface IModulePlugin {
    
    /**
     * Called when the module is initialized.
     * This is where modules should perform one-time setup operations.
     * 
     * @param context The module's context
     * @param xposed The XposedInterface for this module
     */
    fun initialize(context: Context, xposed: XposedInterface)
    
    /**
     * Called when a package is loaded that matches the module's scope.
     * This is where modules should apply their hooks.
     * 
     * @param param The package loaded parameters
     */
    fun onPackageLoaded(param: PackageLoadedParam)
} 