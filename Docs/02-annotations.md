# LSPosedKit Annotations Reference

> Complete guide to LSPosedKit's annotation system that eliminates boilerplate configuration files and enables compile-time validation.

## Core Annotations Overview

LSPosedKit uses annotations to replace traditional configuration files, validate module structure, generate metadata, and enable features like hot-reload and settings binding. This approach provides several advantages:

- **Compile-time validation** of module metadata
- **Type safety** for settings and configuration
- **Code generation** for boilerplate reduction
- **Cleaner codebase** with metadata directly in code
- **IDE support** with autocompletion and documentation

## @XposedPlugin

The primary annotation that defines your module and its metadata. Applied to your main module class that implements `IModulePlugin`.

### Parameters

| Parameter       | Type        | Required | Description                                       | Example                     |
|-----------------|-------------|----------|---------------------------------------------------|----------------------------|
| `id`            | String      | Yes      | Unique identifier in kebab-case                   | `"network-guard"`          |
| `name`          | String      | Yes      | Human-readable display name                       | `"Network Guard"`          |
| `version`       | String      | Yes      | SemVer format (major.minor.patch[-label])         | `"1.2.3"` or `"1.2.3-beta.1"` |
| `scope`         | String[]    | Yes      | Target package names to hook                      | `["com.android.chrome"]` or `["*"]` |
| `description`   | String      | Yes      | Module description (shown in LSPosed Manager)     | `"Blocks suspicious network connections"` |
| `author`        | String      | No       | Module author/maintainer                          | `"Jane Developer"`         |

### Full Example

```kotlin
import com.wobbz.framework.core.IModulePlugin
import com.wobbz.framework.processor.XposedPlugin

@XposedPlugin(
    id = "network-guard",
    name = "Network Guard",
    version = "2.1.0",
    scope = ["com.android.chrome", "com.android.vending", "org.mozilla.firefox"],
    description = "Monitors and blocks suspicious network connections",
    author = "Jane Developer"
)
class NetworkGuard : IModulePlugin {
    // Implementation...
}
```

### Best Practices

1. **ID Convention**: Use kebab-case (lowercase with hyphens) for `id`
2. **Versioning**: Follow [SemVer](https://semver.org/) for `version` field
3. **Scope Targeting**:
   - Use `["*"]` for system-wide hooks
   - List specific packages for targeted modules
   - Avoid targeting too many packages unnecessarily
4. **Description**: Keep concise but informative (~80 chars)

## @HotReloadable

Marks a module class as supporting hot-reload capability, enabling live code updates without device reboot.

### Requirements

- Class must implement `IHotReloadable` interface
- Must provide `onHotReload()` implementation
- Should maintain references to hooks for proper unhooking

### Example

```kotlin
import com.wobbz.framework.core.IModulePlugin
import com.wobbz.framework.hot.IHotReloadable
import com.wobbz.framework.processor.HotReloadable
import com.wobbz.framework.processor.XposedPlugin

@XposedPlugin(
    id = "notification-control",
    name = "Notification Control",
    version = "1.0.0",
    scope = ["android", "com.android.systemui"],
    description = "Blocks unwanted notifications"
)
@HotReloadable
class NotificationControl : IModulePlugin, IHotReloadable {
    private val hooks = mutableListOf<XposedInterface.MethodUnhooker<*>>()
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        val notificationManager = param.xposed.loadClass("android.app.NotificationManager")
        val notify = notificationManager.getDeclaredMethod(
            "notify", 
            String::class.java, 
            Int::class.javaPrimitiveType, 
            Class.forName("android.app.Notification")
        )
        hooks += param.xposed.hook(notify, NotificationBlockHooker::class.java)
    }
    
    override fun onHotReload() {
        // Clean up existing hooks
        hooks.forEach { it.unhook() }
        hooks.clear()
        // New hooks will be applied when onPackageLoaded is called again
    }
    
    class NotificationBlockHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            // Notification blocking logic
        }
    }
}
```

### Hot-Reload Best Practices

1. **Store hook references** in a collection for cleanup
2. **Unhook all methods** in `onHotReload()`
3. **Clear references** after unhooking
4. **Keep stateless** when possible, or implement state persistence
5. **Test hot-reload** frequently during development

## @SettingsKey

Maps class properties to settings values, enabling type-safe access to module preferences.

### Parameters

| Parameter | Type   | Required | Description                           | Default     |
|-----------|--------|----------|---------------------------------------|-------------|
| `value`   | String | Yes      | Key name in settings store            | (none)      |

### Usage Patterns

#### 1. Direct Property Annotation

```kotlin
class NetworkGuardSettings {
    @SettingsKey("enableGuard")
    val isEnabled: Boolean = true
    
    @SettingsKey("maxConnections")
    val maxConnections: Int = 100
    
    @SettingsKey("blockedHosts")
    val blockedHosts: List<String> = listOf()
}
```

#### 2. Constructor Parameter Annotation

```kotlin
class NetworkGuardPrefs(
    @SettingsKey("enableGuard") val isEnabled: Boolean,
    @SettingsKey("maxConnections") val maxConnections: Int,
    @SettingsKey("blockedHosts") val blockedHosts: List<String>
)
```

#### 3. Accessing Settings

```kotlin
// Using the generated settings provider
val settings = SettingsProvider.of(context)
val isEnabled = settings.bool("enableGuard")
val maxConns = settings.int("maxConnections")
val blockedHosts = settings.stringList("blockedHosts")

// Using the annotated class (automatic binding)
val guardSettings = settings.bind(NetworkGuardSettings::class.java)
if (guardSettings.isEnabled) {
    applyBlockRules(guardSettings.blockedHosts)
}
```

### Supported Types

| Kotlin/Java Type | Settings JSON Type | Access Method       |
|------------------|-------------------|---------------------|
| `Boolean`        | boolean          | `bool(key)`         |
| `Int`            | integer          | `int(key)`          |
| `Long`           | integer          | `long(key)`         |
| `Float`          | number           | `float(key)`        |
| `Double`         | number           | `double(key)`       |
| `String`         | string           | `string(key)`       |
| `List<String>`   | array of strings | `stringList(key)`   |
| `List<Int>`      | array of integers| `intList(key)`      |
| `Set<String>`    | array of strings | `stringSet(key)`    |
| `Map<String, *>` | object           | `map(key)`          |

## @Generated Annotations

LSPosedKit internally generates several annotations for module processing:

### @Generated

Marks code as generated by LSPosedKit's annotation processor.

### @ModuleInfo

Contains serialized module metadata generated from `@XposedPlugin`.

## Advanced: Custom Annotations

You can create custom annotations for your modules by leveraging LSPosedKit's annotation processor.

### Example: Creating a Permission Annotation

```kotlin
// Define the annotation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class RequiresPermissions(vararg val permissions: String)

// Create a processor extension
class PermissionProcessor : AbstractProcessor() {
    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(RequiresPermissions::class.java).forEach { element ->
            val permissions = element.getAnnotation(RequiresPermissions::class.java).permissions
            // Process permissions...
        }
        return true
    }
}
```

## Integration with IDEs

LSPosedKit annotations are fully integrated with IDE tooling:

- **Code completion** for annotation parameters
- **Error highlighting** for invalid values
- **Quick documentation** for annotation details
- **Navigation** to annotation definitions
- **Refactoring support** for renames and moves

## Notes for Java Users

While Kotlin is recommended, Java is fully supported:

```java
@XposedPlugin(
    id = "network-guard",
    name = "Network Guard",
    version = "1.0.0",
    scope = {"com.android.chrome", "org.mozilla.firefox"},
    description = "Monitors and blocks suspicious network connections",
    author = "John Developer"
)
@HotReloadable
public class NetworkGuard implements IModulePlugin, IHotReloadable {
    // Implementation...
}
``` 