package com.wobbz.framework.processor.validation

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Validates settings.json schema files against the LSPosedKit schema format.
 * This validator is used during compilation by the annotation processor.
 */
object SchemaValidator {
    
    /**
     * Validation result containing the outcome and any errors found.
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String> = emptyList(),
        val warnings: List<String> = emptyList()
    )
    
    /**
     * Validates a settings.json schema string.
     * @param schemaJson The JSON schema string to validate
     * @return ValidationResult with outcome and any errors/warnings
     */
    fun validateSchema(schemaJson: String): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        try {
            val jsonObject = JSONObject(schemaJson)
            
            // Check required root properties
            validateRootProperties(jsonObject, errors)
            
            // Validate properties section
            if (jsonObject.has("properties")) {
                val properties = jsonObject.getJSONObject("properties")
                validateProperties(properties, errors, warnings)
            }
            
            // Check for deprecated or unknown properties
            validateAdditionalProperties(jsonObject, warnings)
            
        } catch (e: JSONException) {
            errors.add("Invalid JSON format: ${e.message}")
        } catch (e: Exception) {
            errors.add("Validation error: ${e.message}")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }
    
    /**
     * Validates the root level properties of the schema.
     */
    private fun validateRootProperties(jsonObject: JSONObject, errors: MutableList<String>) {
        // Check required type property
        if (!jsonObject.has("type")) {
            errors.add("Missing required property: 'type'")
        } else {
            val type = jsonObject.optString("type")
            if (type != "object") {
                errors.add("Root type must be 'object', found: '$type'")
            }
        }
        
        // Check for properties object
        if (!jsonObject.has("properties")) {
            errors.add("Missing required property: 'properties'")
        }
        
        // Check schema reference
        if (!jsonObject.has("\$schema")) {
            errors.add("Missing '\$schema' property - recommended to include JSON Schema reference")
        }
    }
    
    /**
     * Validates individual properties in the properties section.
     */
    private fun validateProperties(properties: JSONObject, errors: MutableList<String>, warnings: MutableList<String>) {
        for (key in properties.keys()) {
            try {
                val property = properties.getJSONObject(key)
                validateProperty(key, property, errors, warnings)
            } catch (e: Exception) {
                errors.add("Property '$key' is not a valid object: ${e.message}")
            }
        }
    }
    
    /**
     * Validates a single property definition.
     */
    private fun validateProperty(key: String, property: JSONObject, errors: MutableList<String>, warnings: MutableList<String>) {
        // Check required type
        if (!property.has("type")) {
            errors.add("Property '$key' is missing required 'type' field")
            return
        }
        
        val type = property.getString("type")
        val validTypes = listOf("boolean", "integer", "number", "string", "array", "object")
        
        if (type !in validTypes) {
            errors.add("Property '$key' has invalid type: '$type'. Valid types are: ${validTypes.joinToString()}")
        }
        
        // Validate default value against type
        if (property.has("default")) {
            validateDefaultValue(key, type, property.get("default"), errors, warnings)
        }
        
        // Check for recommended properties
        if (!property.has("title")) {
            warnings.add("Property '$key' is missing recommended 'title' field")
        }
        
        if (!property.has("description")) {
            warnings.add("Property '$key' is missing recommended 'description' field")
        }
        
        // Type-specific validation
        when (type) {
            "integer", "number" -> validateNumericProperty(key, property, errors, warnings)
            "string" -> validateStringProperty(key, property, errors, warnings)
            "array" -> validateArrayProperty(key, property, errors, warnings)
            "object" -> validateObjectProperty(key, property, errors, warnings)
        }
    }
    
    /**
     * Validates default value matches the specified type.
     */
    private fun validateDefaultValue(key: String, type: String, defaultValue: Any, errors: MutableList<String>, warnings: MutableList<String>) {
        val isValidType = when (type) {
            "boolean" -> defaultValue is Boolean
            "integer" -> defaultValue is Int || defaultValue is Long
            "number" -> defaultValue is Number
            "string" -> defaultValue is String
            "array" -> defaultValue is JSONArray
            "object" -> defaultValue is JSONObject
            else -> false
        }
        
        if (!isValidType) {
            errors.add("Property '$key' has default value that doesn't match type '$type'")
        }
    }
    
    /**
     * Validates numeric property constraints.
     */
    private fun validateNumericProperty(key: String, property: JSONObject, errors: MutableList<String>, warnings: MutableList<String>) {
        if (property.has("minimum") && property.has("maximum")) {
            try {
                val min = property.getDouble("minimum")
                val max = property.getDouble("maximum")
                if (min >= max) {
                    errors.add("Property '$key' has minimum ($min) >= maximum ($max)")
                }
            } catch (e: Exception) {
                errors.add("Property '$key' has invalid minimum/maximum values")
            }
        }
        
        // Check if default is within range
        if (property.has("default") && property.has("minimum") && property.has("maximum")) {
            try {
                val default = property.getDouble("default")
                val min = property.getDouble("minimum")
                val max = property.getDouble("maximum")
                if (default < min || default > max) {
                    warnings.add("Property '$key' default value ($default) is outside valid range [$min, $max]")
                }
            } catch (e: Exception) {
                // Already handled in validateDefaultValue
            }
        }
    }
    
    /**
     * Validates string property constraints.
     */
    private fun validateStringProperty(key: String, property: JSONObject, errors: MutableList<String>, warnings: MutableList<String>) {
        // Check for enum values
        if (property.has("enum")) {
            try {
                val enumArray = property.getJSONArray("enum")
                if (enumArray.length() == 0) {
                    warnings.add("Property '$key' has empty enum array")
                }
                
                // Validate default is in enum
                if (property.has("default")) {
                    val defaultValue = property.getString("default")
                    var foundInEnum = false
                    for (i in 0 until enumArray.length()) {
                        if (enumArray.getString(i) == defaultValue) {
                            foundInEnum = true
                            break
                        }
                    }
                    if (!foundInEnum) {
                        errors.add("Property '$key' default value '$defaultValue' is not in enum list")
                    }
                }
            } catch (e: Exception) {
                errors.add("Property '$key' has invalid enum definition")
            }
        }
        
        // Check string length constraints
        if (property.has("minLength") || property.has("maxLength")) {
            try {
                val minLength = property.optInt("minLength", 0)
                val maxLength = property.optInt("maxLength", Int.MAX_VALUE)
                if (minLength > maxLength) {
                    errors.add("Property '$key' has minLength ($minLength) > maxLength ($maxLength)")
                }
            } catch (e: Exception) {
                errors.add("Property '$key' has invalid length constraints")
            }
        }
    }
    
    /**
     * Validates array property constraints.
     */
    private fun validateArrayProperty(key: String, property: JSONObject, errors: MutableList<String>, warnings: MutableList<String>) {
        // Check items definition
        if (!property.has("items")) {
            warnings.add("Property '$key' is an array but missing 'items' definition")
        } else {
            try {
                val items = property.getJSONObject("items")
                if (!items.has("type")) {
                    errors.add("Property '$key' items definition is missing 'type'")
                }
            } catch (e: Exception) {
                errors.add("Property '$key' has invalid items definition")
            }
        }
        
        // Check array length constraints
        if (property.has("minItems") || property.has("maxItems")) {
            try {
                val minItems = property.optInt("minItems", 0)
                val maxItems = property.optInt("maxItems", Int.MAX_VALUE)
                if (minItems > maxItems) {
                    errors.add("Property '$key' has minItems ($minItems) > maxItems ($maxItems)")
                }
            } catch (e: Exception) {
                errors.add("Property '$key' has invalid item count constraints")
            }
        }
    }
    
    /**
     * Validates object property constraints.
     */
    private fun validateObjectProperty(key: String, property: JSONObject, errors: MutableList<String>, warnings: MutableList<String>) {
        // Check if properties are defined for nested objects
        if (!property.has("properties")) {
            warnings.add("Property '$key' is an object but has no 'properties' definition")
        } else {
            try {
                val nestedProperties = property.getJSONObject("properties")
                validateProperties(nestedProperties, errors, warnings)
            } catch (e: Exception) {
                errors.add("Property '$key' has invalid nested properties definition")
            }
        }
    }
    
    /**
     * Validates additional properties and checks for deprecated features.
     */
    private fun validateAdditionalProperties(jsonObject: JSONObject, warnings: MutableList<String>) {
        val knownProperties = setOf(
            "\$schema", "type", "title", "description", "properties", 
            "required", "additionalProperties", "definitions"
        )
        
        for (key in jsonObject.keys()) {
            if (key !in knownProperties) {
                warnings.add("Unknown root property: '$key' - may not be supported")
            }
        }
    }
} 