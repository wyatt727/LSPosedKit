package com.wobbz.framework.core

/**
 * Implementation of MethodUnhooker interface.
 * Internal class used by the framework to manage hook cleanup.
 */
internal class MethodUnhookerImpl<T : Hooker>(
    private val hooker: T,
    private val unhookCallback: () -> Unit
) : MethodUnhooker<T> {
    
    private var isHooked = true
    
    override fun unhook() {
        if (isHooked) {
            try {
                unhookCallback()
            } catch (e: Exception) {
                LogUtil.logError("LSPK-Framework", "Error during unhook operation", e)
            } finally {
                isHooked = false
            }
        }
    }
    
    override fun getHooker(): T {
        return hooker
    }
    
    override fun isHooked(): Boolean {
        return isHooked
    }
} 