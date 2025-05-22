package com.wobbz.framework.core

import androidx.annotation.VisibleForTesting
import java.lang.reflect.*

/**
 * Utility class for testing reflection-based operations.
 * Provides helpers for method access, field manipulation, and hook validation.
 */
object ReflectionTestUtil {
    
    /**
     * Creates a mock method for testing purposes.
     */
    fun createMockMethod(
        declaringClass: Class<*> = TestReflectionClass::class.java,
        name: String = "testMethod",
        returnType: Class<*> = Void.TYPE,
        vararg parameterTypes: Class<*>
    ): Method {
        return try {
            declaringClass.getDeclaredMethod(name, *parameterTypes)
        } catch (e: NoSuchMethodException) {
            // Fall back to a simple method if the specific one doesn't exist
            TestReflectionClass::class.java.getDeclaredMethod("simpleMethod")
        }
    }
    
    /**
     * Creates a mock field for testing purposes.
     */
    fun createMockField(
        declaringClass: Class<*> = TestReflectionClass::class.java,
        name: String = "testField",
        type: Class<*> = String::class.java
    ): Field {
        return try {
            declaringClass.getDeclaredField(name)
        } catch (e: NoSuchFieldException) {
            // Fall back to a simple field if the specific one doesn't exist
            TestReflectionClass::class.java.getDeclaredField("stringField")
        }
    }
    
    /**
     * Gets a method safely with error handling for testing.
     */
    fun getMethodSafely(
        clazz: Class<*>,
        methodName: String,
        vararg parameterTypes: Class<*>
    ): Method? {
        return try {
            clazz.getDeclaredMethod(methodName, *parameterTypes)
        } catch (e: NoSuchMethodException) {
            null
        }
    }
    
    /**
     * Gets a field safely with error handling for testing.
     */
    fun getFieldSafely(clazz: Class<*>, fieldName: String): Field? {
        return try {
            clazz.getDeclaredField(fieldName)
        } catch (e: NoSuchFieldException) {
            null
        }
    }
    
    /**
     * Invokes a method safely for testing.
     */
    fun invokeMethodSafely(
        method: Method,
        target: Any?,
        vararg args: Any?
    ): Any? {
        return try {
            method.isAccessible = true
            method.invoke(target, *args)
        } catch (e: Exception) {
            throw ReflectionTestException("Failed to invoke method ${method.name}", e)
        }
    }
    
    /**
     * Sets a field value safely for testing.
     */
    fun setFieldSafely(field: Field, target: Any?, value: Any?) {
        try {
            field.isAccessible = true
            field.set(target, value)
        } catch (e: Exception) {
            throw ReflectionTestException("Failed to set field ${field.name}", e)
        }
    }
    
    /**
     * Gets a field value safely for testing.
     */
    fun getFieldSafely(field: Field, target: Any?): Any? {
        return try {
            field.isAccessible = true
            field.get(target)
        } catch (e: Exception) {
            throw ReflectionTestException("Failed to get field ${field.name}", e)
        }
    }
    
    /**
     * Validates that a method can be hooked by checking its properties.
     */
    fun validateMethodForHooking(method: Method): ValidationResult {
        val issues = mutableListOf<String>()
        
        // Check if method is abstract
        if (Modifier.isAbstract(method.modifiers)) {
            issues.add("Method is abstract")
        }
        
        // Check if method is native
        if (Modifier.isNative(method.modifiers)) {
            issues.add("Method is native")
        }
        
        // Check if method is final (may cause issues with some hook frameworks)
        if (Modifier.isFinal(method.modifiers)) {
            issues.add("Method is final (may cause hook issues)")
        }
        
        // Check if method is synthetic
        if (method.isSynthetic) {
            issues.add("Method is synthetic")
        }
        
        return ValidationResult(issues.isEmpty(), issues)
    }
    
    /**
     * Validates that a field can be accessed/hooked.
     */
    fun validateFieldForHooking(field: Field): ValidationResult {
        val issues = mutableListOf<String>()
        
        // Check if field is final
        if (Modifier.isFinal(field.modifiers)) {
            issues.add("Field is final")
        }
        
        // Check if field is synthetic
        if (field.isSynthetic) {
            issues.add("Field is synthetic")
        }
        
        return ValidationResult(issues.isEmpty(), issues)
    }
    
    /**
     * Finds all methods in a class that match certain criteria.
     */
    fun findMethodsMatching(
        clazz: Class<*>,
        namePattern: Regex? = null,
        returnType: Class<*>? = null,
        modifiers: Int? = null,
        parameterCount: Int? = null
    ): List<Method> {
        return clazz.declaredMethods.filter { method ->
            if (namePattern != null && !namePattern.matches(method.name)) return@filter false
            if (returnType != null && method.returnType != returnType) return@filter false
            if (modifiers != null && method.modifiers != modifiers) return@filter false
            if (parameterCount != null && method.parameterCount != parameterCount) return@filter false
            true
        }
    }
    
    /**
     * Finds all fields in a class that match certain criteria.
     */
    fun findFieldsMatching(
        clazz: Class<*>,
        namePattern: Regex? = null,
        type: Class<*>? = null,
        modifiers: Int? = null
    ): List<Field> {
        return clazz.declaredFields.filter { field ->
            if (namePattern != null && !namePattern.matches(field.name)) return@filter false
            if (type != null && field.type != type) return@filter false
            if (modifiers != null && field.modifiers != modifiers) return@filter false
            true
        }
    }
    
    /**
     * Creates a test instance of a class using reflection.
     */
    fun createTestInstance(clazz: Class<*>, vararg constructorArgs: Any?): Any {
        return try {
            if (constructorArgs.isEmpty()) {
                // Try no-arg constructor
                val constructor = clazz.getDeclaredConstructor()
                constructor.isAccessible = true
                constructor.newInstance()
            } else {
                // Find matching constructor
                val constructors = clazz.declaredConstructors
                val parameterTypes = constructorArgs.map { it?.javaClass ?: Any::class.java }.toTypedArray()
                
                val constructor = constructors.find { constructor ->
                    val params = constructor.parameterTypes
                    if (params.size != parameterTypes.size) return@find false
                    params.zip(parameterTypes).all { (param, arg) ->
                        param.isAssignableFrom(arg) || isCompatiblePrimitive(param, arg)
                    }
                } ?: throw NoSuchMethodException("No matching constructor found")
                
                constructor.isAccessible = true
                constructor.newInstance(*constructorArgs)
            }
        } catch (e: Exception) {
            throw ReflectionTestException("Failed to create test instance of ${clazz.name}", e)
        }
    }
    
    /**
     * Checks if two types are compatible for primitive conversion.
     */
    private fun isCompatiblePrimitive(primitive: Class<*>, wrapper: Class<*>): Boolean {
        return when (primitive) {
            Boolean::class.javaPrimitiveType -> wrapper == Boolean::class.java
            Byte::class.javaPrimitiveType -> wrapper == Byte::class.java
            Short::class.javaPrimitiveType -> wrapper == Short::class.java
            Int::class.javaPrimitiveType -> wrapper == Int::class.java
            Long::class.javaPrimitiveType -> wrapper == Long::class.java
            Float::class.javaPrimitiveType -> wrapper == Float::class.java
            Double::class.javaPrimitiveType -> wrapper == Double::class.java
            Char::class.javaPrimitiveType -> wrapper == Char::class.java
            else -> false
        }
    }
    
    /**
     * Gets all superclasses and interfaces of a class.
     */
    fun getClassHierarchy(clazz: Class<*>): Set<Class<*>> {
        val hierarchy = mutableSetOf<Class<*>>()
        
        // Add the class itself
        hierarchy.add(clazz)
        
        // Add superclasses
        var currentClass = clazz.superclass
        while (currentClass != null) {
            hierarchy.add(currentClass)
            currentClass = currentClass.superclass
        }
        
        // Add interfaces
        fun addInterfaces(c: Class<*>) {
            c.interfaces.forEach { iface ->
                if (hierarchy.add(iface)) {
                    addInterfaces(iface)
                }
            }
        }
        addInterfaces(clazz)
        
        return hierarchy
    }
    
    /**
     * Test class with various methods and fields for reflection testing.
     */
    @VisibleForTesting
    class TestReflectionClass {
        @JvmField
        var stringField: String = "test"
        
        @JvmField
        var intField: Int = 42
        
        private var privateField: String = "private"
        
        val readOnlyField: String = "readonly"
        
        fun simpleMethod() {}
        
        fun methodWithReturn(): String = "result"
        
        fun methodWithParams(param1: String, param2: Int): Boolean = true
        
        private fun privateMethod(): String = "private"
        
        companion object {
            @JvmStatic
            fun staticMethod(): String = "static"
            
            @JvmField
            var staticField: String = "static"
        }
    }
    
    /**
     * Result of validation operations.
     */
    data class ValidationResult(
        val isValid: Boolean,
        val issues: List<String>
    ) {
        override fun toString(): String {
            return if (isValid) {
                "Valid"
            } else {
                "Invalid: ${issues.joinToString(", ")}"
            }
        }
    }
    
    /**
     * Exception thrown during reflection testing operations.
     */
    class ReflectionTestException(message: String, cause: Throwable) : Exception(message, cause)
} 