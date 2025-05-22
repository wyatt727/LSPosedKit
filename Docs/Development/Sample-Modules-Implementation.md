# Sample Modules Implementation Guide

This document provides detailed implementation instructions for creating the sample modules of LSPosedKit. These modules demonstrate common use cases and serve as reference implementations for module developers.

## Overview of Sample Modules

LSPosedKit includes the following sample modules:

1. **DebugApp** - Forces applications to run in debug mode
2. **NetworkGuard** - Monitors and controls network traffic
3. **IntentInterceptor** - Monitors and intercepts intents
4. **UIEnhancer** - Customizes UI elements and behaviors

Each module demonstrates different aspects of the LSPosedKit framework:

| Module | Focus Areas | Key Features |
|--------|------------|--------------|
| DebugApp | Core hooking, Simple module | Single-purpose, Hot-reload |
| NetworkGuard | Service integration, Settings | Complex hooks, Rule system |
| IntentInterceptor | UI interactions, Persistence | Intent monitoring, History viewing |
| UIEnhancer | Resource replacement, Callbacks | UI customization, Layout injection |

## Module Directory Structure

Each module follows this standard directory structure:

```
modules/<module-name>/
├── src/
│   ├── main/
│   │   ├── java/com/wobbz/module/<module-package>/
│   │   │   ├── <ModuleName>.kt              # Main module class
│   │   │   ├── hooks/                       # Hook implementations
│   │   │   ├── ui/                          # Module-specific UI components (if any)
│   │   │   ├── models/                      # Data models
│   │   │   └── services/                    # Service implementations (if any)
│   │   ├── assets/
│   │   │   ├── module-info.json             # Module metadata
│   │   │   └── settings.json                # Settings schema (if needed)
│   │   └── res/                             # Resources (if needed)
│   └── test/                                # Unit tests
├── build.gradle                             # Module-specific build config
└── README.md                                # Module documentation
```

## 1. DebugApp Module Implementation

### Directory Setup

Create the module directory structure:

```bash
mkdir -p modules/DebugApp/src/main/java/com/wobbz/module/debugapp/
mkdir -p modules/DebugApp/src/main/assets/
mkdir -p modules/DebugApp/src/test/java/com/wobbz/module/debugapp/
```

### build.gradle

Create the module's build file:

```groovy
// modules/DebugApp/build.gradle
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
    id 'org.jetbrains.kotlin.kapt'
}

android {
    namespace 'com.wobbz.module.debugapp'
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
    implementation project(':framework')
    kapt project(':framework:processor')
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.mockito:mockito-core:4.0.0'
}
```

### Main Module Class

Create the main module class:

```kotlin
// modules/DebugApp/src/main/java/com/wobbz/module/debugapp/DebugApp.kt
package com.wobbz.module.debugapp

import android.content.Context
import android.content.pm.ApplicationInfo
import com.wobbz.framework.core.IModulePlugin
import com.wobbz.framework.core.LogLevel
import com.wobbz.framework.core.PackageLoadedParam
import com.wobbz.framework.hot.IHotReloadable
import com.wobbz.framework.processor.HotReloadable
import com.wobbz.framework.processor.XposedPlugin
import com.wobbz.framework.settings.SettingsProvider
import com.wobbz.module.debugapp.hooks.ApplicationInfoHooker
import com.wobbz.module.debugapp.hooks.DebugPropertiesHooker

@XposedPlugin(
    id = "debug-app",
    name = "Debug App",
    version = "1.0.0",
    description = "Forces applications to run in debug mode",
    author = "LSPosedKit",
    scope = ["*"]
)
@HotReloadable
class DebugApp : IModulePlugin, IHotReloadable {
    
    private val hooks = mutableListOf<Any>()
    private lateinit var settings: SettingsProvider
    
    override fun initialize(context: Context, xposed: com.wobbz.framework.core.XposedInterface) {
        // Initialize settings
        settings = SettingsProvider.of(context)
        
        xposed.log(LogLevel.INFO, "DebugApp initialized")
    }
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Check if we should enable debug mode for this package
        if (!shouldEnableDebug(param.packageName)) {
            return
        }
        
        try {
            // Hook ApplicationInfo.flags to add FLAG_DEBUGGABLE
            param.xposed.loadClass("android.content.pm.ApplicationInfo").let { clazz ->
                val flagsField = clazz.getDeclaredField("flags")
                flagsField.isAccessible = true
                
                // Hook the getter for flags field
                val unhooker = param.xposed.hook(flagsField, ApplicationInfoHooker::class.java)
                hooks.add(unhooker)
                
                param.xposed.log(LogLevel.INFO, "Hooked ApplicationInfo.flags for ${param.packageName}")
            }
            
            // Hook system properties to enable additional debug features
            hookDebugProperties(param)
            
        } catch (e: Exception) {
            param.xposed.logError("Error setting up DebugApp hooks", e)
        }
    }
    
    private fun hookDebugProperties(param: PackageLoadedParam) {
        try {
            // Hook android.os.SystemProperties
            param.xposed.loadClass("android.os.SystemProperties").let { clazz ->
                val getMethod = clazz.getDeclaredMethod("get", String::class.java, String::class.java)
                val unhooker = param.xposed.hook(
                    clazz, 
                    "get", 
                    arrayOf(String::class.java, String::class.java), 
                    DebugPropertiesHooker::class.java
                )
                hooks.add(unhooker)
                
                param.xposed.log(LogLevel.INFO, "Hooked SystemProperties.get for ${param.packageName}")
            }
        } catch (e: Exception) {
            // SystemProperties might not be available in all Android versions
            param.xposed.log(LogLevel.WARN, "Could not hook SystemProperties: ${e.message}")
        }
    }
    
    private fun shouldEnableDebug(packageName: String): Boolean {
        // Check if this package should be enabled based on settings
        val enabledForAll = settings.bool("enable_for_all", true)
        
        if (enabledForAll) {
            return !isExcluded(packageName)
        }
        
        return isIncluded(packageName)
    }
    
    private fun isExcluded(packageName: String): Boolean {
        // Check if package is in the exclusion list
        val excludedPackages = settings.stringSet("excluded_packages", emptySet())
        return excludedPackages.contains(packageName)
    }
    
    private fun isIncluded(packageName: String): Boolean {
        // Check if package is in the inclusion list
        val includedPackages = settings.stringSet("included_packages", emptySet())
        return includedPackages.contains(packageName)
    }
    
    override fun onHotReload() {
        // Clean up existing hooks
        hooks.forEach { unhooker ->
            if (unhooker is com.wobbz.framework.core.MethodUnhooker<*>) {
                unhooker.unhook()
            }
        }
        hooks.clear()
    }
}
```

### Hook Implementations

Create the hook implementations:

```kotlin
// modules/DebugApp/src/main/java/com/wobbz/module/debugapp/hooks/ApplicationInfoHooker.kt
package com.wobbz.module.debugapp.hooks

import android.content.pm.ApplicationInfo
import com.wobbz.framework.core.HookParam
import com.wobbz.framework.core.Hooker

class ApplicationInfoHooker : Hooker {
    override fun afterHook(param: HookParam) {
        // Get the current flags
        val flags = param.getResult<Int>() ?: 0
        
        // Add the debuggable flag
        val newFlags = flags or ApplicationInfo.FLAG_DEBUGGABLE
        
        // Set the new flags
        param.setResult(newFlags)
    }
}
```

```kotlin
// modules/DebugApp/src/main/java/com/wobbz/module/debugapp/hooks/DebugPropertiesHooker.kt
package com.wobbz.module.debugapp.hooks

import com.wobbz.framework.core.HookParam
import com.wobbz.framework.core.Hooker

class DebugPropertiesHooker : Hooker {
    
    // Debug properties to override
    private val debugProperties = mapOf(
        "debug.debuggable" to "1",
        "ro.debuggable" to "1",
        "persist.sys.usb.config" to "adb",
        "debug.force-debuggable" to "1"
    )
    
    override fun afterHook(param: HookParam) {
        // Get the property key
        val key = param.args[0] as? String ?: return
        
        // If this is one of our debug properties, override the result
        if (debugProperties.containsKey(key)) {
            val value = debugProperties[key]
            param.setResult(value)
        }
    }
}
```

### Settings Schema

Create the settings schema:

```json
// modules/DebugApp/src/main/assets/settings.json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "title": "Debug App Settings",
  "properties": {
    "enable_for_all": {
      "type": "boolean",
      "title": "Enable for all apps",
      "description": "Enable debug mode for all apps (except excluded apps)",
      "default": true
    },
    "excluded_packages": {
      "type": "array",
      "title": "Excluded packages",
      "description": "Packages to exclude when 'Enable for all apps' is on",
      "items": {
        "type": "string"
      },
      "default": ["com.android.systemui", "com.android.settings"]
    },
    "included_packages": {
      "type": "array",
      "title": "Included packages",
      "description": "Packages to include when 'Enable for all apps' is off",
      "items": {
        "type": "string"
      },
      "default": []
    }
  }
}
```

### Module Metadata

Create the module metadata:

```json
// modules/DebugApp/src/main/assets/module-info.json
{
  "id": "debug-app",
  "name": "Debug App",
  "version": "1.0.0",
  "description": "Forces applications to run in debug mode",
  "author": "LSPosedKit",
  "features": [
    "app.debug.force"
  ],
  "capabilities": {
    "hotReload": true
  }
}
```

## 2. NetworkGuard Module Implementation

### Directory Setup

Create the module directory structure:

```bash
mkdir -p modules/NetworkGuard/src/main/java/com/wobbz/module/networkguard/{hooks,rules,monitor,ui,services}
mkdir -p modules/NetworkGuard/src/main/assets/
mkdir -p modules/NetworkGuard/src/main/res/layout/
mkdir -p modules/NetworkGuard/src/test/java/com/wobbz/module/networkguard/
```

### build.gradle

Create the module's build file:

```groovy
// modules/NetworkGuard/build.gradle
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
    id 'org.jetbrains.kotlin.kapt'
}

android {
    namespace 'com.wobbz.module.networkguard'
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
    
    buildFeatures {
        viewBinding true
    }
}

dependencies {
    implementation project(':framework')
    kapt project(':framework:processor')
    
    // UI components
    implementation 'androidx.recyclerview:recyclerview:1.2.1'
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'com.google.android.material:material:1.6.0'
    
    // JSON parsing
    implementation 'com.squareup.moshi:moshi:1.14.0'
    implementation 'com.squareup.moshi:moshi-kotlin:1.14.0'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.mockito:mockito-core:4.0.0'
}
```

### Main Module Class

Create the main module class:

```kotlin
// modules/NetworkGuard/src/main/java/com/wobbz/module/networkguard/NetworkGuard.kt
package com.wobbz.module.networkguard

import android.content.Context
import com.wobbz.framework.core.IModulePlugin
import com.wobbz.framework.core.LogLevel
import com.wobbz.framework.core.PackageLoadedParam
import com.wobbz.framework.hot.IHotReloadable
import com.wobbz.framework.processor.HotReloadable
import com.wobbz.framework.processor.XposedPlugin
import com.wobbz.framework.service.FeatureManager
import com.wobbz.framework.settings.SettingsProvider
import com.wobbz.module.networkguard.hooks.NetworkHooks
import com.wobbz.module.networkguard.rules.NetworkRuleProvider
import com.wobbz.module.networkguard.rules.RuleManager
import com.wobbz.module.networkguard.services.INetworkRuleService

@XposedPlugin(
    id = "network-guard",
    name = "Network Guard",
    version = "1.0.0",
    description = "Monitors and controls network traffic",
    author = "LSPosedKit",
    scope = ["*"]
)
@HotReloadable
class NetworkGuard : IModulePlugin, IHotReloadable {
    
    private val hooks = mutableListOf<Any>()
    private lateinit var settings: SettingsProvider
    private lateinit var ruleManager: RuleManager
    
    override fun initialize(context: Context, xposed: com.wobbz.framework.core.XposedInterface) {
        // Initialize settings
        settings = SettingsProvider.of(context)
        
        // Initialize rule manager
        ruleManager = RuleManager(context)
        
        // Register network rule service
        val networkRuleService = NetworkRuleProvider(ruleManager)
        FeatureManager.register(INetworkRuleService::class, networkRuleService)
        
        // Declare features
        FeatureManager.declareFeature("network.inspection")
        FeatureManager.declareFeature("network.blocking")
        
        xposed.log(LogLevel.INFO, "NetworkGuard initialized")
    }
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Check if we should monitor this package
        if (!shouldMonitorPackage(param.packageName)) {
            return
        }
        
        try {
            // Create network hooks
            val networkHooks = NetworkHooks(param, ruleManager)
            
            // Apply hooks
            val hookResults = networkHooks.applyHooks()
            hooks.addAll(hookResults)
            
            param.xposed.log(LogLevel.INFO, "Applied ${hookResults.size} network hooks for ${param.packageName}")
        } catch (e: Exception) {
            param.xposed.logError("Error setting up NetworkGuard hooks", e)
        }
    }
    
    private fun shouldMonitorPackage(packageName: String): Boolean {
        // Check if this package should be monitored based on settings
        val monitorAll = settings.bool("monitor_all_apps", false)
        
        if (monitorAll) {
            return !isExcluded(packageName)
        }
        
        return isIncluded(packageName)
    }
    
    private fun isExcluded(packageName: String): Boolean {
        // Check if package is in the exclusion list
        val excludedPackages = settings.stringSet("excluded_packages", emptySet())
        return excludedPackages.contains(packageName)
    }
    
    private fun isIncluded(packageName: String): Boolean {
        // Check if package is in the inclusion list
        val includedPackages = settings.stringSet("included_packages", emptySet())
        return includedPackages.contains(packageName)
    }
    
    override fun onHotReload() {
        // Clean up existing hooks
        hooks.forEach { unhooker ->
            if (unhooker is com.wobbz.framework.core.MethodUnhooker<*>) {
                unhooker.unhook()
            }
        }
        hooks.clear()
    }
}
```

### Rule Models and Service

Create the network rule models and service interface:

```kotlin
// modules/NetworkGuard/src/main/java/com/wobbz/module/networkguard/rules/NetworkRule.kt
package com.wobbz.module.networkguard.rules

data class NetworkRule(
    val id: String,
    val action: String, // "ALLOW" or "BLOCK"
    val target: String, // URL or domain pattern
    val description: String = "",
    val enabled: Boolean = true
)
```

```kotlin
// modules/NetworkGuard/src/main/java/com/wobbz/module/networkguard/rules/RuleManager.kt
package com.wobbz.module.networkguard.rules

import android.content.Context
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types
import java.io.File

class RuleManager(private val context: Context) {
    
    private val rules = mutableListOf<NetworkRule>()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val type = Types.newParameterizedType(List::class.java, NetworkRule::class.java)
    private val adapter = moshi.adapter<List<NetworkRule>>(type)
    private val rulesFile: File
    
    init {
        rulesFile = File(context.filesDir, "network_rules.json")
        loadRules()
    }
    
    fun getRules(): List<NetworkRule> {
        return rules.toList()
    }
    
    fun addRule(rule: NetworkRule) {
        rules.add(rule)
        saveRules()
    }
    
    fun removeRule(ruleId: String) {
        rules.removeIf { it.id == ruleId }
        saveRules()
    }
    
    fun updateRule(rule: NetworkRule) {
        val index = rules.indexOfFirst { it.id == rule.id }
        if (index >= 0) {
            rules[index] = rule
            saveRules()
        }
    }
    
    fun shouldAllowConnection(url: String): Boolean {
        // Default to allowing connections
        var shouldAllow = true
        
        // Find the most specific rule that applies
        val applicableRules = rules
            .filter { it.enabled }
            .filter { url.contains(it.target) }
            .sortedByDescending { it.target.length } // Longer patterns are more specific
        
        // Apply the most specific rule if any
        if (applicableRules.isNotEmpty()) {
            shouldAllow = applicableRules[0].action == "ALLOW"
        }
        
        return shouldAllow
    }
    
    private fun loadRules() {
        try {
            if (rulesFile.exists()) {
                val json = rulesFile.readText()
                val loadedRules = adapter.fromJson(json) ?: listOf()
                rules.clear()
                rules.addAll(loadedRules)
            }
        } catch (e: Exception) {
            Log.e("RuleManager", "Error loading rules: ${e.message}")
            // Initialize with default rules if loading fails
            initializeDefaultRules()
        }
    }
    
    private fun saveRules() {
        try {
            val json = adapter.toJson(rules)
            rulesFile.writeText(json)
        } catch (e: Exception) {
            Log.e("RuleManager", "Error saving rules: ${e.message}")
        }
    }
    
    private fun initializeDefaultRules() {
        rules.clear()
        rules.add(
            NetworkRule(
                id = "default-block-ads",
                action = "BLOCK",
                target = "ads.example.com",
                description = "Block example ad server",
                enabled = true
            )
        )
        saveRules()
    }
}
```

```kotlin
// modules/NetworkGuard/src/main/java/com/wobbz/module/networkguard/services/INetworkRuleService.kt
package com.wobbz.module.networkguard.services

import com.wobbz.module.networkguard.rules.NetworkRule

/**
 * Service interface for accessing network rules.
 */
interface INetworkRuleService {
    /**
     * Gets the current list of network rules.
     */
    fun getRules(): List<NetworkRule>
    
    /**
     * Checks if a URL should be allowed.
     */
    fun shouldAllowUrl(url: String): Boolean
    
    /**
     * Gets the version of the rule service.
     */
    fun getVersion(): String
}
```

```kotlin
// modules/NetworkGuard/src/main/java/com/wobbz/module/networkguard/rules/NetworkRuleProvider.kt
package com.wobbz.module.networkguard.rules

import com.wobbz.module.networkguard.services.INetworkRuleService

class NetworkRuleProvider(private val ruleManager: RuleManager) : INetworkRuleService {
    override fun getRules(): List<NetworkRule> {
        return ruleManager.getRules()
    }
    
    override fun shouldAllowUrl(url: String): Boolean {
        return ruleManager.shouldAllowConnection(url)
    }
    
    override fun getVersion(): String {
        return "1.0.0"
    }
}
```

### Network Hooks

Create the network hooks implementation:

```kotlin
// modules/NetworkGuard/src/main/java/com/wobbz/module/networkguard/hooks/NetworkHooks.kt
package com.wobbz.module.networkguard.hooks

import com.wobbz.framework.core.PackageLoadedParam
import com.wobbz.framework.core.LogLevel
import com.wobbz.module.networkguard.rules.RuleManager
import java.net.URL
import java.net.URLConnection

class NetworkHooks(
    private val param: PackageLoadedParam,
    private val ruleManager: RuleManager
) {
    
    private val hooks = mutableListOf<Any>()
    
    fun applyHooks(): List<Any> {
        // Hook URL.openConnection
        hookUrlOpenConnection()
        
        // Hook HttpURLConnection
        hookHttpUrlConnection()
        
        // Hook OkHttp (if present)
        tryHookOkHttp()
        
        return hooks
    }
    
    private fun hookUrlOpenConnection() {
        try {
            val urlClass = param.xposed.loadClass("java.net.URL")
            val unhooker = param.xposed.hook(
                urlClass,
                "openConnection",
                arrayOf(),
                UrlOpenConnectionHooker::class.java
            )
            
            hooks.add(unhooker)
            param.xposed.log(LogLevel.INFO, "Hooked URL.openConnection")
        } catch (e: Exception) {
            param.xposed.logError("Failed to hook URL.openConnection", e)
        }
    }
    
    private fun hookHttpUrlConnection() {
        try {
            // Implementation details would go here
            param.xposed.log(LogLevel.INFO, "Hooked HttpURLConnection")
        } catch (e: Exception) {
            param.xposed.logError("Failed to hook HttpURLConnection", e)
        }
    }
    
    private fun tryHookOkHttp() {
        try {
            // Try to find OkHttp classes
            val okHttpClientClass = param.classLoader.loadClass("okhttp3.OkHttpClient")
            
            // Hook OkHttp
            // Implementation details would go here
            
            param.xposed.log(LogLevel.INFO, "Hooked OkHttp")
        } catch (ClassNotFoundException e) {
            // OkHttp not present in this app, that's fine
            param.xposed.log(LogLevel.DEBUG, "OkHttp not found in ${param.packageName}, skipping hooks")
        } catch (e: Exception) {
            param.xposed.logError("Failed to hook OkHttp", e)
        }
    }
    
    inner class UrlOpenConnectionHooker : com.wobbz.framework.core.Hooker {
        override fun beforeHook(param: com.wobbz.framework.core.HookParam) {
            val url = param.thisObject as? URL ?: return
            val urlString = url.toString()
            
            // Check if the URL is allowed
            if (!ruleManager.shouldAllowConnection(urlString)) {
                // Block the connection by throwing an exception
                param.setThrowable(java.io.IOException("Connection blocked by NetworkGuard"))
                
                // Log the blocked connection
                param.xposed.log(LogLevel.INFO, "Blocked connection to: $urlString")
            }
        }
    }
}
```

### Settings Schema

Create the settings schema:

```json
// modules/NetworkGuard/src/main/assets/settings.json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "title": "Network Guard Settings",
  "properties": {
    "monitor_all_apps": {
      "type": "boolean",
      "title": "Monitor all apps",
      "description": "Monitor network traffic for all apps (except excluded apps)",
      "default": false
    },
    "logging_enabled": {
      "type": "boolean",
      "title": "Enable logging",
      "description": "Log all network connections",
      "default": true
    },
    "block_by_default": {
      "type": "boolean",
      "title": "Block by default",
      "description": "Block all connections unless explicitly allowed",
      "default": false
    },
    "excluded_packages": {
      "type": "array",
      "title": "Excluded packages",
      "description": "Packages to exclude from monitoring",
      "items": {
        "type": "string"
      },
      "default": ["com.android.systemui", "com.android.settings"]
    },
    "included_packages": {
      "type": "array",
      "title": "Included packages",
      "description": "Packages to monitor when 'Monitor all apps' is off",
      "items": {
        "type": "string"
      },
      "default": ["com.android.browser", "com.android.chrome"]
    }
  }
}
```

### Module Metadata

Create the module metadata:

```json
// modules/NetworkGuard/src/main/assets/module-info.json
{
  "id": "network-guard",
  "name": "Network Guard",
  "version": "1.0.0",
  "description": "Monitors and controls network traffic",
  "author": "LSPosedKit",
  "features": [
    "network.inspection",
    "network.blocking",
    "network.rules"
  ],
  "services": {
    "INetworkRuleService": "com.wobbz.module.networkguard.services.INetworkRuleService"
  },
  "capabilities": {
    "hotReload": true
  }
}
```

## Integrating Modules into the Build System

Update the `settings.gradle` file to include the sample modules:

```groovy
// Add at the end of settings.gradle
include ':modules:DebugApp'
include ':modules:NetworkGuard'
// Add other modules as they are implemented
```

## Testing Sample Modules

Create unit tests for each module:

```kotlin
// modules/DebugApp/src/test/java/com/wobbz/module/debugapp/ApplicationInfoHookerTest.kt
package com.wobbz.module.debugapp

import android.content.pm.ApplicationInfo
import com.wobbz.framework.core.HookParam
import com.wobbz.module.debugapp.hooks.ApplicationInfoHooker
import org.junit.Test
import org.mockito.Mockito.*

class ApplicationInfoHookerTest {
    
    @Test
    fun testAddsFlagDebuggable() {
        // Create a mock HookParam
        val param = mock(HookParam::class.java)
        
        // Setup the mock to return an integer flag
        `when`(param.getResult<Int>()).thenReturn(0)
        
        // Create the hooker
        val hooker = ApplicationInfoHooker()
        
        // Call the hook method
        hooker.afterHook(param)
        
        // Verify result was set with FLAG_DEBUGGABLE
        verify(param).setResult(ApplicationInfo.FLAG_DEBUGGABLE)
    }
    
    @Test
    fun testPreservesExistingFlags() {
        // Create a mock HookParam
        val param = mock(HookParam::class.java)
        
        // Setup the mock to return existing flags
        val existingFlags = ApplicationInfo.FLAG_INSTALLED
        `when`(param.getResult<Int>()).thenReturn(existingFlags)
        
        // Create the hooker
        val hooker = ApplicationInfoHooker()
        
        // Call the hook method
        hooker.afterHook(param)
        
        // Verify result was set with FLAG_DEBUGGABLE preserving existing flags
        verify(param).setResult(existingFlags or ApplicationInfo.FLAG_DEBUGGABLE)
    }
}
```

## Conclusion and Next Steps

These sample modules demonstrate different aspects of the LSPosedKit framework:

1. **DebugApp** shows a minimal module implementation with simple hooks
2. **NetworkGuard** demonstrates service registration, rule management, and more complex hooks

For the remaining modules (IntentInterceptor and UIEnhancer), follow similar patterns to implement them, focusing on their specific areas:

- **IntentInterceptor** should focus on monitoring and analyzing Intent objects
- **UIEnhancer** should demonstrate resource replacement and UI customization

After implementing these modules, the next steps are:

1. Test each module on actual devices
2. Create thorough documentation for module developers
3. Add detailed examples to the module README files
4. Create integration tests that verify cross-module functionality 