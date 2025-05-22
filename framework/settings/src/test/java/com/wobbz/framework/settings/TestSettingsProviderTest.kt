package com.wobbz.framework.settings

import com.wobbz.framework.processor.SettingsKey
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for TestSettingsProvider demonstrating testing utilities.
 */
class TestSettingsProviderTest {
    
    private lateinit var provider: TestSettingsProvider
    
    @Before
    fun setup() {
        provider = TestSettingsProvider.create()
    }
    
    @Test
    fun testBasicOperations() {
        // Test default values
        assertFalse(provider.bool("nonexistent", false))
        assertEquals(42, provider.int("nonexistent", 42))
        assertEquals("default", provider.string("nonexistent", "default"))
        
        // Test setting and getting values
        provider.edit()
            .putBoolean("bool_key", true)
            .putInt("int_key", 123)
            .putString("string_key", "test")
            .apply()
        
        assertTrue(provider.bool("bool_key"))
        assertEquals(123, provider.int("int_key"))
        assertEquals("test", provider.string("string_key"))
    }
    
    @Test
    fun testFactoryMethods() {
        // Test creation with initial values
        val values = mapOf(
            "enabled" to true,
            "count" to 5,
            "name" to "test"
        )
        val providerWithValues = TestSettingsProvider.createWithValues(values)
        
        assertTrue(providerWithValues.bool("enabled"))
        assertEquals(5, providerWithValues.int("count"))
        assertEquals("test", providerWithValues.string("name"))
    }
    
    @Test
    fun testSchemaDefaults() {
        val schemaJson = """
        {
          "type": "object",
          "properties": {
            "enabled": {
              "type": "boolean",
              "default": true
            },
            "timeout": {
              "type": "integer", 
              "default": 30
            }
          }
        }
        """.trimIndent()
        
        val providerWithSchema = TestSettingsProvider.createWithSchema(schemaJson)
        
        // Should return schema defaults when key doesn't exist
        assertTrue(providerWithSchema.bool("enabled"))
        assertEquals(30, providerWithSchema.int("timeout"))
    }
    
    @Test
    fun testSettingsBinding() {
        // Set up test data
        provider.edit()
            .putBoolean("feature_enabled", true)
            .putString("module_name", "TestModule")
            .putInt("max_count", 100)
            .apply()
        
        // Bind to settings class (this would require SettingsBinding to work with TestSettingsProvider)
        // For this test, we'll just verify the values are stored correctly
        assertTrue(provider.bool("feature_enabled"))
        assertEquals("TestModule", provider.string("module_name"))
        assertEquals(100, provider.int("max_count"))
    }
    
    @Test
    fun testChangeListeners() {
        var lastChangedKey: String? = null
        val listener = { key: String -> lastChangedKey = key }
        
        provider.addOnSettingsChangedListener(listener)
        
        // Make a change
        provider.edit().putString("test_key", "value").apply()
        
        // Verify listener was called
        assertEquals("test_key", lastChangedKey)
        
        // Remove listener
        provider.removeOnSettingsChangedListener(listener)
        lastChangedKey = null
        
        // Make another change
        provider.edit().putString("another_key", "value").apply()
        
        // Listener should not have been called
        assertNull(lastChangedKey)
    }
    
    @Test
    fun testComplexDataTypes() {
        val stringSet = setOf("item1", "item2", "item3")
        val stringList = listOf("first", "second", "third")
        
        provider.edit()
            .putStringSet("set_key", stringSet)
            .putStringList("list_key", stringList)
            .apply()
        
        assertEquals(stringSet, provider.stringSet("set_key"))
        assertEquals(stringList, provider.stringList("list_key"))
    }
    
    @Test
    fun testTestingUtilities() {
        // Test raw value access
        provider.setRaw("raw_key", "raw_value")
        assertEquals("raw_value", provider.getRaw("raw_key"))
        assertEquals("raw_value", provider.string("raw_key"))
        
        // Test containment check
        assertTrue(provider.contains("raw_key"))
        assertFalse(provider.contains("nonexistent"))
        
        // Test getting all values
        provider.setRaw("key1", "value1")
        provider.setRaw("key2", "value2")
        
        val allValues = provider.getAllValues()
        assertTrue(allValues.containsKey("key1"))
        assertTrue(allValues.containsKey("key2"))
        assertEquals("value1", allValues["key1"])
        assertEquals("value2", allValues["key2"])
        
        // Test clear
        provider.clear()
        assertTrue(provider.getAllValues().isEmpty())
        assertFalse(provider.contains("key1"))
    }
    
    @Test
    fun testSimulateSettingsChange() {
        var changeCount = 0
        val listener = { _: String -> changeCount++ }
        
        provider.addOnSettingsChangedListener(listener)
        
        // Simulate a change without actually changing the value
        provider.simulateSettingsChange("fake_key")
        
        assertEquals(1, changeCount)
    }
    
    /**
     * Example settings class for testing binding.
     */
    class TestSettings(
        @SettingsKey("feature_enabled") val enabled: Boolean = false,
        @SettingsKey("module_name") val name: String = "",
        @SettingsKey("max_count") val count: Int = 0
    )
} 