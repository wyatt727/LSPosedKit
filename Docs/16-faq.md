# Frequently Asked Questions

> Quick answers to common questions about LSPosedKit, its features, and how to solve typical problems.

## General Questions

### What is LSPosedKit?

LSPosedKit is a development toolkit for building, testing, and hot-reloading LSPosed modules on Android 15 and newer. It provides a modern, zero-boilerplate approach to Xposed module development with features like annotation-driven configuration, hot-reload capabilities, and type-safe settings.

### What Android versions are supported?

- **Primary target**: Android 15 (API 35)
- **Fully supported**: Android 14 (API 34) and Android 13 (API 33)
- **Compatible with limitations**: Android 12/12L (API 31-32)
- **Not supported**: Android 11 and below

### What's the minimum setup required to start developing?

- Rooted Android 15 device with LSPosed Manager v1.9.0+
- JDK 17 (Eclipse Temurin recommended)
- Android Studio Hedgehog Canary 15 or newer (or Cursor)
- Gradle 8.4+ with Android Gradle Plugin 8.3+

### How is LSPosedKit different from traditional LSPosed/Xposed development?

| Traditional Xposed Development | LSPosedKit |
|-------------------------------|------------|
| Manual `module.prop` and `xposed_init` files | Annotation-based module metadata (`@XposedPlugin`) |
| Requires device reboot for testing changes | Hot-reload feature for instant code updates |
| Manual preference UI implementation | Automatic UI generation from `settings.json` schema |
| Manual dependency management | Built-in module dependency resolution with SemVer |
| Isolated modules | Service bus for cross-module communication |

## Getting Started

### How do I create a new module?

```bash
# Option 1: Use the scaffold script (recommended)
./gradlew :scripts:newModule -PmoduleName="MyModule" -Pid="my-module"

# Option 2: Manual approach
mkdir -p modules/my-module/src/main/{java,assets,res}
# Then create the necessary build.gradle and source files
```

### Why doesn't my module show up in LSPosed Manager?

Check the following:
1. Ensure the LSPK Host module is enabled in LSPosed Manager
2. Verify that your module has a proper `@XposedPlugin` annotation
3. Make sure your module was built and installed successfully
4. Check for build errors in the Gradle output
5. Look for initialization errors in logcat: `adb logcat -s LSPK:*`

### How do I enable logging for my module?

```kotlin
// In your module code
override fun initialize(context: Context, xposed: XposedInterface) {
    xposed.log(LogLevel.INFO, "Module initialized")
}

// To view logs
adb logcat -s "LSPK-YourModuleId:*" -v time
```

## Hot-Reload

### How does hot-reload work?

LSPosedKit's hot-reload leverages Android 15's DexPatch API to update module code without rebooting. When you make changes to your code and rebuild, LSPosedKit:

1. Compiles your changes into a new DEX file
2. Pushes the DEX to the device
3. Applies the patch to the running process
4. Calls your module's `onHotReload()` method
5. Allows your module to unhook existing hooks and apply updated ones

### Why isn't hot-reload working for my module?

Check these common issues:
1. Ensure your class is annotated with `@HotReloadable`
2. Verify your class implements the `IHotReloadable` interface
3. Check that the hot-reload server is running: `./gradlew runDevServer`
4. Look for hot-reload errors in logcat: `adb logcat -s LSPK-HotReload:*`
5. Confirm ADB connectivity with your device

### Do I need to restart the target app after hot-reloading?

No, that's the beauty of hot-reload! Your code changes take effect immediately without restarting the target app or rebooting the device. However, if your changes modify the app's state in a way that requires initialization, you may need to restart the specific app.

## Module Development

### How do I hook into a specific application?

Specify the target package(s) in your `@XposedPlugin` annotation:

```kotlin
@XposedPlugin(
    id = "my-module",
    name = "My Module",
    version = "1.0.0",
    scope = ["com.android.chrome", "com.android.settings"],
    description = "Enhances Chrome and Settings apps"
)
```

Use `["*"]` to target all applications (global scope).

### How do I access user preferences in my module?

```kotlin
// Option 1: Direct access
val settings = SettingsProvider.of(context)
val isEnabled = settings.bool("enableFeature", defaultValue = true)
val userName = settings.string("userName", defaultValue = "")

// Option 2: Using annotated class (recommended)
class MySettings {
    @SettingsKey("enableFeature") val isEnabled: Boolean = true
    @SettingsKey("userName") val userName: String = ""
}

val mySettings = settings.bind(MySettings::class.java)
if (mySettings.isEnabled) {
    // Use the settings
}
```

### How do I make modules communicate with each other?

Use the FeatureManager service bus:

```kotlin
// Provider module
interface IDataProvider {
    fun getData(): List<String>
}

class MyProviderModule : IModulePlugin, IDataProvider {
    override fun initialize(context: Context, xposed: XposedInterface) {
        FeatureManager.register(IDataProvider::class.java, this)
    }
    
    override fun getData(): List<String> = listOf("data1", "data2")
}

// Consumer module
class MyConsumerModule : IModulePlugin {
    override fun initialize(context: Context, xposed: XposedInterface) {
        val provider = FeatureManager.get(IDataProvider::class.java)
        provider?.getData()?.forEach { data ->
            // Use the data
        }
    }
}
```

### How do I properly handle hooks for hot-reload?

```kotlin
@HotReloadable
class MyModule : IModulePlugin, IHotReloadable {
    private val hooks = mutableListOf<XposedInterface.MethodUnhooker<*>>()
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Store all hooks in the hooks list
        hooks += param.xposed.hook(/* ... */)
    }
    
    override fun onHotReload() {
        // Clean up existing hooks
        hooks.forEach { it.unhook() }
        hooks.clear()
    }
}
```

## Troubleshooting

### My module crashes the target application. How do I debug this?

1. Check the logcat for exceptions: `adb logcat -s "LSPK-YourModuleId:*" -v time`
2. Ensure proper error handling in your hooks:
   ```kotlin
   try {
       // Your hook logic
   } catch (e: Throwable) {
       xposed.logError("Hook failed", e)
   }
   ```
3. Use the hot-reload feature to quickly test fixes without rebooting
4. Temporarily disable other modules to isolate the issue

### Why can't I find certain classes or methods in the target app?

Possible reasons:
1. The class may be obfuscated (ProGuard/R8)
2. The method might be in a different class in the app version you're testing
3. The API level might be different from what you're expecting

Solutions:
1. Use reflection and error handling to gracefully handle missing classes
2. Check the app's DEX with tools like `jadx` or `dexdump` to find obfuscated names
3. Use `try/catch` when loading classes and methods

### How do I fix "ClassNotFoundException" or "NoSuchMethodException"?

These exceptions typically occur when:
1. The class or method doesn't exist in the target app version
2. The class path is incorrect
3. The method signature doesn't match exactly

Try:
1. Double-check class and method names
2. Verify parameter types and return types
3. Add fallback logic for different API versions:
   ```kotlin
   try {
       val cls = xposed.loadClass("com.example.TargetClass")
       // Use the class
   } catch (e: ClassNotFoundException) {
       // Fallback or log the error
       xposed.logError("Target class not found", e)
   }
   ```

### How do I debug a module that doesn't load at all?

1. Check LSPosed Manager to ensure the module is enabled
2. Look for initialization errors in logcat: `adb logcat -s LSPK:* LSPK-Host:*`
3. Verify your module's scope includes the target applications
4. Check for exceptions during the module loading process
5. Ensure your module class is properly annotated with `@XposedPlugin`

## Packaging and Distribution

### How do I create a release version of my module?

```bash
# Build a release version
./gradlew :modules:my-module:assembleRelease

# Create a distribution bundle
./gradlew :modules:my-module:publishBundle
```

The `.lspkmod` bundle will be generated in the `dist/` directory.

### How do I sign my module for distribution?

1. Configure a signing key in your `gradle.properties`:
   ```properties
   signing.keyAlias=upload
   signing.keyPassword=your_key_password
   signing.storeFile=/path/to/keystore.jks
   signing.storePassword=your_store_password
   ```

2. Build with signing enabled:
   ```bash
   ./gradlew :modules:my-module:publishBundle -PuseReleaseKeystore=true
   ```

### Can I publish my module to a repository?

Yes, LSPosedKit modules can be distributed through:
1. GitHub Releases (recommended)
2. LSPosed module repository (if available)
3. F-Droid (requires additional setup)
4. Your own website or distribution channel

Use the `.lspkmod` bundle format for best compatibility.

## Advanced Usage

### How do I update my module's settings schema?

1. Modify your `settings.json` file in `src/main/assets/`
2. Add migration code if needed:
   ```kotlin
   override fun initialize(context: Context, xposed: XposedInterface) {
       val settings = SettingsProvider.of(context)
       val schemaVersion = settings.int("schemaVersion", 0)
       
       if (schemaVersion < 1) {
           // Migrate from v0 to v1
           val oldValue = settings.string("oldKey")
           settings.edit().putString("newKey", oldValue).apply()
           settings.edit().putInt("schemaVersion", 1).apply()
       }
   }
   ```

### How do I implement a custom settings UI?

1. Create a custom settings activity:
   ```kotlin
   class MySettingsActivity : AppCompatActivity() {
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContentView(R.layout.activity_settings)
           // Implement your UI
       }
   }
   ```

2. Register it in `module-info.json`:
   ```json
   {
     "id": "my-module",
     "version": "1.0.0",
     "customSettingsActivity": "com.example.MySettingsActivity"
   }
   ```

### How do I support multiple Android versions in my module?

Use conditional logic based on the Android version:

```kotlin
override fun onPackageLoaded(param: PackageLoadedParam) {
    val sdkVersion = Build.VERSION.SDK_INT
    
    if (sdkVersion >= Build.VERSION_CODES.TIRAMISU) {
        // Android 13+ implementation
        hookTiramisuApi(param)
    } else if (sdkVersion >= Build.VERSION_CODES.S) {
        // Android 12 implementation
        hookSApi(param)
    } else {
        // Fallback for older versions
        hookLegacyApi(param)
    }
}
```

## Performance and Best Practices

### How do I optimize my module for performance?

1. **Hook selectively**: Only hook the methods you need
2. **Avoid heavy processing in hooks**: Hooks are called frequently
3. **Cache results**: Store calculated values instead of recomputing
4. **Lazy initialization**: Initialize resources only when needed
5. **Clean up resources**: Release resources in `onHotReload()`
6. **Use appropriate scopes**: Target only relevant packages

### What are the best practices for module development?

1. **Test thoroughly**: Test on different devices and Android versions
2. **Handle errors gracefully**: Catch exceptions in hooks
3. **Document your code**: Add comments explaining complex hooks
4. **Follow naming conventions**: Use consistent naming patterns
5. **Respect privacy**: Only collect data that's necessary
6. **Be transparent**: Clearly document what your module does
7. **Provide fallbacks**: Handle cases where hooks can't be applied

### How do I minimize battery impact?

1. **Avoid polling**: Don't use timers or frequent background checks
2. **Optimize hook frequency**: Only hook high-level methods when possible
3. **Release resources**: Don't hold references unnecessarily
4. **Reduce logging**: Minimize verbose logging in production
5. **Be event-driven**: React to system events rather than checking constantly

## LSPosedKit Internals

### How does module discovery work?

LSPosedKit uses the Java ServiceLoader mechanism enhanced with annotation processing:
1. The `@XposedPlugin` annotation is processed at compile time
2. The processor generates a ServiceLoader entry in `META-INF/services/`
3. At runtime, the LSPosedKit host discovers all modules via ServiceLoader
4. Each module is initialized based on its metadata

### How are module dependencies resolved?

1. Each module's `module-info.json` declares its dependencies
2. At load time, LSPosedKit validates the dependency graph:
   - Checks that required modules are present
   - Verifies version constraints using SemVer rules
   - Detects and reports circular dependencies
3. Modules are initialized in dependency order
4. Optional dependencies are used if available but not required

### Can I extend LSPosedKit itself?

Yes, LSPosedKit is designed to be extensible:
1. Create custom annotation processors by extending `XposedPluginProcessor`
2. Implement service interfaces for the FeatureManager
3. Develop plugins for the build system by extending Gradle tasks
4. Contribute to the framework codebase via pull requests 