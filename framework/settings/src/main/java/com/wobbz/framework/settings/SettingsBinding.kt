package com.wobbz.framework.settings

import com.wobbz.framework.processor.SettingsKey

/**
 * Utility for binding settings classes using annotations.
 */
internal object SettingsBinding {
    /**
     * Binds a settings class to a SettingsProvider.
     */
    fun <T : Any> bind(clazz: Class<T>, provider: SettingsProvider): T {
        // Create instance using no-arg constructor
        val instance = try {
            clazz.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            throw IllegalArgumentException("Settings class ${clazz.name} must have a no-arg constructor", e)
        }
        
        // Find all fields with @SettingsKey annotation
        for (field in clazz.declaredFields) {
            val annotation = field.getAnnotation(SettingsKey::class.java) ?: continue
            val key = annotation.value
            
            // Make field accessible
            field.isAccessible = true
            
            // Get default value from field
            val defaultValue = field.get(instance)
            
            // Get value from settings based on field type
            val value = when (field.type) {
                Boolean::class.java, java.lang.Boolean::class.java -> 
                    provider.bool(key, defaultValue as? Boolean ?: false)
                Int::class.java, java.lang.Integer::class.java -> 
                    provider.int(key, defaultValue as? Int ?: 0)
                Long::class.java, java.lang.Long::class.java -> 
                    provider.long(key, defaultValue as? Long ?: 0L)
                Float::class.java, java.lang.Float::class.java -> 
                    provider.float(key, defaultValue as? Float ?: 0f)
                String::class.java -> 
                    provider.string(key, defaultValue as? String ?: "")
                Set::class.java -> {
                    @Suppress("UNCHECKED_CAST")
                    provider.stringSet(key, defaultValue as? Set<String> ?: emptySet())
                }
                List::class.java -> {
                    @Suppress("UNCHECKED_CAST")
                    provider.stringList(key, defaultValue as? List<String> ?: emptyList())
                }
                else -> defaultValue
            }
            
            // Set the value on the instance
            field.set(instance, value)
        }
        
        return instance
    }
} 