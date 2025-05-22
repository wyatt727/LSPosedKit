package com.wobbz.framework.settings

import android.content.Context
import android.content.SharedPreferences
import java.util.concurrent.ConcurrentHashMap

/**
 * Storage backend for settings.
 */
internal interface SettingsStorage {
    fun get(key: String): String?
    fun getAll(): Map<String, String>
    fun set(key: String, value: String?): Boolean
    fun setAll(values: Map<String, Any?>): Boolean
    fun contains(key: String): Boolean
    fun remove(key: String): Boolean
    fun clear(): Boolean
    
    companion object {
        /**
         * Creates a storage implementation based on context.
         */
        fun create(context: Context): SettingsStorage {
            val packageName = context.packageName
            val prefsName = "$packageName.settings"
            
            return try {
                // Try to use SharedPreferences
                val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                SharedPreferencesStorage(prefs)
            } catch (e: Exception) {
                // Fall back to in-memory storage for module contexts
                android.util.Log.w("SettingsStorage", "Failed to create SharedPreferences, using in-memory storage", e)
                InMemoryStorage()
            }
        }
        
        /**
         * Creates an in-memory storage for testing.
         */
        fun createInMemory(): SettingsStorage {
            return InMemoryStorage()
        }
    }
}

/**
 * SharedPreferences-based storage implementation.
 */
internal class SharedPreferencesStorage(
    private val prefs: SharedPreferences
) : SettingsStorage {
    override fun get(key: String): String? {
        return if (prefs.contains(key)) prefs.getString(key, null) else null
    }
    
    override fun getAll(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        for ((key, value) in prefs.all) {
            if (value != null) {
                result[key] = value.toString()
            }
        }
        return result
    }
    
    override fun set(key: String, value: String?): Boolean {
        val editor = prefs.edit()
        if (value == null) {
            editor.remove(key)
        } else {
            editor.putString(key, value)
        }
        return editor.commit()
    }
    
    override fun setAll(values: Map<String, Any?>): Boolean {
        val editor = prefs.edit()
        for ((key, value) in values) {
            if (value == null) {
                editor.remove(key)
            } else {
                editor.putString(key, value.toString())
            }
        }
        return editor.commit()
    }
    
    override fun contains(key: String): Boolean {
        return prefs.contains(key)
    }
    
    override fun remove(key: String): Boolean {
        return prefs.edit().remove(key).commit()
    }
    
    override fun clear(): Boolean {
        return prefs.edit().clear().commit()
    }
}

/**
 * In-memory storage implementation for testing or fallback.
 */
internal class InMemoryStorage : SettingsStorage {
    private val map = ConcurrentHashMap<String, String>()
    
    override fun get(key: String): String? {
        return map[key]
    }
    
    override fun getAll(): Map<String, String> {
        return map.toMap()
    }
    
    override fun set(key: String, value: String?): Boolean {
        if (value == null) {
            map.remove(key)
        } else {
            map[key] = value
        }
        return true
    }
    
    override fun setAll(values: Map<String, Any?>): Boolean {
        for ((key, value) in values) {
            if (value == null) {
                map.remove(key)
            } else {
                map[key] = value.toString()
            }
        }
        return true
    }
    
    override fun contains(key: String): Boolean {
        return map.containsKey(key)
    }
    
    override fun remove(key: String): Boolean {
        map.remove(key)
        return true
    }
    
    override fun clear(): Boolean {
        map.clear()
        return true
    }
} 