package com.wobbz.framework.service

/**
 * Listener for service availability notifications.
 */
interface ServiceListener<T : Any> {
    /**
     * Called when a service becomes available.
     * @param implementation The service implementation
     */
    fun onServiceAvailable(implementation: T)
} 