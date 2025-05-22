package com.wobbz.framework.service

import androidx.annotation.GuardedBy
import androidx.annotation.VisibleForTesting
import kotlin.reflect.KClass
import java.util.concurrent.ConcurrentHashMap
import com.wobbz.framework.core.Releasable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Central service registry and feature discovery system for LSPosedKit.
 * Provides thread-safe service registration, feature discovery, and dependency resolution.
 */
object FeatureManager {
    
    private val serviceRegistry = ServiceRegistry()
    private val serviceDescriptors = ConcurrentHashMap<Class<*>, ServiceDescriptor>()
    private val features = ConcurrentHashMap<String, Boolean>()
    private val extensionPoints = ConcurrentHashMap<String, MutableList<Class<*>>>()
    private val hotReloadState = HotReloadState()
    private val serviceScope = CoroutineScope(Dispatchers.Default)
    
    /**
     * Registers a service implementation with version and dependencies.
     * @param serviceClass The service interface class
     * @param implementation The service implementation
     * @param version The service version string
     * @param dependencies List of service classes this service depends on
     * @param moduleId The ID of the module providing the service (optional)
     * @throws IllegalArgumentException if the implementation doesn't implement the service interface or if dependencies are not met
     */
    @JvmStatic
    fun <T : Any> registerService(
        serviceClass: KClass<T>,
        implementation: T,
        version: String,
        dependencies: List<KClass<*>> = emptyList(),
        moduleId: String? = null
    ) {
        registerService(serviceClass.java, implementation, version, dependencies.map { it.java }, moduleId)
    }

    /**
     * Registers a service implementation with version and dependencies using Java class.
     * @param serviceClass The service interface class
     * @param implementation The service implementation
     * @param version The service version string
     * @param dependencies List of service classes this service depends on
     * @param moduleId The ID of the module providing the service (optional)
     * @throws IllegalArgumentException if the implementation doesn't implement the service interface or if dependencies are not met
     */
    @JvmStatic
    fun <T : Any> registerService(
        serviceClass: Class<T>,
        implementation: T,
        version: String,
        dependencies: List<Class<*>> = emptyList(),
        moduleId: String? = null
    ) {
        if (!serviceClass.isAssignableFrom(implementation.javaClass)) {
            throw IllegalArgumentException(
                "Implementation ${implementation.javaClass.name} does not implement ${serviceClass.name}"
            )
        }

        // Validate dependencies
        for (depClass in dependencies) {
            if (serviceRegistry.get(depClass) == null) {
                throw IllegalArgumentException(
                    "Dependency ${depClass.name} for service ${serviceClass.name} is not registered."
                )
            }
            // TODO: Add version check for dependencies if versions are also registered for them
        }
        
        val descriptor = ServiceDescriptor(serviceClass, version, dependencies, moduleId, implementation is Releasable, implementation is ReloadAware)
        serviceDescriptors[serviceClass] = descriptor
        serviceRegistry.register(serviceClass, implementation, moduleId)
        
        android.util.Log.d("FeatureManager", "Registered service: ${serviceClass.name} v$version, Module: $moduleId")
    }

    /**
     * Unregisters a service implementation and cleans up its resources.
     * @param serviceClass The service interface class to unregister
     */
    @JvmStatic
    fun unregisterService(serviceClass: KClass<*>) {
        unregisterService(serviceClass.java)
    }

    /**
     * Unregisters a service implementation and cleans up its resources using Java class.
     * @param serviceClass The service interface class to unregister
     */
    @JvmStatic
    fun unregisterService(serviceClass: Class<*>) {
        val implementation = serviceRegistry.get(serviceClass)

        if (implementation is ReloadAware) {
            implementation.onBeforeReload() // Or a more generic onBeforeUnregister()
        }

        if (implementation is Releasable) {
            try {
                implementation.release()
                android.util.Log.d("FeatureManager", "Released resources for service: ${serviceClass.name}")
            } catch (e: Exception) {
                android.util.Log.e("FeatureManager", "Error releasing resources for ${serviceClass.name}", e)
            }
        }
        
        serviceRegistry.remove(serviceClass)
        serviceDescriptors.remove(serviceClass)
        android.util.Log.d("FeatureManager", "Unregistered service: ${serviceClass.name}")

        if (implementation is ReloadAware) {
            implementation.onAfterReload() // Or a more generic onAfterUnregister()
        }
    }
    
    /**
     * Gets a service implementation. Returns null if the service is not found or if hot-reload is in progress for that service.
     */
    @JvmStatic
    fun <T : Any> get(serviceClass: Class<T>): T? {
        if (hotReloadState.isReloading(serviceRegistry.getModuleIdForService(serviceClass))) { // Check hot-reload state
            android.util.Log.w("FeatureManager", "Access to service ${serviceClass.name} denied during hot-reload.")
            return null // Or queue the request
        }
        return serviceRegistry.get(serviceClass)
    }
    
    /**
     * Gets all registered services.
     */
    @VisibleForTesting
    internal fun getAllServices(): Map<Class<*>, Any> {
        return serviceRegistry.getAll()
    }

    /**
     * Gets the descriptor for a registered service.
     * @param serviceClass The service interface class
     * @return The ServiceDescriptor or null if not found
     */
    @JvmStatic
    fun getServiceDescriptor(serviceClass: KClass<*>): ServiceDescriptor? {
        return getServiceDescriptor(serviceClass.java)
    }

    /**
     * Gets the descriptor for a registered service using Java class.
     * @param serviceClass The service interface class
     * @return The ServiceDescriptor or null if not found
     */
    @JvmStatic
    fun getServiceDescriptor(serviceClass: Class<*>): ServiceDescriptor? {
        return serviceDescriptors[serviceClass]
    }
    
    /**
     * Declares a feature as available.
     */
    @JvmStatic
    fun declareFeature(featureId: String) {
        features[featureId] = true
    }
    
    /**
     * Checks if a feature is available.
     * @param featureId The feature identifier
     * @return true if the feature is available
     */
    @JvmStatic
    fun hasFeature(featureId: String): Boolean {
        return features[featureId] == true
    }
    
    /**
     * Gets all available features.
     * @return A list of feature identifiers
     */
    @JvmStatic
    fun getFeatures(): List<String> {
        return features.keys.filter { features[it] == true }
    }
    
    /**
     * Registers an extension implementation for an extension point.
     * @param extensionPoint The extension point identifier
     * @param extensionClass The extension implementation class
     */
    @JvmStatic
    fun registerExtension(extensionPoint: String, extensionClass: Class<*>) {
        extensionPoints.computeIfAbsent(extensionPoint) { mutableListOf() }
            .add(extensionClass)
    }
    
    /**
     * Gets extensions for a specific extension point.
     * @param extensionPoint The extension point identifier
     * @return A list of extension classes
     */
    @JvmStatic
    fun getExtensions(extensionPoint: String): List<Class<*>> {
        return extensionPoints[extensionPoint]?.toList() ?: emptyList()
    }
    
    /**
     * Resets the service registry, features, and extension points.
     * Only used for testing.
     */
    @VisibleForTesting
    internal fun reset() {
        serviceRegistry.clear()
        serviceDescriptors.clear()
        features.clear()
        extensionPoints.clear()
        hotReloadState.clearAll() // Reset hot-reload states
    }
    
    /**
     * Adds a listener for service availability.
     * @param serviceClass The service class to listen for
     * @param listener The listener to be notified
     * @return A registration token that can be used to unregister
     */
    @JvmStatic
    fun <T : Any> addServiceListener(serviceClass: Class<T>, listener: ServiceListener<T>): Any {
        return serviceRegistry.addServiceListener(serviceClass, listener)
    }
    
    /**
     * Removes a service listener.
     * @param token The registration token returned from addServiceListener
     */
    @JvmStatic
    fun removeServiceListener(token: Any) {
        serviceRegistry.removeServiceListener(token)
    }
    
    /**
     * Called when a module is loaded to register its features from module-info.json.
     * @param moduleId The module ID
     * @param features The list of features provided by the module
     */
    @JvmStatic
    internal fun registerModuleFeatures(moduleId: String, features: List<String>) {
        for (feature in features) {
            declareFeature(feature)
        }
        android.util.Log.d(
            "FeatureManager", 
            "Registered ${features.size} features for module $moduleId"
        )
    }
    
    /**
     * Called when a module is unloaded or hot-reloaded.
     * Services are never unregistered to prevent runtime errors,
     * but this could be used in the future to handle dynamic module unloading.
     */
    @JvmStatic
    internal fun handleModuleUnload(moduleId: String) {
        // Unregister services provided by this module
        val servicesToUnregister = serviceDescriptors.filter { it.value.moduleId == moduleId }.map { it.key }
        servicesToUnregister.forEach { serviceClass ->
            unregisterService(serviceClass)
        }
        // TODO: Potentially unregister features declared by this module if they are exclusively from it.
        // This requires tracking which module declared which feature.
        android.util.Log.d("FeatureManager", "Handled unload for module: $moduleId. Unregistered ${servicesToUnregister.size} services.")
    }

    /**
     * Executes an asynchronous service call using a provided suspending function.
     * This uses the FeatureManager's CoroutineScope.
     * @param serviceCall A suspend function representing the service call.
     * @param T The return type of the service call.
     * @param callback A callback to handle the result or exception.
     */
    @JvmStatic
    fun <T: Any> executeAsyncServiceCall(serviceCall: suspend () -> T, callback: (Result<T>) -> Unit) {
        serviceScope.launch {
            try {
                val result = serviceCall()
                callback(Result.success(result))
            } catch (e: Exception) {
                android.util.Log.e("FeatureManager", "Async service call failed", e)
                callback(Result.failure(e))
            }
        }
    }

    /**
     * Notifies the FeatureManager that a module is about to be hot-reloaded.
     * @param moduleId The ID of the module being reloaded.
     */
    @JvmStatic
    fun onBeforeHotReload(moduleId: String) {
        hotReloadState.setReloading(moduleId, true)
        // Call onBeforeReload for all ReloadAware services provided by this module
        serviceDescriptors.filter { it.value.moduleId == moduleId && it.value.isReloadAware }
            .forEach { entry ->
                (serviceRegistry.get(entry.key) as? ReloadAware)?.onBeforeReload()
            }
    }

    /**
     * Notifies the FeatureManager that a module has finished hot-reloading.
     * @param moduleId The ID of the module that was reloaded.
     */
    @JvmStatic
    fun onAfterHotReload(moduleId: String) {
        hotReloadState.setReloading(moduleId, false)
        // Call onAfterReload for all ReloadAware services provided by this module
        serviceDescriptors.filter { it.value.moduleId == moduleId && it.value.isReloadAware }
            .forEach { entry ->
                (serviceRegistry.get(entry.key) as? ReloadAware)?.onAfterReload()
            }
    }
}

/**
 * Interface for services that need to perform actions before and after a hot-reload.
 */
interface ReloadAware {
    fun onBeforeReload()
    fun onAfterReload()
}

/**
 * Manages the hot-reload state for modules/services.
 */
internal class HotReloadState {
    private val reloadingModules = ConcurrentHashMap<String?, Boolean>() // ModuleId or null for global

    fun isReloading(moduleId: String?): Boolean {
        return reloadingModules[moduleId] == true || reloadingModules[null] == true // Check specific module or global
    }

    fun setReloading(moduleId: String?, state: Boolean) {
        if (moduleId == null) {
            reloadingModules.keys.forEach { key -> reloadingModules[key] = state }
        } else {
            reloadingModules[moduleId] = state
        }
    }
    fun clearAll() {
        reloadingModules.clear()
    }
}

/**
 * Represents a contract for a service operation that executes asynchronously and returns a result of type T.
 * Implementations should use coroutines for their asynchronous logic.
 */
interface AsyncService<T: Any> {
    /**
     * Executes the asynchronous operation.
     * @return The result of the operation.
     * @throws Exception if the operation fails.
     */
    suspend fun execute(): T
} 