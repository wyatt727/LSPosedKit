package com.wobbz.framework.core

import java.lang.reflect.Method

/**
 * Implementation of HookParam interface.
 * Internal class used by the framework to provide hook parameter access.
 */
internal class HookParamImpl(
    override val method: Method,
    override val thisObject: Any?,
    override val args: Array<Any?>
) : HookParam {
    
    private var result: Any? = null
    private var throwable: Throwable? = null
    private var resultSet = false
    private var throwableSet = false
    
    @Suppress("UNCHECKED_CAST")
    override fun <T> getResult(): T? {
        return result as? T
    }
    
    override fun setResult(result: Any?) {
        this.result = result
        this.resultSet = true
        this.throwableSet = false  // Clear throwable when setting result
        this.throwable = null
    }
    
    override fun getThrowable(): Throwable? {
        return throwable
    }
    
    override fun setThrowable(throwable: Throwable) {
        this.throwable = throwable
        this.throwableSet = true
        this.resultSet = false  // Clear result when setting throwable
        this.result = null
    }
    
    override fun hasThrowable(): Boolean {
        return throwableSet
    }
    
    override fun returnEarly(): Boolean {
        return resultSet || throwableSet
    }
    
    /**
     * Internal method to set the original result from the method execution.
     * Used by the framework only.
     */
    internal fun setOriginalResult(result: Any?) {
        if (!resultSet && !throwableSet) {
            this.result = result
        }
    }
    
    /**
     * Internal method to set the original throwable from the method execution.
     * Used by the framework only.
     */
    internal fun setOriginalThrowable(throwable: Throwable) {
        if (!resultSet && !throwableSet) {
            this.throwable = throwable
            this.throwableSet = true
        }
    }
} 