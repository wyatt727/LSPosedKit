# Troubleshooting Guide

> A comprehensive reference of common issues, error messages, debugging techniques, and solutions for LSPosedKit module development.

## Common Error Categories

This guide is organized by the stage at which errors typically occur:

1. [Build & Compilation Errors](#build--compilation-errors)
2. [Module Loading Errors](#module-loading-errors)
3. [Hooking Errors](#hooking-errors)
4. [Hot-Reload Errors](#hot-reload-errors)
5. [Settings Errors](#settings-errors)
6. [Performance Issues](#performance-issues)
7. [Compatibility Issues](#compatibility-issues)
8. [Deployment Issues](#deployment-issues)

Each section includes typical error messages, debugging steps, and solutions.

## Build & Compilation Errors

### Annotation Processor Errors

#### Error: "Could not find @XposedPlugin annotation on main class"

**Cause**: The module's main class is missing the required `@XposedPlugin` annotation.

**Debug Steps**:
1. Check that your main class has the `@XposedPlugin` annotation
2. Verify all required annotation parameters are provided

**Solution**:
```kotlin
@XposedPlugin(
    id = "debug-app",               // Required
    name = "Debug App",             // Required
    version = "1.0.0",              // Required
    scope = ["com.android.settings"], // Required
    description = "Description"     // Required
)
class MyModule : IModulePlugin {
    // Implementation
}
```

#### Error: "Duplicate module ID found: 'debug-app'"

**Cause**: Multiple modules with the same ID in the project.

**Debug Steps**:
1. Search for `@XposedPlugin` annotations across the project
2. Check for duplicate `id` values

**Solution**: Ensure each module has a unique ID.

### Gradle Build Errors

#### Error: "Could not resolve project :framework"

**Cause**: Missing dependency on the LSPosedKit framework.

**Debug Steps**:
1. Check your module's `build.gradle` file
2. Verify the framework dependency is correctly configured

**Solution**:
```groovy
dependencies {
    implementation project(':framework')
    kapt project(':framework:processor')
}
```

#### Error: "Module created with wrong name (using project name instead of -Pname parameter)"

**Cause**: Gradle property conflicts where built-in project properties override command-line `-P` parameters.

**Symptoms**:
- Running `./gradlew :scripts:newModule -Pname="MyModule"` creates a module named "scripts" or "LSPosedKit"
- The `-Pname` parameter appears to be ignored

**Debug Steps**:
1. Check if you're using `project.findProperty('name')` in Gradle scripts
2. Verify that `gradle.startParameter.projectProperties` contains your parameters
3. Look for conflicts with built-in Gradle properties like `project.name`

**Root Cause**: 
Gradle's built-in `project.name` property (which equals the project/subproject name) conflicts with command-line properties. When using `project.findProperty('name')` or `rootProject.hasProperty('name')`, Gradle finds the built-in property instead of the `-P` parameter.

**Solution**:
Use `gradle.startParameter.projectProperties` to access command-line properties directly:

```groovy
// ❌ WRONG - conflicts with built-in project.name
def moduleName = project.findProperty('name') ?: rootProject.findProperty('name')

// ✅ CORRECT - accesses command-line properties directly
def projectProperties = gradle.startParameter.projectProperties
def moduleName = null
if (projectProperties.containsKey('name')) {
    moduleName = projectProperties.get('name')
}
```

This approach avoids conflicts with Gradle's built-in project properties and ensures `-P` parameters are handled correctly.

#### Error: "Execution failed for task ':modules:mymodule:generateModuleInfo'"

**Cause**: Invalid configuration for the module info generation task.

**Debug Steps**:
1. Check the module's `build.gradle` file
2. Look for errors in the module info generation task
3. Verify the JSON structure is valid

**Solution**: Fix the module info generation task or manually create a valid `module-info.json` file.

### Kotlin Errors

#### Error: "Type mismatch: inferred type is XC_MethodHook but Hooker was expected"

**Cause**: Using traditional Xposed API classes instead of LSPosedKit equivalents.

**Debug Steps**:
1. Search for legacy Xposed API usage
2. Check for import statements with `de.robv.android.xposed`

**Solution**: Replace legacy Xposed API usage with LSPosedKit equivalents.

## Module Loading Errors

### LSPosed Manager Errors

#### Error: "Module installation failed"

**Cause**: Issues with the module APK or system permissions.

**Debug Steps**:
1. Check LSPosed Manager logs
2. Verify the module APK is valid
3. Check if you have the necessary permissions

**Solution**:
- Rebuild the module APK
- Check the LSPosed Manager version compatibility
- Verify the module is targeting a compatible Android version

#### Error: "Module not activated for target package"

**Cause**: The module scope doesn't include the target package or it's not activated in LSPosed Manager.

**Debug Steps**:
1. Check module scope in `@XposedPlugin` annotation
2. Verify the module is activated for the target package in LSPosed Manager

**Solution**:
- Add the target package to the module scope
- Activate the module for the target package in LSPosed Manager

### Runtime Loading Errors

#### Error: "ClassNotFoundException: com.example.mymodule.MyModule"

**Cause**: The module class cannot be found by the class loader.

**Debug Steps**:
1. Check the class name and package
2. Verify the class is correctly defined in your code
3. Check for ProGuard/R8 optimization issues

**Solution**:
- Fix class name or package
- Add ProGuard rules to preserve your module class
```proguard
-keep @com.wobbz.framework.processor.XposedPlugin class * {*;}
-keep class * implements com.wobbz.framework.core.IModulePlugin {*;}
```

#### Error: "Module 'debug-app' failed to initialize: java.lang.NoSuchMethodError"

**Cause**: Method signature mismatch between expected and actual implementation.

**Debug Steps**:
1. Check the method signature in your module class
2. Verify you're implementing the correct interface methods
3. Check for API compatibility issues

**Solution**:
- Fix method signatures to match the expected interfaces
- Update to a compatible framework version

## Hooking Errors

### Method/Field Hook Errors

#### Error: "NoSuchMethodError: No virtual method X found in class Y"

**Cause**: The method you're trying to hook doesn't exist or has a different signature.

**Debug Steps**:
1. Verify the method name and signature
2. Check if the method is private (requires `getDeclaredMethod()`)
3. Use `param.xposed.log()` to print available methods in the class

**Solution**:
```kotlin
try {
    // List all methods in the class to find the correct one
    val clazz = param.xposed.loadClass("com.example.TargetClass")
    val methods = clazz.declaredMethods
    methods.forEach { method ->
        param.xposed.log(LogLevel.INFO, "Found method: ${method.name} with params: ${method.parameterTypes.joinToString()}")
    }
    
    // Use the correct method signature
    val method = clazz.getDeclaredMethod("targetMethod", String::class.java, Int::class.javaPrimitiveType)
    hooks += param.xposed.hook(method, MyHooker::class.java)
} catch (e: Exception) {
    param.xposed.logError("Hook failed", e)
}
```

#### Error: "IllegalAccessError: Class com.example.mymodule.MyModule cannot access private method"

**Cause**: Attempting to access a private method without making it accessible.

**Debug Steps**:
1. Check if the method is private
2. Verify reflection access is properly configured

**Solution**:
```kotlin
val method = clazz.getDeclaredMethod("privateMethod", String::class.java)
method.isAccessible = true
hooks += param.xposed.hook(method, MyHooker::class.java)
```

### Hooker Implementation Errors

#### Error: "ClassCastException: Cannot cast Object to X"

**Cause**: Incorrect type casting in hook implementations.

**Debug Steps**:
1. Check the types in your hooker methods
2. Verify the actual type of objects at runtime
3. Add logging to print object types

**Solution**:
```kotlin
class MyHooker : Hooker {
    override fun beforeHook(param: HookParam) {
        try {
            // Print the actual type
            param.xposed.log(LogLevel.INFO, "thisObject type: ${param.thisObject?.javaClass?.name}")
            
            // Use safe cast with nullable type
            val targetObject = param.thisObject as? TargetClass
            if (targetObject != null) {
                // Use the object
            } else {
                param.xposed.log(LogLevel.ERROR, "Expected TargetClass but got ${param.thisObject?.javaClass?.name}")
            }
        } catch (e: Exception) {
            param.xposed.logError("Hook error", e)
        }
    }
}
```

#### Error: "IllegalArgumentException: Hook parameter type mismatch"

**Cause**: The hooker class doesn't implement the correct methods or has incorrect parameter types.

**Debug Steps**:
1. Check the hooker class implementation
2. Verify you're implementing the correct interface methods
3. Check parameter types

**Solution**: Implement the correct hooker interface methods with the proper parameter types.

## Hot-Reload Errors

### Connection Errors

#### Error: "Hot-reload server connection failed"

**Cause**: The development server is not running or is unreachable.

**Debug Steps**:
1. Verify the development server is running (`./gradlew runDevServer`)
2. Check network connectivity between device and development machine
3. Check port forwarding if using an emulator

**Solution**:
- Restart the development server
- Ensure ADB is connected
- Set up proper port forwarding: `adb forward tcp:5795 tcp:5795`

### Reload Errors

#### Error: "Hot-reload failed: unhooking previous hooks failed"

**Cause**: Problems with cleaning up previous hooks during hot-reload.

**Debug Steps**:
1. Check your `onHotReload()` implementation
2. Verify all hooks are being stored and unhooked properly
3. Look for exceptions during hook cleanup

**Solution**:
```kotlin
// Properly store ALL hooks
private val hooks = mutableListOf<XposedInterface.MethodUnhooker<*>>()

override fun onPackageLoaded(param: PackageLoadedParam) {
    // Store hook reference
    hooks += param.xposed.hook(method, MyHooker::class.java)
}

override fun onHotReload() {
    try {
        // Unhook all hooks and clear the list
        hooks.forEach { it.unhook() }
    } catch (e: Exception) {
        // Log error but continue cleanup
        Log.e("MyModule", "Error unhooking", e)
    } finally {
        // Always clear the list
        hooks.clear()
    }
}
```

#### Error: "ClassNotFoundException after hot-reload"

**Cause**: Class definition changes that break compatibility during hot-reload.

**Debug Steps**:
1. Check what changes were made to the class
2. Look for structural changes (added/removed fields, changed interfaces)
3. Check for serialization issues

**Solution**:
- Make minimal changes between hot-reloads
- Restart the app completely after major structural changes
- Use stable class structures for hot-reloadable code

## Settings Errors

### Schema Errors

#### Error: "Invalid settings schema: Expected boolean but got string"

**Cause**: Type mismatch between settings schema and actual settings values.

**Debug Steps**:
1. Check your `settings.json` schema
2. Verify the types match the actual usage in code
3. Look for type conversion issues

**Solution**: Fix the schema to match the expected types.

### Binding Errors

#### Error: "Could not bind setting 'missingKey' to field"

**Cause**: Missing setting key in `settings.json` or mismatched name.

**Debug Steps**:
1. Check your `@SettingsKey` annotations
2. Verify the keys exist in `settings.json`
3. Check for typos in key names

**Solution**:
```kotlin
// Make sure annotation key matches settings.json
class MySettings(
    @SettingsKey("enable_feature") val isEnabled: Boolean = true
)

// In settings.json
{
  "properties": {
    "enable_feature": {  // Must match the annotation key
      "type": "boolean",
      "default": true
    }
  }
}
```

### Persistence Errors

#### Error: "Settings changes not persisted after restart"

**Cause**: Settings not being saved properly or loading from wrong location.

**Debug Steps**:
1. Check the settings storage location
2. Verify write permissions
3. Look for errors during save operations

**Solution**:
```kotlin
// Ensure proper commit of settings changes
val settings = SettingsProvider.of(context)
settings.edit()
    .putBoolean("enable_feature", true)
    .commit() // Use commit() instead of apply() for synchronous operation
```

## Performance Issues

### Memory Leaks

#### Issue: "Increasing memory usage after multiple hot-reloads"

**Cause**: Hooks or objects not being properly cleaned up during hot-reload.

**Debug Steps**:
1. Monitor memory usage during hot-reloads
2. Check for strong references to objects that should be released
3. Look for missing cleanup in `onHotReload()`

**Solution**:
```kotlin
// Avoid strong references to contexts or large objects
class MyModule : IModulePlugin, IHotReloadable {
    // Use weak references for contexts
    private var weakContext: WeakReference<Context>? = null
    
    override fun initialize(context: Context, xposed: XposedInterface) {
        weakContext = WeakReference(context)
    }
    
    override fun onHotReload() {
        // Clear all references
        weakContext = null
        // Clean up other resources
    }
}
```

### Hook Performance

#### Issue: "App becomes slow after module is activated"

**Cause**: Too many hooks or expensive operations in hook callbacks.

**Debug Steps**:
1. Profile the target app with and without the module
2. Identify slow hooks by selectively disabling them
3. Look for expensive operations in frequently called hooks

**Solution**:
```kotlin
// Optimize hook performance
class OptimizedHooker : Hooker {
    // Cache frequently used data
    companion object {
        private val cache = ConcurrentHashMap<String, Any>()
    }
    
    override fun beforeHook(param: HookParam) {
        // Avoid expensive operations in frequently called hooks
        val key = "some_key"
        val value = cache.getOrPut(key) {
            // This expensive operation only runs once
            computeExpensiveValue()
        }
        
        // Use the cached value
    }
}
```

## Compatibility Issues

### Android Version Compatibility

#### Issue: "Module works on Android 13 but not on Android 14"

**Cause**: API changes between Android versions.

**Debug Steps**:
1. Check for API differences between Android versions
2. Look for changes in the classes/methods you're hooking
3. Test on both versions to identify differences

**Solution**:
```kotlin
// Handle API differences
override fun onPackageLoaded(param: PackageLoadedParam) {
    val xposed = param.xposed
    val androidVersion = Build.VERSION.SDK_INT
    
    try {
        if (androidVersion >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            // Use Android 14+ specific approach
            val newMethod = xposed.loadClass("com.example.Target").getDeclaredMethod("newMethod")
            hooks += xposed.hook(newMethod, NewMethodHooker::class.java)
        } else {
            // Use approach for older Android versions
            val oldMethod = xposed.loadClass("com.example.Target").getDeclaredMethod("oldMethod")
            hooks += xposed.hook(oldMethod, OldMethodHooker::class.java)
        }
    } catch (e: Exception) {
        xposed.logError("Failed to hook method", e)
    }
}
```

### App Version Compatibility

#### Issue: "Module stops working after target app update"

**Cause**: Changes in the target app's code structure or obfuscation.

**Debug Steps**:
1. Compare the app's old and new versions
2. Look for class/method signature changes
3. Check for changes in obfuscation patterns

**Solution**:
```kotlin
// Use more robust hooking approach
override fun onPackageLoaded(param: PackageLoadedParam) {
    val xposed = param.xposed
    val targetAppVersion = getTargetAppVersion(param)
    
    try {
        // Hook by method signature instead of name for more resilience
        val clazz = xposed.loadClass("com.example.Target")
        
        // Find method by signature pattern
        val methods = clazz.declaredMethods.filter { method ->
            method.returnType == Boolean::class.javaPrimitiveType &&
            method.parameterTypes.size == 1 &&
            method.parameterTypes[0] == String::class.java
        }
        
        if (methods.isNotEmpty()) {
            val method = methods.first()
            xposed.log(LogLevel.INFO, "Found target method: ${method.name}")
            hooks += xposed.hook(method, MyHooker::class.java)
        } else {
            xposed.log(LogLevel.ERROR, "Could not find target method")
        }
    } catch (e: Exception) {
        xposed.logError("Hook failed", e)
    }
}

private fun getTargetAppVersion(param: PackageLoadedParam): Int {
    try {
        val context = param.xposed.getSystemContext()
        val packageInfo = context.packageManager.getPackageInfo(param.packageName, 0)
        return packageInfo.versionCode
    } catch (e: Exception) {
        return -1
    }
}
```

## Deployment Issues

### Bundle Creation Errors

#### Error: "Invalid .lspkmod bundle: missing module-info.json"

**Cause**: Required files missing from the module bundle.

**Debug Steps**:
1. Check the bundle creation task
2. Verify all required files are included
3. Inspect the generated bundle structure

**Solution**:
```groovy
// Ensure all required files are included in the bundle
tasks.register('createBundle') {
    dependsOn 'assembleRelease'
    
    doLast {
        def bundleDir = file("$buildDir/bundle")
        def bundleFile = file("$buildDir/outputs/bundle/${project.name}-${project.version}.lspkmod")
        
        // Create bundle directory structure
        delete bundleDir
        bundleDir.mkdirs()
        
        // Copy APK
        copy {
            from "$buildDir/outputs/apk/release"
            include "*-release.apk"
            into bundleDir
            rename { "module.apk" }
        }
        
        // Copy module-info.json (required)
        if (!file("src/main/assets/module-info.json").exists()) {
            throw new GradleException("Missing required file: module-info.json")
        }
        copy {
            from "src/main/assets"
            include "module-info.json"
            into bundleDir
        }
        
        // Create bundle zip
        ant.zip(destfile: bundleFile, basedir: bundleDir)
    }
}
```

### Signing Issues

#### Error: "Invalid signature for module bundle"

**Cause**: Bundle signing failed or incorrect signing configuration.

**Debug Steps**:
1. Check the signing configuration
2. Verify the keystore file exists and is valid
3. Ensure the correct keystore password and alias are used

**Solution**:
```groovy
// Correct signing configuration
android {
    signingConfigs {
        release {
            storeFile file(System.getenv("LSPK_STORE_FILE") ?: "${rootProject.rootDir}/keystore.jks")
            storePassword System.getenv("LSPK_STORE_PASSWORD") ?: "password"
            keyAlias System.getenv("LSPK_KEY_ALIAS") ?: "upload"
            keyPassword System.getenv("LSPK_KEY_PASSWORD") ?: "password"
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

## Advanced Debugging Techniques

### LSPosed Logs & LSPosedKit Unified Logging

LSPosed generates its own logs, and LSPosedKit components use a unified logging scheme with specific tags. Understanding these logs is crucial for diagnosing issues.

**Key Log Tags:**

*   **LSPosed Framework Logs:**
    *   `LSPosed`: General LSPosed framework messages.
    *   `XSharedPreferences`: Logs related to Xposed's shared preferences implementation.
    *   `LSPosed-Bridge`: Messages from the core Xposed bridge.
*   **LSPosedKit Core Tags:**
    *   `LSPK-Framework`: General logs from the LSPosedKit core framework (`XposedInterfaceImpl`, etc.).
    *   `LSPK-HotReload`: Logs specific to the hot-reload mechanism (client and manager).
    *   `LSPK-HotReload-Patcher`: Detailed logs from the `DexPatcher` component during DEX patching attempts.
    *   `LSPK-Settings`: Logs from the `SettingsProvider` and related settings components.
    *   `LSPK-ServiceBus`: Logs from the `FeatureManager` and service bus operations.
    *   `LSPK-Processor`: Logs from annotation processors (though these usually appear at compile-time).
*   **Module Specific Tags:**
    *   Each module should use its own tag, ideally prefixed with `LSPK-` for consistency (e.g., `LSPK-DebugApp`, `LSPK-NetworkGuard`). This is typically configured in the module's `LogUtil` instantiation.

**Viewing Logs:**

Use `adb logcat` with appropriate filters. The `-s` option sets a silent filter for all tags and then you specify the tags you want to see with their desired log level (V, D, I, W, E).

```bash
# View all LSPosed-related logs and general LSPK framework logs
adb logcat -s LSPosed:V XSharedPreferences:V LSPosed-Bridge:V LSPK-Framework:D

# View logs for a specific module (e.g., DebugApp) and Hot-Reload Patcher details
adb logcat -s LSPK-DebugApp:V LSPK-HotReload-Patcher:V

# View only errors from LSPosedKit components and a specific module
adb logcat -s LSPK-*:E LSPK-MyModule:E

# Grep for all LSPK related messages (less precise but can be useful)
adb logcat | grep "LSPK-"

# View hot-reload specific logs (manager and client)
adb logcat -s LSPK-HotReload:V
```

**Interpreting Logs:**
*   Look for Exception stack traces. These usually pinpoint the source of an error.
*   Pay attention to log levels (Error, Warn, Info, Debug, Verbose).
*   Correlate timestamps between different log messages to understand the sequence of events.
*   `LSPK-HotReload-Patcher` logs are especially important for diagnosing hot-reload failures, as they will indicate issues with reflection or API availability for DEX patching.

### Framework Hooks Debugging

To debug framework hooking issues:

```kotlin
// List all loaded classes in a package
override fun onPackageLoaded(param: PackageLoadedParam) {
    val xposed = param.xposed
    val classLoader = param.classLoader
    
    try {
        // Get all loaded classes
        val pathList = classLoader.javaClass.getDeclaredField("pathList")
        pathList.isAccessible = true
        val pathListObj = pathList.get(classLoader)
        
        val dexElements = pathListObj.javaClass.getDeclaredField("dexElements")
        dexElements.isAccessible = true
        val dexElementsArray = dexElements.get(pathListObj) as Array<*>
        
        for (element in dexElementsArray) {
            val dexFile = element.javaClass.getDeclaredField("dexFile")
            dexFile.isAccessible = true
            val dexFileObj = dexFile.get(element)
            
            val entries = dexFileObj.javaClass.getDeclaredMethod("entries").invoke(dexFileObj) as Enumeration<*>
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement() as String
                if (entry.startsWith("com.example.target")) {
                    xposed.log(LogLevel.INFO, "Found class: $entry")
                }
            }
        }
    } catch (e: Exception) {
        xposed.logError("Failed to list classes", e)
    }
}

// Debug method parameters and return values
class DebugHooker : Hooker {
    override fun beforeHook(param: HookParam) {
        val xposed = param.xposed
        xposed.log(LogLevel.INFO, "Method called: ${param.method.name}")
        
        // Log all parameters
        param.args.forEachIndexed { index, arg ->
            xposed.log(LogLevel.INFO, "Param[$index]: ${arg?.toString() ?: "null"} (${arg?.javaClass?.name ?: "null"})")
        }
    }
    
    override fun afterHook(param: HookParam) {
        val xposed = param.xposed
        val result = param.getResult<Any>()
        xposed.log(LogLevel.INFO, "Method returned: ${result?.toString() ?: "null"} (${result?.javaClass?.name ?: "null"})")
    }
}
```

### Memory Analysis

For tracking memory issues:

```kotlin
class MemoryDebugHooker : Hooker {
    companion object {
        private val instanceCounts = mutableMapOf<String, Int>()
    }
    
    override fun beforeHook(param: HookParam) {
        // Track object instances
        val className = param.thisObject?.javaClass?.name ?: return
        instanceCounts[className] = (instanceCounts[className] ?: 0) + 1
        
        // Log if count exceeds threshold
        if ((instanceCounts[className] ?: 0) % 100 == 0) {
            param.xposed.log(LogLevel.WARN, "High instance count for $className: ${instanceCounts[className]}")
        }
        
        // Log memory usage
        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
        param.xposed.log(LogLevel.INFO, "Used memory: $usedMemory MB")
    }
}
```

## Creating Good Bug Reports

When seeking help with an issue, include the following information:

1. **LSPosedKit version**
2. **Android version and device model**
3. **Target app version**
4. **Relevant error messages** (copy from logcat)
5. **Minimal code example** that reproduces the issue
6. **Steps to reproduce** the problem
7. **Expected vs. actual behavior**

Example bug report template:

```
## Environment
- LSPosedKit Version: 1.0.0
- Android Version: 14 (API 34)
- Device: Pixel 7 Pro
- Target App: Settings (v14.0.1)

## Issue Description
When trying to hook the Settings app's onCreate method, the hook is not triggered.

## Error Messages
```
E LSPK-DebugApp: Hook failed
    java.lang.NoSuchMethodException: com.android.settings.Settings.onCreate [class android.os.Bundle]
```

## Code Example
```kotlin
val settingsClass = xposed.loadClass("com.android.settings.Settings")
val onCreateMethod = settingsClass.getDeclaredMethod("onCreate", Bundle::class.java)
hooks += xposed.hook(onCreateMethod, SettingsHooker::class.java)
```

## Steps to Reproduce
1. Install module
2. Activate for Settings app
3. Launch Settings app
4. Check logs - no hook trigger seen

## Expected Behavior
Hook should be triggered when Settings app is opened

## Actual Behavior
No hook is triggered, NoSuchMethodException is thrown
```

## Frequently Encountered Errors

Here's a quick reference of common errors and their solutions:

| Error | Likely Cause | Solution |
|-------|--------------|----------|
| `ClassNotFoundException` | Class not found in target app | Check class name and package, verify it exists in the target app |
| `NoSuchMethodException` | Method signature mismatch | Check exact method signature including parameter types |
| `IllegalAccessException` | Accessing private field/method | Make the field/method accessible with `setAccessible(true)` |
| `ClassCastException` | Incorrect type casting | Verify object types before casting, use safe casts |
| `NullPointerException` | Null object reference | Check for null values before using objects |
| `SecurityException` | Missing permissions | Verify LSPosed has proper permissions |
| `IllegalArgumentException` | Invalid parameters | Check parameter values and types |
| `UnsatisfiedLinkError` | Missing native library | Ensure required native libraries are included |
| Hot-reload failing | Hooks not properly unhooked | Implement proper cleanup in `onHotReload()` |
| Settings not saving | Incorrect settings configuration | Verify `settings.json` schema and usage |

## Conclusion

Debugging LSPosedKit modules requires understanding both Android system internals and the LSPosed framework. This guide covers the most common issues you might encounter, but complex modules may face unique challenges.

Remember these key debugging principles:

1. **Start simple**: Test with minimal hook implementations first
2. **Isolate the problem**: Disable parts of your module to find the problematic area
3. **Log extensively**: Use detailed logging during development
4. **Check the basics**: Package names, method signatures, and class existence
5. **Read the logs**: Most errors are explained in logcat
6. **Handle gracefully**: Implement proper error handling for production modules

By following these principles and using the techniques in this guide, you should be able to diagnose and fix most issues you encounter while developing LSPosedKit modules.

## Gradle Development Best Practices

To avoid common Gradle scripting issues when extending LSPosedKit's build system:

### Command-Line Property Handling

When writing custom Gradle tasks that need to accept `-P` parameters:

```groovy
// ❌ AVOID - Can conflict with built-in project properties
task myTask {
    doLast {
        def myValue = project.findProperty('myParam')
        // This may return project.name instead of -PmyParam value
    }
}

// ✅ RECOMMENDED - Direct access to command-line properties
task myTask {
    doLast {
        def cmdProps = gradle.startParameter.projectProperties
        def myValue = cmdProps.containsKey('myParam') ? cmdProps.get('myParam') : null
        // This always gets the actual -PmyParam value
    }
}
```

### Property Validation

Always validate that required parameters were actually provided:

```groovy
task myTask {
    doLast {
        def cmdProps = gradle.startParameter.projectProperties
        def requiredParam = cmdProps.get('required')
        
        if (!requiredParam) {
            throw new GradleException("Required parameter missing. Use -Prequired='value'")
        }
    }
}
```

### Common Property Conflicts

Be aware of these built-in Gradle properties that can cause conflicts:
- `name` (project name)
- `version` (project version) 
- `group` (project group)
- `description` (project description)

Use specific parameter names or prefixes to avoid conflicts:
- Instead of `-Pname`, use `-PmoduleName`
- Instead of `-Pversion`, use `-PmoduleVersion` 