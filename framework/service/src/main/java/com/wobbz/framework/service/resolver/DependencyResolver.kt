package com.wobbz.framework.service.resolver

import android.util.Log
import com.wobbz.framework.service.FeatureManager
import com.wobbz.framework.service.ServiceListener
import kotlin.reflect.KClass

/**
 * Resolves service dependencies.
 */
object DependencyResolver {
    private const val TAG = "DependencyResolver"
    
    /**
     * Resolves a service dependency.
     * @param dependency The service dependency
     * @return A token that can be used to cancel the resolution
     */
    fun resolve(dependency: ServiceDependency): Any {
        return when (dependency) {
            is ServiceDependency.Service<*> -> resolveServiceAny(dependency)
            is ServiceDependency.Feature -> resolveFeature(dependency)
        }
    }
    
    /**
     * Resolves a service dependency.
     */
    @Suppress("UNCHECKED_CAST")
    private fun resolveServiceAny(dependency: ServiceDependency.Service<*>): Any {
        // Check if service is already available
        val serviceClass = dependency.serviceClass.java as Class<Any>
        val existingService = FeatureManager.get(serviceClass)
        if (existingService != null) {
            try {
                val callback = dependency.callback as (Any) -> Unit
                callback(existingService)
            } catch (e: Exception) {
                Log.e(TAG, "Error in service callback: ${e.message}", e)
            }
            return Any() // Dummy token since no listener was registered
        }
        
        // Register listener for service availability
        return FeatureManager.addServiceListener(
            serviceClass,
            object : ServiceListener<Any> {
                override fun onServiceAvailable(implementation: Any) {
                    try {
                        val callback = dependency.callback as (Any) -> Unit
                        callback(implementation)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in service callback: ${e.message}", e)
                    }
                }
            }
        )
    }
    
    /**
     * Resolves a feature dependency.
     */
    private fun resolveFeature(dependency: ServiceDependency.Feature): Any {
        // Check if feature is already available
        if (FeatureManager.hasFeature(dependency.featureId)) {
            dependency.callback(true)
        } else {
            dependency.callback(false)
        }
        
        // Return dummy token since features are checked once
        return Any()
    }
} 