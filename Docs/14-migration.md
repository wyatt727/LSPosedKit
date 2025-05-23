# Migrating from Vanilla LSPosed Modules

> A step-by-step guide to porting traditional LSPosed modules to the LSPosedKit framework, with code examples and best practices.

## Introduction

This guide will help you migrate existing LSPosed modules to take advantage of LSPosedKit's streamlined development experience. The migration process involves:

1. **Project Structure Conversion**: Moving from traditional Xposed project structure to LSPosedKit's organization
2. **Annotation-Based Configuration**: Replacing `assets/xposed_init` and metadata files with annotations
3. **API Migration**: Updating API usage to leverage LSPosedKit's enhanced interfaces
4. **Adding Hot-Reload Support**: Implementing the hot-reload lifecycle for rapid development
5. **Settings Integration**: Converting preferences to LSPosedKit's schema-based system

## Prerequisites

Before beginning migration, ensure you have:

- An existing working LSPosed/Xposed module
- LSPosedKit project cloned and set up (see [Getting Started](01-getting-started.md))
- Familiarity with LSPosedKit's core concepts

## Step 1: Project Structure Migration

### Traditional LSPosed Structure

A typical LSPosed module has this structure:

```
MyXposedModule/
├── app/
│   ├── src/main/
│   │   ├── java/
│   │   │   └── com/example/mymodule/
│   │   │       ├── MainActivity.java
│   │   │       └── MyXposedModule.java
│   │   ├── assets/
│   │   │   └── xposed_init            # Contains main class name
│   │   ├── res/
│   │   │   └── ... (resources)
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

### New LSPosedKit Structure

```
LSPosedKit/
├── modules/
│   └── my-module/                     # Your migrated module
│       ├── src/main/
│       │   ├── java/
│       │   │   └── com/example/mymodule/
│       │   │       ├── MainActivity.kt
│       │   │       └── MyModule.kt    # Main module class with annotations
│       │   ├── assets/
│       │   │   ├── module-info.json   # Module metadata
│       │   │   └── settings.json      # Settings schema
│       │   ├── res/
│       │   │   └── ... (resources)
│       │   └── AndroidManifest.xml
│       └── build.gradle
└── ... (other LSPosedKit files)
```

### Migration Steps

1. **Create a new module in LSPosedKit**:

```bash
./gradlew :scripts:newModule -PmoduleName="MyModule" -Pid="my-module"
```

2. **Copy source files**:

```bash
# Create directory for your package
mkdir -p LSPosedKit/modules/my-module/src/main/java/com/example/mymodule

# Copy Java/Kotlin files
cp MyXposedModule/app/src/main/java/com/example/mymodule/*.java \
   LSPosedKit/modules/my-module/src/main/java/com/example/mymodule/

# Copy resources (if needed)
cp -r MyXposedModule/app/src/main/res/* \
      LSPosedKit/modules/my-module/src/main/res/
```

3. **Copy and adapt AndroidManifest.xml**:

```bash
cp MyXposedModule/app/src/main/AndroidManifest.xml \
   LSPosedKit/modules/my-module/src/main/
```

Edit the manifest to update package name and remove Xposed-specific metadata.

## Step 2: Converting to Annotation-Based Configuration

### Before: Traditional Xposed Configuration

**assets/xposed_init**:
```
com.example.mymodule.MyXposedModule
```

**AndroidManifest.xml**:
```xml
<manifest ...>
    <application ...>
        <meta-data
            android:name="xposedmodule"
            android:value="true" />
        <meta-data
            android:name="xposeddescription"
            android:value="My awesome module" />
        <meta-data
            android:name="xposedminversion"
            android:value="53" />
        <meta-data
            android:name="xposedscope"
            android:resource="@array/xposed_scope" />
    </application>
</manifest>
```

**res/values/arrays.xml**:
```xml
<resources>
    <string-array name="xposed_scope">
        <item>com.android.settings</item>
        <item>com.android.systemui</item>
    </string-array>
</resources>
```

### After: LSPosedKit Annotation Configuration

**MyModule.kt**:
```kotlin
@XposedPlugin(
    id = "my-module",
    name = "My Module",
    version = "1.0.0",
    scope = ["com.android.settings", "com.android.systemui"],
    description = "My awesome module",
    author = "Your Name"
)
@HotReloadable
class MyModule : IModulePlugin, IHotReloadable {
    // Implementation (to be migrated in Step 3)
}
```

**module-info.json**:
```json
{
  "$schema": "https://json-schema.org/draft-07/schema#",
  "id": "my-module",
  "version": "1.0.0",
  "features": [
    "category.feature1",
    "category.feature2"
  ]
}
```

**AndroidManifest.xml**:
```xml
<manifest package="com.example.mymodule">
    <application android:label="My Module">
        <!-- Regular activities, services, etc. -->
        <!-- No Xposed metadata needed -->
    </application>
</manifest>
```

### Migration Steps

1. **Add LSPosedKit dependencies** to your module's `build.gradle`:

```groovy
dependencies {
    implementation project(':framework')
    kapt project(':framework:processor')
}
```

2. **Convert main class** to use LSPosedKit annotations:

```kotlin
// Original Java class
public class MyXposedModule implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        // Hook implementation
    }
}

// Migrated Kotlin class with annotations
@XposedPlugin(
    id = "my-module",
    name = "My Module",
    version = "1.0.0",
    scope = ["com.android.settings", "com.android.systemui"],
    description = "My awesome module",
    author = "Your Name"
)
class MyModule : IModulePlugin {
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Migrated hook implementation (see Step 3)
    }
}
```

3. **Create module-info.json** in `src/main/assets/`:

```json
{
  "$schema": "https://json-schema.org/draft-07/schema#",
  "id": "my-module",
  "version": "1.0.0"
}
```

4. **Remove Xposed-specific files**:
   - Delete `assets/xposed_init`
   - Remove Xposed metadata from AndroidManifest.xml

## Step 3: API Migration

### Before: Traditional Xposed API

```java
public class MyXposedModule implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.android.settings")) {
            Class<?> settingsClass = findClass("com.android.settings.Settings", lpparam.classLoader);
            XposedHelpers.findAndHookMethod(settingsClass, "onCreate", 
                Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("Settings activity created");
                    }
                    
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Activity activity = (Activity) param.thisObject;
                        Toast.makeText(activity, "Hooked by my module", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }
}
```

### After: LSPosedKit API

```kotlin
@XposedPlugin(
    id = "my-module",
    name = "My Module",
    version = "1.0.0",
    scope = ["com.android.settings"],
    description = "My awesome module",
    author = "Your Name"
)
@HotReloadable
class MyModule : IModulePlugin, IHotReloadable {
    private val hooks = mutableListOf<XposedInterface.MethodUnhooker<*>>()
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        val xposed = param.xposed
        xposed.log(LogLevel.INFO, "Loading for package: ${param.packageName}")
        
        val settingsClass = xposed.loadClass("com.android.settings.Settings")
        val onCreateMethod = settingsClass.getDeclaredMethod("onCreate", Bundle::class.java)
        
        hooks += xposed.hook(onCreateMethod, SettingsHooker::class.java)
    }
    
    override fun onHotReload() {
        hooks.forEach { it.unhook() }
        hooks.clear()
    }
    
    class SettingsHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            param.xposed.log(LogLevel.INFO, "Settings activity created")
        }
        
        override fun afterHook(param: HookParam) {
            val activity = param.thisObject as Activity
            Toast.makeText(activity, "Hooked by my module", Toast.LENGTH_SHORT).show()
        }
    }
}
```

### API Mapping Reference

| Traditional Xposed                      | LSPosedKit Equivalent                        |
|----------------------------------------|---------------------------------------------|
| `IXposedHookLoadPackage`               | `IModulePlugin`                              |
| `handleLoadPackage(lpparam)`           | `onPackageLoaded(param)`                     |
| `lpparam.packageName`                  | `param.packageName`                          |
| `lpparam.classLoader`                  | `param.classLoader`                          |
| `findClass(name, classLoader)`         | `param.xposed.loadClass(name)`               |
| `findAndHookMethod(...)`               | `xposed.hook(method, HookerClass::class.java)` |
| `XC_MethodHook`                        | `Hooker` interface                           |
| `beforeHookedMethod(param)`            | `beforeHook(param)`                          |
| `afterHookedMethod(param)`             | `afterHook(param)`                           |
| `param.thisObject`                     | `param.thisObject`                           |
| `param.args[index]`                    | `param.args[index]`                          |
| `param.setResult(obj)`                 | `param.setResult(obj)`                       |
| `param.getResult()`                    | `param.getResult<T>()`                       |
| `XposedBridge.log(msg)`                | `param.xposed.log(LogLevel.INFO, msg)`       |

### Migration Steps

1. **Convert your main class** to implement `IModulePlugin`:

```kotlin
// From:
class MyXposedModule implements IXposedHookLoadPackage

// To:
class MyModule : IModulePlugin
```

2. **Convert hook handling method**:

```kotlin
// From:
@Override
public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable

// To:
override fun onPackageLoaded(param: PackageLoadedParam)
```

3. **Convert class loading**:

```kotlin
// From:
Class<?> clazz = findClass("com.example.Class", lpparam.classLoader);

// To:
val clazz = param.xposed.loadClass("com.example.Class")
```

4. **Convert method hooking**:

```kotlin
// From:
XposedHelpers.findAndHookMethod(clazz, "methodName", 
    String.class, int.class, new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            // Hook logic
        }
    });

// To:
// Create separate Hooker class
class MyMethodHooker : Hooker {
    override fun beforeHook(param: HookParam) {
        // Hook logic
    }
}

// In onPackageLoaded
val method = clazz.getDeclaredMethod("methodName", String::class.java, Int::class.javaPrimitiveType)
hooks += param.xposed.hook(method, MyMethodHooker::class.java)
```

5. **Convert logging**:

```kotlin
// From:
XposedBridge.log("My log message");

// To:
param.xposed.log(LogLevel.INFO, "My log message")
```

## Step 4: Adding Hot-Reload Support

To make your module hot-reloadable, you need to:

1. **Add `@HotReloadable` annotation** to your module class
2. **Implement `IHotReloadable` interface**
3. **Store hook references** for proper cleanup
4. **Implement `onHotReload()` method** to unhook all hooks

```kotlin
@XposedPlugin(
    id = "my-module",
    name = "My Module",
    version = "1.0.0",
    scope = ["com.android.settings"],
    description = "My awesome module"
)
@HotReloadable  // Add this annotation
class MyModule : IModulePlugin, IHotReloadable {  // Implement IHotReloadable
    // Store hook references
    private val hooks = mutableListOf<XposedInterface.MethodUnhooker<*>>()
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // ... hook implementation
        
        // Store hook reference when hooking
        hooks += param.xposed.hook(method, MyMethodHooker::class.java)
    }
    
    // Implement onHotReload method
    override fun onHotReload() {
        // Unhook all hooks
        hooks.forEach { it.unhook() }
        hooks.clear()
        
        // If you have resources to release, do it here
    }
}
```

## Step 5: Settings Integration

### Before: Traditional SharedPreferences

```java
// Loading preferences
Context context = (Context) XposedHelpers.callMethod(
    XposedHelpers.callStaticMethod(
        XposedHelpers.findClass("android.app.ActivityThread", null),
        "currentActivityThread"
    ),
    "getSystemContext"
);
Context moduleContext = context.createPackageContext(
    "com.example.mymodule", 
    Context.CONTEXT_IGNORE_SECURITY
);
SharedPreferences prefs = moduleContext.getSharedPreferences(
    "module_prefs", 
    Context.MODE_PRIVATE
);
boolean enableFeature = prefs.getBoolean("enable_feature", true);
```

### After: LSPosedKit Settings Provider

**settings.json**:
```json
{
  "$schema": "https://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "enable_feature": {
      "type": "boolean",
      "title": "Enable Feature",
      "description": "Turn the main feature on or off",
      "default": true
    },
    "log_level": {
      "type": "string",
      "title": "Log Level",
      "enum": ["OFF", "INFO", "DEBUG", "VERBOSE"],
      "default": "INFO"
    },
    "custom_text": {
      "type": "string",
      "title": "Custom Text",
      "description": "Text to display in the UI",
      "default": "Hello World"
    }
  }
}
```

**Using settings in code**:
```kotlin
// Direct access
override fun initialize(context: Context, xposed: XposedInterface) {
    // Get settings provider
    val settings = SettingsProvider.of(context)
    
    // Access settings
    val enableFeature = settings.bool("enable_feature", true)
    val logLevel = settings.string("log_level", "INFO")
    val customText = settings.string("custom_text", "Hello World")
    
    // Use settings values
    xposed.log(LogLevel.INFO, "Feature enabled: $enableFeature")
}

// Using @SettingsKey annotation
class MyModuleSettings(
    @SettingsKey("enable_feature") val isEnabled: Boolean = true,
    @SettingsKey("log_level") val logLevel: String = "INFO",
    @SettingsKey("custom_text") val customText: String = "Hello World"
)

// In your module
override fun initialize(context: Context, xposed: XposedInterface) {
    val settings = SettingsProvider.of(context)
    val mySettings = settings.bind(MyModuleSettings::class.java)
    
    if (mySettings.isEnabled) {
        xposed.log(LogLevel.INFO, "Module enabled with custom text: ${mySettings.customText}")
    }
}
```

### Migration Steps

1. **Create settings.json** in `src/main/assets/`:

```json
{
  "$schema": "https://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    // Convert your existing preferences to this schema
  }
}
```

2. **Replace SharedPreferences code** with SettingsProvider:

```kotlin
// Replace context creation and SharedPreferences access
val settings = SettingsProvider.of(context)

// Replace preference access
val enableFeature = settings.bool("enable_feature", true)
```

3. **Optional: Use annotation-based settings**:

```kotlin
// Create a settings class
class MyModuleSettings(
    @SettingsKey("key1") val value1: Boolean = true,
    @SettingsKey("key2") val value2: String = "default"
)

// Bind settings
val mySettings = settings.bind(MyModuleSettings::class.java)
```

## Example: Complete Migration

### Original Module

**MyXposedModule.java**:
```java
package com.example.mymodule;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import android.content.Context;
import android.content.SharedPreferences;

public class MyXposedModule implements IXposedHookLoadPackage {
    private boolean isEnabled = true;
    
    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        // Load preferences
        Context context = getModuleContext();
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences("module_prefs", Context.MODE_PRIVATE);
            isEnabled = prefs.getBoolean("enable_feature", true);
        }
        
        if (!isEnabled) {
            XposedBridge.log("Module disabled in settings");
            return;
        }
        
        if (lpparam.packageName.equals("com.android.settings")) {
            XposedBridge.log("Hooking Settings app");
            
            Class<?> settingsClass = XposedHelpers.findClass(
                "com.android.settings.Settings", lpparam.classLoader);
            
            XposedHelpers.findAndHookMethod(settingsClass, "onCreate", 
                android.os.Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("Settings.onCreate called");
                        // Do something with the activity
                    }
                });
        }
    }
    
    private Context getModuleContext() {
        try {
            Object activityThread = XposedHelpers.callStaticMethod(
                XposedHelpers.findClass("android.app.ActivityThread", null),
                "currentActivityThread");
            Context context = (Context) XposedHelpers.callMethod(activityThread, "getSystemContext");
            return context.createPackageContext("com.example.mymodule", Context.CONTEXT_IGNORE_SECURITY);
        } catch (Exception e) {
            XposedBridge.log("Failed to get module context: " + e.getMessage());
            return null;
        }
    }
}
```

### Migrated Module

**MyModule.kt**:
```kotlin
package com.example.mymodule

import android.content.Context
import android.os.Bundle
import com.wobbz.framework.core.IModulePlugin
import com.wobbz.framework.core.PackageLoadedParam
import com.wobbz.framework.hot.IHotReloadable
import com.wobbz.framework.processor.HotReloadable
import com.wobbz.framework.processor.SettingsKey
import com.wobbz.framework.processor.XposedPlugin
import com.wobbz.framework.settings.SettingsProvider
import com.wobbz.framework.util.LogLevel

@XposedPlugin(
    id = "my-module",
    name = "My Module",
    version = "1.0.0",
    scope = ["com.android.settings"],
    description = "Module migrated from traditional LSPosed",
    author = "Your Name"
)
@HotReloadable
class MyModule : IModulePlugin, IHotReloadable {
    private val hooks = mutableListOf<XposedInterface.MethodUnhooker<*>>()
    private lateinit var settings: MyModuleSettings
    
    override fun initialize(context: Context, xposed: XposedInterface) {
        // Initialize settings
        val provider = SettingsProvider.of(context)
        settings = provider.bind(MyModuleSettings::class.java)
        
        xposed.log(LogLevel.INFO, "Module initialized with feature enabled: ${settings.isEnabled}")
    }
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        val xposed = param.xposed
        
        if (!settings.isEnabled) {
            xposed.log(LogLevel.INFO, "Module disabled in settings")
            return
        }
        
        xposed.log(LogLevel.INFO, "Hooking Settings app")
        
        val settingsClass = xposed.loadClass("com.android.settings.Settings")
        val onCreateMethod = settingsClass.getDeclaredMethod("onCreate", Bundle::class.java)
        
        hooks += xposed.hook(onCreateMethod, SettingsHooker::class.java)
    }
    
    override fun onHotReload() {
        hooks.forEach { it.unhook() }
        hooks.clear()
    }
    
    class SettingsHooker : Hooker {
        override fun afterHook(param: HookParam) {
            param.xposed.log(LogLevel.INFO, "Settings.onCreate called")
            // Do something with the activity
        }
    }
    
    class MyModuleSettings(
        @SettingsKey("enable_feature") val isEnabled: Boolean = true
    )
}
```

**settings.json**:
```json
{
  "$schema": "https://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "enable_feature": {
      "type": "boolean",
      "title": "Enable Feature",
      "description": "Turn the module on or off",
      "default": true
    }
  }
}
```

**module-info.json**:
```json
{
  "$schema": "https://json-schema.org/draft-07/schema#",
  "id": "my-module",
  "version": "1.0.0"
}
```

## Troubleshooting Common Migration Issues

### 1. Class Loading Failures

**Problem**: `ClassNotFoundException` when using `loadClass()`

**Solution**:
- Check if the class is actually in the target package
- Verify package is in the scope array in `@XposedPlugin`
- Check class name for typos
- Try using the complete package name

### 2. Method Hooking Issues

**Problem**: Cannot find or hook methods

**Solution**:
- For overloaded methods, specify the exact parameter types
- Make sure to match primitive types correctly (e.g., `Int::class.javaPrimitiveType` instead of `Int::class.java`)
- For private methods, make sure to use `getDeclaredMethod()` instead of `getMethod()`

### 3. Context Availability

**Problem**: Context is not available in certain scenarios

**Solution**:
- Use the `context` parameter provided in the `initialize()` method
- Store the context for later use (but be careful about memory leaks)
- For accessing settings, always use `SettingsProvider.of(context)`

### 4. Settings Migration

**Problem**: Settings not loading or saving correctly

**Solution**:
- Verify your `settings.json` file is correctly formatted
- Check that property names match between `settings.json` and your `@SettingsKey` annotations
- Ensure types match (e.g., don't try to load a string as a boolean)

### 5. Hot-Reload Issues

**Problem**: Hot-reload not working correctly

**Solution**:
- Make sure your class has the `@HotReloadable` annotation
- Implement `IHotReloadable` interface
- Store all hook references and unhook them in `onHotReload()`
- Check logs for hot-reload errors (`adb logcat -s LSPK-HotReload:V`)

## Best Practices for Migrated Modules

1. **Modularize your code**: Break down large classes into smaller, focused components
2. **Use Kotlin features**: Take advantage of Kotlin's null safety, extension functions, and functional programming
3. **Follow LSPosedKit conventions**: Adhere to naming conventions and code organization as outlined in the style guide
4. **Add proper documentation**: Include KDoc comments explaining class and method purposes
5. **Implement comprehensive testing**: Add unit tests for your module's core functionality
6. **Consider feature discovery**: Implement interfaces for the FeatureManager if your module provides services to others
7. **Design for backward compatibility**: If people use your module, ensure smooth migration for them
8. **Use type-safe APIs**: Leverage LSPosedKit's type-safe APIs instead of reflection where possible

## Advanced Migration Topics

### 1. Resource Hooking Migration

**Before**:
```java
public class MyXposedModule implements IXposedHookInitPackageResources {
    @Override
    public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
        if (resparam.packageName.equals("com.android.systemui")) {
            resparam.res.setReplacement(
                "com.android.systemui", 
                "string", 
                "quick_settings_tiles_default",
                "wifi,bt,dnd,flashlight,rotation,battery,cell,airplane"
            );
        }
    }
}
```

**After**:
```kotlin
@XposedPlugin(
    id = "my-module",
    name = "My Module",
    version = "1.0.0",
    scope = ["com.android.systemui"],
    description = "Customize SystemUI resources"
)
class MyModule : IModulePlugin, IResourceHookPlugin {
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Regular hooks
    }
    
    override fun onInitPackageResources(param: ResourcesLoadedParam) {
        val resources = param.resources
        
        resources.replaceString(
            "com.android.systemui",
            "quick_settings_tiles_default",
            "wifi,bt,dnd,flashlight,rotation,battery,cell,airplane"
        )
    }
}
```

### 2. Migrating Zygote Hooks

**Before**:
```java
public class MyXposedModule implements IXposedHookZygoteInit {
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedBridge.log("Initializing in zygote");
        // System-wide hooks
        XposedHelpers.findAndHookMethod("android.app.ActivityManager", null,
            "isUserAMonkey", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return false;
                }
            });
    }
}
```

**After**:
```kotlin
@XposedPlugin(
    id = "my-module",
    name = "My Module",
    version = "1.0.0",
    scope = ["android"],
    description = "System-wide hooks"
)
class MyModule : IModulePlugin, IZygotePlugin {
    private val hooks = mutableListOf<XposedInterface.MethodUnhooker<*>>()
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Package-specific hooks
    }
    
    override fun onZygoteLoaded(param: ZygoteLoadedParam) {
        val xposed = param.xposed
        xposed.log(LogLevel.INFO, "Initializing in zygote")
        
        val activityManagerClass = xposed.loadClass("android.app.ActivityManager")
        val isUserAMonkeyMethod = activityManagerClass.getDeclaredMethod("isUserAMonkey")
        
        hooks += xposed.hook(isUserAMonkeyMethod, MonkeyHooker::class.java)
    }
    
    override fun onHotReload() {
        hooks.forEach { it.unhook() }
        hooks.clear()
    }
    
    class MonkeyHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            param.setResult(false)
        }
    }
}
```

## Conclusion

Migrating from traditional LSPosed modules to LSPosedKit brings significant benefits:

- **Improved development experience** with hot-reload
- **Reduced boilerplate** with annotation-based configuration
- **Type-safe APIs** for easier development and fewer bugs
- **Better settings management** with schema-based validation
- **Modern Kotlin features** for cleaner, more concise code

By following this guide, you can smoothly transition your existing modules to take full advantage of the LSPosedKit framework while maintaining compatibility with your user base. 