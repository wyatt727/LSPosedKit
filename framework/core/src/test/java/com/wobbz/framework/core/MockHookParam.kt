package com.wobbz.framework.core

import java.lang.reflect.Method

/**
 * Mock implementation of HookParam for unit testing.
 * Allows testing of hooker implementations without requiring the actual Xposed framework.
 */
class MockHookParam(
    override val xposed: XposedInterface,
    override val method: Method,
    override val thisObject: Any?,
    override val args: Array<Any?>
) : HookParam {
    
    private var result: Any? = null
    private var throwable: Throwable? = null
    private var hasResultValue = false
    private var hasThrowableValue = false
    
    @Suppress("UNCHECKED_CAST")
    override fun <T> getResult(): T? {
        return result as? T
    }
    
    override fun setResult(result: Any?) {
        this.result = result
        this.hasResultValue = true
        this.throwable = null
        this.hasThrowableValue = false
    }
    
    override fun getThrowable(): Throwable? {
        return throwable
    }
    
    override fun setThrowable(throwable: Throwable) {
        this.throwable = throwable
        this.hasThrowableValue = true
        this.result = null
        this.hasResultValue = false
    }
    
    override fun hasResult(): Boolean {
        return hasResultValue
    }
    
    override fun hasThrowable(): Boolean {
        return hasThrowableValue
    }
    
    // Testing utilities
    
    /**
     * Resets the hook param state for reuse in tests.
     */
    fun reset() {
        result = null
        throwable = null
        hasResultValue = false
        hasThrowableValue = false
    }
    
    /**
     * Creates a copy of this hook param with different arguments.
     */
    fun withArgs(vararg newArgs: Any?): MockHookParam {
        return MockHookParam(xposed, method, thisObject, arrayOf(*newArgs))
    }
    
    /**
     * Creates a copy of this hook param with a different thisObject.
     */
    fun withThisObject(newThisObject: Any?): MockHookParam {
        return MockHookParam(xposed, method, newThisObject, args)
    }
} 