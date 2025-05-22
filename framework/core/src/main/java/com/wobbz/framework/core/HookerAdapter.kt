package com.wobbz.framework.core

import io.github.libxposed.api.XposedInterface as LibXposedInterface
import java.lang.reflect.Method

/**
 * Adapter that bridges LSPosedKit Hooker with libxposed Hooker interface.
 * This class converts between the different hook callback systems.
 */
internal class HookerAdapter<T : Hooker>(
    private val lspkHooker: T
) : LibXposedInterface.Hooker {
    
    companion object {
        private const val TAG = "LSPK-HookerAdapter"
        
        // Thread-local storage for the current hooker instance
        private val currentHooker = ThreadLocal<Hooker>()
        
        /**
         * Before hook callback for libxposed API.
         * This method is called by the libxposed framework.
         */
        @JvmStatic
        fun before(callback: LibXposedInterface.BeforeHookCallback) {
            try {
                val hooker = currentHooker.get() ?: return
                
                val lspkParam = HookParamImpl(
                    method = callback.member as Method,
                    thisObject = callback.thisObject,
                    args = callback.args
                )
                
                hooker.beforeHook(lspkParam)
                
                // Handle early returns
                if (lspkParam.returnEarly()) {
                    if (lspkParam.hasThrowable()) {
                        callback.throwAndSkip(lspkParam.getThrowable())
                    } else {
                        callback.returnAndSkip(lspkParam.getResult<Any>())
                    }
                }
            } catch (e: Exception) {
                LogUtil.logError(TAG, "Error in before hook", e)
            }
        }
        
        /**
         * After hook callback for libxposed API.
         * This method is called by the libxposed framework.
         */
        @JvmStatic
        fun after(callback: LibXposedInterface.AfterHookCallback) {
            try {
                val hooker = currentHooker.get() ?: return
                
                val lspkParam = HookParamImpl(
                    method = callback.member as Method,
                    thisObject = callback.thisObject,
                    args = callback.args
                )
                
                // Set original result/throwable
                val throwable = callback.throwable
                if (throwable != null) {
                    lspkParam.setOriginalThrowable(throwable)
                } else {
                    lspkParam.setOriginalResult(callback.result)
                }
                
                hooker.afterHook(lspkParam)
                
                // Handle result modifications
                if (lspkParam.returnEarly()) {
                    if (lspkParam.hasThrowable()) {
                        callback.setThrowable(lspkParam.getThrowable())
                    } else {
                        callback.setResult(lspkParam.getResult<Any>())
                    }
                }
            } catch (e: Exception) {
                LogUtil.logError(TAG, "Error in after hook", e)
            }
        }
    }
    
    /**
     * Sets the current hooker for this thread.
     */
    fun setCurrentHooker() {
        currentHooker.set(lspkHooker)
    }
    
    /**
     * Clears the current hooker for this thread.
     */
    fun clearCurrentHooker() {
        currentHooker.remove()
    }
} 