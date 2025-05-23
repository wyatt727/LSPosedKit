# Packaging & Release Management

> Complete guide to signing, packaging, and distributing LSPosedKit modules as **standalone APKs**.

## Overview

LSPosedKit provides a streamlined workflow for packaging and releasing modules. This guide covers:

1. **Signing Configuration**: Setting up signing keys for release APKs
2. **Version Management**: Managing version numbers across files
3. **APK Generation**: Building standalone, installable module APKs
4. **Release Channels**: Options for distributing your module APKs
5. **Metadata Management**: Ensuring consistent metadata across files

**Key Concept**: Each LSPosed module is packaged as a **standalone Android APK** containing the entire LSPosedKit framework embedded within it. No external dependencies or "host" APKs are required.

## Signing Configuration

### Setting Up Signing Keys

Before releasing modules, you need to create and configure signing keys for your APKs:

#### 1. Generate a Keystore

```bash
keytool -genkeypair -v -keystore module-release.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000
```

This command will:
- Create a keystore file named `module-release.jks`
- Create a key with alias `upload`
- Use RSA encryption with 2048-bit key size
- Set validity for 10,000 days (~27 years)

You'll be prompted to:
- Create a keystore password
- Enter your name, organization, and location
- Create a key password (can be the same as keystore password)

**Note**: Store this keystore file securely and never commit it to version control.

#### 2. Configure Gradle Properties

Create or edit `~/.gradle/gradle.properties` (user-level) to store credentials:

```properties
# Signing config
LSPK_STORE_FILE=/path/to/module-release.jks
LSPK_STORE_PASSWORD=your_keystore_password
LSPK_KEY_ALIAS=upload
LSPK_KEY_PASSWORD=your_key_password
```

This keeps sensitive information out of your project repository.

#### 3. Configure Project-Level Gradle

In your project's `build.gradle`, add:

```groovy
// Read signing config from gradle.properties
def keystorePropertiesFile = rootProject.file("keystore.properties")
def keystoreProperties = new Properties()

// Try to load from file if it exists (CI/CD environments)
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
}

// Project-wide signing configuration
ext {
    signingConfig = [
        storeFile: System.getenv("LSPK_STORE_FILE") ?: 
                   keystoreProperties.getProperty("LSPK_STORE_FILE") ?: 
                   System.getProperty("user.home") + "/.android/module-release.jks",
        storePassword: System.getenv("LSPK_STORE_PASSWORD") ?: 
                       keystoreProperties.getProperty("LSPK_STORE_PASSWORD") ?: "",
        keyAlias: System.getenv("LSPK_KEY_ALIAS") ?: 
                  keystoreProperties.getProperty("LSPK_KEY_ALIAS") ?: "upload",
        keyPassword: System.getenv("LSPK_KEY_PASSWORD") ?: 
                     keystoreProperties.getProperty("LSPK_KEY_PASSWORD") ?: ""
    ]
}
```

#### 4. Configure Module-Level Gradle (Android Application)

In each module's `build.gradle`:

```groovy
plugins {
    id 'com.android.application'  // 🔥 APPLICATION, not library
    id 'org.jetbrains.kotlin.android'
    id 'org.jetbrains.kotlin.kapt'
}

android {
    namespace 'com.wobbz.module.debugapp'
    compileSdk 35
    
    defaultConfig {
        applicationId "com.wobbz.module.debugapp"  // 🔥 Required for APKs
        minSdk 31
        targetSdk 35
        versionCode rootProject.ext.modules.debugApp.versionCode
        versionName rootProject.ext.modules.debugApp.versionName
        
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    
    signingConfigs {
        release {
            storeFile file(rootProject.ext.signingConfig.storeFile)
            storePassword rootProject.ext.signingConfig.storePassword
            keyAlias rootProject.ext.signingConfig.keyAlias
            keyPassword rootProject.ext.signingConfig.keyPassword
        }
    }
    
    buildTypes {
        debug {
            debuggable true
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
        release {
            debuggable false
            minifyEnabled false  // LSPosed modules typically don't minify
            signingConfig signingConfigs.release
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
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

### Using the Signing Configuration

Once configured, you can build signed release APKs:

```bash
# Build signed release APK for specific module
./gradlew :modules:DebugApp:assembleRelease

# Build all module release APKs
./gradlew assembleRelease

# Install release APK to device
./gradlew :modules:DebugApp:installRelease
```

For CI/CD environments, you can pass credentials directly:

```bash
./gradlew assembleRelease \
    -PLSPK_STORE_FILE=/path/to/keystore.jks \
    -PLSPK_STORE_PASSWORD=password \
    -PLSPK_KEY_ALIAS=upload \
    -PLSPK_KEY_PASSWORD=password
```

## Version Management

Maintaining consistent versions across your module is essential:

### Single Source of Truth for Versions

Create a Gradle file for version management, e.g., `versions.gradle`:

```groovy
ext {
    // Module versions
    modules = [
        debugApp: [
            versionName: "1.0.0",
            versionCode: 1
        ],
        networkGuard: [
            versionName: "2.1.0", 
            versionCode: 5
        ],
        intentInterceptor: [
            versionName: "1.2.0",
            versionCode: 3
        ],
        uiEnhancer: [
            versionName: "1.1.0",
            versionCode: 2
        ]
    ]
}
```

Include this in your root `build.gradle`:

```groovy
apply from: 'versions.gradle'
```

### Synchronizing Versions

Your module's version should be consistent across:
1. `@XposedPlugin` annotation
2. `module-info.json`  
3. APK manifest (`versionName` and `versionCode`)

Use Gradle to ensure consistency:

```groovy
// In module's build.gradle
android {
    defaultConfig {
        versionCode rootProject.ext.modules.debugApp.versionCode
        versionName rootProject.ext.modules.debugApp.versionName
    }
}

// Use annotation processor to ensure @XposedPlugin version matches
kapt {
    arguments {
        arg("moduleVersion", rootProject.ext.modules.debugApp.versionName)
    }
}

// Generate module-info.json with correct version
task generateModuleInfo {
    doLast {
        def moduleInfo = [
            id: "debug-app",
            version: rootProject.ext.modules.debugApp.versionName,
            // Other fields...
        ]
        
        def moduleInfoFile = file("src/main/assets/module-info.json")
        moduleInfoFile.text = groovy.json.JsonOutput.prettyPrint(
            groovy.json.JsonOutput.toJson(moduleInfo)
        )
    }
}

preBuild.dependsOn generateModuleInfo
```

## APK Generation and Distribution

### Building Release APKs

```bash
# Build all module APKs
./gradlew assembleRelease

# Build specific module APK
./gradlew :modules:NetworkGuard:assembleRelease

# Output location
ls modules/*/build/outputs/apk/release/*.apk
```

### APK Structure

Each generated APK contains:

```
NetworkGuard-release.apk
├── META-INF/
│   ├── MANIFEST.MF
│   ├── CERT.SF          ← Your signing certificate
│   └── CERT.RSA         ← Your signing key
├── classes.dex          ← Module code + embedded LSPosedKit framework
├── assets/
│   ├── module.prop      ← Generated: LSPosed metadata
│   ├── xposed_init      ← Generated: Entry point class
│   ├── module-info.json ← Generated: Extended metadata 
│   └── settings.json    ← Your settings schema
├── res/                 ← Any Android resources
└── AndroidManifest.xml  ← Application manifest
```

### Distribution Options

#### Option 1: Direct APK Distribution

Share the APK files directly:

```bash
# Share via file hosting
cp modules/NetworkGuard/build/outputs/apk/release/NetworkGuard-release.apk ~/Downloads/
```

**Installation**: `adb install NetworkGuard-release.apk` or side-load manually

#### Option 2: GitHub Releases

Automate APK distribution via GitHub releases:

```yaml
# .github/workflows/release.yml
name: Release APKs

on:
  push:
    tags:
      - 'v*'

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
    
    - name: Build Release APKs
      run: ./gradlew assembleRelease
      env:
        LSPK_STORE_FILE: ${{ secrets.KEYSTORE_BASE64 }}
        LSPK_STORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
        LSPK_KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
        LSPK_KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
    
    - name: Create Release
      uses: actions/create-release@v1
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
      with:
        tag_name: ${{ github.ref }}
        release_name: Release ${{ github.ref }}
        draft: false
        prerelease: false
    
    - name: Upload APKs
      run: |
        for apk in modules/*/build/outputs/apk/release/*.apk; do
          gh release upload ${{ github.ref }} "$apk"
        done
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

#### Option 3: F-Droid (Advanced)

For open-source modules, consider F-Droid distribution:

1. Create F-Droid metadata file
2. Submit to F-Droid repository
3. Follow F-Droid guidelines for reproducible builds

### Quality Assurance

#### APK Verification

```bash
# Verify APK signing
apksigner verify --verbose modules/NetworkGuard/build/outputs/apk/release/NetworkGuard-release.apk

# Check APK contents
aapt dump badging modules/NetworkGuard/build/outputs/apk/release/NetworkGuard-release.apk

# Extract and examine
unzip -l modules/NetworkGuard/build/outputs/apk/release/NetworkGuard-release.apk
```

#### Testing Checklist

Before distributing APKs:

- [ ] **Build Verification**: APK builds successfully with release signing
- [ ] **Installation Test**: APK installs cleanly on test device
- [ ] **LSPosed Recognition**: Module appears in LSPosed Manager
- [ ] **Functionality Test**: Core module features work as expected  
- [ ] **Hot-Reload Test**: Hot-reload functionality works (if enabled)
- [ ] **Settings Test**: Settings UI generates correctly from schema
- [ ] **Logs Test**: Module logs appear with correct tags
- [ ] **Uninstall Test**: Module uninstalls cleanly

## Advanced Packaging

### Custom Gradle Tasks

Create custom tasks for packaging workflows:

```groovy
// In module's build.gradle

task packageForDistribution(type: Copy, dependsOn: assembleRelease) {
    description = "Package module APK with documentation for distribution"
    group = "distribution"
    
    from("build/outputs/apk/release") {
        include "*.apk"
    }
    from("src/main/assets") {
        include "README.md"
        include "CHANGELOG.md"
    }
    
    into("$buildDir/dist")
    
    doLast {
        println "Distribution package created at: $buildDir/dist"
    }
}

task generateChecksums(type: Exec, dependsOn: packageForDistribution) {
    description = "Generate SHA256 checksums for APK files"
    group = "distribution"
    
    workingDir "$buildDir/dist"
    commandLine "sh", "-c", "sha256sum *.apk > checksums.txt"
}
```

### Module Dependencies

If your modules depend on each other (rare), ensure proper version constraints:

```json
// module-info.json
{
  "id": "advanced-module",
  "dependencies": {
    "network-guard": "^2.0.0",
    "intent-interceptor": ">=1.1.0"
  }
}
```

Note: Dependencies are checked at runtime, not at APK install time.

## Conclusion

LSPosedKit's packaging system produces **standalone APKs** that can be distributed and installed independently. Each APK contains:

- ✅ Your module implementation
- ✅ Complete LSPosedKit framework (embedded)
- ✅ Generated metadata files
- ✅ Settings and UI resources
- ✅ Proper Android application structure

This approach eliminates dependency management issues and makes distribution straightforward — just share the APK file! 