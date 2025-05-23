package com.wobbz.module.testmodule

import android.content.Context
import com.wobbz.framework.core.IModulePlugin
import com.wobbz.framework.core.LogLevel
import com.wobbz.framework.core.ModuleLifecycle
import com.wobbz.framework.core.PackageLoadedParam
import com.wobbz.framework.hot.IHotReloadable
import com.wobbz.framework.processor.HotReloadable
import com.wobbz.framework.processor.XposedPlugin
import com.wobbz.framework.settings.SettingsProvider
import com.wobbz.module.testmodule.hooks.ExampleTestModuleHooks

@XposedPlugin(
    id = "test-module",
    name = "TestModule - Definitive Proof",
    version = "1.0.0",
    description = "Test module that provides definitive proof of hooking functionality",
    author = "LSPosedKit Developer",
    scope = [
        "android",                    // System server for Build properties
        "com.android.settings",       // Settings app for visible proof
        "com.android.systemui",       // Standard SystemUI
        "com.oneplus.systemui",       // OnePlus SystemUI
        "com.oplus.systemui",         // Alternative OnePlus package
        "*"                           // All other apps for general hooks
    ]
)
@HotReloadable
class TestModule : IModulePlugin, IHotReloadable, ModuleLifecycle {
    
    private val hooks = mutableListOf<Any>()
    private lateinit var settings: SettingsProvider
    
    override fun initialize(context: Context, xposed: com.wobbz.framework.core.XposedInterface) {
        // Initialize settings
        settings = SettingsProvider.of(context)
        
        xposed.log(LogLevel.INFO, "TestModule initialized")
    }
    
    override fun onStart() {
        // Called when all module dependencies are satisfied
        // Perform any initialization that requires other services
    }
    
    override fun onStop() {
        // Called when the module is being unloaded
        // Clean up resources and unregister services
        hooks.clear()
    }
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Check if we should hook this package
        if (!shouldHookPackage(param.packageName)) {
            return
        }
        
        param.xposed.log(LogLevel.INFO, "🎯 PROOF: Preparing to hook package: ${param.packageName}")
        
        try {
            // Apply hooks using the hooks manager
            val moduleHooks = ExampleTestModuleHooks(param)
            val appliedHooks = moduleHooks.applyHooks()
            hooks.addAll(appliedHooks)
            
            param.xposed.log(LogLevel.INFO, "✅ PROOF: TestModule hooks successfully applied for ${param.packageName} (${appliedHooks.size} hooks)")
            
            // Log special proof messages for critical packages
            when (param.packageName) {
                "android" -> param.xposed.log(LogLevel.INFO, "🚀 CRITICAL PROOF: System server hooks active - Build properties will be modified!")
                "com.android.settings" -> param.xposed.log(LogLevel.INFO, "🚀 CRITICAL PROOF: Settings app hooks active - Proof toast will appear!")
                "com.android.systemui", "com.oneplus.systemui", "com.oplus.systemui" -> 
                    param.xposed.log(LogLevel.INFO, "🚀 CRITICAL PROOF: SystemUI hooks active - System UI modifications in effect!")
            }
            
        } catch (e: Exception) {
            param.xposed.logError("❌ PROOF FAILED: Error setting up TestModule hooks for ${param.packageName}", e)
        }
    }
    
    private fun shouldHookPackage(packageName: String): Boolean {
        // Always hook these critical packages for definitive proof
        val criticalPackages = setOf(
            "android",                    // System server for Build properties
            "com.android.settings",       // Settings app for visible proof toast
            "com.android.systemui",       // Standard SystemUI
            "com.oneplus.systemui",       // OnePlus SystemUI
            "com.oplus.systemui"          // Alternative OnePlus package
        )
        
        if (criticalPackages.contains(packageName)) {
            return true
        }
        
        // Check module settings for other packages
        val enabledForAll = settings.bool("enable_for_all", true) // Default to true for proof
        
        if (enabledForAll) {
            val excludedPackages = settings.stringSet("excluded_packages", emptySet())
            return !excludedPackages.contains(packageName)
        } else {
            val includedPackages = settings.stringSet("included_packages", emptySet())
            return includedPackages.contains(packageName)
        }
    }
    
    override fun onHotReload() {
        // Clean up existing hooks for hot-reload
        hooks.forEach { unhooker ->
            if (unhooker is com.wobbz.framework.core.MethodUnhooker<*>) {
                unhooker.unhook()
            }
        }
        hooks.clear()
    }
}
