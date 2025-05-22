package com.wobbz.framework.settings

/**
 * Interface for modifying settings values.
 */
interface SettingsEditor {
    fun putBoolean(key: String, value: Boolean): SettingsEditor
    fun putInt(key: String, value: Int): SettingsEditor
    fun putLong(key: String, value: Long): SettingsEditor
    fun putFloat(key: String, value: Float): SettingsEditor
    fun putString(key: String, value: String): SettingsEditor
    fun putStringSet(key: String, value: Set<String>): SettingsEditor
    fun remove(key: String): SettingsEditor
    fun apply()
    fun commit(): Boolean
}

/**
 * Implementation of SettingsEditor.
 */
internal class SettingsEditorImpl(
    private val storage: SettingsStorage,
    private val notifyChange: (String) -> Unit
) : SettingsEditor {
    private val modifications = mutableMapOf<String, Any?>()
    
    override fun putBoolean(key: String, value: Boolean): SettingsEditor {
        modifications[key] = value.toString()
        return this
    }
    
    override fun putInt(key: String, value: Int): SettingsEditor {
        modifications[key] = value.toString()
        return this
    }
    
    override fun putLong(key: String, value: Long): SettingsEditor {
        modifications[key] = value.toString()
        return this
    }
    
    override fun putFloat(key: String, value: Float): SettingsEditor {
        modifications[key] = value.toString()
        return this
    }
    
    override fun putString(key: String, value: String): SettingsEditor {
        modifications[key] = value
        return this
    }
    
    override fun putStringSet(key: String, value: Set<String>): SettingsEditor {
        // Store as JSON array format
        val jsonArray = value.joinToString(",", "[", "]") { "\"$it\"" }
        modifications[key] = jsonArray
        return this
    }
    
    override fun remove(key: String): SettingsEditor {
        modifications[key] = null
        return this
    }
    
    override fun apply() {
        // Apply changes asynchronously
        Thread {
            commitInternal()
        }.start()
    }
    
    override fun commit(): Boolean {
        return commitInternal()
    }
    
    private fun commitInternal(): Boolean {
        val result = storage.setAll(modifications)
        
        // Notify listeners of all changes
        modifications.keys.forEach { key ->
            notifyChange(key)
        }
        
        // Clear pending modifications
        modifications.clear()
        
        return result
    }
} 