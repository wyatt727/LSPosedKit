# CLI & Gradle Reference Guide

> Complete reference for all command-line tasks, Gradle configuration, build flags, and advanced development workflows in LSPosedKit.

## Gradle Task Overview

LSPosedKit extends the standard Android Gradle Plugin with specialized tasks for module development, hot-reloading, and deployment. This guide covers all available tasks and their configuration options.

## Common Development Tasks

### Building Modules

| Task                         | Description                                         | Example                                  |
|------------------------------|-----------------------------------------------------|------------------------------------------|
| `assembleDebug`              | Build debug APK for all modules                     | `./gradlew assembleDebug`                |
| `assembleRelease`            | Build release APK for all modules                   | `./gradlew assembleRelease`              |
| `:modules:name:assembleDebug` | Build specific module debug APK                     | `./gradlew :modules:debug-app:assembleDebug` |
| `installDebug`               | Build & install debug APKs on connected device      | `./gradlew installDebug`                 |
| `uninstallDebug`             | Uninstall debug APKs from connected device          | `./gradlew uninstallDebug`               |
| `connectedDebugAndroidTest`  | Run instrumented tests on connected device          | `./gradlew connectedDebugAndroidTest`    |

### Hot-Reload Development

| Task                         | Description                                         | Example                                  |
|------------------------------|-----------------------------------------------------|------------------------------------------|
| `runDevServer`               | Start hot-reload development server                 | `./gradlew runDevServer`                 |
| `stopDevServer`              | Stop hot-reload development server                  | `./gradlew stopDevServer`                |
| `hotReload`                  | Trigger hot-reload for all modules                  | `./gradlew hotReload`                    |
| `:modules:name:hotReload`    | Trigger hot-reload for specific module              | `./gradlew :modules:debug-app:hotReload` |

### Module Management

| Task                         | Description                                         | Example                                  |
|------------------------------|-----------------------------------------------------|------------------------------------------|
| `:scripts:newModule`         | Generate new module from template                   | `./gradlew :scripts:newModule -Pname="Debug App" -Pid=debug-app` |
| `processAnnotations`         | Process annotations for all modules                 | `./gradlew processAnnotations`           |
| `verifyDependencies`         | Validate module dependency graph                    | `./gradlew verifyDependencies`           |

### Release Management

| Task                         | Description                                         | Example                                  |
|------------------------------|-----------------------------------------------------|------------------------------------------|
| `publishBundle`              | Generate .lspkmod bundles in dist/                  | `./gradlew publishBundle`                |
| `:modules:name:publishBundle`| Generate .lspkmod bundle for specific module        | `./gradlew :modules:debug-app:publishBundle` |
| `generateChangeLog`          | Auto-generate changelog from git commits            | `./gradlew generateChangeLog`            |

## Configuration Parameters

LSPosedKit supports various configuration parameters passed via Gradle properties:

### Global Configuration

| Property                  | Description                                   | Example                                  |
|---------------------------|-----------------------------------------------|------------------------------------------|
| `-Pjava21`                | Enable Java 21 support                        | `./gradlew assembleDebug -Pjava21`       |
| `-PskipTests`             | Skip running tests during build               | `./gradlew assembleRelease -PskipTests`  |
| `-PdevicePort=18777`      | Set custom port for hot-reload server         | `./gradlew runDevServer -PdevicePort=19999` |
| `-PuseReleaseKeystore`    | Use release signing configuration             | `./gradlew assembleRelease -PuseReleaseKeystore` |

### Module Creation

| Property                  | Description                                   | Example                                  |
|---------------------------|-----------------------------------------------|------------------------------------------|
| `-Pname`                  | Human-readable module name                    | `./gradlew :scripts:newModule -Pname="My Module"` |
| `-Pid`                    | Module identifier (kebab-case)                | `./gradlew :scripts:newModule -Pid=my-module` |
| `-Ppackage`               | Java package name (optional)                  | `./gradlew :scripts:newModule -Ppackage=com.example.mymodule` |
| `-Pauthor`                | Module author name (optional)                 | `./gradlew :scripts:newModule -Pauthor="Jane Developer"` |

### Release Configuration

| Property                  | Description                                   | Example                                  |
|---------------------------|-----------------------------------------------|------------------------------------------|
| `-PversionCode=10`        | Set specific version code                     | `./gradlew assembleRelease -PversionCode=10` |
| `-PversionName=1.2.0`     | Set specific version name                     | `./gradlew assembleRelease -PversionName=1.2.0` |
| `-PkeyAlias=upload`       | Set signing key alias                         | `./gradlew publishBundle -PkeyAlias=upload` |
| `-PkeyPassword=secret`    | Set signing key password                      | `./gradlew publishBundle -PkeyPassword=secret` |

## Task Details & Examples

### Building & Installing

#### Basic Build

```bash
# Build all modules in debug mode
./gradlew assembleDebug

# Build specific module
./gradlew :modules:debug-app:assembleDebug

# Build release versions (requires signing config)
./gradlew assembleRelease
```

#### Installation

```bash
# Build and install all modules
./gradlew installDebug

# Install specific module
./gradlew :modules:debug-app:installDebug

# Uninstall all modules
./gradlew uninstallDebug

# Refresh package database after install
./gradlew installDebug && adb shell cmd package reconcile
```

### Hot-Reload Development

#### Start Development Server

```bash
# Start dev server on default port (18777)
./gradlew runDevServer

# Start with custom port
./gradlew runDevServer -PdevicePort=19999

# Start in background
./gradlew runDevServer -PbackgroundServer=true
```

#### Automatic Hot-Reload Workflow

With the dev server running, any module rebuilds will trigger automatic hot-reload:

```bash
# Start development server (in one terminal)
./gradlew runDevServer

# In another terminal, build a module
./gradlew :modules:debug-app:assembleDebug

# Changes will automatically hot-reload to the device
```

#### Manual Hot-Reload

If needed, manually trigger hot-reload for specific modules:

```bash
# Manually trigger hot-reload for a specific module
./gradlew :modules:debug-app:hotReload

# Force hot-reload of all modules
./gradlew hotReload -PforceReload=true
```

### Module Creation & Management

#### Creating a New Module

```bash
# Basic module creation
./gradlew :scripts:newModule -Pname="Debug App" -Pid=debug-app

# Full configuration
./gradlew :scripts:newModule \
  -Pname="Debug App" \
  -Pid=debug-app \
  -Ppackage=com.example.debugapp \
  -Pauthor="Jane Developer" \
  -PtargetSdk=35 \
  -PminSdk=31 \
  -PwithHotReload=true \
  -PwithSettings=true
```

This creates:
- Module directory structure at `modules/debug-app/`
- Gradle build configuration
- Basic module implementation
- Module metadata files
- Test skeleton

**Note**: The module scaffolding script properly handles command-line properties using `gradle.startParameter.projectProperties` to avoid conflicts with Gradle's built-in `project.name` property. This ensures that `-Pname` parameters are correctly applied instead of being overridden by the project name.

#### Verifying Module Dependencies

```bash
# Check all module dependencies
./gradlew verifyDependencies

# Detailed verification report
./gradlew verifyDependencies -PdetailedReport=true
```

### Release Management

#### Building a Release Bundle

```bash
# Generate .lspkmod bundle for all modules
./gradlew publishBundle

# Generate for specific module
./gradlew :modules:network-guard:publishBundle

# With custom signing configuration
./gradlew publishBundle \
  -PuseReleaseKeystore=true \
  -PkeyAlias=upload \
  -PkeyPassword=secret \
  -PstorePassword=secret
```

The resulting `.lspkmod` bundles will be located in the `dist/` directory.

#### Automated Changelog Generation

```bash
# Generate changelog from git commits
./gradlew generateChangeLog

# Generate changelog for specific version
./gradlew generateChangeLog -PsinceTag=v1.0.0 -PtoTag=v1.1.0
```

## Advanced Gradle Configuration

### Custom gradle.properties

Create or modify `gradle.properties` in the project root for persistent configuration:

```properties
# Custom SDK locations
sdk.dir=/custom/path/to/android-sdk

# Development server configuration
devServerPort=18777
devServerAutoReload=true

# Signing configuration
signing.keyAlias=upload
signing.keyPassword=******
signing.storeFile=/path/to/keystore.jks
signing.storePassword=******

# Build options
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
org.gradle.parallel=true
android.useAndroidX=true
```

### Module-Specific build.gradle Customization

Each module's `build.gradle` can be customized:

```groovy
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
    id 'org.jetbrains.kotlin.kapt'
}

android {
    namespace 'com.example.networkguard'
    compileSdk 35

    defaultConfig {
        minSdk 31
        targetSdk 35
        
        // Custom settings
        manifestPlaceholders = [
            moduleDescription: "Network traffic monitoring and control"
        ]
        
        // Test instrumentation
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    
    // Custom build types
    buildTypes {
        debug {
            // Development settings
            buildConfigField "boolean", "VERBOSE_LOGGING", "true"
        }
        release {
            // Production settings
            buildConfigField "boolean", "VERBOSE_LOGGING", "false"
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    
    // Add custom source sets if needed
    sourceSets {
        main {
            assets.srcDirs += ['src/main/config']
        }
    }
}

dependencies {
    // Core dependencies
    implementation project(':framework')
    kapt project(':framework:processor')
    
    // Add custom dependencies
    implementation 'com.squareup.okhttp3:okhttp:4.10.0'
    
    // Test dependencies
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
}
```

## Continuous Integration Tasks

LSPosedKit includes specialized tasks for CI/CD environments:

```bash
# Validate all modules (useful for pull requests)
./gradlew validateModules

# Run all tests (unit + instrumented if device connected)
./gradlew runAllTests

# Build all module variants
./gradlew buildAllVariants

# Create release artifacts
./gradlew createReleaseArtifacts
```

## Command-Line Utilities

LSPosedKit includes several helper scripts in the `scripts/` directory:

### Module Management

```bash
# Create a new module
./scripts/create-module.sh "Network Guard" network-guard

# List all modules and their versions
./scripts/list-modules.sh

# Check module dependencies
./scripts/check-dependencies.sh
```

### Device Interaction

```bash
# Push a DEX patch to device
./scripts/push-dex.sh modules/network-guard/build/intermediates/dex/debug/out/classes.dex

# Trigger module reload
./scripts/reload-module.sh network-guard

# View module logs
./scripts/view-logs.sh network-guard
```

## Environment Variables

LSPosedKit recognizes several environment variables:

| Variable                     | Description                                      | Example                         |
|------------------------------|--------------------------------------------------|--------------------------------|
| `ANDROID_SDK_ROOT`           | Android SDK location                             | `/home/user/Android/Sdk`        |
| `ANDROID_HOME`               | Alternative Android SDK location                 | `/home/user/android-sdk`        |
| `JAVA_HOME`                  | JDK location                                     | `/opt/jdk-17`                   |
| `LSPK_KEYSTORE_PATH`         | Path to signing keystore                         | `/home/user/.signing/keys.jks`  |
| `LSPK_KEYSTORE_PASSWORD`     | Keystore password                                | `keystore_password`             |
| `LSPK_KEY_ALIAS`             | Signing key alias                                | `upload_key`                    |
| `LSPK_KEY_PASSWORD`          | Signing key password                             | `key_password`                  |

## Troubleshooting Build Issues

### Common Errors

| Error                                    | Solution                                                      |
|------------------------------------------|---------------------------------------------------------------|
| `SDK location not found`                 | Set `ANDROID_HOME` or create `local.properties` with `sdk.dir` |
| `Could not find tools.jar`               | Ensure `JAVA_HOME` points to a valid JDK                       |
| `Cannot run program "adb"`               | Add Android SDK platform-tools to your PATH                    |
| `Failed to apply plugin [...]`           | Update Gradle version or check plugin compatibility            |
| `Annotation processor [...] not found`   | Run `./gradlew clean` and rebuild                             |

### Diagnostic Commands

```bash
# Check environment configuration
./gradlew environmentCheck

# Print build environment details
./gradlew buildEnvironment

# Show project dependencies
./gradlew :modules:debug-app:dependencies

# Get detailed build info
./gradlew assembleDebug --info

# Debug build process
./gradlew assembleDebug --debug
```

## Best Practices

1. **Use the New Module Script** for consistent module structure
2. **Enable Hot-Reload Server** during development for faster iteration
3. **Run `verifyDependencies`** before publishing to ensure compatibility
4. **Keep Module ID Consistent** between `@XposedPlugin` and `module-info.json`
5. **Use Project-Level gradle.properties** for team-shared settings
6. **Use User-Level gradle.properties** for personal credentials
7. **Commit build.gradle Changes** but never commit keystore credentials