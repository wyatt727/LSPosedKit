package com.wobbz.framework.core

/**
 * Interface for objects that need to release resources when they are no longer needed.
 * This interface is used to ensure proper cleanup of resources during module lifecycle events.
 */
interface Releasable {
    
    /**
     * Releases any resources held by this object.
     * This method should be idempotent and safe to call multiple times.
     */
    fun release()
} 