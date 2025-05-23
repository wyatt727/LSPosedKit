package com.wobbz.framework.core

import android.content.Context
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 * Abstraction layer over the Xposed API.
 * Provides a clean interface for hooking methods and accessing system services.
 */
interface XposedInterface {
    
    /**
     * Loads a class by name using the current class loader.
     * 
     * @param className The fully qualified class name
     * @return The loaded class
     * @throws ClassNotFoundException if the class cannot be found
     */
    fun loadClass(className: String): Class<*>
    
    /**
     * Hooks a method with the specified hooker.
     * 
     * @param clazz The class containing the method
     * @param methodName The name of the method to hook
     * @param parameterTypes The parameter types of the method
     * @param hookerClass The hooker class to use for callbacks
     * @return A MethodUnhooker to unhook the method later
     */
    fun <T : Hooker> hook(
        clazz: Class<*>,
        methodName: String,
        parameterTypes: Array<Class<*>>,
        hookerClass: Class<T>
    ): MethodUnhooker<T>
    
    /**
     * Hooks a method with the specified hooker.
     * 
     * @param method The method to hook
     * @param hookerClass The hooker class to use for callbacks
     * @return A MethodUnhooker to unhook the method later
     */
    fun <T : Hooker> hook(
        method: Method,
        hookerClass: Class<T>
    ): MethodUnhooker<T>
    
    /**
     * Hooks a constructor with the specified hooker.
     * 
     * @param constructor The constructor to hook
     * @param hookerClass The hooker class to use for callbacks
     * @return A MethodUnhooker to unhook the constructor later
     */
    fun <T : Hooker> hook(
        constructor: Constructor<*>,
        hookerClass: Class<T>
    ): MethodUnhooker<T>
    
    /**
     * Hooks a field getter with the specified hooker.
     * 
     * @param field The field to hook
     * @param hookerClass The hooker class to use for callbacks
     * @return A MethodUnhooker to unhook the field later
     */
    fun <T : Hooker> hook(
        field: Field,
        hookerClass: Class<T>
    ): MethodUnhooker<T>
    
    /**
     * Logs a message with the specified level.
     * 
     * @param level The log level
     * @param message The message to log
     */
    fun log(level: LogLevel, message: String)
    
    /**
     * Logs an error message with a throwable.
     * 
     * @param message The error message
     * @param throwable The throwable to log
     */
    fun logError(message: String, throwable: Throwable)
    
    /**
     * Gets the system context.
     * 
     * @return The system context
     */
    fun getSystemContext(): Context
    
    /**
     * Gets the current class loader.
     * 
     * @return The current class loader
     */
    fun getClassLoader(): ClassLoader
} 