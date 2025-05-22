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

// Framework modules
include ':framework:core'
include ':framework:processor'
include ':framework:hot'
include ':framework:settings'
include ':framework'

// libxposed-api
include ':libxposed-api:api'

// Sample modules will be included here
// Example: include ':modules:DebugApp'

// Find all modules in modules directory
file('modules').eachDir { dir ->
    include ":modules:${dir.name}"
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

## Next Steps

After completing the repository setup:

1. Implement the core interfaces and classes as defined in the API reference
2. Create the annotation processor for generating module metadata
3. Implement the hot-reload system
4. Create the settings management system
5. Build the sample modules

Refer to the respective implementation guides for details on each component. 