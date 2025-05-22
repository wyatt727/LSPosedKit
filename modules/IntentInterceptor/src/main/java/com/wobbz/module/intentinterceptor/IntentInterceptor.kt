package com.wobbz.module.intentinterceptor

import android.content.Context
import com.wobbz.framework.core.IModulePlugin
import com.wobbz.framework.core.LogLevel
import com.wobbz.framework.core.ModuleLifecycle
import com.wobbz.framework.core.PackageLoadedParam
import com.wobbz.framework.hot.IHotReloadable
import com.wobbz.framework.processor.HotReloadable
import com.wobbz.framework.processor.XposedPlugin
import com.wobbz.framework.service.FeatureManager
import com.wobbz.framework.settings.SettingsProvider
import com.wobbz.module.intentinterceptor.hooks.IntentHooks
import com.wobbz.module.intentinterceptor.services.IntentHistoryService
import com.wobbz.module.intentinterceptor.services.IIntentHistoryService
import com.wobbz.module.intentinterceptor.monitor.IntentMonitor
import com.wobbz.module.intentinterceptor.filters.IntentFilterManager

@XposedPlugin(
    id = "intent-interceptor",
    name = "Intent Interceptor",
    version = "1.0.0",
    description = "Monitors and intercepts Intent communications between apps and components",
    author = "LSPosedKit",
    scope = ["*"]
)
@HotReloadable
class IntentInterceptor : IModulePlugin, IHotReloadable, ModuleLifecycle {
    
    private val hooks = mutableListOf<Any>()
    private lateinit var settings: SettingsProvider
    private lateinit var intentMonitor: IntentMonitor
    private lateinit var filterManager: IntentFilterManager
    private lateinit var historyService: IntentHistoryService
    
    override fun initialize(context: Context, xposed: com.wobbz.framework.core.XposedInterface) {
        // Initialize settings
        settings = SettingsProvider.of(context)
        
        // Initialize components
        intentMonitor = IntentMonitor(xposed, settings)
        filterManager = IntentFilterManager(context, settings)
        historyService = IntentHistoryService(context, settings)
        
        // Register the intent history service
        FeatureManager.registerService(
            IIntentHistoryService::class,
            historyService,
            version = "1.0.0",
            moduleId = "intent-interceptor"
        )
        
        // Declare features
        FeatureManager.declareFeature("intent.monitoring")
        FeatureManager.declareFeature("intent.interception")
        FeatureManager.declareFeature("intent.history")
        
        xposed.log(LogLevel.INFO, "IntentInterceptor initialized")
    }
    
    override fun onStart() {
        // Called when all module dependencies are satisfied
        // Start intent monitoring
        try {
            intentMonitor.start()
            filterManager.loadFilters()
            
            // Check for logging service integration
            val loggingService = FeatureManager.get(ILoggingService::class.java)
            if (loggingService != null) {
                loggingService.log(LogLevel.INFO, "IntentInterceptor started with logging integration")
            }
        } catch (e: Exception) {
            // Handle startup errors gracefully
        }
    }
    
    override fun onStop() {
        // Called when the module is being unloaded
        // Unregister services and clean up resources
        try {
            FeatureManager.unregisterService(IIntentHistoryService::class)
            intentMonitor.stop()
            historyService.flush()
        } catch (e: Exception) {
            // Log any errors during cleanup
        }
        
        hooks.clear()
    }
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Check if we should monitor this package
        if (!shouldMonitorPackage(param.packageName)) {
            return
        }
        
        try {
            // Create intent hooks
            val intentHooks = IntentHooks(param, intentMonitor, filterManager, historyService)
            
            // Apply hooks
            val hookResults = intentHooks.applyHooks()
            hooks.addAll(hookResults)
            
            param.xposed.log(LogLevel.INFO, "Applied ${hookResults.size} intent hooks for ${param.packageName}")
        } catch (e: Exception) {
            param.xposed.logError("Error setting up IntentInterceptor hooks", e)
        }
    }
    
    private fun shouldMonitorPackage(packageName: String): Boolean {
        // Check module settings to determine if this package should be monitored
        val monitorAll = settings.bool("monitor_all_apps", true)
        
        if (monitorAll) {
            return !isExcluded(packageName)
        }
        
        return isIncluded(packageName)
    }
    
    private fun isExcluded(packageName: String): Boolean {
        // Check if package is in the exclusion list
        val excludedPackages = settings.stringSet("excluded_packages", 
            setOf("com.android.systemui", "com.android.settings", "android")
        )
        return excludedPackages.contains(packageName)
    }
    
    private fun isIncluded(packageName: String): Boolean {
        // Check if package is in the inclusion list
        val includedPackages = settings.stringSet("included_packages", emptySet())
        return includedPackages.contains(packageName)
    }
    
    override fun onHotReload() {
        // Clean up existing hooks for hot-reload
        hooks.forEach { unhooker ->
            if (unhooker is com.wobbz.framework.core.MethodUnhooker<*>) {
                unhooker.unhook()
            }
        }
        hooks.clear()
        
        // Note: Services will be handled by their ReloadAware interface
    }
}

// Placeholder interface for logging service integration
interface ILoggingService {
    fun log(level: LogLevel, message: String)
}
