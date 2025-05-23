package com.wobbz.module.testmodule.hooks

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.wobbz.framework.core.HookParam
import com.wobbz.framework.core.Hooker
import com.wobbz.framework.core.LogLevel
import com.wobbz.framework.core.PackageLoadedParam

/**
 * Definitive proof hooks for TestModule that demonstrate the module is actively working
 * by modifying system behavior in ways only possible through Xposed hooking.
 */
class ExampleTestModuleHooks(
    private val packageParam: PackageLoadedParam
) {
    
    private val hooks = mutableListOf<Any>()
    
    /**
     * Apply all proof-of-concept hooks for this module.
     * These hooks provide definitive evidence that the module is working.
     */
    fun applyHooks(): List<Any> {
        when (packageParam.packageName) {
            "android" -> {
                // Hook system server for device info modifications
                hookBuildPropertiesForProof()
                hookSystemProperties()
            }
            "com.android.settings" -> {
                // Hook Settings app to add custom behavior
                hookSettingsAppForProof()
            }
            "com.oneplus.systemui", "com.oplus.systemui" -> {
                // Hook OnePlus SystemUI for visible modifications
                hookOnePlusSystemUIForProof()
            }
            "com.android.systemui" -> {
                // Hook standard SystemUI as fallback
                hookSystemUIForProof()
            }
            else -> {
                // Hook common Android components for general proof
                hookToastForProof()
                hookActivityForProof()
            }
        }
        
        return hooks
    }
    
    /**
     * Hook Build properties to return custom values - definitive proof of system-level hooking
     */
    private fun hookBuildPropertiesForProof() {
        try {
            val buildClass = packageParam.xposed.loadClass("android.os.Build")
            
            // Hook the MODEL field getter to return our custom string
            val modelField = buildClass.getDeclaredField("MODEL")
            val unhooker = packageParam.xposed.hook(modelField, BuildModelHooker::class.java)
            hooks.add(unhooker)
            
            packageParam.xposed.log(LogLevel.INFO, "PROOF: Hooked Build.MODEL - will return 'LSPosedKit-TestModule-PROOF'")
        } catch (e: Exception) {
            packageParam.xposed.logError("Failed to hook Build properties", e)
        }
    }
    
    /**
     * Hook system properties to intercept property reads
     */
    private fun hookSystemProperties() {
        try {
            val systemPropertiesClass = packageParam.xposed.loadClass("android.os.SystemProperties")
            val getMethod = systemPropertiesClass.getDeclaredMethod("get", String::class.java, String::class.java)
            
            val unhooker = packageParam.xposed.hook(getMethod, SystemPropertiesHooker::class.java)
            hooks.add(unhooker)
            
            packageParam.xposed.log(LogLevel.INFO, "PROOF: Hooked SystemProperties.get() - will intercept property reads")
        } catch (e: Exception) {
            packageParam.xposed.logError("Failed to hook SystemProperties", e)
        }
    }
    
    /**
     * Hook Settings app to demonstrate UI modification capabilities
     */
    private fun hookSettingsAppForProof() {
        try {
            // Hook the Settings main activity to show our proof
            val activityClass = packageParam.xposed.loadClass("com.android.settings.Settings")
            val onCreateMethod = activityClass.getDeclaredMethod("onCreate", android.os.Bundle::class.java)
            
            val unhooker = packageParam.xposed.hook(onCreateMethod, SettingsProofHooker::class.java)
            hooks.add(unhooker)
            
            packageParam.xposed.log(LogLevel.INFO, "PROOF: Hooked Settings.onCreate - will show proof toast")
        } catch (e: Exception) {
            packageParam.xposed.logError("Failed to hook Settings app", e)
        }
    }
    
    /**
     * Hook OnePlus SystemUI for platform-specific proof
     */
    private fun hookOnePlusSystemUIForProof() {
        try {
            // Hook OnePlus-specific SystemUI components
            val statusBarClass = packageParam.xposed.loadClass("com.android.systemui.statusbar.phone.StatusBar")
            val startMethod = statusBarClass.getDeclaredMethod("start")
            
            val unhooker = packageParam.xposed.hook(startMethod, OnePlusSystemUIProofHooker::class.java)
            hooks.add(unhooker)
            
            packageParam.xposed.log(LogLevel.INFO, "PROOF: Hooked OnePlus SystemUI StatusBar - will log definitive proof")
        } catch (e: Exception) {
            packageParam.xposed.logError("Failed to hook OnePlus SystemUI", e)
        }
    }
    
    /**
     * Hook standard SystemUI as fallback
     */
    private fun hookSystemUIForProof() {
        try {
            val systemUIClass = packageParam.xposed.loadClass("com.android.systemui.SystemUIApplication")
            val onCreateMethod = systemUIClass.getDeclaredMethod("onCreate")
            
            val unhooker = packageParam.xposed.hook(onCreateMethod, SystemUIProofHooker::class.java)
            hooks.add(unhooker)
            
            packageParam.xposed.log(LogLevel.INFO, "PROOF: Hooked SystemUI Application - definitive proof active")
        } catch (e: Exception) {
            packageParam.xposed.logError("Failed to hook SystemUI", e)
        }
    }
    
    /**
     * Hook Toast to intercept and modify toast messages
     */
    private fun hookToastForProof() {
        try {
            val toastClass = packageParam.xposed.loadClass("android.widget.Toast")
            val makeTextMethod = toastClass.getDeclaredMethod("makeText", Context::class.java, CharSequence::class.java, Int::class.javaPrimitiveType)
            
            val unhooker = packageParam.xposed.hook(makeTextMethod, ToastProofHooker::class.java)
            hooks.add(unhooker)
            
            packageParam.xposed.log(LogLevel.INFO, "PROOF: Hooked Toast.makeText - will modify toast messages")
        } catch (e: Exception) {
            packageParam.xposed.logError("Failed to hook Toast", e)
        }
    }
    
    /**
     * Hook Activity lifecycle for general proof
     */
    private fun hookActivityForProof() {
        try {
            val activityClass = packageParam.xposed.loadClass("android.app.Activity")
            val setTitleMethod = activityClass.getDeclaredMethod("setTitle", CharSequence::class.java)
            
            val unhooker = packageParam.xposed.hook(setTitleMethod, ActivityTitleProofHooker::class.java)
            hooks.add(unhooker)
            
            packageParam.xposed.log(LogLevel.INFO, "PROOF: Hooked Activity.setTitle - will modify activity titles")
        } catch (e: Exception) {
            packageParam.xposed.logError("Failed to hook Activity.setTitle", e)
        }
    }
    
    // ========== HOOKER IMPLEMENTATIONS ==========
    
    /**
     * Modifies Build.MODEL to return proof string
     */
    inner class BuildModelHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            // Replace the MODEL value with our proof
            param.setResult("LSPosedKit-TestModule-PROOF-${Build.MODEL}")
            packageParam.xposed.log(LogLevel.INFO, "PROOF TRIGGERED: Build.MODEL hooked and modified!")
        }
        
        override fun afterHook(param: HookParam) {}
    }
    
    /**
     * Intercepts system property reads and injects proof values
     */
    inner class SystemPropertiesHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            val key = param.args[0] as? String
            val defaultValue = param.args[1] as? String
            
            // Inject proof into specific properties
            when (key) {
                "ro.build.version.release" -> {
                    param.setResult("LSPosedKit-PROOF-Android-${defaultValue}")
                    packageParam.xposed.log(LogLevel.INFO, "PROOF TRIGGERED: Modified Android version property!")
                }
                "persist.sys.oplus.hbm_mode" -> {
                    param.setResult("LSPosedKit-PROOF-ENABLED")
                    packageParam.xposed.log(LogLevel.INFO, "PROOF TRIGGERED: Modified OnePlus HBM property!")
                }
            }
        }
        
        override fun afterHook(param: HookParam) {}
    }
    
    /**
     * Shows proof toast when Settings app opens
     */
    inner class SettingsProofHooker : Hooker {
        override fun beforeHook(param: HookParam) {}
        
        override fun afterHook(param: HookParam) {
            val activity = param.thisObject
            if (activity is Context) {
                try {
                    Toast.makeText(
                        activity,
                        "🎯 LSPosedKit TestModule is ACTIVE! 🎯\nHooks are working perfectly!",
                        Toast.LENGTH_LONG
                    ).show()
                    
                    packageParam.xposed.log(LogLevel.INFO, "PROOF TRIGGERED: Settings app toast displayed!")
                } catch (e: Exception) {
                    packageParam.xposed.logError("Failed to show proof toast", e)
                }
            }
        }
    }
    
    /**
     * Logs definitive proof when OnePlus SystemUI starts
     */
    inner class OnePlusSystemUIProofHooker : Hooker {
        override fun beforeHook(param: HookParam) {}
        
        override fun afterHook(param: HookParam) {
            packageParam.xposed.log(LogLevel.INFO, "🚀 DEFINITIVE PROOF: OnePlus SystemUI StatusBar hooked successfully!")
            packageParam.xposed.log(LogLevel.INFO, "🎯 LSPosedKit TestModule is actively modifying OnePlus system components!")
            
            // Try to access OnePlus-specific properties to prove we're in the right context
            try {
                val context = param.thisObject
                if (context is Context) {
                    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                    packageParam.xposed.log(LogLevel.INFO, "PROOF: Hooked OnePlus SystemUI version: ${packageInfo.versionName}")
                }
            } catch (e: Exception) {
                packageParam.xposed.logError("Error getting OnePlus SystemUI info", e)
            }
        }
    }
    
    /**
     * Logs proof when standard SystemUI starts
     */
    inner class SystemUIProofHooker : Hooker {
        override fun beforeHook(param: HookParam) {}
        
        override fun afterHook(param: HookParam) {
            packageParam.xposed.log(LogLevel.INFO, "🚀 DEFINITIVE PROOF: SystemUI Application hooked successfully!")
            packageParam.xposed.log(LogLevel.INFO, "🎯 LSPosedKit TestModule is actively modifying system UI components!")
        }
    }
    
    /**
     * Modifies toast messages to prove interception
     */
    inner class ToastProofHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            val originalText = param.args[1] as? CharSequence
            if (originalText != null && !originalText.contains("LSPosedKit")) {
                param.args[1] = "🎯 HOOKED: $originalText [LSPosedKit TestModule Active]"
                packageParam.xposed.log(LogLevel.INFO, "PROOF TRIGGERED: Toast message modified!")
            }
        }
        
        override fun afterHook(param: HookParam) {}
    }
    
    /**
     * Modifies activity titles to prove hooking
     */
    inner class ActivityTitleProofHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            val originalTitle = param.args[0] as? CharSequence
            if (originalTitle != null && !originalTitle.contains("🎯")) {
                param.args[0] = "🎯 $originalTitle [LSPosedKit Active]"
                packageParam.xposed.log(LogLevel.INFO, "PROOF TRIGGERED: Activity title modified!")
            }
        }
        
        override fun afterHook(param: HookParam) {}
    }
}
