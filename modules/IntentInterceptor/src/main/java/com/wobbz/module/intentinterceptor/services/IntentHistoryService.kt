package com.wobbz.module.intentinterceptor.services

import android.content.Context
import android.content.Intent
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types
import com.wobbz.framework.core.Releasable
import com.wobbz.framework.service.ReloadAware
import com.wobbz.framework.settings.SettingsProvider
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Service interface for accessing Intent history
 */
interface IIntentHistoryService {
    fun getRecentIntents(limit: Int = 100): List<IntentRecord>
    fun getIntentsByType(type: String): List<IntentRecord>
    fun getIntentsByPackage(packageName: String): List<IntentRecord>
    fun searchIntents(query: String): List<IntentRecord>
    fun clearHistory()
    fun getStatistics(): IntentStatistics
}

/**
 * Implementation of Intent history service with persistence and Service Bus integration
 */
class IntentHistoryService(
    private val context: Context,
    private val settings: SettingsProvider
) : IIntentHistoryService, Releasable, ReloadAware {
    
    private val intentHistory = ConcurrentLinkedQueue<IntentRecord>()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val type = Types.newParameterizedType(List::class.java, IntentRecord::class.java)
    private val adapter = moshi.adapter<List<IntentRecord>>(type)
    private val historyFile: File
    private val maxHistorySize: Int
    
    private val statistics = mutableMapOf<String, Long>()
    
    init {
        historyFile = File(context.filesDir, "intent_history.json")
        maxHistorySize = settings.int("max_history_size", 1000)
        loadHistory()
    }
    
    /**
     * Records a new Intent operation
     */
    fun recordIntent(
        operation: String,
        intent: Intent,
        packageName: String?,
        requestCode: Int? = null
    ) {
        if (!settings.bool("enable_intent_history", true)) {
            return
        }
        
        val record = IntentRecord(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            operation = operation,
            action = intent.action,
            component = intent.component?.className,
            packageName = packageName ?: intent.component?.packageName,
            dataUri = intent.dataString,
            categories = intent.categories?.toList() ?: emptyList(),
            extras = extractExtras(intent),
            requestCode = requestCode
        )
        
        // Add to history
        intentHistory.offer(record)
        
        // Maintain size limit
        while (intentHistory.size > maxHistorySize) {
            intentHistory.poll()
        }
        
        // Update statistics
        updateStatistics(operation, packageName)
        
        // Periodic save
        if (intentHistory.size % 50 == 0) {
            saveHistory()
        }
    }
    
    override fun getRecentIntents(limit: Int): List<IntentRecord> {
        return intentHistory.toList().takeLast(limit.coerceAtMost(intentHistory.size))
    }
    
    override fun getIntentsByType(type: String): List<IntentRecord> {
        return intentHistory.filter { it.operation == type }
    }
    
    override fun getIntentsByPackage(packageName: String): List<IntentRecord> {
        return intentHistory.filter { it.packageName == packageName }
    }
    
    override fun searchIntents(query: String): List<IntentRecord> {
        val lowerQuery = query.lowercase()
        return intentHistory.filter { record ->
            record.action?.lowercase()?.contains(lowerQuery) == true ||
            record.component?.lowercase()?.contains(lowerQuery) == true ||
            record.packageName?.lowercase()?.contains(lowerQuery) == true ||
            record.dataUri?.lowercase()?.contains(lowerQuery) == true
        }
    }
    
    override fun clearHistory() {
        intentHistory.clear()
        statistics.clear()
        saveHistory()
        Log.i("IntentHistoryService", "Intent history cleared")
    }
    
    override fun getStatistics(): IntentStatistics {
        val totalIntents = intentHistory.size.toLong()
        val operationCounts = mutableMapOf<String, Long>()
        val packageCounts = mutableMapOf<String, Long>()
        val actionCounts = mutableMapOf<String, Long>()
        
        intentHistory.forEach { record ->
            operationCounts[record.operation] = (operationCounts[record.operation] ?: 0) + 1
            record.packageName?.let { pkg ->
                packageCounts[pkg] = (packageCounts[pkg] ?: 0) + 1
            }
            record.action?.let { action ->
                actionCounts[action] = (actionCounts[action] ?: 0) + 1
            }
        }
        
        return IntentStatistics(
            totalIntents = totalIntents,
            operationCounts = operationCounts,
            packageCounts = packageCounts,
            actionCounts = actionCounts,
            oldestRecord = intentHistory.minByOrNull { it.timestamp }?.timestamp,
            newestRecord = intentHistory.maxByOrNull { it.timestamp }?.timestamp
        )
    }
    
    /**
     * Flushes pending changes to storage
     */
    fun flush() {
        saveHistory()
    }
    
    /**
     * Loads Intent history from storage
     */
    private fun loadHistory() {
        try {
            if (historyFile.exists()) {
                val json = historyFile.readText()
                val loadedHistory = adapter.fromJson(json) ?: emptyList()
                intentHistory.clear()
                intentHistory.addAll(loadedHistory.takeLast(maxHistorySize))
                Log.i("IntentHistoryService", "Loaded ${intentHistory.size} intent records")
            }
        } catch (e: Exception) {
            Log.e("IntentHistoryService", "Error loading history: ${e.message}")
        }
    }
    
    /**
     * Saves Intent history to storage
     */
    private fun saveHistory() {
        try {
            val json = adapter.toJson(intentHistory.toList())
            historyFile.writeText(json)
        } catch (e: Exception) {
            Log.e("IntentHistoryService", "Error saving history: ${e.message}")
        }
    }
    
    /**
     * Extracts Intent extras in a safe way for serialization
     */
    private fun extractExtras(intent: Intent): Map<String, String> {
        val extras = mutableMapOf<String, String>()
        
        try {
            intent.extras?.let { bundle ->
                for (key in bundle.keySet()) {
                    try {
                        val value = bundle.get(key)
                        extras[key] = formatValueForSerialization(value)
                    } catch (e: Exception) {
                        extras[key] = "Error: ${e.message}"
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("IntentHistoryService", "Error extracting Intent extras: ${e.message}")
        }
        
        return extras
    }
    
    /**
     * Formats values for safe JSON serialization
     */
    private fun formatValueForSerialization(value: Any?): String {
        return when (value) {
            null -> "null"
            is String -> value
            is Number -> value.toString()
            is Boolean -> value.toString()
            is Array<*> -> "Array[${value.size}]"
            is Collection<*> -> "${value.javaClass.simpleName}[${value.size}]"
            else -> value.toString().take(200) // Limit length
        }
    }
    
    /**
     * Updates Intent statistics
     */
    private fun updateStatistics(operation: String, packageName: String?) {
        statistics[operation] = (statistics[operation] ?: 0) + 1
        packageName?.let { pkg ->
            statistics["package:$pkg"] = (statistics["package:$pkg"] ?: 0) + 1
        }
    }
    
    override fun release() {
        // Save any pending changes
        saveHistory()
        Log.i("IntentHistoryService", "IntentHistoryService released")
    }
    
    override fun onBeforeReload() {
        // Save current state before hot-reload
        saveHistory()
        Log.i("IntentHistoryService", "Saved intent history before reload")
    }
    
    override fun onAfterReload() {
        // Reload state after hot-reload
        loadHistory()
        Log.i("IntentHistoryService", "Reloaded intent history after reload")
    }
}

/**
 * Represents a recorded Intent operation
 */
data class IntentRecord(
    val id: String,
    val timestamp: Long,
    val operation: String,
    val action: String?,
    val component: String?,
    val packageName: String?,
    val dataUri: String?,
    val categories: List<String>,
    val extras: Map<String, String>,
    val requestCode: Int? = null
)

/**
 * Statistics about Intent usage
 */
data class IntentStatistics(
    val totalIntents: Long,
    val operationCounts: Map<String, Long>,
    val packageCounts: Map<String, Long>,
    val actionCounts: Map<String, Long>,
    val oldestRecord: Long?,
    val newestRecord: Long?
) 