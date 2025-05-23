package com.wobbz.framework.core

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Method

/**
 * Unit tests for XposedInterface implementations.
 * Demonstrates how to test hooking behavior using the mock infrastructure.
 */
class XposedInterfaceTest {
    
    private lateinit var mockXposed: MockXposedInterface
    
    @Before
    fun setup() {
        mockXposed = MockXposedInterface()
    }
    
    @Test
    fun testLogging() {
        // Test info logging
        mockXposed.log(LogLevel.INFO, "Test message")
        
        val logs = mockXposed.getLogs()
        assertEquals(1, logs.size)
        assertEquals(LogLevel.INFO, logs[0].level)
        assertEquals("Test message", logs[0].message)
        assertNull(logs[0].throwable)
    }
    
    @Test
    fun testErrorLogging() {
        val testException = RuntimeException("Test error")
        mockXposed.logError("Error occurred", testException)
        
        val errorLogs = mockXposed.getLogs(LogLevel.ERROR)
        assertEquals(1, errorLogs.size)
        assertEquals("Error occurred", errorLogs[0].message)
        assertEquals(testException, errorLogs[0].throwable)
    }
    
    @Test
    fun testClassLoading() {
        // Test loading an existing class
        val stringClass = mockXposed.loadClass("java.lang.String")
        assertEquals(String::class.java, stringClass)
        
        // Test registering a custom class
        mockXposed.registerClass("com.test.CustomClass", HookTestUtil.TestClass::class.java)
        val customClass = mockXposed.loadClass("com.test.CustomClass")
        assertEquals(HookTestUtil.TestClass::class.java, customClass)
    }
    
    @Test
    fun testClassLoadingNotFound() {
        try {
            mockXposed.loadClass("com.nonexistent.Class")
            fail("Expected ClassNotFoundException")
        } catch (e: ClassNotFoundException) {
            assertTrue(e.message!!.contains("Mock class not found"))
        }
    }
    
    @Test
    fun testMethodHooking() {
        val method = HookTestUtil.createTestMethod("methodWithReturn", String::class.java)
        
        // Hook the method
        val unhooker = mockXposed.hook(method, TestHooker::class.java)
        
        // Verify the hook was registered
        assertTrue(mockXposed.isMethodHooked(method))
        
        // Verify unhooker works
        assertNotNull(unhooker)
        assertTrue(unhooker.isHooked())
        
        val hookedMethods = mockXposed.getHookedMethods()
        assertEquals(1, hookedMethods.size)
        assertEquals(method, hookedMethods[0].method)
        assertEquals(TestHooker::class.java, hookedMethods[0].hookerClass)
    }
    
    @Test
    fun testMethodHookingByName() {
        val testClass = HookTestUtil.TestClass::class.java
        
        // Hook method by name
        val unhooker = mockXposed.hook(
            testClass,
            "methodWithParams",
            arrayOf(String::class.java, Int::class.java),
            TestHooker::class.java
        )
        
        assertNotNull(unhooker)
        
        val hookedMethods = mockXposed.getHookedMethods()
        assertEquals(1, hookedMethods.size)
        assertEquals("methodWithParams", hookedMethods[0].method.name)
    }
    
    @Test
    fun testHookExecution() {
        val method = HookTestUtil.createTestMethod("methodWithReturn", String::class.java)
        mockXposed.hook(method, TestHooker::class.java)
        
        // Simulate method call
        val result = mockXposed.simulateMethodCall(method, null, emptyArray())
        
        // TestHooker modifies the result to "modified"
        assertEquals("modified", result)
        
        // Check that the hooker logged its activity
        val logs = mockXposed.getLogs(LogLevel.INFO)
        assertTrue(logs.any { it.message.contains("TestHooker executed") })
    }
    
    @Test
    fun testHookWithException() {
        val method = HookTestUtil.createTestMethod("methodThatThrows", String::class.java)
        mockXposed.hook(method, ExceptionThrowingHooker::class.java)
        
        try {
            mockXposed.simulateMethodCall(method, null, emptyArray())
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals("Hooked exception", e.message)
        }
    }
    
    @Test
    fun testUnhooking() {
        val method = HookTestUtil.createTestMethod("methodWithReturn", String::class.java)
        val unhooker = mockXposed.hook(method, TestHooker::class.java)
        
        // Verify hook is active
        assertTrue(mockXposed.isMethodHooked(method))
        assertTrue(unhooker.isHooked())
        
        // Unhook
        unhooker.unhook()
        assertFalse(unhooker.isHooked())
    }
    
    @Test
    fun testMultipleHooks() {
        val method1 = HookTestUtil.createTestMethod("methodWithReturn", String::class.java)
        val method2 = HookTestUtil.createTestMethod("simpleMethod")
        
        mockXposed.hook(method1, TestHooker::class.java)
        mockXposed.hook(method2, TestHooker::class.java)
        
        val hookedMethods = mockXposed.getHookedMethods()
        assertEquals(2, hookedMethods.size)
        
        assertTrue(mockXposed.isMethodHooked(method1))
        assertTrue(mockXposed.isMethodHooked(method2))
    }
    
    @Test
    fun testClearLogs() {
        mockXposed.log(LogLevel.INFO, "Message 1")
        mockXposed.log(LogLevel.ERROR, "Message 2")
        
        assertEquals(2, mockXposed.getLogs().size)
        
        mockXposed.clearLogs()
        assertEquals(0, mockXposed.getLogs().size)
    }
    
    /**
     * Test hooker that modifies method results.
     */
    class TestHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            // For testing, we can access the mock xposed through the MockHookParam
            if (param is MockHookParam) {
                param.xposed.log(LogLevel.INFO, "TestHooker executed - beforeHook")
            }
        }
        
        override fun afterHook(param: HookParam) {
            // For testing, we can access the mock xposed through the MockHookParam
            if (param is MockHookParam) {
                param.xposed.log(LogLevel.INFO, "TestHooker executed - afterHook")
            }
            // Modify the result
            param.setResult("modified")
        }
    }
    
    /**
     * Test hooker that throws exceptions.
     */
    class ExceptionThrowingHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            param.setThrowable(RuntimeException("Hooked exception"))
        }
    }
} 