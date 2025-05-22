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
                
                properties[key] = SchemaProperty(
                    type = type,
                    default = default,
                    title = title,
                    description = description
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
    val description: String
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