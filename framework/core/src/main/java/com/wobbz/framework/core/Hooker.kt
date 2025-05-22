package com.wobbz.framework.core

/**
 * Interface for implementing hook callbacks.
 * Modules should implement this interface to define their hook behavior.
 */
interface Hooker {
    
    /**
     * Called before the hooked method executes.
     * 
     * @param param The hook parameters containing method information and arguments
     */
    fun beforeHook(param: HookParam) {
        // Default implementation does nothing
    }
    
    /**
     * Called after the hooked method executes.
     * 
     * @param param The hook parameters containing method information, arguments, and result
     */
    fun afterHook(param: HookParam) {
        // Default implementation does nothing
    }
} 