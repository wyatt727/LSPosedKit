package com.wobbz.framework.service.resolver

import kotlin.reflect.KClass

/**
 * Represents a service dependency.
 */
sealed class ServiceDependency {
    /**
     * Dependency on a service.
     * @param serviceClass The service class
     * @param callback Callback to invoke when the service is available
     */
    data class Service<T : Any>(
        val serviceClass: KClass<T>,
        val callback: (T) -> Unit
    ) : ServiceDependency()
    
    /**
     * Dependency on a feature.
     * @param featureId The feature ID
     * @param callback Callback to invoke with feature availability
     */
    data class Feature(
        val featureId: String,
        val callback: (Boolean) -> Unit
    ) : ServiceDependency()
} 