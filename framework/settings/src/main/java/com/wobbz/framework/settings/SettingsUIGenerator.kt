package com.wobbz.framework.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.preference.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * Generates preference screens from settings.json schema.
 * This class provides automatic UI generation based on the settings schema,
 * while still allowing modules to create custom settings activities if needed.
 */
object SettingsUIGenerator {
    
    private const val TAG = "SettingsUIGenerator"
    
    /**
     * Generates preferences from a schema and adds them to the preference screen.
     * @param context The context for creating preferences
     * @param settingsProvider The settings provider for reading/writing values
     * @param preferenceScreen The preference screen to add preferences to
     */
    fun generatePreferences(
        context: Context,
        settingsProvider: SettingsProvider,
        preferenceScreen: PreferenceScreen
    ) {
        try {
            // Load schema from assets
            val schema = context.assets.open("settings.json").use { input ->
                SettingsSchema.parse(input.reader().readText())
            }
            
            // Generate preferences from schema
            for ((key, property) in schema.properties) {
                val preference = when (property.type) {
                    SchemaPropertyType.BOOLEAN -> createSwitchPreference(context, key, property, settingsProvider)
                    SchemaPropertyType.INTEGER -> createSeekBarPreference(context, key, property, settingsProvider)
                    SchemaPropertyType.FLOAT -> createEditTextPreference(context, key, property, settingsProvider, isNumeric = true)
                    SchemaPropertyType.STRING -> {
                        // Check if this is an enum property with predefined values
                        if (hasEnumValues(property)) {
                            createListPreference(context, key, property, settingsProvider)
                        } else {
                            createEditTextPreference(context, key, property, settingsProvider)
                        }
                    }
                    SchemaPropertyType.ARRAY -> createMultiSelectListPreference(context, key, property, settingsProvider)
                    SchemaPropertyType.OBJECT -> createPreferenceCategory(context, key, property)
                }
                
                preference?.let {
                    preferenceScreen.addPreference(it)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate preferences", e)
        }
    }
    
    /**
     * Creates a SwitchPreference from a schema property.
     */
    private fun createSwitchPreference(
        context: Context,
        key: String,
        property: SchemaProperty,
        provider: SettingsProvider
    ): SwitchPreference {
        return SwitchPreference(context).apply {
            this.key = key
            title = property.title
            summary = property.description
            isChecked = provider.bool(key, property.default as? Boolean ?: false)
            
            setOnPreferenceChangeListener { _, newValue ->
                provider.edit().putBoolean(key, newValue as Boolean).apply()
                true
            }
        }
    }
    
    /**
     * Creates a SeekBarPreference for integer values.
     */
    private fun createSeekBarPreference(
        context: Context,
        key: String,
        property: SchemaProperty,
        provider: SettingsProvider
    ): SeekBarPreference {
        return SeekBarPreference(context).apply {
            this.key = key
            title = property.title
            summary = property.description
            
            // Extract range from schema if available
            val min = getPropertyMin(property)
            val max = getPropertyMax(property)
            
            this.min = min
            this.max = max
            value = provider.int(key, property.default as? Int ?: min)
            
            setOnPreferenceChangeListener { _, newValue ->
                provider.edit().putInt(key, newValue as Int).apply()
                true
            }
        }
    }
    
    /**
     * Creates an EditTextPreference for string or numeric input.
     */
    private fun createEditTextPreference(
        context: Context,
        key: String,
        property: SchemaProperty,
        provider: SettingsProvider,
        isNumeric: Boolean = false
    ): EditTextPreference {
        return EditTextPreference(context).apply {
            this.key = key
            title = property.title
            summary = property.description
            text = if (isNumeric) {
                provider.float(key, property.default as? Float ?: 0f).toString()
            } else {
                provider.string(key, property.default as? String ?: "")
            }
            
            setOnPreferenceChangeListener { _, newValue ->
                if (isNumeric) {
                    try {
                        val floatValue = (newValue as String).toFloat()
                        provider.edit().putFloat(key, floatValue).apply()
                    } catch (e: NumberFormatException) {
                        return@setOnPreferenceChangeListener false
                    }
                } else {
                    provider.edit().putString(key, newValue as String).apply()
                }
                true
            }
        }
    }
    
    /**
     * Creates a ListPreference for enum values.
     */
    private fun createListPreference(
        context: Context,
        key: String,
        property: SchemaProperty,
        provider: SettingsProvider
    ): ListPreference {
        return ListPreference(context).apply {
            this.key = key
            title = property.title
            summary = property.description
            
            val enumValues = getEnumValues(property)
            entries = enumValues.toTypedArray()
            entryValues = enumValues.toTypedArray()
            
            value = provider.string(key, property.default as? String ?: enumValues.firstOrNull() ?: "")
            
            setOnPreferenceChangeListener { _, newValue ->
                provider.edit().putString(key, newValue as String).apply()
                true
            }
        }
    }
    
    /**
     * Creates a MultiSelectListPreference for array values.
     */
    private fun createMultiSelectListPreference(
        context: Context,
        key: String,
        property: SchemaProperty,
        provider: SettingsProvider
    ): MultiSelectListPreference {
        return MultiSelectListPreference(context).apply {
            this.key = key
            title = property.title
            summary = property.description
            
            val possibleValues = getArrayValues(property)
            entries = possibleValues.toTypedArray()
            entryValues = possibleValues.toTypedArray()
            
            val defaultValues = getDefaultArrayValues(property)
            values = provider.stringSet(key, defaultValues)
            
            setOnPreferenceChangeListener { _, newValue ->
                @Suppress("UNCHECKED_CAST")
                provider.edit().putStringSet(key, newValue as Set<String>).apply()
                true
            }
        }
    }
    
    /**
     * Creates a PreferenceCategory for object properties.
     */
    private fun createPreferenceCategory(
        context: Context,
        key: String,
        property: SchemaProperty
    ): PreferenceCategory {
        return PreferenceCategory(context).apply {
            this.key = key
            title = property.title
            summary = property.description
        }
    }
    
    /**
     * Checks if a property has enum values defined.
     */
    private fun hasEnumValues(property: SchemaProperty): Boolean {
        // This would need to be extended to support enum definitions in the schema
        // For now, we'll assume simple string properties without enums
        return false
    }
    
    /**
     * Gets enum values from property schema.
     */
    private fun getEnumValues(property: SchemaProperty): List<String> {
        // This would parse enum values from the schema
        // For now, return empty list
        return emptyList()
    }
    
    /**
     * Gets array values from property schema.
     */
    private fun getArrayValues(property: SchemaProperty): List<String> {
        // This would parse possible array values from the schema
        // For now, return empty list
        return emptyList()
    }
    
    /**
     * Gets default array values from property schema.
     */
    private fun getDefaultArrayValues(property: SchemaProperty): Set<String> {
        return when (val default = property.default) {
            is JSONArray -> {
                val result = mutableSetOf<String>()
                for (i in 0 until default.length()) {
                    result.add(default.getString(i))
                }
                result
            }
            is List<*> -> {
                @Suppress("UNCHECKED_CAST")
                (default as? List<String>)?.toSet() ?: emptySet()
            }
            else -> emptySet()
        }
    }
    
    /**
     * Gets minimum value for numeric properties.
     */
    private fun getPropertyMin(property: SchemaProperty): Int {
        // This would parse min value from schema
        // For now, return default minimum
        return 0
    }
    
    /**
     * Gets maximum value for numeric properties.
     */
    private fun getPropertyMax(property: SchemaProperty): Int {
        // This would parse max value from schema
        // For now, return default maximum
        return 100
    }
    
    /**
     * Creates a simple activity that hosts the generated preferences.
     * This is a helper method that modules can use if they don't need complex settings UI.
     */
    fun createSettingsActivity(
        context: Context,
        settingsProvider: SettingsProvider
    ): androidx.fragment.app.Fragment {
        return object : PreferenceFragmentCompat() {
            override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
                val context = preferenceManager.context
                val screen = preferenceManager.createPreferenceScreen(context)
                preferenceScreen = screen
                
                generatePreferences(context, settingsProvider, screen)
            }
        }
    }
} 