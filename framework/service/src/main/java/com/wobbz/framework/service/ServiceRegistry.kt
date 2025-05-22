package com.wobbz.framework.service

import androidx.annotation.GuardedBy
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Thread-safe registry for services.
 */
internal class ServiceRegistry {
    private val services = ConcurrentHashMap<Class<*>, Any>()
    private val serviceModuleMapping = ConcurrentHashMap<Class<*>, String?>() // Added to track module ID for services
    private val pendingListeners = ConcurrentHashMap<Class<*>, MutableSet<ServiceListenerRegistration<*>>>()
    
    /**
     * Registers a service implementation.
     * @param serviceClass The service interface class
     * @param implementation The service implementation
     * @param moduleId The ID of the module providing the service (optional)
     */
    fun <T : Any> register(serviceClass: Class<T>, implementation: T, moduleId: String? = null) {
        // Register the service
        services[serviceClass] = implementation
        if (moduleId != null) {
            serviceModuleMapping[serviceClass] = moduleId
        }
        
        // Notify pending listeners
        notifyListeners(serviceClass, implementation)
    }
    
    /**
     * Gets a service implementation.
     * @param serviceClass The service interface class
     * @return The service implementation or null if not found
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(serviceClass: Class<T>): T? {
        return services[serviceClass] as? T
    }
    
    /**
     * Gets all registered services.
     * @return A map of service class to implementation
     */
    fun getAll(): Map<Class<*>, Any> {
        return services.toMap()
    }
    
    /**
     * Clears all registered services.
     */
    fun clear() {
        services.clear()
        pendingListeners.clear()
        serviceModuleMapping.clear()
    }
    
    /**
     * Adds a listener for service availability.
     * @param serviceClass The service class to listen for
     * @param listener The listener to be notified
     * @return A registration token that can be used to unregister
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> addServiceListener(serviceClass: Class<T>, listener: ServiceListener<T>): Any {
        // Check if service is already available
        val existingService = get(serviceClass)
        if (existingService != null) {
            listener.onServiceAvailable(existingService)
            return UUID.randomUUID() // Return dummy token since no registration needed
        }
        
        // Create registration
        val registration = ServiceListenerRegistration(serviceClass, listener as ServiceListener<Any>)
        
        // Add to pending listeners
        pendingListeners.computeIfAbsent(serviceClass) { mutableSetOf() }
            .add(registration)
        
        return registration.token
    }
    
    /**
     * Removes a service listener.
     * @param token The registration token returned from addServiceListener
     */
    fun removeServiceListener(token: Any) {
        for (listeners in pendingListeners.values) {
            listeners.removeIf { it.token == token }
        }
        // Clean up empty sets
        pendingListeners.entries.removeIf { it.value.isEmpty() }
    }
    
    /**
     * Notifies listeners that a service is available.
     */
    @Suppress("UNCHECKED_CAST")
    private fun notifyListeners(serviceClass: Class<*>, implementation: Any) {
        val listeners = pendingListeners[serviceClass] ?: return
        
        // Notify all listeners
        for (registration in listeners) {
            try {
                registration.listener.onServiceAvailable(implementation)
            } catch (e: Exception) {
                android.util.Log.e(
                    "ServiceRegistry", 
                    "Exception in service listener: ${e.message}", 
                    e
                )
            }
        }
        
        // Remove all notified listeners
        pendingListeners.remove(serviceClass)
    }
    
    /**
     * Registration for a service listener.
     */
    private data class ServiceListenerRegistration<T : Any>(
        val serviceClass: Class<T>,
        val listener: ServiceListener<Any>,
        val token: UUID = UUID.randomUUID()
    )

    /**
     * Removes a service implementation from the registry.
     * @param serviceClass The service interface class to remove.
     */
    fun remove(serviceClass: Class<*>) {
        services.remove(serviceClass)
        serviceModuleMapping.remove(serviceClass)
    }

    /**
     * Gets the module ID that provided a given service.
     * @param serviceClass The service interface class
     * @return The module ID, or null if not found or not module-provided.
     */
    fun getModuleIdForService(serviceClass: Class<*>?): String? {
        return if (serviceClass == null) null else serviceModuleMapping[serviceClass]
    }
} 