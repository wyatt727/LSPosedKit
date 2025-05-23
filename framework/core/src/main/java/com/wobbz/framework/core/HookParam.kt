package com.wobbz.framework.core

import java.lang.reflect.Method

/**
 * Interface providing access to hook parameters and results.
 * Contains information about the hooked method, its arguments, and execution context.
 */
interface HookParam {
    
    /**
     * The hooked method.
     */
    val method: Method
    
    /**
     * The object instance on which the method was called (null for static methods).
     */
    val thisObject: Any?
    
    /**
     * The arguments passed to the method.
     */
    val args: Array<Any?>
    
    /**
     * Gets the result of the method execution.
     * Only available in afterHook() callbacks.
     * 
     * @return The method result, or null if not set or method returns void
     */
    fun <T> getResult(): T?
    
    /**
     * Sets the result of the method execution.
     * This will override the original method's return value.
     * 
     * @param result The new result to return
     */
    fun setResult(result: Any?)
    
    /**
     * Gets the throwable that was thrown by the method.
     * Only available in afterHook() callbacks if an exception occurred.
     * 
     * @return The throwable, or null if no exception was thrown
     */
    fun getThrowable(): Throwable?
    
    /**
     * Sets a throwable to be thrown instead of the normal method execution.
     * This will cause the method to throw the specified exception.
     * 
     * @param throwable The throwable to throw
     */
    fun setThrowable(throwable: Throwable)
    
    /**
     * Checks if the method result has been modified.
     * 
     * @return true if the result has been modified, false otherwise
     */
    fun hasResult(): Boolean
    
    /**
     * Checks if a throwable has been set.
     * 
     * @return true if a throwable has been set, false otherwise
     */
    fun hasThrowable(): Boolean
    
    /**
     * Checks if the method should return early (either result or throwable has been set).
     * 
     * @return true if the method should return early, false otherwise
     */
    fun returnEarly(): Boolean
} 