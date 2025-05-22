package com.wobbz.framework.core

/**
 * Interface for unhooking methods.
 * Returned by hook operations to allow cleanup of hooks.
 * 
 * @param T The type of the hooker class
 */
interface MethodUnhooker<T : Hooker> {
    
    /**
     * Unhooks the method, removing the hook callback.
     * After calling this method, the hook will no longer be triggered.
     * This method is idempotent and safe to call multiple times.
     */
    fun unhook()
    
    /**
     * Gets the hooker instance associated with this unhook mechanism.
     * 
     * @return The hooker instance
     */
    fun getHooker(): T
    
    /**
     * Checks if this hook is still active.
     * 
     * @return true if the hook is active, false if it has been unhooked
     */
    fun isHooked(): Boolean
} 