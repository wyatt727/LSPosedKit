package com.wobbz.framework.settings

import androidx.annotation.VisibleForTesting
import java.util.concurrent.ConcurrentHashMap

/**
 * Test implementation of SettingsProvider for unit testing.
 * Provides an in-memory storage backend with testing utilities.
 */
class TestSettingsProvider private constructor() {
    private val storage = ConcurrentHashMap<String, String>()
    private val listeners = mutableListOf<(String) -> Unit>()
    private var schema: SettingsSchema? = null
    
    companion object {
        /**
         * Creates a test settings provider with empty storage.
         */
        fun create(): TestSettingsProvider {
            return TestSettingsProvider()
        }
        
        /**
         * Creates a test settings provider with initial values.
         */
        fun createWithValues(values: Map<String, Any>): TestSettingsProvider {
            val provider = TestSettingsProvider()
            values.forEach { (key, value) ->
                provider.setRaw(key, value.toString())
            }
            return provider
        }
        
        /**
         * Creates a test settings provider with a predefined schema.
         */
        fun createWithSchema(schemaJson: String): TestSettingsProvider {
            val provider = TestSettingsProvider()
            provider.schema = try {
                SettingsSchema.parse(schemaJson)
            } catch (e: Exception) {
                null
            }
            return provider
        }
    }
    
    // Type-safe getters (mirroring SettingsProvider interface)
    fun bool(key: String, defaultValue: Boolean = false): Boolean {
        val value = storage[key] ?: return getDefaultFromSchema(key, defaultValue)
        return try { 
            value.toBoolean() 
        } catch (e: Exception) { 
            defaultValue 
        }
    }
    
    fun int(key: String, defaultValue: Int = 0): Int {
        val value = storage[key] ?: return getDefaultFromSchema(key, defaultValue)
        return try { 
            value.toInt() 
        } catch (e: Exception) { 
            defaultValue 
        }
    }
    
    fun long(key: String, defaultValue: Long = 0L): Long {
        val value = storage[key] ?: return getDefaultFromSchema(key, defaultValue)
        return try { 
            value.toLong() 
        } catch (e: Exception) { 
            defaultValue 
        }
    }
    
    fun float(key: String, defaultValue: Float = 0f): Float {
        val value = storage[key] ?: return getDefaultFromSchema(key, defaultValue)
        return try { 
            value.toFloat() 
        } catch (e: Exception) { 
            defaultValue 
        }
    }
    
    fun string(key: String, defaultValue: String = ""): String {
        return storage[key] ?: getDefaultFromSchema(key, defaultValue)
    }
    
    fun stringSet(key: String, defaultValue: Set<String> = emptySet()): Set<String> {
        val value = storage[key] ?: return getDefaultFromSchema(key, defaultValue)
        return try {
            // Assume comma-separated values for testing
            value.split(",").filter { it.isNotBlank() }.toSet()
        } catch (e: Exception) {
            defaultValue
        }
    }
    
    fun stringList(key: String, defaultValue: List<String> = emptyList()): List<String> {
        val value = storage[key] ?: return getDefaultFromSchema(key, defaultValue)
        return try {
            // Assume comma-separated values for testing
            value.split(",").filter { it.isNotBlank() }
        } catch (e: Exception) {
            defaultValue
        }
    }
    
    // Settings modification
    fun edit(): TestSettingsEditor {
        return TestSettingsEditor(this)
    }
    
    // Settings binding
    fun <T : Any> bind(clazz: Class<T>): T {
        return SettingsBinding.bind(clazz, this as SettingsProvider)
    }
    
    // Change listeners
    fun addOnSettingsChangedListener(listener: (String) -> Unit): Any {
        listeners.add(listener)
        return listener
    }
    
    fun removeOnSettingsChangedListener(token: Any) {
        listeners.remove(token)
    }
    
    // Testing utilities
    
    /**
     * Sets a raw value directly in storage (for test setup).
     */
    @VisibleForTesting
    fun setRaw(key: String, value: String?) {
        if (value != null) {
            storage[key] = value
        } else {
            storage.remove(key)
        }
    }
    
    /**
     * Gets a raw value from storage (for test verification).
     */
    @VisibleForTesting
    fun getRaw(key: String): String? {
        return storage[key]
    }
    
    /**
     * Gets all stored values (for test verification).
     */
    @VisibleForTesting
    fun getAllValues(): Map<String, String> {
        return storage.toMap()
    }
    
    /**
     * Clears all stored values (for test cleanup).
     */
    @VisibleForTesting
    fun clear() {
        storage.clear()
    }
    
    /**
     * Checks if a key exists in storage.
     */
    @VisibleForTesting
    fun contains(key: String): Boolean {
        return storage.containsKey(key)
    }
    
    /**
     * Simulates a settings change and notifies listeners (for testing).
     */
    @VisibleForTesting
    fun simulateSettingsChange(key: String) {
        listeners.forEach { listener ->
            try {
                listener(key)
            } catch (e: Exception) {
                // Ignore exceptions in test listeners
            }
        }
    }
    
    /**
     * Sets the schema for testing default value resolution.
     */
    @VisibleForTesting
    fun setSchema(schemaJson: String?) {
        schema = if (schemaJson != null) {
            try {
                SettingsSchema.parse(schemaJson)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    private fun <T> getDefaultFromSchema(key: String, defaultValue: T): T {
        return schema?.getDefaultValue(key) as? T ?: defaultValue
    }
    
    /**
     * Test implementation of SettingsEditor.
     */
    inner class TestSettingsEditor internal constructor(
        private val provider: TestSettingsProvider
    ) {
        private val changes = mutableMapOf<String, String?>()
        
        fun putBoolean(key: String, value: Boolean): TestSettingsEditor {
            changes[key] = value.toString()
            return this
        }
        
        fun putInt(key: String, value: Int): TestSettingsEditor {
            changes[key] = value.toString()
            return this
        }
        
        fun putLong(key: String, value: Long): TestSettingsEditor {
            changes[key] = value.toString()
            return this
        }
        
        fun putFloat(key: String, value: Float): TestSettingsEditor {
            changes[key] = value.toString()
            return this
        }
        
        fun putString(key: String, value: String?): TestSettingsEditor {
            changes[key] = value
            return this
        }
        
        fun putStringSet(key: String, value: Set<String>?): TestSettingsEditor {
            changes[key] = value?.joinToString(",")
            return this
        }
        
        fun putStringList(key: String, value: List<String>?): TestSettingsEditor {
            changes[key] = value?.joinToString(",")
            return this
        }
        
        fun remove(key: String): TestSettingsEditor {
            changes[key] = null
            return this
        }
        
        fun clear(): TestSettingsEditor {
            provider.storage.clear()
            return this
        }
        
        fun commit(): Boolean {
            return apply()
        }
        
        fun apply(): Boolean {
            changes.forEach { (key, value) ->
                if (value != null) {
                    provider.storage[key] = value
                } else {
                    provider.storage.remove(key)
                }
                // Notify listeners
                provider.listeners.forEach { listener ->
                    try {
                        listener(key)
                    } catch (e: Exception) {
                        // Ignore exceptions in test listeners
                    }
                }
            }
            changes.clear()
            return true
        }
    }
} 