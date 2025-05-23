# Getting Started with LSPosedKit

> Complete guide to setting up your development environment, building your first **standalone module APK**, and understanding the LSPosedKit workflow.

## System Requirements

### Development Environment

| Component               | Requirement                             | Notes                                       |
|-------------------------|----------------------------------------|---------------------------------------------|
| **Operating System**    | Windows, macOS, or Linux               | Any OS that supports Android development    |
| **IDE**                 | Android Studio Hedgehog Canary (or newer) | Recommended for best Android 15 support     |
|                         | Cursor.ai                              | Alternative with AI assistance              |
| **JDK**                 | JDK 17 (Temurin/Eclipse Adoptium)      | Required minimum                            |
|                         | JDK 21 (optional)                      | Enable with `-Pjava21` flag                 |
| **Android SDK**         | API 35 (Android 15) SDK                | Required for full feature support           |
| **Gradle**              | 8.4 or newer                           | Required for Android 15 build support       |
| **Build Tools**         | 35.0.0 or newer                        | Needed for API 35 compatibility             |

### Test Device Requirements

| Component               | Requirement                             | Notes                                       |
|-------------------------|----------------------------------------|---------------------------------------------|
| **Android Version**     | Android 15 (API 35)                    | Recommended for full feature set            |
|                         | Android 12-14 (API 31-34)              | Compatible with limitations                 |
| **Device Status**       | Rooted                                 | Required for LSPosed framework              |
| **LSPosed Framework**   | v1.9.0 or newer                        | Required for module execution               |
| **LSPosed Manager**     | Latest version                         | Required for module management              |

## Environment Setup

### Step 1: IDE and SDK Configuration

1. **Install Android Studio Hedgehog** (or newer):
   - Download from [Android Developer site](https://developer.android.com/studio/preview)
   - During installation, ensure the Android SDK is installed

2. **Configure SDK Components**:
   - Open Android Studio → Settings/Preferences → Appearance & Behavior → System Settings → Android SDK
   - Install or update:
     - Android 15 (API 35) SDK Platform
     - Android SDK Build-Tools 35.0.0
     - Android SDK Command-line Tools
     - Android SDK Platform-Tools

3. **Set up JDK 17**:
   - Download and install [Eclipse Temurin JDK 17](https://adoptium.net/temurin/releases/?version=17)
   - Configure in Android Studio: Settings/Preferences → Build, Execution, Deployment → Build Tools → Gradle
   - Set "Gradle JDK" to your JDK 17 installation

### Step 2: Prepare Test Device

1. **Root your device** (if not already rooted)
   - Method varies by device manufacturer
   - Recommended: Use [Magisk](https://github.com/topjohnwu/Magisk)

2. **Install LSPosed Framework**:
   - Install latest [LSPosed Magisk Module](https://github.com/LSPosed/LSPosed/releases)
   - Reboot your device
   - Verify LSPosed Manager is installed

3. **Enable USB Debugging**:
   - Settings → System → Developer options → USB debugging
   - Connect to computer and authorize debugging

## Cloning and Initial Setup

```bash
# Clone the repository
git clone https://github.com/your-org/LSPosedKit.git

# Navigate to project directory
cd LSPosedKit

# Initialize and update submodules (for libxposed-api)
git submodule update --init --recursive

# Optional: Verify your environment
./gradlew environmentCheck
```

## Build and Install Standalone Module APKs

**Key Concept**: Each LSPosed module is built as a standalone APK containing the LSPosedKit framework embedded within it.

```bash
# Build all modules as standalone APKs
./gradlew assembleDebug

# Build specific module APK
./gradlew :modules:DebugApp:assembleDebug

# Install specific module APK to connected device 
./gradlew :modules:DebugApp:installDebug

# Or install directly with adb
adb install modules/DebugApp/build/outputs/apk/debug/DebugApp-debug.apk

# Install all module APKs
./gradlew installDebug

# Optional: Refresh package database
adb shell cmd package reconcile

# View logs during testing
adb logcat -s LSPK:* -v time
```

## Module Activation

**Important**: No "LSPK Host" module needed — each module is completely self-contained!

1. Open **LSPosed Manager** on your device
2. Navigate to **Modules** tab
3. Enable your installed modules (e.g., "DebugApp", "NetworkGuard")
4. For each module, select target applications in scope
5. Tap the check (✓) icon to apply changes
6. Reboot your device when prompted

## Verifying Installation

After reboot, check for proper initialization:

```bash
# Filter for module-specific logs
adb logcat -s LSPK-DebugApp:V | grep "\[INIT\]"
```

Expected output:
```
05-22 12:34:56.789 LSPK-DebugApp I [INIT] DebugApp loaded for com.android.systemui
```

## Creating Your First Module

### Option 1: Use the Scaffold Script (Recommended)

```bash
# Create a new module with standardized template
./gradlew :scripts:newModule -PmoduleName="DebugApp" -Pid="debug-app"

# With optional description and author
./gradlew :scripts:newModule \
  -PmoduleName="DebugApp" \
  -Pid="debug-app" \
  -Pdescription="Force enable debug flags in apps" \
  -Pauthor="Your Name"
```

This **automatically generates everything** needed for a standalone APK module:

**📁 Directory Structure**:
- Module directory structure under `modules/DebugApp/`
- Source directories: `src/main/java/`, `src/test/java/`, `src/main/assets/`
- Resource directories: `src/main/res/values/`, `src/main/res/mipmap-mdpi/`

**🔧 Application Configuration**:
- **Android application** Gradle configuration (not library!)
- Complete `build.gradle` with embedded framework dependencies
- `AndroidManifest.xml` with LSPosed metadata and launcher activity

**📱 Android Resources**:
- `strings.xml` with module name and description
- `arrays.xml` with xposed_scope configuration
- `ic_launcher.xml` vector drawable launcher icon
- `MainActivity.kt` for settings access (customizable)

**💻 Code Templates**:
- Main module class with `@XposedPlugin` annotation
- Example hooks manager with proper `PackageLoadedParam` access
- Unit test template with mock utilities
- Complete module implementation skeleton

**📄 Metadata Files**:
- `settings.json` schema with package filtering and logging options
- `module-info.json` with capabilities and service declarations
- `README.md` with module-specific documentation

**🎯 Result**: A complete, buildable standalone APK module ready for customization!

### Option 2: Manual Setup

1. Create module directory:
   ```bash
   mkdir -p modules/debug-app/src/main/{java,assets,res}
   ```

2. Create Gradle build file (`modules/debug-app/build.gradle`):
   ```groovy
   plugins {
       id 'com.android.application'  // 🔥 APPLICATION, not library!
       id 'org.jetbrains.kotlin.android'
       id 'org.jetbrains.kotlin.kapt'
   }
   
   android {
       namespace 'com.example.debugapp'
       compileSdk 35
       
       defaultConfig {
           applicationId "com.example.debugapp"  // 🔥 Required for APKs
           minSdk 31
           targetSdk 35
           versionCode 1                         // 🔥 Required for APKs
           versionName "1.0.0"                   // 🔥 Required for APKs
           
           testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
       }
       
       buildTypes {
           debug {
               debuggable true
               minifyEnabled false
           }
           release {
               debuggable false
               minifyEnabled false  // LSPosed modules typically don't minify
           }
       }
   }
   
   dependencies {
       // Framework gets EMBEDDED in the APK
       implementation project(':framework')
       kapt project(':framework:processor')
       
       // Standard dependencies
       implementation "org.jetbrains.kotlin:kotlin-stdlib:${rootProject.ext.kotlinVersion}"
       implementation 'androidx.annotation:annotation:1.5.0'
   }
   ```

3. Create minimal module implementation:
   ```kotlin
   // src/main/java/com/example/debugapp/DebugApp.kt
   package com.example.debugapp
   
   import com.wobbz.framework.core.IModulePlugin
   import com.wobbz.framework.hot.IHotReloadable
   import com.wobbz.framework.processor.HotReloadable
   import com.wobbz.framework.processor.XposedPlugin
   
   @XposedPlugin(
     id = "debug-app",
     name = "Debug App",
     version = "1.0.0",
     scope = ["*"],
     description = "My first LSPosedKit module",
     author = "Your Name"
   )
   @HotReloadable
   class DebugApp : IModulePlugin, IHotReloadable {
     override fun onPackageLoaded(param: PackageLoadedParam) {
       // Your hook logic goes here
     }
     
     override fun onHotReload() {
       // Your hot-reload logic goes here
     }
   }
   ```

4. Register in `settings.gradle`:
   ```groovy
   include ':modules:debug-app'
   ```

5. **Build and install the standalone APK**:
   ```bash
   # Build your module APK
   ./gradlew :modules:debug-app:assembleDebug
   
   # Install to device
   ./gradlew :modules:debug-app:installDebug
   ```

## Architecture Understanding

### Standalone APK Structure

Each module APK contains:

```
debug-app.apk
├── META-INF/
├── classes.dex           ← Your module code + embedded framework
├── assets/
│   ├── module.prop       ← Generated by scaffolding script & annotation processor
│   ├── xposed_init       ← Generated by scaffolding script (points to LSPosedEntry bridge)
│   ├── module-info.json  ← Generated by scaffolding script & annotation processor
│   └── settings.json     ← Your settings schema
├── res/                  ← Any resources your module needs
└── AndroidManifest.xml   ← Application manifest
```

### No Dependencies

**Unlike traditional LSPosed development**:
- ❌ No need for separate "LSPosedKit Host" APK
- ❌ No runtime dependencies on external modules
- ✅ Each module APK is completely self-contained
- ✅ Install any module independently: `adb install MyModule.apk`
- ✅ Framework code embedded in each APK

## Next Steps

1. **Explore Sample Modules**: Check out `modules/NetworkGuard/`, `modules/IntentInterceptor/`, etc.
2. **Read Documentation**: Browse `Docs/` for detailed guides on settings, hot-reload, service bus, etc.
3. **Start Development**: Use hot-reload for rapid iteration during development
4. **Package for Release**: Generate signed APKs for distribution

## Troubleshooting

### Build Issues

**Error**: "This project uses Android Gradle plugin 8.3.0..."
**Solution**: Ensure you're using Gradle 8.4+ and JDK 17

**Error**: "Cannot resolve symbol 'IModulePlugin'"  
**Solution**: Verify the framework dependency is included: `implementation project(':framework')`

### Installation Issues

**Error**: "App not installed" when using `adb install`
**Solution**: Uninstall any previous version: `adb uninstall com.example.debugapp`

**Module not showing in LSPosed Manager**
**Solution**: 
1. Verify the APK was installed: `adb shell pm list packages | grep debugapp`
2. Check that `xposed_init` and `module.prop` are in `assets/` (auto-generated by scaffolding script)
3. For scaffolded modules: Verify `xposed_init` points to the correct LSPosedEntry bridge class
4. Restart LSPosed Manager

For more troubleshooting, see [Docs/15-troubleshooting.md](15-troubleshooting.md). 