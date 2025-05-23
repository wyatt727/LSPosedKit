package com.wobbz.module.networkguard

import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModuleInterface
import com.wobbz.framework.core.LSPKModule

/**
 * LSPosed entry point for NetworkGuard module.
 * This class implements the standard LSPosed interfaces and bridges to our LSPosedKit framework.
 */
class LSPosedEntry : XposedModuleInterface {
    
    override fun onPackageLoaded(param: XposedInterface.PackageLoadedParam) {
        // Create our framework module
        val module = NetworkGuard()
        
        // Create the bridge and initialize
        val lspkModule = LSPKModule(module)
        lspkModule.onPackageLoaded(param)
    }
} 