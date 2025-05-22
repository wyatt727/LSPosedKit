# Feature Manager Service Bus

> In-depth guide to LSPosedKit's FeatureManager, a powerful service bus that enables cross-module communication, dependency management, and feature discovery with thread safety guarantees.

## Overview

The FeatureManager is a central component in LSPosedKit that enables modules to:

1. **Discover and use services** provided by other modules
2. **Register capabilities** for other modules to consume
3. **Advertise features** without direct code dependencies
4. **Communicate safely** across module boundaries
5. **Implement extension points** for pluggable functionality

This lightweight service bus follows a publish-subscribe pattern with lifecycle awareness, allowing modules to interact without creating direct dependencies on each other.

## Key Concepts

### Services

Services are interfaces implemented by modules that provide specific functionality to other modules. They are registered with the FeatureManager and can be discovered at runtime.

### Features

Features are string identifiers that indicate a module provides a specific capability. They can be checked for availability without creating a direct dependency.

### Extension Points

Extension points allow modules to define "slots" where other modules can plug in custom implementations. This enables highly customizable and extensible module architectures.

## Using the FeatureManager

### Service Registration and Discovery

To provide a service to other modules:

```kotlin
// In your service provider module
class NetworkRuleProvider : INetworkRuleProvider {
    override fun getRules(): List<NetworkRule> {
        return listOf(
            NetworkRule("BLOCK", "bad.example.com"),
            NetworkRule("ALLOW", "good.example.com")
        )
    }
}

// Register during module initialization
override fun initialize(context: Context, xposed: XposedInterface) {
    // Register using Kotlin class reference
    FeatureManager.register(INetworkRuleProvider::class, NetworkRuleProvider())
    
    // Or using Java class reference
    FeatureManager.register(INetworkRuleProvider::class.java, NetworkRuleProvider())
}
```

To consume a service from another module:

```kotlin
// In your consumer module
override fun onPackageLoaded(param: PackageLoadedParam) {
    // Get service using Kotlin class reference
    val provider = FeatureManager.get(INetworkRuleProvider::class)
    
    // Or using Java class reference
    val provider = FeatureManager.get(INetworkRuleProvider::class.java)
    
    // Use the service if available
    provider?.getRules()?.forEach { rule ->
        applyNetworkRule(rule)
    }
}
```

### Feature Declaration and Discovery

To declare features your module provides:

```json
// In module-info.json
{
  "id": "network-guard",
  "version": "1.0.0",
  "features": [
    "network.inspection",
    "network.blocking",
    "logging.traffic"
  ]
}
```

To check for feature availability:

```kotlin
// Check if a feature is available
if (FeatureManager.hasFeature("network.blocking")) {
    // Use network blocking functionality
    setupNetworkBlockingHooks()
}
```

### Extension Points

To define an extension point:

```json
// In module-info.json
{
  "id": "network-guard",
  "version": "1.0.0",
  "extensions": {
    "network.rule.provider": "com.example.networkguard.NetworkRuleProvider",
    "network.traffic.analyzer": "com.example.networkguard.TrafficAnalyzer"
  }
}
```

To discover extensions:

```kotlin
// Get all implementations for an extension point
val analyzers = FeatureManager.getExtensions("network.traffic.analyzer")
analyzers.forEach { analyzerClass ->
    val analyzer = analyzerClass.getDeclaredConstructor().newInstance() as ITrafficAnalyzer
    analyzer.analyze(trafficData)
}
```

## Thread Safety Guarantees

The FeatureManager is designed for multi-threaded environments with the following guarantees:

1. **Thread-safe registration**: Service registration is atomic and visible to all threads
2. **Immutable views**: Lists of features and extensions are immutable snapshots
3. **Concurrent access**: Multiple modules can register and retrieve services concurrently
4. **No deadlocks**: All operations are non-blocking and designed to prevent deadlocks
5. **Memory visibility**: Changes are immediately visible to all threads

These guarantees are implemented using `ConcurrentHashMap` and immutable collections, making the FeatureManager safe to use in any module, regardless of threading model.

## Lifecycle Management

### Service Lifespan

Services registered with the FeatureManager live for the duration of the LSPosed framework process. They are:

1. **Created** when the provider module is initialized
2. **Registered** during the module's `initialize()` method
3. **Available** to all modules throughout the application lifecycle
4. **Destroyed** when the LSPosed framework is unloaded (typically on reboot)

There is no explicit unregistration API, as services are expected to live for the entire process lifetime.

### Hot-Reload Considerations

During hot-reload, the service registry is preserved. This means:

1. **Existing services remain**: Services registered before hot-reload remain available
2. **Implementations update**: Service implementations are updated when their module is reloaded
3. **References preserved**: Other modules can keep references to services across hot-reloads

For stateful services, implement proper state handling in the `onHotReload()` method:

```kotlin
@HotReloadable
class StatefulService : IService, IHotReloadable {
    private var state: Any? = null
    
    override fun onHotReload() {
        // Preserve important state or release resources
        saveState()
    }
}
```

## Best Practices

### Interface Design

When creating service interfaces:

1. **Keep interfaces small and focused**: One interface per responsibility
2. **Use immutable data types**: Return immutable collections and data objects
3. **Design for extension**: Include version or capability methods
4. **Handle failure gracefully**: Define error handling strategies
5. **Document thread safety**: Clearly state threading expectations

Example:

```kotlin
/**
 * Provides network traffic rules.
 * Thread-safe: All methods can be called from any thread.
 */
interface INetworkRuleProvider {
    /**
     * Gets the current set of network rules.
     * @return An immutable list of rules
     */
    fun getRules(): List<NetworkRule>
    
    /**
     * Gets the version of the rule provider.
     * @return Version identifier
     */
    fun getVersion(): String
    
    /**
     * Checks if this provider supports a specific rule type.
     * @param ruleType The rule type to check
     * @return true if supported
     */
    fun supportsRuleType(ruleType: String): Boolean
}
```

### Registration Patterns

For optimal service registration:

1. **Register early**: Register services during module initialization
2. **Use interfaces**: Register interface types, not implementations
3. **Singleton pattern**: Use singleton or object instances for services
4. **Lazy initialization**: Initialize expensive resources on first use
5. **Fail gracefully**: Handle registration failures without crashing

### Consumption Patterns

For consuming services safely:

1. **Handle absence**: Always check for null when getting services
2. **Cache conservatively**: Don't cache service references indefinitely
3. **Verify capabilities**: Check for specific features before using them
4. **Fallback mechanisms**: Provide alternatives when services are unavailable
5. **Fail gracefully**: Handle service failures without crashing

## Example: Cross-Module Communication

### Provider Module: Rule System

```kotlin
// Interface definition
interface IRuleProvider {
    fun getRules(): List<Rule>
}

// Implementation
class NetworkRuleProvider : IRuleProvider {
    override fun getRules(): List<Rule> {
        return listOf(
            Rule("BLOCK", "bad.example.com"),
            Rule("ALLOW", "good.example.com")
        )
    }
}

// Registration
class RuleModule : IModulePlugin {
    override fun initialize(context: Context, xposed: XposedInterface) {
        FeatureManager.register(IRuleProvider::class.java, NetworkRuleProvider())
    }
}
```

### Consumer Module: Enforcer

```kotlin
class EnforcerModule : IModulePlugin {
    private var ruleProvider: IRuleProvider? = null
    
    override fun initialize(context: Context, xposed: XposedInterface) {
        // Discover the rule provider
        ruleProvider = FeatureManager.get(IRuleProvider::class.java)
    }
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Use the rules if available
        val rules = ruleProvider?.getRules() ?: emptyList()
        
        // Apply the rules
        if (rules.isNotEmpty()) {
            applyRules(param, rules)
        } else {
            // Fallback to default rules if no provider available
            applyDefaultRules(param)
        }
    }
}
```

## Advanced: Custom Service Registry

For advanced use cases, you can create module-specific service registries:

```kotlin
class ModuleServiceRegistry {
    private val services = ConcurrentHashMap<Class<*>, Any>()
    
    fun <T : Any> register(serviceClass: Class<T>, implementation: T) {
        services[serviceClass] = implementation
    }
    
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(serviceClass: Class<T>): T? {
        return services[serviceClass] as? T
    }
    
    fun clear() {
        services.clear()
    }
}
```

## Debugging Service Issues

### Logging Service Registration

To debug service registration and discovery:

```kotlin
// When registering
FeatureManager.register(INetworkRuleProvider::class.java, provider)
xposed.log(LogLevel.INFO, "Registered NetworkRuleProvider")

// When discovering
val provider = FeatureManager.get(INetworkRuleProvider::class.java)
if (provider != null) {
    xposed.log(LogLevel.INFO, "Found NetworkRuleProvider")
} else {
    xposed.log(LogLevel.WARN, "NetworkRuleProvider not found")
}
```

### Inspecting Available Services

You can create a debug module to inspect all registered services:

```kotlin
class ServiceInspectorModule : IModulePlugin {
    override fun initialize(context: Context, xposed: XposedInterface) {
        // Register a command to dump services
        FeatureManager.register(IDebugCommand::class.java, DumpServicesCommand())
    }
    
    class DumpServicesCommand : IDebugCommand {
        override fun execute(params: List<String>): String {
            // This would require internal access to FeatureManager
            return "Registered services: ..."
        }
    }
}
```

## Performance Considerations

The FeatureManager is designed for high performance with minimal overhead:

1. **Constant-time lookups**: Service retrieval is O(1)
2. **No reflection at runtime**: Service casting uses type erasure
3. **Minimal locking**: Uses lock-free data structures where possible
4. **Small memory footprint**: Stores only references to services
5. **Zero serialization**: Services are passed by reference

## Security Considerations

When using the FeatureManager, consider these security aspects:

1. **Trust boundaries**: All modules with access to FeatureManager can register and retrieve services
2. **No sandboxing**: Services run with the same privileges as the calling module
3. **No validation**: The framework doesn't validate service implementations
4. **No isolation**: A misbehaving service can affect other modules

Design your services with these considerations in mind, and assume all module code runs in the same trust boundary.

## Common Patterns

### Observer Pattern

```kotlin
// Event interface
interface NetworkEvent {
    val url: String
    val timestamp: Long
}

// Observer interface
interface NetworkObserver {
    fun onNetworkEvent(event: NetworkEvent)
}

// Subject
class NetworkMonitor {
    private val observers = mutableListOf<NetworkObserver>()
    
    fun addObserver(observer: NetworkObserver) {
        observers.add(observer)
    }
    
    fun notifyEvent(event: NetworkEvent) {
        observers.forEach { it.onNetworkEvent(event) }
    }
}

// Registration
FeatureManager.register(NetworkMonitor::class.java, NetworkMonitor())

// Observer module
val monitor = FeatureManager.get(NetworkMonitor::class.java)
monitor?.addObserver(MyNetworkObserver())
```

### Factory Pattern

```kotlin
// Factory interface
interface RuleFactory {
    fun createRule(type: String, pattern: String): Rule
}

// Registration
FeatureManager.register(RuleFactory::class.java, DefaultRuleFactory())

// Usage
val factory = FeatureManager.get(RuleFactory::class.java)
val rule = factory?.createRule("BLOCK", "example.com") ?: DefaultRule()
```

### Strategy Pattern

```kotlin
// Strategy interface
interface TrafficAnalysisStrategy {
    fun analyze(data: ByteArray): AnalysisResult
}

// Registration
FeatureManager.register(TrafficAnalysisStrategy::class.java, DeepPacketInspection())

// Usage
val strategy = FeatureManager.get(TrafficAnalysisStrategy::class.java) ?: BasicAnalyzer()
val result = strategy.analyze(packetData)
```

## Conclusion

The FeatureManager service bus is a powerful tool for building modular, extensible LSPosed modules. By following the patterns and best practices outlined in this guide, you can create modules that:

1. **Cooperate** with other modules without tight coupling
2. **Extend** their functionality through plugin architecture
3. **Discover** capabilities at runtime
4. **Communicate** safely across module boundaries
5. **Scale** to complex multi-module systems

This architectural approach enables a rich ecosystem of interoperable modules that can be combined in powerful ways. 