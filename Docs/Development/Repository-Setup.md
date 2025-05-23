# Repository Setup Guide

This document provides detailed instructions for setting up the LSPosedKit repository structure, including the initialization of the libxposed-api submodule and project configuration.

## Initial Repository Setup

1. Create the base repository structure:

```bash
mkdir -p LSPosedKit/{framework/{core,processor,hot,settings},modules,Docs,scripts}
cd LSPosedKit
```

2. Initialize git repository:

```bash
git init
```

3. Create a .gitignore file with standard Android/Kotlin/Gradle entries:

```bash
# Generated files
bin/
gen/
out/
build/

# Gradle files
.gradle/
gradle-app.setting
.gradletasknamecache

# Local configuration file
local.properties

# IntelliJ/Android Studio
*.iml
.idea/
.idea/workspace.xml
.idea/tasks.xml
.idea/gradle.xml
.idea/assetWizardSettings.xml
.idea/dictionaries
.idea/libraries
.idea/caches

# Android Studio captures folder
captures/

# External native build folder
.externalNativeBuild
.cxx/

# Mac OS X files
.DS_Store

# Windows files
Thumbs.db
ehthumbs.db
Desktop.ini

# Keystore files
*.jks
*.keystore

# APK files
*.apk
*.aab

# Log files
*.log

# Other
.navigation/
.settings/
proguard/
```

## Submodule Initialization

Initialize the libxposed-api submodule:

```bash
git submodule add https://github.com/libxposed/api libxposed-api
git submodule update --init --recursive
```

## Gradle Configuration

1. Create root build.gradle file:

```groovy
buildscript {
    ext {
        // SDK versions
        compileSdk = 35
        minSdk = 31
        targetSdk = 35
        
        // Java/Kotlin versions
        javaVersion = JavaVersion.VERSION_17
        kotlinVersion = '1.9.0'
        
        // Library versions
        agpVersion = '8.3.0'
        gradleVersion = '8.4'
    }
    
    repositories {
        google()
        mavenCentral()
    }
    
    dependencies {
        classpath "com.android.tools.build:gradle:${agpVersion}"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}"
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

2. Create settings.gradle file:

```groovy
rootProject.name = 'LSPosedKit'

// Framework modules (libraries - get embedded in module APKs)
include ':framework:core'
include ':framework:processor'
include ':framework:hot'
include ':framework:settings'
include ':framework:service'
include ':framework'

// libxposed-api (library - gets embedded)
include ':libxposed-api:api'

// Scripts for module generation
include ':scripts'

// Sample modules (applications - produce standalone APKs)
include ':modules:DebugApp'
include ':modules:NetworkGuard'
include ':modules:IntentInterceptor'
include ':modules:UIEnhancer'

// Auto-discover additional modules in modules directory
file('modules').eachDir { dir ->
    if (!['DebugApp', 'NetworkGuard', 'IntentInterceptor', 'UIEnhancer'].contains(dir.name)) {
        include ":modules:${dir.name}"
    }
}
```

3. Create gradle.properties file:

```properties
# Project-wide Gradle settings

# IDE (e.g. Android Studio) users:
# Gradle settings configured through the IDE *will override*
# any settings specified in this file.

# Android
android.useAndroidX=true
android.nonTransitiveRClass=true
android.defaults.buildfeatures.buildconfig=true
android.defaults.buildfeatures.aidl=false
android.defaults.buildfeatures.renderscript=false
android.defaults.buildfeatures.resvalues=true
android.defaults.buildfeatures.shaders=false

# Kotlin
kotlin.code.style=official

# Gradle
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
```

4. Create gradle wrapper:

```bash
gradle wrapper --gradle-version 8.4
```

## Create Placeholder README.md

Create a basic README.md file:

```markdown
# LSPosedKit

> A zero‑boilerplate, Android 15–ready toolkit for building, testing, and hot‑reloading LSPosed modules.

## Project Layout

```text
LSPosedKit/
├── framework/                # Core runtime, annotation processor, hot-reload daemon
├── libxposed-api/            # Local source of libxposed-api (api/)
├── modules/                  # One sub-project per feature module
├── Docs/                     # Reference docs (schemas, guides, examples)
├── scripts/                  # Utility scripts (e.g. newModule)
├── build.gradle              # Root Gradle config (SDK 35, Java 17)
└── settings.gradle           # Includes :framework, :libxposed-api:api, :modules/*
```

## Current Status

This project is currently under development. Please refer to the documentation in the `Docs/` directory for more information about the planned features and architecture.
```

## Framework Module Gradle Files

1. Create framework/build.gradle:

```groovy
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.wobbz.framework'
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
    api project(':framework:core')
    api project(':framework:processor')
    api project(':framework:hot')
    api project(':framework:settings')
    api project(':libxposed-api:api')
}
```

2. Create framework/core/build.gradle:

```groovy
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.wobbz.framework.core'
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
    implementation project(':libxposed-api:api')
}
```

Create similar build.gradle files for the other framework modules (processor, hot, settings) with appropriate namespace values.

## Initial Commit

Once everything is set up, create an initial commit:

```bash
git add .
git commit -m "Initial repository setup"
```

## Automated Module Creation

LSPosedKit includes a powerful module scaffolding system that automatically generates complete, buildable standalone APK modules. This system is available once the repository is set up.

### Module Scaffolding Script

The `scripts/newModule.gradle` script generates everything needed for a standalone APK module:

```bash
# Create a new module with comprehensive auto-generation
./gradlew :scripts:newModule \
  -PmoduleName="TestModule" \
  -Pid="test-module" \
  -Pdescription="Test module for development" \
  -Pauthor="Your Name"
```

### What Gets Auto-Generated

The script creates a **complete, installable Android application** with:

**📁 Directory Structure**:
```
modules/TestModule/
├── src/main/java/com/wobbz/module/testmodule/
│   ├── TestModule.kt                    # Main module class
│   ├── MainActivity.kt                  # Launcher activity
│   └── hooks/ExampleTestModuleHooks.kt  # Hooks manager
├── src/main/assets/
│   ├── settings.json                    # Settings schema
│   └── module-info.json                 # Module metadata
├── src/main/res/
│   ├── values/strings.xml               # String resources
│   ├── values/arrays.xml                # xposed_scope array
│   └── mipmap-mdpi/ic_launcher.xml      # Vector icon
├── src/test/java/com/wobbz/module/testmodule/
│   └── TestModuleTest.kt                # Unit tests
├── src/main/AndroidManifest.xml         # Application manifest
├── build.gradle                        # Application build config
└── README.md                           # Module documentation
```

**🔧 Complete Application Configuration**:
- `build.gradle` with `com.android.application` plugin
- Proper `applicationId`, `versionCode`, `versionName` for APK
- Embedded framework with `implementation project(':framework')`
- All required Android dependencies

**📱 Ready-to-Install APK Components**:
- `AndroidManifest.xml` with LSPosed metadata:
  - `xposedmodule=true`
  - `xposeddescription` with module description
  - `xposedminversion=93`
  - `xposedscope` resource reference
- Launcher activity for settings access
- Complete resource files for Android installation

**💻 Template Code with Best Practices**:
- Main module class implementing `IModulePlugin`, `IHotReloadable`, `ModuleLifecycle`
- Hooks manager with proper `PackageLoadedParam` integration
- Example hooker implementation showing `beforeHook`/`afterHook` patterns
- Unit test template with mock utilities

### Build and Install

The generated module can immediately be built as a standalone APK:

```bash
# Build the standalone APK (contains embedded framework)
./gradlew :modules:TestModule:assembleDebug

# Install directly to device
adb install modules/TestModule/build/outputs/apk/debug/TestModule-debug.apk

# Or use Gradle install task
./gradlew :modules:TestModule:installDebug
```

**Result**: A complete ~14MB standalone APK ready for activation in LSPosed Manager!

### Module Architecture Features

Generated modules include:

- **Package Filtering**: Settings-based inclusion/exclusion of target apps
- **Hot-Reload Support**: `@HotReloadable` annotation and lifecycle hooks
- **Service Bus Integration**: Ready for cross-module communication
- **Proper Resource Management**: `Releasable` interface implementation
- **Debug Logging**: Configurable logging with `LogLevel` utilities
- **Settings Management**: JSON schema with automatic UI generation

### Customization Points

After generation, customize your module by:

1. **Replace example hooks** in `hooks/ExampleTestModuleHooks.kt`
2. **Update settings schema** in `assets/settings.json`  
3. **Modify package filtering** logic in main module class
4. **Add additional services** and register with `FeatureManager`
5. **Customize UI** by modifying `MainActivity.kt`

The scaffolding provides a complete foundation that follows LSPosedKit best practices and patterns.

## Next Steps

After completing the repository setup:

1. Implement the core interfaces and classes as defined in the API reference
2. Create the annotation processor for generating module metadata  
3. Implement the hot-reload system
4. Create the settings management system
5. Build the sample modules using the scaffolding script

Refer to the respective implementation guides for details on each component. 