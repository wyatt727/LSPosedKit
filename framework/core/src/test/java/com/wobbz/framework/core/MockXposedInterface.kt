package com.wobbz.framework.core

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Mock implementation of XposedInterface for unit testing.
 * Allows testing of hook behavior without requiring the actual Xposed framework.
 */
class MockXposedInterface : XposedInterface {
    
    private val logs = mutableListOf<LogEntry>()
    private val loadedClasses = mutableMapOf<String, Class<*>>()
    private val hookedMethods = mutableListOf<HookInfo>()
    private val systemContext: Any? = null
    
    data class LogEntry(
        val level: LogLevel,
        val message: String,
        val throwable: Throwable? = null
    )
    
    data class HookInfo(
        val method: Method,
        val hookerClass: Class<out Hooker>
    )
    
    override fun log(level: LogLevel, message: String) {
        logs.add(LogEntry(level, message))
    }
    
    override fun logError(message: String, throwable: Throwable) {
        logs.add(LogEntry(LogLevel.ERROR, message, throwable))
    }
    
    override fun loadClass(className: String): Class<*> {
        return loadedClasses.getOrPut(className) {
            try {
                Class.forName(className)
            } catch (e: ClassNotFoundException) {
                // For testing, create a mock class if not found
                createMockClass(className)
            }
        }
    }
    
    override fun hook(method: Method, hookerClass: Class<out Hooker>): MethodUnhooker<Method> {
        hookedMethods.add(HookInfo(method, hookerClass))
        return MockMethodUnhooker(method)
    }
    
    override fun hook(field: Field, hookerClass: Class<out Hooker>): MethodUnhooker<Field> {
        // For field hooks, we simulate by creating a method hook
        return MockFieldUnhooker(field)
    }
    
    override fun hook(
        clazz: Class<*>, 
        methodName: String, 
        parameterTypes: Array<Class<*>>, 
        hookerClass: Class<out Hooker>
    ): MethodUnhooker<Method> {
        val method = try {
            clazz.getDeclaredMethod(methodName, *parameterTypes)
        } catch (e: NoSuchMethodException) {
            // Create a mock method for testing
            createMockMethod(clazz, methodName, parameterTypes)
        }
        return hook(method, hookerClass)
    }
    
    override fun getSystemContext(): Any? {
        return systemContext
    }
    
    // Testing utilities
    
    /**
     * Gets all log entries for testing verification.
     */
    fun getLogs(): List<LogEntry> = logs.toList()
    
    /**
     * Gets logs filtered by level.
     */
    fun getLogs(level: LogLevel): List<LogEntry> = logs.filter { it.level == level }
    
    /**
     * Clears all logs.
     */
    fun clearLogs() = logs.clear()
    
    /**
     * Gets all hooked methods for testing verification.
     */
    fun getHookedMethods(): List<HookInfo> = hookedMethods.toList()
    
    /**
     * Checks if a specific method was hooked.
     */
    fun isMethodHooked(method: Method): Boolean {
        return hookedMethods.any { it.method == method }
    }
    
    /**
     * Simulates calling a hooked method for testing.
     */
    fun simulateMethodCall(method: Method, thisObject: Any?, args: Array<Any?>): Any? {
        val hookInfo = hookedMethods.find { it.method == method }
        if (hookInfo != null) {
            val hooker = hookInfo.hookerClass.getDeclaredConstructor().newInstance()
            val mockParam = MockHookParam(this, method, thisObject, args)
            
            try {
                hooker.beforeHook(mockParam)
                if (mockParam.hasThrowable()) {
                    throw mockParam.getThrowable()!!
                }
                
                // Simulate original method call if not replaced
                if (!mockParam.hasResult()) {
                    // Set a default result for testing
                    mockParam.setResult(getDefaultReturnValue(method.returnType))
                }
                
                hooker.afterHook(mockParam)
                if (mockParam.hasThrowable()) {
                    throw mockParam.getThrowable()!!
                }
                
                return mockParam.getResult<Any>()
            } catch (e: Exception) {
                logError("Error in mock method call", e)
                throw e
            }
        }
        
        // Method not hooked, return default value
        return getDefaultReturnValue(method.returnType)
    }
    
    /**
     * Registers a class for loading by loadClass().
     */
    fun registerClass(className: String, clazz: Class<*>) {
        loadedClasses[className] = clazz
    }
    
    private fun createMockClass(className: String): Class<*> {
        // For testing purposes, we can't actually create classes at runtime easily
        // So we'll throw an exception that tests can catch and handle
        throw ClassNotFoundException("Mock class not found: $className. Use registerClass() to register test classes.")
    }
    
    private fun createMockMethod(clazz: Class<*>, methodName: String, parameterTypes: Array<Class<*>>): Method {
        throw NoSuchMethodException("Mock method not found: ${clazz.name}.$methodName. Consider using a real class or mock framework.")
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
}

/**
 * Mock implementation of MethodUnhooker for testing.
 */
class MockMethodUnhooker<T>(private val target: T) : MethodUnhooker<T> {
    private var isUnhooked = false
    
    override fun unhook() {
        isUnhooked = true
    }
    
    override fun getTarget(): T = target
    
    fun isUnhooked(): Boolean = isUnhooked
}

/**
 * Mock implementation for field unhookers.
 */
class MockFieldUnhooker(field: Field) : MethodUnhooker<Field> {
    private var isUnhooked = false
    private val target = field
    
    override fun unhook() {
        isUnhooked = true
    }
    
    override fun getTarget(): Field = target
    
    fun isUnhooked(): Boolean = isUnhooked
} 