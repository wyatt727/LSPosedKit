package com.wobbz.framework.hot

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.wobbz.framework.core.LogLevel
import com.wobbz.framework.hot.client.DexReloader
import com.wobbz.framework.hot.client.HotReloadClient
import com.wobbz.framework.hot.exceptions.HotReloadException
import com.wobbz.framework.service.FeatureManager
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * Manages the hot-reload process for LSPosed modules.
 * This is the main entry point for hot-reload functionality.
 */
class HotReloadManager private constructor(private val context: Context) {
    
    private val TAG = "LSPK-HotReload"
    private val mainHandler = Handler(Looper.getMainLooper())
    private val executorService = Executors.newSingleThreadExecutor()
    private val client = HotReloadClient(context)
    private val dexReloader = DexReloader(context)
    
    private val registeredModules = ConcurrentHashMap<String, IHotReloadable>()
    private var isMonitoring = false
    
    /**
     * Registers a module for hot-reload capability.
     */
    fun registerModule(moduleId: String, module: IHotReloadable) {
        registeredModules[moduleId] = module
        Log.i(TAG, "Registered hot-reloadable module: $moduleId")
        
        // If monitoring is already started, this module will be included in future updates
        if (isMonitoring) {
            Log.d(TAG, "Module $moduleId registered while monitoring is active")
        }
    }
    
    /**
     * Unregisters a module from hot-reload.
     */
    fun unregisterModule(moduleId: String) {
        registeredModules.remove(moduleId)
        Log.i(TAG, "Unregistered hot-reloadable module: $moduleId")
    }
    
    /**
     * Starts the hot-reload monitoring service.
     */
    fun startMonitoring() {
        if (isMonitoring) {
            Log.w(TAG, "Hot-reload monitoring is already active")
            return
        }
        
        if (registeredModules.isEmpty()) {
            Log.w(TAG, "No hot-reloadable modules registered, monitoring will be limited")
        }
        
        // Check if hot-reload is supported
        if (!isSupported()) {
            Log.e(TAG, "Hot-reload is not supported on this device")
            throw HotReloadException("Hot-reload is not supported on this device")
        }
        
        isMonitoring = true
        Log.i(TAG, "Starting hot-reload monitoring for ${registeredModules.size} modules")
        
        executorService.submit {
            try {
                client.startListening { moduleId, dexBytes ->
                    handleHotReload(moduleId, dexBytes)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start hot-reload monitoring", e)
                isMonitoring = false
            }
        }
        
        Log.i(TAG, "Hot-reload monitoring started successfully")
    }
    
    /**
     * Stops the hot-reload monitoring service.
     */
    fun stopMonitoring() {
        if (!isMonitoring) {
            Log.d(TAG, "Hot-reload monitoring is not active")
            return
        }
        
        isMonitoring = false
        client.stopListening()
        Log.i(TAG, "Hot-reload monitoring stopped")
    }
    
    /**
     * Manually triggers a hot-reload for a specific module.
     */
    fun triggerHotReload(moduleId: String) {
        if (!isSupported()) {
            Log.e(TAG, "Cannot trigger hot-reload: not supported on this device")
            return
        }
        
        if (!registeredModules.containsKey(moduleId)) {
            Log.w(TAG, "Module $moduleId is not registered for hot-reload")
            return
        }
        
        Log.i(TAG, "Manually triggering hot-reload for module: $moduleId")
        
        client.triggerUpdateCheck(moduleId) { actualModuleId, dexBytes ->
            handleHotReload(actualModuleId, dexBytes)
        }
    }
    
    /**
     * Handles a hot-reload request for a specific module.
     */
    private fun handleHotReload(moduleId: String, dexBytes: ByteArray) {
        Log.i(TAG, "Processing hot-reload for module: $moduleId (${dexBytes.size} bytes)")
        
        val module = registeredModules[moduleId]
        if (module == null) {
            Log.w(TAG, "Received hot-reload for unregistered module: $moduleId")
            return
        }
        
        try {
            // Notify FeatureManager before hot-reload
            FeatureManager.onBeforeHotReload(moduleId)
            
            // Save DEX to temporary file
            val dexFile = File(context.cacheDir, "$moduleId-${System.currentTimeMillis()}.dex")
            dexFile.writeBytes(dexBytes)
            
            Log.d(TAG, "Saved DEX to temporary file: ${dexFile.absolutePath}")
            
            // Execute hot-reload on main thread to ensure proper context
            mainHandler.post {
                try {
                    // Call onHotReload to clean up existing hooks
                    Log.d(TAG, "Calling onHotReload() for module: $moduleId")
                    module.onHotReload()
                    
                    // Reload the DEX
                    Log.d(TAG, "Starting DEX reload for module: $moduleId")
                    val success = dexReloader.reloadDex(moduleId, dexFile)
                    
                    if (success) {
                        Log.i(TAG, "Hot-reload completed successfully for module: $moduleId")
                        onHotReloadSuccess(moduleId)
                    } else {
                        Log.w(TAG, "Hot-reload completed with warnings for module: $moduleId")
                        onHotReloadPartialSuccess(moduleId)
                    }
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Hot-reload failed for module: $moduleId", e)
                    onHotReloadFailure(moduleId, e)
                } finally {
                    // Notify FeatureManager after hot-reload attempt
                    FeatureManager.onAfterHotReload(moduleId)
                    
                    // Clean up temporary file
                    try {
                        if (dexFile.exists()) {
                            dexFile.delete()
                            Log.d(TAG, "Cleaned up temporary DEX file")
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to clean up temporary DEX file", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling hot-reload for module: $moduleId", e)
            FeatureManager.onAfterHotReload(moduleId) // Ensure cleanup
        }
    }
    
    /**
     * Called when hot-reload succeeds.
     */
    private fun onHotReloadSuccess(moduleId: String) {
        Log.i(TAG, "Hot-reload success callback for module: $moduleId")
        // Future: could emit events, update UI, etc.
    }
    
    /**
     * Called when hot-reload partially succeeds.
     */
    private fun onHotReloadPartialSuccess(moduleId: String) {
        Log.w(TAG, "Hot-reload partial success callback for module: $moduleId")
        // Future: could show warnings, suggest full restart, etc.
    }
    
    /**
     * Called when hot-reload fails.
     */
    private fun onHotReloadFailure(moduleId: String, error: Throwable) {
        Log.e(TAG, "Hot-reload failure callback for module: $moduleId", error)
        // Future: could show error notifications, suggest troubleshooting steps, etc.
    }
    
    /**
     * Gets information about registered modules.
     */
    fun getRegisteredModules(): Map<String, String> {
        return registeredModules.mapValues { (_, module) ->
            module.javaClass.name
        }
    }
    
    /**
     * Gets hot-reload statistics.
     */
    fun getStatistics(): Map<String, Any> {
        return mapOf(
            "isSupported" to isSupported(),
            "isMonitoring" to isMonitoring,
            "registeredModulesCount" to registeredModules.size,
            "registeredModules" to getRegisteredModules().keys.toList(),
            "dexReloaderStats" to dexReloader.getStatistics()
        )
    }
    
    /**
     * Sets a custom development server address.
     */
    fun setDevelopmentServer(host: String, port: Int = 5795) {
        client.setDevelopmentServer(host, port)
        Log.i(TAG, "Development server configured: $host:$port")
    }
    
    companion object {
        @Volatile
        private var instance: HotReloadManager? = null
        
        /**
         * Gets the singleton instance of the HotReloadManager.
         */
        fun getInstance(context: Context): HotReloadManager {
            return instance ?: synchronized(this) {
                instance ?: HotReloadManager(context.applicationContext).also { instance = it }
            }
        }
        
        /**
         * Checks if hot-reload is supported on this device.
         */
        fun isSupported(): Boolean {
            return DexPatcher.isSupported()
        }
        
        /**
         * Logs a message with the specified level using the unified tag.
         */
        private fun log(level: LogLevel, message: String) {
            val priority = when (level) {
                LogLevel.VERBOSE -> Log.VERBOSE
                LogLevel.DEBUG -> Log.DEBUG
                LogLevel.INFO -> Log.INFO
                LogLevel.WARN -> Log.WARN
                LogLevel.ERROR -> Log.ERROR
            }
            Log.println(priority, "LSPK-HotReload", message)
        }
    }
} 