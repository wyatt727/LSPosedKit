package com.wobbz.framework.service

/**
 * Represents a service definition with metadata.
 * @param serviceClass The service interface class
 * @param implementation The service implementation
 * @param moduleId The ID of the module providing this service
 * @param version The version of the service implementation
 * @param dependencies List of service classes this service depends on
 * @param isReleasable Whether the service implements Releasable
 * @param isReloadAware Whether the service implements ReloadAware
 */
internal data class ServiceDefinition<T : Any>(
    val serviceClass: Class<T>,
    var implementation: T, // Made var to allow update during hot-reload if necessary, though unregister/register is cleaner
    val moduleId: String? = null,
    val version: String,
    val dependencies: List<Class<*>> = emptyList(),
    val isReleasable: Boolean = false,
    val isReloadAware: Boolean = false
)

/**
 * Describes a registered service, including its version and dependencies.
 * This is for external query, ServiceDefinition is internal.
 */
data class ServiceDescriptor(
    val serviceClass: Class<*>, // Changed from KClass to Class for Java compatibility
    val version: String,
    val dependencies: List<Class<*>>, // Changed from KClass to Class
    val moduleId: String? = null,
    val isReleasable: Boolean,
    val isReloadAware: Boolean
) 