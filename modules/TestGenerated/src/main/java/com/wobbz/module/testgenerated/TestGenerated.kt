package com.wobbz.module.testgenerated

import android.content.Context
import com.wobbz.framework.core.IModulePlugin
import com.wobbz.framework.core.LogLevel
import com.wobbz.framework.core.ModuleLifecycle
import com.wobbz.framework.core.PackageLoadedParam
import com.wobbz.framework.hot.IHotReloadable
import com.wobbz.framework.processor.HotReloadable
import com.wobbz.framework.processor.XposedPlugin
import com.wobbz.framework.settings.SettingsProvider
import com.wobbz.module.testgenerated.hooks.ExampleTestGeneratedHooker

@XposedPlugin(
    id = "test-generated",
    name = "TestGenerated",
    version = "1.0.0",
    description = "Test module to verify generation works",
    author = "LSPosedKit Developer",
    scope = ["*"]
)
@HotReloadable
class TestGenerated : IModulePlugin, IHotReloadable, ModuleLifecycle {
    
    private val hooks = mutableListOf<Any>()
    private lateinit var settings: SettingsProvider
    
    override fun initialize(context: Context, xposed: com.wobbz.framework.core.XposedInterface) {
        // Initialize settings
        settings = SettingsProvider.of(context)
        
        xposed.log(LogLevel.INFO, "TestGenerated initialized")
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
        
        try {
            // Example: Hook a method in the target application
            // Replace this with your actual hooking logic
            val targetClass = param.xposed.loadClass("android.app.Activity")
            val onCreateMethod = targetClass.getDeclaredMethod("onCreate", android.os.Bundle::class.java)
            
            val unhooker = param.xposed.hook(onCreateMethod, ExampleTestGeneratedHooker::class.java)
            hooks.add(unhooker)
            
            param.xposed.log(LogLevel.INFO, "Hooked Activity.onCreate for ${param.packageName}")
        } catch (e: Exception) {
            param.xposed.logError("Error setting up TestGenerated hooks", e)
        }
    }
    
    private fun shouldHookPackage(packageName: String): Boolean {
        // Check module settings to determine if this package should be hooked
        val enabledForAll = settings.bool("enable_for_all", false)
        
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
