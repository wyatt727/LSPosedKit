package com.wobbz.framework.core

import org.junit.Assert.*
import org.junit.Test
import java.lang.reflect.Modifier

/**
 * Unit tests for ReflectionTestUtil demonstrating reflection testing capabilities.
 */
class ReflectionTestUtilTest {
    
    @Test
    fun testCreateMockMethod() {
        val method = ReflectionTestUtil.createMockMethod(
            name = "methodWithReturn",
            returnType = String::class.java
        )
        
        assertNotNull(method)
        assertEquals("methodWithReturn", method.name)
        assertEquals(String::class.java, method.returnType)
    }
    
    @Test
    fun testCreateMockField() {
        val field = ReflectionTestUtil.createMockField(
            name = "stringField",
            type = String::class.java
        )
        
        assertNotNull(field)
        assertEquals("stringField", field.name)
        assertEquals(String::class.java, field.type)
    }
    
    @Test
    fun testGetMethodSafely() {
        val testClass = ReflectionTestUtil.TestReflectionClass::class.java
        
        // Test existing method
        val existingMethod = ReflectionTestUtil.getMethodSafely(
            testClass, 
            "methodWithReturn"
        )
        assertNotNull(existingMethod)
        assertEquals("methodWithReturn", existingMethod!!.name)
        
        // Test non-existing method
        val nonExistingMethod = ReflectionTestUtil.getMethodSafely(
            testClass,
            "nonExistentMethod"
        )
        assertNull(nonExistingMethod)
    }
    
    @Test
    fun testGetFieldSafely() {
        val testClass = ReflectionTestUtil.TestReflectionClass::class.java
        
        // Test existing field
        val existingField = ReflectionTestUtil.getFieldSafely(testClass, "stringField")
        assertNotNull(existingField)
        assertEquals("stringField", existingField!!.name)
        
        // Test non-existing field
        val nonExistingField = ReflectionTestUtil.getFieldSafely(testClass, "nonExistentField")
        assertNull(nonExistingField)
    }
    
    @Test
    fun testInvokeMethodSafely() {
        val testInstance = ReflectionTestUtil.TestReflectionClass()
        val method = ReflectionTestUtil.getMethodSafely(
            ReflectionTestUtil.TestReflectionClass::class.java,
            "methodWithReturn"
        )!!
        
        val result = ReflectionTestUtil.invokeMethodSafely(method, testInstance)
        assertEquals("result", result)
    }
    
    @Test
    fun testSetAndGetFieldSafely() {
        val testInstance = ReflectionTestUtil.TestReflectionClass()
        val field = ReflectionTestUtil.getFieldSafely(
            ReflectionTestUtil.TestReflectionClass::class.java,
            "stringField"
        )!!
        
        // Test getting original value
        val originalValue = ReflectionTestUtil.getFieldSafely(field, testInstance)
        assertEquals("test", originalValue)
        
        // Test setting new value
        ReflectionTestUtil.setFieldSafely(field, testInstance, "modified")
        val modifiedValue = ReflectionTestUtil.getFieldSafely(field, testInstance)
        assertEquals("modified", modifiedValue)
    }
    
    @Test
    fun testValidateMethodForHooking() {
        val testClass = ReflectionTestUtil.TestReflectionClass::class.java
        
        // Test normal method (should be valid)
        val normalMethod = testClass.getDeclaredMethod("simpleMethod")
        val normalResult = ReflectionTestUtil.validateMethodForHooking(normalMethod)
        assertTrue("Normal method should be valid for hooking", normalResult.isValid)
        
        // Test static method
        val staticMethod = testClass.getDeclaredMethod("staticMethod")
        val staticResult = ReflectionTestUtil.validateMethodForHooking(staticMethod)
        assertTrue("Static method should be valid for hooking", staticResult.isValid)
    }
    
    @Test
    fun testValidateFieldForHooking() {
        val testClass = ReflectionTestUtil.TestReflectionClass::class.java
        
        // Test mutable field (should be valid)
        val mutableField = testClass.getDeclaredField("stringField")
        val mutableResult = ReflectionTestUtil.validateFieldForHooking(mutableField)
        assertTrue("Mutable field should be valid for hooking", mutableResult.isValid)
        
        // Test read-only field (should have issues)
        val readOnlyField = testClass.getDeclaredField("readOnlyField")
        val readOnlyResult = ReflectionTestUtil.validateFieldForHooking(readOnlyField)
        assertFalse("Read-only field should have validation issues", readOnlyResult.isValid)
        assertTrue("Should mention final field", readOnlyResult.issues.any { it.contains("final") })
    }
    
    @Test
    fun testFindMethodsMatching() {
        val testClass = ReflectionTestUtil.TestReflectionClass::class.java
        
        // Find methods by name pattern
        val methodsWithMethod = ReflectionTestUtil.findMethodsMatching(
            testClass,
            namePattern = Regex(".*method.*", RegexOption.IGNORE_CASE)
        )
        assertTrue("Should find methods containing 'method'", methodsWithMethod.isNotEmpty())
        assertTrue("Should include simpleMethod", 
            methodsWithMethod.any { it.name == "simpleMethod" })
        
        // Find methods by return type
        val stringMethods = ReflectionTestUtil.findMethodsMatching(
            testClass,
            returnType = String::class.java
        )
        assertTrue("Should find methods returning String", stringMethods.isNotEmpty())
        assertTrue("Should include methodWithReturn",
            stringMethods.any { it.name == "methodWithReturn" })
        
        // Find methods by parameter count
        val noParamMethods = ReflectionTestUtil.findMethodsMatching(
            testClass,
            parameterCount = 0
        )
        assertTrue("Should find methods with no parameters", noParamMethods.isNotEmpty())
        assertTrue("Should include simpleMethod",
            noParamMethods.any { it.name == "simpleMethod" })
    }
    
    @Test
    fun testFindFieldsMatching() {
        val testClass = ReflectionTestUtil.TestReflectionClass::class.java
        
        // Find fields by name pattern
        val fieldFields = ReflectionTestUtil.findFieldsMatching(
            testClass,
            namePattern = Regex(".*Field", RegexOption.IGNORE_CASE)
        )
        assertTrue("Should find fields ending with 'Field'", fieldFields.isNotEmpty())
        
        // Find fields by type
        val stringFields = ReflectionTestUtil.findFieldsMatching(
            testClass,
            type = String::class.java
        )
        assertTrue("Should find String fields", stringFields.isNotEmpty())
        assertTrue("Should include stringField",
            stringFields.any { it.name == "stringField" })
    }
    
    @Test
    fun testCreateTestInstance() {
        // Test no-arg constructor
        val instance = ReflectionTestUtil.createTestInstance(
            ReflectionTestUtil.TestReflectionClass::class.java
        )
        assertNotNull(instance)
        assertTrue(instance is ReflectionTestUtil.TestReflectionClass)
        
        // Test with String class (has constructors with parameters)
        val stringInstance = ReflectionTestUtil.createTestInstance(String::class.java)
        assertNotNull(stringInstance)
        assertTrue(stringInstance is String)
    }
    
    @Test
    fun testGetClassHierarchy() {
        val hierarchy = ReflectionTestUtil.getClassHierarchy(
            ReflectionTestUtil.TestReflectionClass::class.java
        )
        
        // Should include the class itself
        assertTrue("Should include the class itself", 
            hierarchy.contains(ReflectionTestUtil.TestReflectionClass::class.java))
        
        // Should include Object
        assertTrue("Should include Object", hierarchy.contains(Object::class.java))
        
        // Should have multiple entries
        assertTrue("Should have multiple entries in hierarchy", hierarchy.size > 1)
    }
    
    @Test
    fun testValidationResult() {
        val validResult = ReflectionTestUtil.ValidationResult(true, emptyList())
        assertTrue(validResult.isValid)
        assertEquals("Valid", validResult.toString())
        
        val invalidResult = ReflectionTestUtil.ValidationResult(
            false, 
            listOf("Issue 1", "Issue 2")
        )
        assertFalse(invalidResult.isValid)
        assertEquals("Invalid: Issue 1, Issue 2", invalidResult.toString())
    }
    
    @Test
    fun testReflectionTestException() {
        val cause = RuntimeException("Original error")
        val exception = ReflectionTestUtil.ReflectionTestException("Test error", cause)
        
        assertEquals("Test error", exception.message)
        assertEquals(cause, exception.cause)
    }
    
    @Test
    fun testWorkingWithPrivateMembers() {
        val testInstance = ReflectionTestUtil.TestReflectionClass()
        
        // Test accessing private field
        val privateField = ReflectionTestUtil.getFieldSafely(
            ReflectionTestUtil.TestReflectionClass::class.java,
            "privateField"
        )
        assertNotNull(privateField)
        
        val privateValue = ReflectionTestUtil.getFieldSafely(privateField!!, testInstance)
        assertEquals("private", privateValue)
        
        // Test modifying private field
        ReflectionTestUtil.setFieldSafely(privateField, testInstance, "modified private")
        val modifiedPrivateValue = ReflectionTestUtil.getFieldSafely(privateField, testInstance)
        assertEquals("modified private", modifiedPrivateValue)
        
        // Test calling private method
        val privateMethod = ReflectionTestUtil.getMethodSafely(
            ReflectionTestUtil.TestReflectionClass::class.java,
            "privateMethod"
        )
        assertNotNull(privateMethod)
        
        val result = ReflectionTestUtil.invokeMethodSafely(privateMethod!!, testInstance)
        assertEquals("private", result)
    }
} 