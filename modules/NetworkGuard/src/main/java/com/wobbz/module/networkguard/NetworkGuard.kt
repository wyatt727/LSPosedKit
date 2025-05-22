package com.wobbz.module.networkguard

import android.content.Context
import com.wobbz.framework.core.IModulePlugin
import com.wobbz.framework.core.LogLevel
import com.wobbz.framework.core.ModuleLifecycle
import com.wobbz.framework.core.PackageLoadedParam
import com.wobbz.framework.hot.IHotReloadable
import com.wobbz.framework.hot.HotReloadManager
import com.wobbz.framework.processor.HotReloadable
import com.wobbz.framework.processor.XposedPlugin
import com.wobbz.framework.service.FeatureManager
import com.wobbz.framework.settings.SettingsProvider
import com.wobbz.module.networkguard.hooks.NetworkHooks
import com.wobbz.module.networkguard.rules.NetworkRuleProvider
import com.wobbz.module.networkguard.rules.RuleManager
import com.wobbz.module.networkguard.services.INetworkRuleService

@XposedPlugin(
    id = "network-guard",
    name = "Network Guard",
    version = "1.0.0",
    description = "Monitors and controls network traffic using rule-based filtering",
    author = "LSPosedKit",
    scope = ["*"]
)
@HotReloadable
class NetworkGuard : IModulePlugin, IHotReloadable, ModuleLifecycle {
    
    private val hooks = mutableListOf<Any>()
    private lateinit var settings: SettingsProvider
    private lateinit var ruleManager: RuleManager
    private lateinit var networkRuleService: NetworkRuleProvider
    
    override fun initialize(context: Context, xposed: com.wobbz.framework.core.XposedInterface) {
        try {
            // Initialize settings
            settings = SettingsProvider.of(context)
            
            // Initialize rule manager
            ruleManager = RuleManager(context)
            
            // Initialize network rule service
            networkRuleService = NetworkRuleProvider(ruleManager)
            
            // Register network rule service with the Service Bus
            FeatureManager.registerService(
                INetworkRuleService::class, 
                networkRuleService,
                version = "1.0.0",
                dependencies = emptyList(), // No dependencies for this service
                moduleId = "network-guard"
            )
            
            // Declare features that this module provides
            FeatureManager.declareFeature("network.inspection")
            FeatureManager.declareFeature("network.blocking")
            FeatureManager.declareFeature("network.rules")
            
            // Register with hot-reload manager
            try {
                val hotReloadManager = HotReloadManager.getInstance(context)
                hotReloadManager.registerModule("network-guard", this)
                xposed.log(LogLevel.INFO, "NetworkGuard registered for hot-reload")
            } catch (e: Exception) {
                xposed.log(LogLevel.WARN, "Could not register for hot-reload: ${e.message}")
            }
            
            xposed.log(LogLevel.INFO, "NetworkGuard initialized successfully")
            
        } catch (e: Exception) {
            xposed.logError("Failed to initialize NetworkGuard", e)
            throw e
        }
    }
    
    override fun onStart() {
        // Called when all module dependencies are satisfied and it's fully operational
        // This is a good place for tasks that require other modules' services
        
        try {
            // Example: Check if a logging service is available from another module
            // This demonstrates how modules can discover and use services from other modules
            val loggingService = FeatureManager.get(ILoggingService::class.java)
            if (loggingService != null) {
                loggingService.log("NetworkGuard", "NetworkGuard module started successfully")
            } else {
                // Logging service not available, use built-in logging
                android.util.Log.i("NetworkGuard", "NetworkGuard started (no external logging service found)")
            }
            
            // Log current rule count
            val rules = ruleManager.getRules()
            android.util.Log.i("NetworkGuard", "NetworkGuard started with ${rules.size} network rules")
            
        } catch (e: Exception) {
            android.util.Log.e("NetworkGuard", "Error during NetworkGuard startup", e)
        }
    }
    
    override fun onStop() {
        // Called when the module is being unloaded or the application is shutting down
        // This is where we unregister our services and release resources
        
        try {
            // Unregister our service from the Service Bus
            FeatureManager.unregisterService(INetworkRuleService::class)
            android.util.Log.d("NetworkGuard", "Unregistered NetworkRuleService from Service Bus")
            
            // Clear hooks (they should already be unhooked by onHotReload if applicable)
            hooks.clear()
            
            android.util.Log.i("NetworkGuard", "NetworkGuard stopped and cleaned up")
            
        } catch (e: Exception) {
            android.util.Log.e("NetworkGuard", "Error during NetworkGuard shutdown", e)
        }
    }
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Check if we should monitor this package
        if (!shouldMonitorPackage(param.packageName)) {
            param.xposed.log(LogLevel.DEBUG, "NetworkGuard: Skipping monitoring for ${param.packageName}")
            return
        }
        
        try {
            // Create network hooks for this package
            val networkHooks = NetworkHooks(param, ruleManager)
            
            // Apply hooks and store references for cleanup
            val hookResults = networkHooks.applyHooks()
            hooks.addAll(hookResults)
            
            param.xposed.log(LogLevel.INFO, "NetworkGuard: Applied ${hookResults.size} network hooks for ${param.packageName}")
            
        } catch (e: Exception) {
            param.xposed.logError("NetworkGuard: Error setting up hooks for ${param.packageName}", e)
        }
    }
    
    private fun shouldMonitorPackage(packageName: String): Boolean {
        // Check if this package should be monitored based on settings
        val monitorAll = settings.bool("monitor_all_apps", false)
        
        if (monitorAll) {
            return !isExcluded(packageName)
        }
        
        return isIncluded(packageName)
    }
    
    private fun isExcluded(packageName: String): Boolean {
        // Check if package is in the exclusion list
        val excludedPackages = settings.stringSet("excluded_packages", setOf(
            "com.android.systemui", 
            "com.android.settings",
            "de.robv.android.xposed.installer" // Exclude LSPosed Manager itself
        ))
        return excludedPackages.contains(packageName)
    }
    
    private fun isIncluded(packageName: String): Boolean {
        // Check if package is in the inclusion list
        val includedPackages = settings.stringSet("included_packages", setOf(
            "com.android.browser", 
            "com.android.chrome"
        ))
        return includedPackages.contains(packageName)
    }
    
    override fun onHotReload() {
        // Clean up existing hooks before hot-reload
        android.util.Log.d("NetworkGuard", "Preparing for hot-reload, cleaning up ${hooks.size} hooks")
        
        try {
            hooks.forEach { unhooker ->
                if (unhooker is com.wobbz.framework.core.MethodUnhooker<*>) {
                    unhooker.unhook()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("NetworkGuard", "Error unhooking during hot-reload", e)
        } finally {
            // Always clear the list
            hooks.clear()
        }
        
        android.util.Log.d("NetworkGuard", "Hot-reload cleanup completed")
        
        // Note: We don't need to unregister/re-register services on hot-reload
        // That's handled automatically by the ReloadAware interface implementation on NetworkRuleProvider
    }
}

/**
 * Placeholder interface for a hypothetical logging service from another module.
 * This demonstrates how modules can define interfaces for services they want to use.
 */
interface ILoggingService {
    fun log(tag: String, message: String)
} 