package com.wobbz.framework.core

import android.content.Context
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

/**
 * Base class for LSPosedKit modules that bridges the libxposed API with LSPosedKit interfaces.
 * This class handles the integration between libxposed API and LSPosedKit's module system.
 */
abstract class LSPKModule(
    base: XposedInterface,
    param: XposedModuleInterface.ModuleLoadedParam
) : XposedModule(base, param) {
    
    private val lspkInterface: XposedInterfaceImpl by lazy {
        XposedInterfaceImpl(base, param.processName)
    }
    
    /**
     * Called when the module is initialized.
     * Subclasses should override this method to implement their initialization logic.
     */
    abstract fun initialize(context: Context, xposed: com.wobbz.framework.core.XposedInterface)
    
    /**
     * Called when a package is loaded that matches the module's scope.
     * Subclasses should override this method to apply their hooks.
     */
    abstract fun onPackageLoaded(param: PackageLoadedParam)
    
    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        try {
            // Convert libxposed API param to LSPosedKit param
            val lspkParam = PackageLoadedParam(
                packageName = param.packageName,
                classLoader = param.classLoader,
                xposed = lspkInterface,
                isSystemApp = param.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM != 0,
                appInfo = param.applicationInfo
            )
            
            onPackageLoaded(lspkParam)
        } catch (e: Exception) {
            log("Error in onPackageLoaded: ${e.message}", e)
        }
    }
    
    override fun onSystemServerLoaded(param: XposedModuleInterface.SystemServerLoadedParam) {
        try {
            // Handle system server loading if needed
            val lspkParam = PackageLoadedParam(
                packageName = "android",
                classLoader = param.classLoader,
                xposed = lspkInterface,
                isSystemApp = true
            )
            
            onPackageLoaded(lspkParam)
        } catch (e: Exception) {
            log("Error in onSystemServerLoaded: ${e.message}", e)
        }
    }
    
    /**
     * Gets the LSPosedKit XposedInterface wrapper.
     */
    protected fun getLSPKInterface(): com.wobbz.framework.core.XposedInterface {
        return lspkInterface
    }
} 