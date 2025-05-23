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
            Log.d(TAG, "Loading settings schema from assets...")
            // Load schema from assets
            val schema = context.assets.open("settings.json").use { input ->
                SettingsSchema.parse(input.reader().readText())
            }
            Log.d(TAG, "Schema loaded successfully with ${schema.properties.size} properties")
            
            // Generate preferences from schema
            for ((key, property) in schema.properties) {
                Log.d(TAG, "Creating preference for key: $key, type: ${property.type}")
                
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
                    Log.d(TAG, "Added preference for key: $key")
                } ?: Log.w(TAG, "Failed to create preference for key: $key")
            }
            Log.d(TAG, "All preferences generated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate preferences", e)
            throw e // Re-throw to let caller handle it
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
            val defaultValue = when (val def = property.default) {
                is Boolean -> def
                else -> false
            }
            isChecked = provider.bool(key, defaultValue)
            
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
            val defaultValue = when (val def = property.default) {
                is Number -> def.toInt()
                else -> min
            }
            value = provider.int(key, defaultValue)
            
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
                val defaultValue = when (val def = property.default) {
                    is Number -> def.toFloat()
                    else -> 0f
                }
                provider.float(key, defaultValue).toString()
            } else {
                provider.string(key, property.default as? String ?: "")
            }
            
            setOnPreferenceChangeListener { _, newValue ->
                if (isNumeric) {
                    try {
                        val floatValue = (newValue as String).toFloat()
                        // Validate range if specified
                        val min = getPropertyMinFloat(property)
                        val max = getPropertyMaxFloat(property)
                        if ((min != null && floatValue < min) || (max != null && floatValue > max)) {
                            return@setOnPreferenceChangeListener false
                        }
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
        return property.enumValues?.isNotEmpty() == true
    }
    
    /**
     * Gets enum values from property schema.
     */
    private fun getEnumValues(property: SchemaProperty): List<String> {
        return property.enumValues ?: emptyList()
    }
    
    /**
     * Gets array values from property schema.
     */
    private fun getArrayValues(property: SchemaProperty): List<String> {
        // For target_packages, provide common options including the important "*" wildcard
        return listOf(
            "*",  // All packages - most important option
            "com.android.settings",
            "com.android.systemui", 
            "com.google.android.apps.messaging",
            "com.whatsapp",
            "com.facebook.katana",
            "com.instagram.android",
            "com.twitter.android",
            "com.spotify.music",
            "com.netflix.mediaclient",
            "com.google.android.youtube",
            "Custom package..."
        )
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
        return getPropertyMinFloat(property)?.toInt() ?: 0
    }
    
    /**
     * Gets maximum value for numeric properties.
     */
    private fun getPropertyMax(property: SchemaProperty): Int {
        return getPropertyMaxFloat(property)?.toInt() ?: 100
    }
    
    /**
     * Gets minimum value for float properties from schema.
     */
    private fun getPropertyMinFloat(property: SchemaProperty): Float? {
        return when (val min = property.minimum) {
            is Number -> min.toFloat()
            is String -> min.toFloatOrNull()
            else -> null
        }
    }
    
    /**
     * Gets maximum value for float properties from schema.
     */
    private fun getPropertyMaxFloat(property: SchemaProperty): Float? {
        return when (val max = property.maximum) {
            is Number -> max.toFloat()
            is String -> max.toFloatOrNull()
            else -> null
        }
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