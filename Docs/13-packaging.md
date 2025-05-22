# Packaging & Release Management

> Complete guide to signing, packaging, and distributing LSPosedKit modules as `.lspkmod` bundles.

## Overview

LSPosedKit provides a streamlined workflow for packaging and releasing modules. This guide covers:

1. **Signing Configuration**: Setting up signing keys for release builds
2. **Version Management**: Managing version numbers across files
3. **`.lspkmod` Bundle Creation**: Packaging modules for distribution
4. **Release Channels**: Options for distributing your modules
5. **Metadata Management**: Ensuring consistent metadata across files

## Signing Configuration

### Setting Up Signing Keys

Before releasing modules, you need to create and configure signing keys:

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

#### 4. Configure Module-Level Gradle

In each module's `build.gradle`:

```groovy
android {
    signingConfigs {
        release {
            storeFile file(rootProject.ext.signingConfig.storeFile)
            storePassword rootProject.ext.signingConfig.storePassword
            keyAlias rootProject.ext.signingConfig.keyAlias
            keyPassword rootProject.ext.signingConfig.keyPassword
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### Using the Signing Configuration

Once configured, you can build signed releases:

```bash
./gradlew assembleRelease
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

### Semantic Versioning

Follow [Semantic Versioning](https://semver.org/) (SemVer) for your modules:

- **MAJOR**: Incompatible API changes
- **MINOR**: Backward-compatible functionality additions
- **PATCH**: Backward-compatible bug fixes
- **Pre-release**: `-alpha.1`, `-beta.2`, `-rc.1`

Example versioning:
- `1.0.0`: Initial release
- `1.0.1`: Bug fixes
- `1.1.0`: New features, backward-compatible
- `2.0.0`: Breaking changes
- `1.1.0-beta.1`: Beta release of new features

## Creating `.lspkmod` Bundles

`.lspkmod` bundles are the standard distribution format for LSPosedKit modules:

### Bundle Structure

A `.lspkmod` bundle is essentially a ZIP file containing:

```
module-name.lspkmod/
├── module.apk              # The signed APK file
├── module-info.json        # Module metadata
├── changelog.md            # Release notes (optional)
├── icon.png                # Module icon (optional)
└── META-INF/
    └── signatures.json     # Bundle signature information
```

### Building Bundles with Gradle

LSPosedKit provides Gradle tasks for bundle creation:

```groovy
// In module's build.gradle
tasks.register('createBundle') {
    dependsOn 'assembleRelease'
    
    doLast {
        def bundleName = "debug-app-${android.defaultConfig.versionName}.lspkmod"
        def bundleDir = file("$buildDir/bundle")
        def bundleFile = file("$buildDir/outputs/bundle/$bundleName")
        
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
        
        // Copy metadata
        copy {
            from "src/main/assets"
            include "module-info.json"
            into bundleDir
        }
        
        // Copy changelog if it exists
        if (file("CHANGELOG.md").exists()) {
            copy {
                from "CHANGELOG.md"
                into bundleDir
            }
        }
        
        // Create bundle zip
        file("$buildDir/outputs/bundle").mkdirs()
        ant.zip(destfile: bundleFile, basedir: bundleDir)
        
        println "Created bundle: $bundleFile"
    }
}
```

### Using the Bundle Task

```bash
# Create bundle for a specific module
./gradlew :modules:debug-app:createBundle

# Create bundles for all modules
./gradlew createBundle
```

### Simplified Bundle Creation

LSPosedKit's framework module includes a pre-configured task:

```bash
# Create bundle with default settings
./gradlew :modules:debug-app:publishBundle

# Override settings
./gradlew :modules:debug-app:publishBundle -PbundleVersion=1.0.0-beta1
```

The `publishBundle` task:
1. Builds a release APK
2. Validates module metadata
3. Creates a properly structured bundle
4. Signs the bundle
5. Places the output in `dist/`

## Bundle Signing and Verification

LSPosedKit bundles include integrity verification:

### Signing Bundles

```groovy
// In module's build.gradle
tasks.register('signBundle') {
    dependsOn 'createBundle'
    
    doLast {
        def bundleName = "debug-app-${android.defaultConfig.versionName}.lspkmod"
        def bundleFile = file("$buildDir/outputs/bundle/$bundleName")
        
        // Generate signature file
        exec {
            commandLine 'java', '-jar', '../tools/bundlesigner.jar', 
                     'sign', 
                     '--keystore', rootProject.ext.signingConfig.storeFile,
                     '--storepass', rootProject.ext.signingConfig.storePassword,
                     '--keyalias', rootProject.ext.signingConfig.keyAlias,
                     '--keypass', rootProject.ext.signingConfig.keyPassword,
                     bundleFile
        }
        
        println "Signed bundle: $bundleFile"
    }
}
```

### Verifying Bundles

```bash
# Verify a bundle's integrity
java -jar tools/bundlesigner.jar verify path/to/module.lspkmod
```

## Release Channels

Several options exist for distributing your module bundles:

### GitHub Releases

1. Create a GitHub release
2. Upload the `.lspkmod` bundle
3. Add release notes and changelog

Example GitHub Action:

```yaml
name: Release Module

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
          
      - name: Import GPG key
        uses: crazy-max/ghaction-import-gpg@v5
        with:
          gpg_private_key: ${{ secrets.GPG_PRIVATE_KEY }}
          passphrase: ${{ secrets.GPG_PASSPHRASE }}
          
      - name: Import keystore
        run: |
          echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > keystore.jks
          
      - name: Build bundle
        run: |
          ./gradlew :modules:debug-app:publishBundle \
            -PLSPK_STORE_FILE=keystore.jks \
            -PLSPK_STORE_PASSWORD=${{ secrets.KEYSTORE_PASSWORD }} \
            -PLSPK_KEY_ALIAS=upload \
            -PLSPK_KEY_PASSWORD=${{ secrets.KEY_PASSWORD }}
            
      - name: Create Release
        uses: softprops/action-gh-release@v1
        with:
          files: dist/*.lspkmod
          draft: false
          prerelease: false
```

### LSPosed Repository

If you want your module included in the official LSPosed repository:

1. Create a pull request to the [LSPosed Module Repository](https://github.com/LSPosed/LSPosed-Repo)
2. Include your module metadata and bundle URL
3. Follow the repository guidelines for submission

### Custom Distribution

For private or enterprise distribution:

1. Set up a web server to host `.lspkmod` files
2. Create a JSON API for module discovery
3. Implement a custom module repository

## Bundle Metadata Management

Proper metadata is crucial for module discoverability and user experience:

### Required Metadata

Your `module-info.json` should include:

```json
{
  "$schema": "https://json-schema.org/draft-07/schema#",
  "id": "debug-app",
  "version": "1.0.0",
  "name": "Debug App",
  "author": "Your Name",
  "description": "Force-enable debugging flags for all applications",
  "minHostVersion": "1.0.0",
  "features": [
    "app.debugging"
  ],
  "screenshots": [
    "https://example.com/screenshots/screenshot1.png",
    "https://example.com/screenshots/screenshot2.png"
  ],
  "changelog": "https://example.com/changelog",
  "website": "https://example.com/debug-app",
  "source": "https://github.com/yourusername/debug-app",
  "support": "https://github.com/yourusername/debug-app/issues",
  "donate": "https://example.com/donate"
}
```

### Icon Requirements

For optimal display in LSPosed Manager:

- Format: PNG
- Size: 192×192 pixels
- Filename: `icon.png`
- Location: Bundle root directory

### Changelog Format

Include a `CHANGELOG.md` in your bundle:

```markdown
# Changelog

## [1.0.0] - 2023-06-01

### Added
- Initial release
- Support for enabling debug flags in all applications

### Fixed
- Properly handle system applications

## [0.9.0] - 2023-05-15

### Added
- Beta preview release
```

## Advanced Packaging Options

### ProGuard/R8 Configuration

For optimal release builds, configure ProGuard:

Create `proguard-rules.pro` in your module directory:

```proguard
# Keep annotations
-keep @com.wobbz.framework.processor.XposedPlugin class * {*;}
-keep @com.wobbz.framework.processor.HotReloadable class * {*;}
-keep @com.wobbz.framework.processor.SettingsKey class * {*;}

# Keep interfaces
-keep interface com.wobbz.framework.core.IModulePlugin {*;}
-keep interface com.wobbz.framework.hot.IHotReloadable {*;}
-keep interface com.wobbz.framework.settings.SettingsProvider {*;}

# Keep all hookers
-keep class * implements de.robv.android.xposed.callback.XC_LoadPackage$Hooker {*;}

# Keep settings classes
-keepclassmembers class * {
    @com.wobbz.framework.processor.SettingsKey *;
}

# Keep module main class and its methods
-keep class * implements com.wobbz.framework.core.IModulePlugin {
    public <methods>;
}
```

Enable ProGuard in your module's `build.gradle`:

```groovy
android {
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### Asset Bundling

Include additional assets in your module:

```groovy
android {
    sourceSets {
        main {
            assets {
                srcDirs = ['src/main/assets', 'assets']
            }
        }
    }
}
```

Create an `assets` directory with:
- Documentation
- Configuration templates
- Sample data

### Localization

For international users, include localized resources:

```
src/main/res/
├── values/
│   └── strings.xml          # Default (English)
├── values-es/
│   └── strings.xml          # Spanish
├── values-ja/
│   └── strings.xml          # Japanese
└── values-ru/
    └── strings.xml          # Russian
```

Configure your `settings.json` to use resource strings:

```json
{
  "properties": {
    "enable_debug": {
      "type": "boolean",
      "title": "@string/debug_enable_title",
      "description": "@string/debug_enable_description",
      "default": true
    }
  }
}
```

### Flavor Variants

Create different variants of your module:

```groovy
android {
    flavorDimensions "version"
    productFlavors {
        free {
            dimension "version"
            applicationIdSuffix ".free"
            versionNameSuffix "-free"
        }
        pro {
            dimension "version"
            applicationIdSuffix ".pro"
            versionNameSuffix "-pro"
        }
    }
}
```

Build flavor-specific bundles:

```bash
./gradlew :modules:debug-app:publishFreeBundle
./gradlew :modules:debug-app:publishProBundle
```

## Release Checklist

Before releasing a module, verify:

1. **Version Numbers**:
   - `@XposedPlugin` annotation version
   - `module-info.json` version
   - Android manifest versionCode and versionName

2. **Metadata**:
   - Accurate description
   - Proper feature tags
   - Correct compatibility information

3. **Documentation**:
   - Updated README
   - Updated CHANGELOG
   - User guide

4. **Testing**:
   - Unit tests pass
   - Integration tests pass
   - Manual testing on target device(s)
   - Tested with target applications

5. **Signing**:
   - Signed with release key
   - Bundle verified

6. **Distribution**:
   - Checksums generated
   - Release notes prepared
   - Upload destinations configured

## Continuous Delivery

Automate your release process:

### Semantic Release

Install dependencies:

```bash
npm install -g semantic-release @semantic-release/git @semantic-release/changelog
```

Create `.releaserc.json`:

```json
{
  "branches": ["main"],
  "plugins": [
    "@semantic-release/commit-analyzer",
    "@semantic-release/release-notes-generator",
    "@semantic-release/changelog",
    ["@semantic-release/exec", {
      "prepareCmd": "./gradlew :modules:debug-app:publishBundle -PbundleVersion=${nextRelease.version}"
    }],
    ["@semantic-release/github", {
      "assets": [
        {"path": "dist/debug-app-*.lspkmod", "label": "Debug App Module"}
      ]
    }],
    "@semantic-release/git"
  ]
}
```

Configure GitHub Actions:

```yaml
name: Release

on:
  push:
    branches:
      - main

jobs:
  release:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
        with:
          fetch-depth: 0
          
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: 16
          
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          
      - name: Import keystore
        run: |
          echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > keystore.jks
          
      - name: Install semantic-release
        run: npm install -g semantic-release @semantic-release/git @semantic-release/changelog @semantic-release/exec
          
      - name: Release
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          LSPK_STORE_FILE: keystore.jks
          LSPK_STORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          LSPK_KEY_ALIAS: upload
          LSPK_KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
        run: npx semantic-release
```

## Conclusion

Proper packaging and release management ensures your modules are:

- **Reliable**: Properly signed and verified
- **Discoverable**: Well-documented with accurate metadata
- **Maintainable**: Versioned according to best practices
- **Professional**: Polished and ready for distribution

By following this guide, you'll create high-quality releases that users can confidently install and enjoy. 