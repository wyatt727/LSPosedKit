package com.wobbz.framework.core

import android.content.Context
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModuleInterface

/**
 * Concrete bridge implementation that wraps an IModulePlugin and extends LSPKModule.
 * This class provides the bridge between the libxposed API and LSPosedKit framework.
 */
class ModulePluginBridge(
    private val modulePlugin: IModulePlugin,
    base: XposedInterface,
    param: XposedModuleInterface.ModuleLoadedParam
) : LSPKModule(base, param) {
    
    private var initialized = false
    
    override fun initialize(context: Context, xposed: com.wobbz.framework.core.XposedInterface) {
        if (!initialized) {
            modulePlugin.initialize(context, xposed)
            initialized = true
        }
    }
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Ensure initialization happens before package loading
        if (!initialized) {
            initialize(param.xposed.getSystemContext(), param.xposed)
        }
        
        modulePlugin.onPackageLoaded(param)
    }
} 