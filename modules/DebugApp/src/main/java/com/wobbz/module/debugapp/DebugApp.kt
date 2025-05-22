package com.wobbz.module.debugapp

import android.content.Context
import android.content.pm.ApplicationInfo
import com.wobbz.framework.core.IModulePlugin
import com.wobbz.framework.core.LogLevel
import com.wobbz.framework.core.PackageLoadedParam
import com.wobbz.framework.core.XposedInterface
import com.wobbz.framework.core.Hooker
import com.wobbz.framework.core.HookParam
import com.wobbz.framework.core.MethodUnhooker
import com.wobbz.framework.hot.IHotReloadable
import com.wobbz.framework.hot.HotReloadManager
import com.wobbz.framework.processor.HotReloadable
import com.wobbz.framework.processor.SettingsKey
import com.wobbz.framework.processor.XposedPlugin
import com.wobbz.framework.settings.SettingsProvider

@XposedPlugin(
    id = "debug-app",
    name = "Debug App",
    version = "1.0.0",
    description = "Forces applications to run in debug mode",
    author = "LSPosedKit",
    scope = ["*"]
)
@HotReloadable
class DebugApp : IModulePlugin, IHotReloadable {
    
    private val hooks = mutableListOf<MethodUnhooker<*>>()
    private lateinit var settings: SettingsProvider
    private lateinit var moduleSettings: DebugAppSettings
    
    override fun initialize(context: Context, xposed: XposedInterface) {
        // Initialize settings
        settings = SettingsProvider.of(context)
        
        // Bind settings to a typed class
        moduleSettings = settings.bind(DebugAppSettings::class.java)
        
        // Register with hot-reload manager
        try {
            val hotReloadManager = HotReloadManager.getInstance(context)
            hotReloadManager.registerModule("debug-app", this)
            xposed.log(LogLevel.INFO, "DebugApp registered for hot-reload")
        } catch (e: Exception) {
            xposed.log(LogLevel.WARN, "Could not register for hot-reload: ${e.message}")
        }
        
        xposed.log(LogLevel.INFO, "DebugApp initialized with settings: enableForAll=${moduleSettings.enableForAll}, logLevel=${moduleSettings.logLevel}")
    }
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Check if we should enable debug mode for this package
        if (!shouldEnableDebug(param.packageName)) {
            param.xposed.log(LogLevel.DEBUG, "Skipping debug mode for ${param.packageName}")
            return
        }
        
        try {
            // Hook ApplicationInfo.flags to add FLAG_DEBUGGABLE
            val appInfoClass = param.xposed.loadClass("android.content.pm.ApplicationInfo")
            val flagsField = appInfoClass.getDeclaredField("flags")
            
            // Hook the field getter to modify the flags
            val unhooker = param.xposed.hook(flagsField, ApplicationInfoFlagsHooker::class.java)
            hooks.add(unhooker)
            
            param.xposed.log(LogLevel.INFO, "Enabled debug mode for ${param.packageName}")
            
            // Also hook some system properties for enhanced debugging
            hookSystemProperties(param)
            
        } catch (e: Exception) {
            param.xposed.logError("Error setting up debug hooks for ${param.packageName}", e)
        }
    }
    
    private fun hookSystemProperties(param: PackageLoadedParam) {
        try {
            // Hook android.os.SystemProperties.get method
            val sysPropClass = param.xposed.loadClass("android.os.SystemProperties")
            val getMethod = sysPropClass.getDeclaredMethod("get", String::class.java, String::class.java)
            
            val unhooker = param.xposed.hook(getMethod, SystemPropertiesHooker::class.java)
            hooks.add(unhooker)
            
            param.xposed.log(LogLevel.DEBUG, "Hooked SystemProperties.get for ${param.packageName}")
            
        } catch (e: Exception) {
            // SystemProperties might not be available in all contexts, which is fine
            param.xposed.log(LogLevel.DEBUG, "Could not hook SystemProperties: ${e.message}")
        }
    }
    
    private fun shouldEnableDebug(packageName: String): Boolean {
        if (moduleSettings.enableForAll) {
            return !moduleSettings.excludedPackages.contains(packageName)
        } else {
            return moduleSettings.includedPackages.contains(packageName)
        }
    }
    
    override fun onHotReload() {
        // Clean up existing hooks during hot-reload
        hooks.forEach { unhooker ->
            try {
                unhooker.unhook()
            } catch (e: Exception) {
                // Log but don't fail on unhook errors
                android.util.Log.w("DebugApp", "Error unhooking during hot-reload", e)
            }
        }
        hooks.clear()
        
        // Re-read settings after hot-reload
        if (::settings.isInitialized) {
            moduleSettings = settings.bind(DebugAppSettings::class.java)
        }
    }
    
    /**
     * Settings class demonstrating @SettingsKey annotation usage.
     */
    class DebugAppSettings {
        @SettingsKey("enable_for_all")
        val enableForAll: Boolean = true
        
        @SettingsKey("excluded_packages")
        val excludedPackages: Set<String> = setOf("com.android.systemui", "com.android.settings")
        
        @SettingsKey("included_packages")
        val includedPackages: Set<String> = emptySet()
        
        @SettingsKey("log_level")
        val logLevel: String = "INFO"
    }
    
    /**
     * Hooker that modifies ApplicationInfo.flags to add FLAG_DEBUGGABLE.
     */
    class ApplicationInfoFlagsHooker : Hooker {
        override fun afterHook(param: HookParam) {
            try {
                // Get the current flags value
                val currentFlags = param.getResult<Int>() ?: 0
                
                // Add the debuggable flag
                val newFlags = currentFlags or ApplicationInfo.FLAG_DEBUGGABLE
                
                // Set the modified flags
                param.setResult(newFlags)
                
                android.util.Log.d("DebugApp", "Modified ApplicationInfo flags: $currentFlags -> $newFlags")
                
            } catch (e: Exception) {
                android.util.Log.e("DebugApp", "Error in ApplicationInfoFlagsHooker", e)
            }
        }
    }
    
    /**
     * Hooker that modifies system properties to return debug-friendly values.
     */
    class SystemPropertiesHooker : Hooker {
        
        companion object {
            // Map of properties to override for debugging
            private val debugProperties = mapOf(
                "ro.debuggable" to "1",
                "ro.secure" to "0",
                "debug.force-debuggable" to "1",
                "persist.sys.usb.config" to "adb"
            )
        }
        
        override fun afterHook(param: HookParam) {
            try {
                // Get the property key being requested
                val key = param.args[0] as? String ?: return
                
                // If this is a debug-related property, override it
                debugProperties[key]?.let { debugValue ->
                    param.setResult(debugValue)
                    android.util.Log.d("DebugApp", "Overrode system property $key = $debugValue")
                }
                
            } catch (e: Exception) {
                android.util.Log.e("DebugApp", "Error in SystemPropertiesHooker", e)
            }
        }
    }
} 