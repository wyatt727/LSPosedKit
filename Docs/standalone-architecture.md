# Standalone APK Architecture

> Understanding LSPosedKit's revolutionary approach to module distribution through standalone, self-contained APKs.

## The Revolutionary Change

LSPosedKit breaks from traditional LSPosed module development by packaging each module as a **completely standalone APK** containing the entire framework embedded within it.

### Traditional LSPosed Development

```
❌ Traditional Approach:
┌─────────────────────────────────────────────────────────────┐
│                    LSPosed Framework                        │
├─────────────────────────────────────────────────────────────┤
│ 📦 Module A.jar    📦 Module B.jar    📦 Module C.jar     │
│ (depends on       (depends on        (depends on          │
│  framework)        framework)         framework)           │
└─────────────────────────────────────────────────────────────┘

Problems:
• Modules depend on LSPosed framework being present
• Complex dependency management
• Version compatibility issues
• Difficult distribution and installation
```

### LSPosedKit's Standalone Approach

```
✅ LSPosedKit Approach:
┌─────────────────────┐ ┌─────────────────────┐ ┌─────────────────────┐
│    ModuleA.apk      │ │    ModuleB.apk      │ │    ModuleC.apk      │
├─────────────────────┤ ├─────────────────────┤ ├─────────────────────┤
│ Module A Code       │ │ Module B Code       │ │ Module C Code       │
├─────────────────────┤ ├─────────────────────┤ ├─────────────────────┤
│ LSPosedKit Framework│ │ LSPosedKit Framework│ │ LSPosedKit Framework│
│ (embedded)          │ │ (embedded)          │ │ (embedded)          │
└─────────────────────┘ └─────────────────────┘ └─────────────────────┘

Benefits:
• Each module is completely self-contained
• No external dependencies
• Install any module independently: adb install ModuleA.apk
• Standard APK distribution (GitHub, F-Droid, etc.)
```

## How It Works

### 1. Framework Embedding

When you build a module, the entire LSPosedKit framework gets embedded into your APK:

```kotlin
// Your module code
@XposedPlugin(id = "my-module", ...)
class MyModule : IModulePlugin {
    // Your implementation
}

// Gets compiled into MyModule.apk containing:
// - Your module code
// - Complete LSPosedKit framework
// - Generated metadata (module.prop, xposed_init, etc.)
```

### 2. Independent Installation

Each module APK can be installed independently:

```bash
# Install any module without dependencies
adb install NetworkGuard.apk
adb install IntentInterceptor.apk  
adb install DebugApp.apk

# Each module works standalone
# No "host" or "framework" APK needed!
```

### 3. Cross-Module Communication (Optional)

When multiple LSPosedKit modules are installed, they can optionally communicate:

```kotlin
// NetworkGuard.apk provides a service
FeatureManager.register(INetworkRuleService::class.java, NetworkRuleProvider())

// IntentInterceptor.apk can discover and use it
val networkService = FeatureManager.get(INetworkRuleService::class.java)
val decision = networkService?.checkRule(packageName, url)
```

## Key Benefits

### 1. Zero Dependencies

**Traditional LSPosed**:
- Requires LSPosed framework to be installed
- Modules break if framework updates
- Complex version compatibility matrix

**LSPosedKit**:
- Each APK is completely self-contained
- Install any module independently
- No version compatibility issues

### 2. Standard Distribution

**Traditional LSPosed**:
- Custom `.jar` files
- Manual installation process
- Requires custom repositories

**LSPosedKit**:
- Standard Android APKs
- Install with any APK installer
- Distribute via GitHub, F-Droid, or any file sharing

### 3. Simplified Development

**Traditional LSPosed**:
```groovy
// Complex dependency management
dependencies {
    compileOnly 'de.robv.android.xposed:api:82'
    // Version conflicts, API changes, etc.
}
```

**LSPosedKit**:
```groovy
// Framework is embedded - just one dependency
dependencies {
    implementation project(':framework')  // Everything included!
}
```

### 4. Hot-Reload Per Module

**Traditional LSPosed**:
- Reload affects entire framework
- Risk of breaking other modules
- System-wide restarts often needed

**LSPosedKit**:
- Hot-reload only affects specific module
- Other modules continue running unaffected
- Faster iteration cycles

## Architecture Comparison

### File Structure

**Traditional LSPosed Module**:
```
MyModule.jar
├── classes.dex          ← Module code only
└── assets/
    ├── module.prop      ← Manual configuration
    └── xposed_init      ← Manual entry point
```

**LSPosedKit Module**:
```
MyModule.apk
├── classes.dex          ← Module code + embedded framework
├── assets/
│   ├── module.prop      ← Generated automatically
│   ├── xposed_init      ← Generated automatically (points to LSPosedEntry bridge)
│   ├── module-info.json ← Generated automatically
│   └── settings.json    ← Your configuration
├── res/                 ← Android resources
└── AndroidManifest.xml  ← Application manifest
```

### Build Configuration

**Traditional LSPosed Module**:
```groovy
// Library module - produces JAR
apply plugin: 'com.android.library'

android {
    // Library configuration
}

dependencies {
    compileOnly 'de.robv.android.xposed:api:82'  // External dependency
}
```

**LSPosedKit Module**:
```groovy
// Application module - produces APK
apply plugin: 'com.android.application'

android {
    defaultConfig {
        applicationId "com.example.mymodule"  // Required for APK
        // Application configuration
    }
}

dependencies {
    implementation project(':framework')  // Framework embedded
}
```

## Memory and Performance

### Memory Overhead

**Concern**: "Doesn't embedding the framework in each module waste memory?"

**Reality**: 
- Framework overhead: ~500KB per module
- Android's memory management deduplicates identical code
- Most users install 2-5 modules, so total overhead is minimal
- Benefits outweigh the small memory cost

### Performance Impact

**Framework Loading**:
- Each module initializes its embedded framework
- Lazy initialization minimizes boot time impact
- Hot-reload is faster (per-module vs system-wide)

**Cross-Module Communication**:
- Optional service discovery uses minimal IPC
- Services are registered only when needed
- Automatic cleanup when modules are unloaded

## Migration from Traditional LSPosed

### Code Changes

**Minimal changes needed**:

```diff
// build.gradle
- apply plugin: 'com.android.library'
+ apply plugin: 'com.android.application'

android {
+   defaultConfig {
+       applicationId "com.example.mymodule"
+   }
}

dependencies {
-   compileOnly 'de.robv.android.xposed:api:82'
+   implementation project(':framework')
}
```

```diff
// Module class
- import de.robv.android.xposed.XposedBridge;
+ import com.wobbz.framework.core.*;

@XposedPlugin(
    id = "my-module",
    // ... metadata
)
- public class MyModule implements IXposedHookLoadPackage {
+ public class MyModule implements IModulePlugin {
-     public void handleLoadPackage(LoadPackageParam lpparam) {
+     public void onPackageLoaded(PackageLoadedParam param) {
        // Hook logic remains largely the same
    }
}
```

### Distribution Changes

**Old way**:
1. Build JAR file
2. Upload to custom repository
3. Users manually install framework
4. Users manually install module JAR

**New way**:
1. Build APK file
2. Upload anywhere (GitHub, F-Droid, etc.)
3. Users install APK: `adb install MyModule.apk`
4. Done!

## Use Cases and Examples

### Individual Module Distribution

Perfect for:
- Open source modules on GitHub
- Commercial modules (standard APK licensing)
- Enterprise internal modules
- Educational/research modules

### Module Suites

You can still create related modules:
```
MyCompany-SecuritySuite/
├── NetworkGuard.apk     ← Network monitoring
├── AppFirewall.apk      ← Application firewall  
├── DataProtector.apk    ← Data protection
└── CentralDashboard.apk ← Management UI
```

Each APK is standalone, but they can communicate via services.

### Legacy Module Support

LSPosedKit modules can coexist with traditional LSPosed modules:
- Traditional modules continue working with LSPosed framework
- LSPosedKit modules work independently
- Both can run simultaneously on the same device

## Best Practices

### 1. Design for Independence

```kotlin
// ✅ Good: Module works standalone
@XposedPlugin(id = "my-module", scope = ["com.example.targetapp"])
class MyModule : IModulePlugin {
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Module functionality is self-contained
    }
}
```

```kotlin
// ❌ Avoid: Hard dependencies on other modules
class MyModule : IModulePlugin {
    override fun onPackageLoaded(param: PackageLoadedParam) {
        val otherModule = findOtherModule() // Hard dependency - bad!
        if (otherModule == null) {
            throw RuntimeException("Other module required!") // Module breaks
        }
    }
}
```

### 2. Use Optional Service Discovery

```kotlin
// ✅ Good: Graceful degradation
class MyModule : IModulePlugin {
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Try to find complementary services
        val networkService = FeatureManager.get(INetworkRuleService::class.java)
        
        if (networkService != null) {
            // Enhanced functionality when NetworkGuard is also installed
            val decision = networkService.checkRule(packageName, url)
            handleWithNetworkRules(decision)
        } else {
            // Fallback functionality when standalone
            handleStandalone()
        }
    }
}
```

### 3. Proper Resource Management

```kotlin
// ✅ Good: Clean resource management
class MyModule : IModulePlugin, Releasable {
    private val hooks = mutableListOf<MethodUnhooker<*>>()
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        val unhooker = param.xposed.hook(targetMethod, MyHooker::class.java)
        hooks.add(unhooker)
    }
    
    override fun release() {
        hooks.forEach { it.unhook() }
        hooks.clear()
    }
}
```

## Conclusion

LSPosedKit's standalone APK architecture represents a paradigm shift in LSPosed module development:

✅ **Eliminates dependency hell**  
✅ **Simplifies distribution**  
✅ **Enables standard Android tooling**  
✅ **Provides better isolation**  
✅ **Maintains cross-module communication when needed**  

This approach makes LSPosed module development more accessible, reliable, and maintainable while preserving the power and flexibility that makes LSPosed valuable. 