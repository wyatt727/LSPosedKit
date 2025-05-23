# LSPosedKit Build System

This directory contains Kotlin DSL utilities for managing the LSPosedKit build system.

## Features

### Centralized Dependency Management

All dependency versions are centralized in `DependencyVersions.kt`:

```kotlin
object DependencyVersions {
    const val kotlin = "1.9.0"
    const val compileSdk = 35
    const val minSdk = 31
    // ... more versions
}
```

### Extension Functions

Common configuration patterns are available as extension functions in `ModuleExtensions.kt`:

```kotlin
// Configure Android library settings
android.configureAndroidLibrary()

// Add common dependencies
addFrameworkDependencies()
addTestingDependencies()
addUIDependencies()
addJSONDependencies()
addCoroutinesDependencies()
```

### Convention Plugins

Pre-configured plugins for different module types in `LSPosedKitConventions.kt`:

- `LSPosedKitModulePlugin` - Standard LSPosedKit module
- `LSPosedKitFrameworkPlugin` - Framework component
- `LSPosedKitUIModulePlugin` - UI-enabled module

### Usage Examples

#### Standard Module
```kotlin
plugins {
    id("lspkit-module")
}

// Additional dependencies if needed
addUIDependencies()
addJSONDependencies()
```

#### Framework Component
```kotlin
plugins {
    id("lspkit-framework")
}

dependencies {
    api(project(":libxposed-api:api"))
}
```

#### UI Module
```kotlin
plugins {
    id("lspkit-ui-module")
}
// UI and JSON dependencies are automatically added
```

### Benefits

1. **Consistency** - All modules use the same configurations
2. **Maintainability** - Version updates happen in one place  
3. **Reduced Boilerplate** - Less repetitive build script code
4. **Type Safety** - Kotlin DSL provides better IDE support
5. **Convention Over Configuration** - Sensible defaults for LSPosedKit modules

### Migration Guide

To migrate existing modules to use the new buildSrc utilities:

1. Replace plugin applications with convention plugins
2. Use extension functions instead of manual configuration
3. Remove hardcoded version numbers
4. Simplify dependency declarations

Before:
```kotlin
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
    id 'org.jetbrains.kotlin.kapt'
}

android {
    compileSdk 35
    defaultConfig {
        minSdk 31
        targetSdk 35
    }
    // ... more config
}

dependencies {
    implementation project(':framework')
    implementation 'junit:junit:4.13.2'
    // ... more dependencies
}
```

After:
```kotlin
plugins {
    id("lspkit-module")
}

// That's it! All configuration is handled by the convention plugin
``` 