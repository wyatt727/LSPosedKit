package com.wobbz.framework.core

import java.lang.reflect.Method

/**
 * Utility class for testing hook behavior.
 * Provides common patterns and helpers for writing hook tests.
 */
object HookTestUtil {
    
    /**
     * Creates a test method for hooking.
     */
    fun createTestMethod(
        name: String = "testMethod", 
        returnType: Class<*> = Void.TYPE,
        vararg parameterTypes: Class<*>
    ): Method {
        // Create a simple test class with the requested method
        val testClass = TestClass::class.java
        return try {
            testClass.getDeclaredMethod(name, *parameterTypes)
        } catch (e: NoSuchMethodException) {
            // If method doesn't exist, return a generic one
            testClass.getDeclaredMethod("simpleMethod")
        }
    }
    
    /**
     * Tests a hooker implementation by simulating method calls.
     */
    fun testHooker(
        hooker: Hooker,
        method: Method = createTestMethod(),
        thisObject: Any? = null,
        args: Array<Any?> = emptyArray(),
        xposed: XposedInterface = MockXposedInterface()
    ): MockHookParam {
        val param = MockHookParam(xposed, method, thisObject, args)
        
        // Test beforeHook
        hooker.beforeHook(param)
        
        // If no result was set and no exception thrown, simulate original method
        if (!param.hasResult() && !param.hasThrowable()) {
            val defaultResult = getDefaultReturnValue(method.returnType)
            param.setResult(defaultResult)
        }
        
        // Test afterHook
        hooker.afterHook(param)
        
        return param
    }
    
    /**
     * Tests that a hooker properly modifies method behavior.
     */
    fun assertHookerModifiesResult(
        hooker: Hooker,
        expectedResult: Any?,
        method: Method = createTestMethod(),
        thisObject: Any? = null,
        args: Array<Any?> = emptyArray()
    ) {
        val param = testHooker(hooker, method, thisObject, args)
        val actualResult = param.getResult<Any>()
        
        if (actualResult != expectedResult) {
            throw AssertionError("Expected result: $expectedResult, but got: $actualResult")
        }
    }
    
    /**
     * Tests that a hooker throws an expected exception.
     */
    fun assertHookerThrowsException(
        hooker: Hooker,
        expectedExceptionType: Class<out Throwable>,
        method: Method = createTestMethod(),
        thisObject: Any? = null,
        args: Array<Any?> = emptyArray()
    ) {
        val param = testHooker(hooker, method, thisObject, args)
        val throwable = param.getThrowable()
        
        if (throwable == null) {
            throw AssertionError("Expected exception of type ${expectedExceptionType.name}, but no exception was thrown")
        }
        
        if (!expectedExceptionType.isAssignableFrom(throwable.javaClass)) {
            throw AssertionError("Expected exception of type ${expectedExceptionType.name}, but got ${throwable.javaClass.name}")
        }
    }
    
    /**
     * Tests hooker logging behavior.
     */
    fun assertHookerLogs(
        hooker: Hooker,
        expectedLogLevel: LogLevel,
        expectedMessagePattern: String? = null,
        method: Method = createTestMethod(),
        thisObject: Any? = null,
        args: Array<Any?> = emptyArray()
    ) {
        val mockXposed = MockXposedInterface()
        testHooker(hooker, method, thisObject, args, mockXposed)
        
        val logs = mockXposed.getLogs(expectedLogLevel)
        if (logs.isEmpty()) {
            throw AssertionError("Expected at least one log entry with level $expectedLogLevel, but found none")
        }
        
        if (expectedMessagePattern != null) {
            val matchingLogs = logs.filter { it.message.contains(expectedMessagePattern) }
            if (matchingLogs.isEmpty()) {
                throw AssertionError("Expected log message containing '$expectedMessagePattern', but found: ${logs.map { it.message }}")
            }
        }
    }
    
    /**
     * Creates a mock context for testing module initialization.
     */
    fun createMockModuleContext(packageName: String = "com.test.module"): MockModuleContext {
        return MockModuleContext(packageName)
    }
    
    private fun getDefaultReturnValue(returnType: Class<*>): Any? {
        return when (returnType) {
            Void.TYPE -> null
            Boolean::class.javaPrimitiveType -> false
            Byte::class.javaPrimitiveType -> 0.toByte()
            Short::class.javaPrimitiveType -> 0.toShort()
            Int::class.javaPrimitiveType -> 0
            Long::class.javaPrimitiveType -> 0L
            Float::class.javaPrimitiveType -> 0.0f
            Double::class.javaPrimitiveType -> 0.0
            Char::class.javaPrimitiveType -> '\u0000'
            String::class.java -> ""
            else -> null
        }
    }
    
    /**
     * Test class with various methods for hooking tests.
     */
    class TestClass {
        fun simpleMethod() {}
        
        fun methodWithReturn(): String = "original"
        
        fun methodWithParams(param1: String, param2: Int): Boolean = true
        
        fun methodThatThrows(): String {
            throw RuntimeException("Original exception")
        }
        
        @JvmField
        var testField: String = "original"
        
        companion object {
            @JvmStatic
            fun staticMethod(): String = "static"
        }
    }
}

/**
 * Mock context for testing module behavior.
 */
class MockModuleContext(private val packageName: String) {
    private val assets = mutableMapOf<String, String>()
    private val preferences = mutableMapOf<String, Any>()
    
    fun addAsset(filename: String, content: String) {
        assets[filename] = content
    }
    
    fun getAsset(filename: String): String? {
        return assets[filename]
    }
    
    fun setPreference(key: String, value: Any) {
        preferences[key] = value
    }
    
    fun getPreference(key: String): Any? {
        return preferences[key]
    }
    
    fun getPackageName(): String = packageName
} 