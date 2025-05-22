package com.wobbz.module.intentinterceptor.monitor

import android.content.Intent
import android.os.Bundle
import com.wobbz.framework.core.LogLevel
import com.wobbz.framework.core.XposedInterface
import com.wobbz.framework.settings.SettingsProvider

/**
 * Monitors and logs Intent activities throughout the system.
 */
class IntentMonitor(
    private val xposed: XposedInterface,
    private val settings: SettingsProvider
) {
    
    private var isStarted = false
    private val intentCount = mutableMapOf<String, Long>()
    
    fun start() {
        isStarted = true
        xposed.log(LogLevel.INFO, "IntentMonitor started")
    }
    
    fun stop() {
        isStarted = false
        xposed.log(LogLevel.INFO, "IntentMonitor stopped")
    }
    
    /**
     * Called when an Activity.startActivity() is invoked
     */
    fun onActivityStart(intent: Intent, callerClass: String) {
        if (!isStarted || !shouldLogIntent("activity_start")) return
        
        val action = intent.action ?: "No Action"
        val component = intent.component?.className ?: "No Component"
        
        incrementIntentCount("activity_start")
        
        xposed.log(LogLevel.INFO, buildString {
            append("🚀 START_ACTIVITY")
            append(" | Action: $action")
            append(" | Component: $component")
            append(" | Caller: $callerClass")
            intent.categories?.let { categories ->
                if (categories.isNotEmpty()) {
                    append(" | Categories: ${categories.joinToString(", ")}")
                }
            }
            intent.dataString?.let { data ->
                append(" | Data: $data")
            }
        })
        
        // Log extras if enabled
        if (settings.bool("log_intent_extras", false)) {
            logIntentExtras(intent, "START_ACTIVITY")
        }
    }
    
    /**
     * Called when an Activity.startActivityForResult() is invoked
     */
    fun onActivityStartForResult(intent: Intent, requestCode: Int, callerClass: String) {
        if (!isStarted || !shouldLogIntent("activity_start_for_result")) return
        
        val action = intent.action ?: "No Action"
        val component = intent.component?.className ?: "No Component"
        
        incrementIntentCount("activity_start_for_result")
        
        xposed.log(LogLevel.INFO, buildString {
            append("🎯 START_ACTIVITY_FOR_RESULT")
            append(" | Action: $action")
            append(" | Component: $component")
            append(" | RequestCode: $requestCode")
            append(" | Caller: $callerClass")
        })
        
        if (settings.bool("log_intent_extras", false)) {
            logIntentExtras(intent, "START_ACTIVITY_FOR_RESULT")
        }
    }
    
    /**
     * Called when a Context.sendBroadcast() is invoked
     */
    fun onBroadcast(intent: Intent, callerClass: String) {
        if (!isStarted || !shouldLogIntent("broadcast")) return
        
        val action = intent.action ?: "No Action"
        
        incrementIntentCount("broadcast")
        
        xposed.log(LogLevel.INFO, buildString {
            append("📡 SEND_BROADCAST")
            append(" | Action: $action")
            append(" | Caller: $callerClass")
            intent.dataString?.let { data ->
                append(" | Data: $data")
            }
        })
        
        if (settings.bool("log_intent_extras", false)) {
            logIntentExtras(intent, "SEND_BROADCAST")
        }
    }
    
    /**
     * Called when a Context.startService() is invoked
     */
    fun onServiceStart(intent: Intent, callerClass: String) {
        if (!isStarted || !shouldLogIntent("service_start")) return
        
        val action = intent.action ?: "No Action"
        val component = intent.component?.className ?: "No Component"
        
        incrementIntentCount("service_start")
        
        xposed.log(LogLevel.INFO, buildString {
            append("🔧 START_SERVICE")
            append(" | Action: $action")
            append(" | Component: $component")
            append(" | Caller: $callerClass")
        })
        
        if (settings.bool("log_intent_extras", false)) {
            logIntentExtras(intent, "START_SERVICE")
        }
    }
    
    /**
     * Called when a Context.bindService() is invoked
     */
    fun onServiceBind(intent: Intent, callerClass: String) {
        if (!isStarted || !shouldLogIntent("service_bind")) return
        
        val action = intent.action ?: "No Action"
        val component = intent.component?.className ?: "No Component"
        
        incrementIntentCount("service_bind")
        
        xposed.log(LogLevel.INFO, buildString {
            append("🔗 BIND_SERVICE")
            append(" | Action: $action")
            append(" | Component: $component")
            append(" | Caller: $callerClass")
        })
        
        if (settings.bool("log_intent_extras", false)) {
            logIntentExtras(intent, "BIND_SERVICE")
        }
    }
    
    /**
     * Called when a Service.onStartCommand() is invoked
     */
    fun onServiceCommand(intent: Intent, flags: Int, startId: Int, serviceClass: String) {
        if (!isStarted || !shouldLogIntent("service_command")) return
        
        val action = intent.action ?: "No Action"
        
        incrementIntentCount("service_command")
        
        xposed.log(LogLevel.INFO, buildString {
            append("⚙️ SERVICE_COMMAND")
            append(" | Action: $action")
            append(" | Service: $serviceClass")
            append(" | Flags: $flags")
            append(" | StartId: $startId")
        })
        
        if (settings.bool("log_intent_extras", false)) {
            logIntentExtras(intent, "SERVICE_COMMAND")
        }
    }
    
    /**
     * Called when a BroadcastReceiver.onReceive() is invoked
     */
    fun onBroadcastReceive(intent: Intent, receiverClass: String) {
        if (!isStarted || !shouldLogIntent("broadcast_receive")) return
        
        val action = intent.action ?: "No Action"
        
        incrementIntentCount("broadcast_receive")
        
        xposed.log(LogLevel.INFO, buildString {
            append("📻 BROADCAST_RECEIVE")
            append(" | Action: $action")
            append(" | Receiver: $receiverClass")
            intent.dataString?.let { data ->
                append(" | Data: $data")
            }
        })
        
        if (settings.bool("log_intent_extras", false)) {
            logIntentExtras(intent, "BROADCAST_RECEIVE")
        }
    }
    
    /**
     * Called when an Intent is modified
     */
    fun onIntentModified(intent: Intent, methodName: String) {
        if (!isStarted || !shouldLogIntent("intent_modification")) return
        
        xposed.log(LogLevel.DEBUG, buildString {
            append("✏️ INTENT_MODIFIED")
            append(" | Method: $methodName")
            append(" | Action: ${intent.action ?: "No Action"}")
        })
    }
    
    /**
     * Logs detailed Intent extras if enabled
     */
    private fun logIntentExtras(intent: Intent, operation: String) {
        try {
            val extras = intent.extras
            if (extras != null && !extras.isEmpty) {
                val extrasInfo = buildString {
                    append("📋 $operation EXTRAS:")
                    for (key in extras.keySet()) {
                        val value = extras.get(key)
                        append(" | $key: ${formatExtraValue(value)}")
                    }
                }
                xposed.log(LogLevel.DEBUG, extrasInfo)
            }
        } catch (e: Exception) {
            xposed.log(LogLevel.WARN, "Failed to log intent extras: ${e.message}")
        }
    }
    
    /**
     * Formats extra values for logging, truncating long strings
     */
    private fun formatExtraValue(value: Any?): String {
        return when (value) {
            null -> "null"
            is String -> {
                if (value.length > 100) {
                    "${value.take(97)}..."
                } else {
                    "\"$value\""
                }
            }
            is Bundle -> {
                val size = try { value.size() } catch (e: Exception) { "?" }
                "Bundle[$size items]"
            }
            is Array<*> -> "Array[${value.size} items]"
            is Collection<*> -> "${value.javaClass.simpleName}[${value.size} items]"
            else -> value.toString()
        }
    }
    
    /**
     * Checks if a specific type of intent should be logged based on settings
     */
    private fun shouldLogIntent(intentType: String): Boolean {
        val enabledTypes = settings.stringSet("enabled_intent_types", 
            setOf("activity_start", "broadcast", "service_start")
        )
        return enabledTypes.contains(intentType)
    }
    
    /**
     * Increments the count for a specific intent type
     */
    private fun incrementIntentCount(intentType: String) {
        intentCount[intentType] = (intentCount[intentType] ?: 0) + 1
        
        // Log statistics periodically
        val count = intentCount[intentType] ?: 0
        if (count % 100 == 0L && settings.bool("log_statistics", false)) {
            xposed.log(LogLevel.INFO, "📊 Intent Statistics: $intentType = $count")
        }
    }
    
    /**
     * Gets current intent statistics
     */
    fun getStatistics(): Map<String, Long> {
        return intentCount.toMap()
    }
    
    /**
     * Resets intent statistics
     */
    fun resetStatistics() {
        intentCount.clear()
        xposed.log(LogLevel.INFO, "📊 Intent statistics reset")
    }
} 