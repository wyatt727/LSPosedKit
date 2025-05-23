# CLI & Gradle Reference

> Complete command-line reference for building, testing, and deploying LSPosedKit modules as **standalone APKs**.

## Quick Reference

### Building Standalone APKs

```bash
# Build all module APKs
./gradlew assembleDebug
./gradlew assembleRelease

# Build specific module APK
./gradlew :modules:DebugApp:assembleDebug
./gradlew :modules:NetworkGuard:assembleRelease

# Check build outputs
ls modules/*/build/outputs/apk/debug/*.apk
ls modules/*/build/outputs/apk/release/*.apk
```

### Installation

```bash
# Install specific module APK
./gradlew :modules:DebugApp:installDebug
adb install modules/DebugApp/build/outputs/apk/debug/DebugApp-debug.apk

# Install all module APKs
./gradlew installDebug

# Uninstall specific module
./gradlew :modules:DebugApp:uninstallDebug
adb uninstall com.wobbz.module.debugapp

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

# In another terminal, build a module APK
./gradlew :modules:DebugApp:assembleDebug

# Changes will automatically hot-reload to the device
```

#### Manual Hot-Reload

If needed, manually trigger hot-reload for specific modules:

```bash
# Manually trigger hot-reload for a specific module
./gradlew :modules:DebugApp:hotReload

# Force hot-reload of all modules
./gradlew hotReload -PforceReload=true
```

### Module Creation & Management

#### Creating a New Module

```bash
# Basic module creation
./gradlew :scripts:newModule -PmoduleName="DebugApp" -Pid="debug-app"

# With optional configuration
./gradlew :scripts:newModule \
  -PmoduleName="DebugApp" \
  -Pid="debug-app" \
  -Pdescription="Force enable debug flags in apps" \
  -Pauthor="Jane Developer" \
  -Pscope="*"
```

This **auto-generates a complete standalone APK module** with:

**📁 Complete Directory Structure**:
- Module directory at `modules/DebugApp/`
- Source: `src/main/java/`, `src/test/java/`, `src/main/assets/`
- Resources: `src/main/res/values/`, `src/main/res/mipmap-mdpi/`

**🔧 Android Application Configuration**:
- `build.gradle` with `com.android.application` plugin
- Complete APK configuration with `applicationId`, `versionCode`, `versionName`
- Embedded framework dependencies with `implementation project(':framework')`

**🔗 Automatic LSPosed Compatibility**:
- `LSPosedEntry.kt` - Bridge class implementing `IXposedHookLoadPackage`
- `xposed_init` file pointing to the bridge class
- No manual setup required for LSPosed Manager recognition

**📱 Auto-Generated Android Files**:
- `AndroidManifest.xml` with LSPosed metadata and launcher activity
- `MainActivity.kt` with module information dialog (no immediate finish())
- `strings.xml`, `arrays.xml`, and vector launcher icon
- All required files for installable APK

**💻 Ready-to-Use Code Templates**:
- Main module class with `@XposedPlugin` annotation and lifecycle management
- LSPosed compatibility layer with automatic framework bridging
- Example hooks manager with proper `PackageLoadedParam` integration
- Unit test template with mock utilities and assertion examples

**📄 Complete Metadata**:
- `settings.json` with package filtering and debugging options
- `module-info.json` with service declarations and capabilities
- `README.md` with module-specific implementation notes and architecture details

**🎯 Result**: Run `./gradlew :modules:DebugApp:assembleDebug` and get a complete 14MB standalone APK ready for `adb install` and immediate LSPosed Manager recognition!

**Note**: The module scaffolding script properly handles command-line properties using `gradle.startParameter.projectProperties` to avoid conflicts with Gradle's built-in `project.name` property. This ensures that `-PmoduleName` parameters are correctly applied instead of being overridden by the project name.

#### Verifying Module Dependencies

```bash
# Check all module dependencies
./gradlew verifyDependencies

# Detailed verification report
./gradlew verifyDependencies -PdetailedReport=true
```

### Release Management

#### Building Release APKs

```bash
# Generate signed release APKs for all modules
./gradlew assembleRelease

# Generate for specific module
./gradlew :modules:NetworkGuard:assembleRelease

# With custom signing configuration
./gradlew assembleRelease \
  -PuseReleaseKeystore=true \
  -PkeyAlias=upload \
  -PkeyPassword=secret \
  -PstorePassword=secret
```

The resulting signed APKs will be located in each module's `build/outputs/apk/release/` directory.

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
    id 'com.android.application'  // 🔥 APPLICATION for standalone APKs
    id 'org.jetbrains.kotlin.android'
    id 'org.jetbrains.kotlin.kapt'
}

android {
    namespace 'com.example.networkguard'
    compileSdk 35

    defaultConfig {
        applicationId "com.example.networkguard"  // 🔥 Required for APKs
        minSdk 31
        targetSdk 35
        versionCode 1
        versionName "1.0.0"
        
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
            debuggable true
            buildConfigField "boolean", "VERBOSE_LOGGING", "true"
        }
        release {
            // Production settings
            debuggable false
            buildConfigField "boolean", "VERBOSE_LOGGING", "false"
            minifyEnabled false  // LSPosed modules typically don't minify
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
    // Core dependencies - framework gets EMBEDDED in APK
    implementation project(':framework')
    kapt project(':framework:processor')
    
    // Standard dependencies
    implementation "org.jetbrains.kotlin:kotlin-stdlib:${rootProject.ext.kotlinVersion}"
    implementation 'androidx.annotation:annotation:1.5.0'
    
    // Optional UI dependencies for settings screens
    implementation 'androidx.core:core-ktx:1.9.0'
    implementation 'com.google.android.material:material:1.8.0'
    
    // JSON processing for network rules
    implementation 'com.squareup.moshi:moshi:1.14.0'
    implementation 'com.squareup.moshi:moshi-kotlin:1.14.0'
}
```

## Development Workflow

### Typical Development Session

```bash
# 1. Start development server for hot-reload
./gradlew runDevServer

# 2. In another terminal, build and install your module
./gradlew :modules:MyModule:assembleDebug
./gradlew :modules:MyModule:installDebug

# 3. Make code changes, then rebuild
./gradlew :modules:MyModule:assembleDebug
# Hot-reload automatically applies changes!

# 4. Run tests
./gradlew :modules:MyModule:testDebug

# 5. Generate release APK when ready
./gradlew :modules:MyModule:assembleRelease
```

### APK Outputs and Verification

```bash
# Check APK build outputs
find modules/*/build/outputs/apk -name "*.apk" -type f

# Verify APK signing
apksigner verify --verbose modules/MyModule/build/outputs/apk/release/MyModule-release.apk

# Check APK contents and metadata
aapt dump badging modules/MyModule/build/outputs/apk/release/MyModule-release.apk

# Extract APK to examine structure
unzip -l modules/MyModule/build/outputs/apk/debug/MyModule-debug.apk
```

### Debugging Commands

```bash
# View build dependencies
./gradlew :modules:MyModule:dependencies

# Check for dependency conflicts
./gradlew :modules:MyModule:dependencyInsight --dependency kotlin-stdlib

# Debug annotation processing
./gradlew :modules:MyModule:assembleDebug --debug

# Clean specific module
./gradlew :modules:MyModule:clean

# Clean all modules
./gradlew clean
```

## Environment Setup Commands

### Project Initialization

```bash
# Clone repository with submodules
git clone --recursive https://github.com/your-org/LSPosedKit.git

# Or initialize submodules after cloning
git submodule update --init --recursive

# Verify environment
./gradlew tasks --all | grep -E "(assemble|install|test)"
```

### Development Tools

```bash
# Start logcat for LSPosed modules
adb logcat -s LSPK:* -v time

# Start logcat for specific module
adb logcat -s LSPK-NetworkGuard:* -v time

# Clear logs
adb logcat -c

# Monitor device storage
adb shell df -h
```

### Device Management

```bash
# List connected devices
adb devices

# Install to specific device
adb -s DEVICE_ID install modules/MyModule/build/outputs/apk/debug/MyModule-debug.apk

# Check installed packages
adb shell pm list packages | grep -E "(wobbz|module)"

# Grant runtime permissions (if needed)
adb shell pm grant com.wobbz.module.mymodule android.permission.WRITE_EXTERNAL_STORAGE
```

## Performance & Optimization

### Build Performance

```bash
# Enable parallel builds
echo "org.gradle.parallel=true" >> gradle.properties

# Enable build cache
echo "org.gradle.caching=true" >> gradle.properties

# Increase heap size
echo "org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m" >> gradle.properties

# Profile build performance
./gradlew assembleDebug --profile
```

### Module Size Optimization

```bash
# Analyze APK size
./gradlew :modules:MyModule:assembleRelease
bundletool analyze-apks --apk modules/MyModule/build/outputs/apk/release/MyModule-release.apk

# Check for unused resources
./gradlew :modules:MyModule:assembleRelease --scan

# Enable resource shrinking (use carefully with LSPosed modules)
# Modify build.gradle:
# buildTypes {
#     release {
#         shrinkResources true
#         minifyEnabled true
#     }
# }
```

## CI/CD Integration

### GitHub Actions

```yaml
name: Build and Test Modules

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
      with:
        submodules: 'recursive'
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-
    
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    
    - name: Build debug APKs
      run: ./gradlew assembleDebug
    
    - name: Run tests
      run: ./gradlew testDebug
    
    - name: Upload APKs
      uses: actions/upload-artifact@v3
      with:
        name: debug-apks
        path: modules/*/build/outputs/apk/debug/*.apk
```

### GitLab CI

```yaml
image: openjdk:17-jdk

variables:
  ANDROID_SDK_ROOT: "/opt/android-sdk"

before_script:
  - apt-get --quiet update --yes
  - apt-get --quiet install --yes wget tar unzip lib32stdc++6 lib32z1
  
stages:
  - build
  - test

build_apks:
  stage: build
  script:
    - ./gradlew assembleDebug
  artifacts:
    paths:
      - modules/*/build/outputs/apk/debug/*.apk
    expire_in: 1 week

test_modules:
  stage: test
  script:
    - ./gradlew testDebug
  artifacts:
    reports:
      junit: modules/*/build/test-results/testDebug/*.xml
```

## Troubleshooting Commands

### Common Build Issues

```bash
# Clear Gradle cache
rm -rf ~/.gradle/caches/
./gradlew clean

# Invalidate IDE caches (Android Studio)
# File → Invalidate Caches and Restart

# Check for conflicting dependencies
./gradlew :modules:MyModule:dependencyInsight --dependency "group:artifact"

# Force dependency updates
./gradlew --refresh-dependencies
```

### Installation Issues

```bash
# Force uninstall before install
./gradlew :modules:MyModule:uninstallDebug :modules:MyModule:installDebug

# Check package installation status
adb shell pm list packages | grep mymodule

# Clear app data
adb shell pm clear com.wobbz.module.mymodule

# Restart LSPosed Manager
adb shell am force-stop org.lsposed.manager
```

### Hot-Reload Issues

```bash
# Check development server status
curl http://localhost:18777/status

# Restart development server
./gradlew stopDevServer
./gradlew runDevServer

# Check ADB connection
adb devices
adb shell echo "Connected"

# Verify hot-reload ports
adb shell netstat -tuln | grep 18777
```

This comprehensive CLI reference should provide all the commands needed for effective LSPosed module development with the corrected standalone APK architecture.