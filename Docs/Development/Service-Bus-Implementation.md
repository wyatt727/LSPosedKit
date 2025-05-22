# Service Bus Implementation Guide

This document provides detailed implementation guidelines for the service bus component of LSPosedKit. The service bus enables cross-module communication, dependency management, and feature discovery with thread-safety guarantees.

## Directory Structure

Create the following files in the `framework/service/src/main/java/com/wobbz/framework/service` directory:

```
framework/service/src/main/java/com/wobbz/framework/service/
├── FeatureManager.kt
├── ServiceDefinition.kt
├── ServiceListener.kt
├── ServiceRegistry.kt 
├── FeatureFinder.kt
├── ServiceProvider.kt
├── ServiceBootstrap.kt
└── resolver/
    ├── DependencyResolver.kt
    └── ServiceDependency.kt
```

## Gradle Configuration

First, configure the `framework/service/build.gradle` file with necessary dependencies:

```groovy
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.wobbz.framework.service'
    compileSdk rootProject.ext.compileSdk
    
    defaultConfig {
        minSdk rootProject.ext.minSdk
        targetSdk rootProject.ext.targetSdk
        
        consumerProguardFiles "consumer-rules.pro"
    }
    
    compileOptions {
        sourceCompatibility rootProject.ext.javaVersion
        targetCompatibility rootProject.ext.javaVersion
    }
    
    kotlinOptions {
        jvmTarget = rootProject.ext.javaVersion.toString()
    }
}

dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib:${rootProject.ext.kotlinVersion}"
    implementation "org.jetbrains.kotlin:kotlin-reflect:${rootProject.ext.kotlinVersion}"
    implementation 'androidx.annotation:annotation:1.5.0'
}
```

## Core Components Implementation

### FeatureManager.kt

The central component of the service bus:

```kotlin
package com.wobbz.framework.service

import androidx.annotation.GuardedBy
import androidx.annotation.VisibleForTesting
import kotlin.reflect.KClass
import java.util.concurrent.ConcurrentHashMap

/**
 * Central service registry and feature discovery system for LSPosedKit.
 * Provides thread-safe service registration, feature discovery, and dependency resolution.
 */
object FeatureManager {
    
    private val serviceRegistry = ServiceRegistry()
    private val features = ConcurrentHashMap<String, Boolean>()
    private val extensionPoints = ConcurrentHashMap<String, MutableList<Class<*>>>()
    
    /**
     * Registers a service implementation.
     * @param serviceClass The service interface class
     * @param implementation The service implementation
     * @throws IllegalArgumentException if the implementation doesn't implement the service interface
     */
    @JvmStatic
    fun <T : Any> register(serviceClass: KClass<T>, implementation: T) {
        register(serviceClass.java, implementation)
    }
    
    /**
     * Registers a service implementation using Java class.
     * @param serviceClass The service interface class
     * @param implementation The service implementation
     * @throws IllegalArgumentException if the implementation doesn't implement the service interface
     */
    @JvmStatic
    fun <T : Any> register(serviceClass: Class<T>, implementation: T) {
        if (!serviceClass.isAssignableFrom(implementation.javaClass)) {
            throw IllegalArgumentException(
                "Implementation ${implementation.javaClass.name} does not implement ${serviceClass.name}"
            )
        }
        
        serviceRegistry.register(serviceClass, implementation)
        
        // Log successful registration
        android.util.Log.d("FeatureManager", "Registered service: ${serviceClass.name}")
    }
    
    /**
     * Gets a service implementation.
     * @param serviceClass The service interface class
     * @return The service implementation or null if not found
     */
    @JvmStatic
    fun <T : Any> get(serviceClass: KClass<T>): T? {
        return get(serviceClass.java)
    }
    
    /**
     * Gets a service implementation using Java class.
     * @param serviceClass The service interface class
     * @return The service implementation or null if not found
     */
    @JvmStatic
    fun <T : Any> get(serviceClass: Class<T>): T? {
        return serviceRegistry.get(serviceClass)
    }
    
    /**
     * Gets all registered services.
     * @return A map of service class to implementation
     */
    @VisibleForTesting
    internal fun getAllServices(): Map<Class<*>, Any> {
        return serviceRegistry.getAll()
    }
    
    /**
     * Declares a feature as available.
     * @param featureId The feature identifier
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
        features.clear()
        extensionPoints.clear()
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
        // Currently, services persist for the entire process lifetime
    }
}
```

### ServiceDefinition.kt

Represents a service definition:

```kotlin
package com.wobbz.framework.service

/**
 * Represents a service definition with metadata.
 * @param serviceClass The service interface class
 * @param implementation The service implementation
 * @param moduleId The ID of the module providing this service
 */
internal data class ServiceDefinition<T : Any>(
    val serviceClass: Class<T>,
    val implementation: T,
    val moduleId: String? = null,
    val version: String? = null
)
```

### ServiceListener.kt

Interface for service availability notifications:

```kotlin
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
```

### ServiceRegistry.kt

Thread-safe registry for services:

```kotlin
package com.wobbz.framework.service

import androidx.annotation.GuardedBy
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Thread-safe registry for services.
 */
internal class ServiceRegistry {
    private val services = ConcurrentHashMap<Class<*>, Any>()
    private val pendingListeners = ConcurrentHashMap<Class<*>, MutableSet<ServiceListenerRegistration<*>>>()
    
    /**
     * Registers a service implementation.
     * @param serviceClass The service interface class
     * @param implementation The service implementation
     */
    fun <T : Any> register(serviceClass: Class<T>, implementation: T) {
        // Register the service
        services[serviceClass] = implementation
        
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
}
```

### FeatureFinder.kt

Utility for discovering features:

```kotlin
package com.wobbz.framework.service

import android.content.Context
import org.json.JSONObject
import java.io.IOException

/**
 * Utility for discovering features from module-info.json files.
 */
internal object FeatureFinder {
    
    /**
     * Loads features from a module's module-info.json.
     * @param context The module's context
     * @return A pair of module ID and list of features
     */
    fun loadFeatures(context: Context): Pair<String, List<String>> {
        try {
            val inputStream = context.assets.open("module-info.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(jsonString)
            
            val moduleId = json.getString("id")
            val featureArray = json.optJSONArray("features") ?: return Pair(moduleId, emptyList())
            
            val features = mutableListOf<String>()
            for (i in 0 until featureArray.length()) {
                features.add(featureArray.getString(i))
            }
            
            return Pair(moduleId, features)
        } catch (e: IOException) {
            android.util.Log.e("FeatureFinder", "Failed to load features: ${e.message}", e)
            return Pair("unknown", emptyList())
        } catch (e: Exception) {
            android.util.Log.e("FeatureFinder", "Failed to parse module-info.json: ${e.message}", e)
            return Pair("unknown", emptyList())
        }
    }
    
    /**
     * Loads extension points from a module's module-info.json.
     * @param context The module's context
     * @return A map of extension point to implementation class name
     */
    fun loadExtensionPoints(context: Context): Map<String, String> {
        try {
            val inputStream = context.assets.open("module-info.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(jsonString)
            
            val extensionsObj = json.optJSONObject("extensions") ?: return emptyMap()
            
            val extensions = mutableMapOf<String, String>()
            for (key in extensionsObj.keys()) {
                extensions[key] = extensionsObj.getString(key)
            }
            
            return extensions
        } catch (e: IOException) {
            android.util.Log.e("FeatureFinder", "Failed to load extensions: ${e.message}", e)
            return emptyMap()
        } catch (e: Exception) {
            android.util.Log.e("FeatureFinder", "Failed to parse module-info.json: ${e.message}", e)
            return emptyMap()
        }
    }
}
```

### ServiceProvider.kt

Interface for modules that provide services:

```kotlin
package com.wobbz.framework.service

import android.content.Context

/**
 * Interface for modules that provide services.
 * Optional interface that modules can implement to register
 * services during module initialization.
 */
interface ServiceProvider {
    /**
     * Called to register services with the FeatureManager.
     * @param context The module's context
     */
    fun registerServices(context: Context)
}
```

### ServiceBootstrap.kt

Utility for bootstrapping the service system:

```kotlin
package com.wobbz.framework.service

import android.content.Context
import android.util.Log
import kotlin.reflect.KClass

/**
 * Utility for bootstrapping the service system.
 */
object ServiceBootstrap {
    
    private const val TAG = "ServiceBootstrap"
    
    /**
     * Initializes the service system for a module.
     * @param context The module's context
     * @param moduleClass The module's main class
     */
    fun initialize(context: Context, moduleClass: KClass<*>) {
        initialize(context, moduleClass.java)
    }
    
    /**
     * Initializes the service system for a module using Java class.
     * @param context The module's context
     * @param moduleClass The module's main class
     */
    fun initialize(context: Context, moduleClass: Class<*>) {
        // Register module features
        val (moduleId, features) = FeatureFinder.loadFeatures(context)
        FeatureManager.registerModuleFeatures(moduleId, features)
        
        // Register extension points
        val extensions = FeatureFinder.loadExtensionPoints(context)
        for ((extensionPoint, className) in extensions) {
            try {
                val extensionClass = moduleClass.classLoader?.loadClass(className)
                if (extensionClass != null) {
                    FeatureManager.registerExtension(extensionPoint, extensionClass)
                }
            } catch (e: ClassNotFoundException) {
                Log.e(TAG, "Failed to load extension class: $className", e)
            }
        }
        
        // If module implements ServiceProvider, call registerServices
        val moduleInstance = moduleClass.kotlin.objectInstance
            ?: moduleClass.kotlin.java.getDeclaredConstructor().newInstance()
        
        if (moduleInstance is ServiceProvider) {
            moduleInstance.registerServices(context)
        }
    }
}
```

### resolver/DependencyResolver.kt

Resolves service dependencies:

```kotlin
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
     * @param callback Callback to invoke when the dependency is resolved
     * @return A token that can be used to cancel the resolution
     */
    fun resolve(dependency: ServiceDependency): Any {
        return when (dependency) {
            is ServiceDependency.Service<*> -> resolveService(dependency)
            is ServiceDependency.Feature -> resolveFeature(dependency)
        }
    }
    
    /**
     * Resolves a service dependency.
     */
    private fun resolveService(dependency: ServiceDependency.Service<*>): Any {
        // Check if service is already available
        val existingService = FeatureManager.get(dependency.serviceClass)
        if (existingService != null) {
            dependency.callback(existingService)
            return Any() // Dummy token since no listener was registered
        }
        
        // Register listener for service availability
        return FeatureManager.addServiceListener(
            dependency.serviceClass.java,
            object : ServiceListener<Any> {
                override fun onServiceAvailable(implementation: Any) {
                    try {
                        @Suppress("UNCHECKED_CAST")
                        dependency.callback(implementation)
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
```

### resolver/ServiceDependency.kt

Represents a service dependency:

```kotlin
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
```

## Integration with Module System

### Module Initialization

Update the module initialization code to initialize the service system:

```kotlin
// In the module's initialize method
override fun initialize(context: Context, xposed: XposedInterface) {
    // Initialize service system
    ServiceBootstrap.initialize(context, this::class)
    
    // Register services
    FeatureManager.register(INetworkRuleProvider::class, NetworkRuleProvider())
    
    // Declare features
    FeatureManager.declareFeature("network.inspection")
}
```

### Service Registration

To register a service provided by a module:

```kotlin
// Define a service interface
interface INetworkRuleProvider {
    fun getRules(): List<NetworkRule>
}

// Implement the service
class NetworkRuleProvider : INetworkRuleProvider {
    override fun getRules(): List<NetworkRule> {
        return listOf(
            NetworkRule("BLOCK", "bad.example.com"),
            NetworkRule("ALLOW", "good.example.com")
        )
    }
}

// Register during module initialization
FeatureManager.register(INetworkRuleProvider::class, NetworkRuleProvider())
```

### Service Discovery

To discover and use a service from another module:

```kotlin
// Try to get the service immediately
val provider = FeatureManager.get(INetworkRuleProvider::class)
if (provider != null) {
    // Use the service
    val rules = provider.getRules()
    // Apply rules...
}

// Or register a listener for when the service becomes available
val token = FeatureManager.addServiceListener(
    INetworkRuleProvider::class.java,
    object : ServiceListener<INetworkRuleProvider> {
        override fun onServiceAvailable(implementation: INetworkRuleProvider) {
            val rules = implementation.getRules()
            // Apply rules...
        }
    }
)

// Remember to unregister when no longer needed
FeatureManager.removeServiceListener(token)
```

### Feature Declaration and Discovery

Declare features in module-info.json:

```json
{
  "id": "network-guard",
  "name": "Network Guard",
  "version": "1.0.0",
  "description": "Network traffic monitoring and blocking",
  "features": [
    "network.inspection",
    "network.blocking",
    "logging.traffic"
  ]
}
```

To check for feature availability:

```kotlin
if (FeatureManager.hasFeature("network.blocking")) {
    // Feature is available, use it
    setupNetworkBlockingHooks()
} else {
    // Feature not available, use alternative approach
    setupBasicNetworkHooks()
}
```

### Extension Points

Define extension points in module-info.json:

```json
{
  "id": "network-guard",
  "name": "Network Guard",
  "version": "1.0.0",
  "description": "Network traffic monitoring and blocking",
  "extensions": {
    "network.rule.provider": "com.example.networkguard.NetworkRuleProvider",
    "network.traffic.analyzer": "com.example.networkguard.TrafficAnalyzer"
  }
}
```

To discover and use extensions:

```kotlin
// Get all extensions for an extension point
val analyzers = FeatureManager.getExtensions("network.traffic.analyzer")

// Instantiate and use each extension
analyzers.forEach { analyzerClass ->
    try {
        val analyzer = analyzerClass.getDeclaredConstructor().newInstance() as ITrafficAnalyzer
        analyzer.analyze(trafficData)
    } catch (e: Exception) {
        xposed.logError("Error using traffic analyzer extension", e)
    }
}
```

## Testing

Create unit tests for the service bus:

```kotlin
class FeatureManagerTest {
    @Before
    fun setup() {
        FeatureManager.reset()
    }
    
    @Test
    fun testRegisterAndGetService() {
        // Create a service implementation
        val service = TestService()
        
        // Register the service
        FeatureManager.register(ITestService::class, service)
        
        // Get the service
        val retrievedService = FeatureManager.get(ITestService::class)
        
        // Verify it's the same instance
        assertSame(service, retrievedService)
    }
    
    @Test
    fun testServiceListener() {
        // Create a mock listener
        val listener = mock(ServiceListener::class.java)
        
        // Register the listener
        val token = FeatureManager.addServiceListener(
            ITestService::class.java,
            listener as ServiceListener<ITestService>
        )
        
        // Create a service implementation
        val service = TestService()
        
        // Register the service
        FeatureManager.register(ITestService::class, service)
        
        // Verify the listener was called with the service
        verify(listener).onServiceAvailable(service)
    }
    
    @Test
    fun testFeatureDeclaration() {
        // Declare a feature
        FeatureManager.declareFeature("test.feature")
        
        // Check if the feature exists
        assertTrue(FeatureManager.hasFeature("test.feature"))
        
        // Check if a non-existent feature doesn't exist
        assertFalse(FeatureManager.hasFeature("non.existent.feature"))
    }
    
    // Define test interfaces and classes
    interface ITestService {
        fun doSomething(): String
    }
    
    class TestService : ITestService {
        override fun doSomething(): String {
            return "Hello, world!"
        }
    }
}
```

## Conclusion

The service bus implementation provides a powerful mechanism for cross-module communication and feature discovery. It enables:

1. **Service registration and discovery**: Modules can provide services and discover services from other modules
2. **Feature declaration and checking**: Modules can declare features and check for feature availability
3. **Extension points**: Modules can define extension points and discover extensions
4. **Thread-safe operation**: All operations are thread-safe using concurrent collections

This system allows for loose coupling between modules while still enabling rich interactions between them. By using the service bus, modules can collaborate without creating direct dependencies on each other.

## Next Steps

After implementing the service bus:

1. Update the core module loading system to initialize the service bus
2. Create sample modules that use the service bus for communication
3. Add tests for service discovery and feature detection
4. Document the service API for module developers 