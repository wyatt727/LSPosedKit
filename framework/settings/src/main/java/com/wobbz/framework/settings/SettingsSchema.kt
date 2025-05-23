package com.wobbz.framework.settings

import org.json.JSONObject

/**
 * Represents a parsed settings.json schema.
 */
internal class SettingsSchema(
    val properties: Map<String, SchemaProperty>
) {
    /**
     * Gets the default value for a setting.
     */
    fun getDefaultValue(key: String): Any? {
        return properties[key]?.default
    }
    
    /**
     * Gets the type of a property.
     */
    fun getPropertyType(key: String): SchemaPropertyType? {
        return properties[key]?.type
    }
    
    companion object {
        /**
         * Parses a settings schema from JSON.
         */
        fun parse(json: String): SettingsSchema {
            val jsonObject = JSONObject(json)
            val propertiesObj = jsonObject.optJSONObject("properties") ?: return SettingsSchema(emptyMap())
            
            val properties = mutableMapOf<String, SchemaProperty>()
            for (key in propertiesObj.keys()) {
                val propObj = propertiesObj.getJSONObject(key)
                val type = when (propObj.optString("type")) {
                    "boolean" -> SchemaPropertyType.BOOLEAN
                    "integer" -> SchemaPropertyType.INTEGER
                    "number" -> SchemaPropertyType.FLOAT
                    "string" -> SchemaPropertyType.STRING
                    "array" -> SchemaPropertyType.ARRAY
                    "object" -> SchemaPropertyType.OBJECT
                    else -> SchemaPropertyType.STRING
                }
                
                val default = propObj.opt("default")
                val title = propObj.optString("title", key)
                val description = propObj.optString("description", "")
                val minimum = propObj.opt("minimum")
                val maximum = propObj.opt("maximum")
                
                // Parse enum values if present
                val enumValues = propObj.optJSONArray("enum")?.let { enumArray ->
                    val values = mutableListOf<String>()
                    for (i in 0 until enumArray.length()) {
                        values.add(enumArray.getString(i))
                    }
                    values
                }
                
                properties[key] = SchemaProperty(
                    type = type,
                    default = default,
                    title = title,
                    description = description,
                    minimum = minimum,
                    maximum = maximum,
                    enumValues = enumValues
                )
            }
            
            return SettingsSchema(properties)
        }
    }
}

/**
 * Represents a property in the settings schema.
 */
internal data class SchemaProperty(
    val type: SchemaPropertyType,
    val default: Any?,
    val title: String,
    val description: String,
    val minimum: Any? = null,
    val maximum: Any? = null,
    val enumValues: List<String>? = null
)

/**
 * Enum of supported property types.
 */
internal enum class SchemaPropertyType {
    BOOLEAN,
    INTEGER,
    FLOAT,
    STRING,
    ARRAY,
    OBJECT
} 