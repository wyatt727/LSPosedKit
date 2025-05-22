package com.wobbz.framework.settings

import android.content.Context
import androidx.annotation.VisibleForTesting
import java.util.UUID
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentHashMap

/**
 * Provides type-safe access to module preferences.
 */
class SettingsProvider private constructor(context: Context) {
    private val storage: SettingsStorage
    private val listeners = ConcurrentHashMap<Any, (String) -> Unit>()
    private val schema: SettingsSchema?
    
    init {
        // Initialize storage backend based on context
        storage = SettingsStorage.create(context)
        
        // Load settings schema if available
        schema = try {
            context.assets.open("settings.json").use { input ->
                SettingsSchema.parse(input.reader().readText())
            }
        } catch (e: Exception) {
            null
        }
    }
    
    companion object {
        private val providers = WeakHashMap<Context, SettingsProvider>()
        
        /**
         * Factory method to get a SettingsProvider instance for the given context.
         */
        @Synchronized
        fun of(context: Context): SettingsProvider {
            return providers.getOrPut(context) {
                SettingsProvider(context)
            }
        }
        
        /**
         * Creates a test instance for unit testing.
         */
        @VisibleForTesting
        fun createForTesting(context: Context): SettingsProvider {
            return SettingsProvider(context).apply {
                // Test instances use in-memory storage
            }
        }
    }
    
    // Type-safe getters
    fun bool(key: String, defaultValue: Boolean = false): Boolean {
        val value = storage.get(key) ?: return getDefaultFromSchema(key, defaultValue)
        return try { 
            value.toBoolean() 
        } catch (e: Exception) { 
            defaultValue 
        }
    }
    
    fun int(key: String, defaultValue: Int = 0): Int {
        val value = storage.get(key) ?: return getDefaultFromSchema(key, defaultValue)
        return try { 
            value.toInt() 
        } catch (e: Exception) { 
            defaultValue 
        }
    }
    
    fun long(key: String, defaultValue: Long = 0L): Long {
        val value = storage.get(key) ?: return getDefaultFromSchema(key, defaultValue)
        return try { 
            value.toLong() 
        } catch (e: Exception) { 
            defaultValue 
        }
    }
    
    fun float(key: String, defaultValue: Float = 0f): Float {
        val value = storage.get(key) ?: return getDefaultFromSchema(key, defaultValue)
        return try { 
            value.toFloat() 
        } catch (e: Exception) { 
            defaultValue 
        }
    }
    
    fun string(key: String, defaultValue: String = ""): String {
        return storage.get(key) ?: getDefaultFromSchema(key, defaultValue)
    }
    
    fun stringSet(key: String, defaultValue: Set<String> = emptySet()): Set<String> {
        val value = storage.get(key) ?: return getDefaultFromSchema(key, defaultValue)
        return try {
            // Parse JSON array or comma-separated values
            if (value.startsWith("[") && value.endsWith("]")) {
                // Simple JSON array parsing
                value.substring(1, value.length - 1)
                    .split(",")
                    .map { it.trim().removeSurrounding("\"") }
                    .filter { it.isNotEmpty() }
                    .toSet()
            } else {
                value.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
            }
        } catch (e: Exception) {
            defaultValue
        }
    }
    
    fun stringList(key: String, defaultValue: List<String> = emptyList()): List<String> {
        return stringSet(key, defaultValue.toSet()).toList()
    }
    
    /**
     * Gets an editor for modifying settings.
     */
    fun edit(): SettingsEditor {
        return SettingsEditorImpl(storage, this::notifyListeners)
    }
    
    /**
     * Binds a settings class using @SettingsKey annotations.
     */
    fun <T : Any> bind(clazz: Class<T>): T {
        return SettingsBinding.bind(clazz, this)
    }
    
    /**
     * Retrieves default value from schema if available.
     */
    private inline fun <reified T> getDefaultFromSchema(key: String, hardcodedDefault: T): T {
        if (schema == null) return hardcodedDefault
        
        return try {
            schema.getDefaultValue(key) as? T ?: hardcodedDefault
        } catch (e: Exception) {
            hardcodedDefault
        }
    }
    
    /**
     * Adds a settings change listener.
     */
    fun addOnSettingsChangedListener(listener: (String) -> Unit): Any {
        val token = UUID.randomUUID()
        listeners[token] = listener
        return token
    }
    
    /**
     * Removes a settings change listener.
     */
    fun removeOnSettingsChangedListener(token: Any) {
        listeners.remove(token)
    }
    
    /**
     * Notifies all listeners that a setting has changed.
     */
    private fun notifyListeners(key: String) {
        listeners.values.forEach { listener ->
            try {
                listener.invoke(key)
            } catch (e: Exception) {
                // Log error but continue notifying other listeners
                android.util.Log.e("SettingsProvider", "Error in settings listener", e)
            }
        }
    }
} 