# Settings Schema Guide

> Complete reference for creating and using the `settings.json` schema to generate type-safe module preferences with automatic UI generation in LSPosed Manager.

## Overview

LSPosedKit uses a declarative JSON schema to define module settings, which automatically:

1. **Generates UI** in the LSPosed Manager module settings screen
2. **Provides type-safe access** to settings values at runtime
3. **Validates user inputs** according to constraints
4. **Persists preferences** across reboots and module updates
5. **Supports complex types** like arrays, objects, and enums

## Schema Location and Structure

Each module should include a `settings.json` file in its `src/main/assets` directory:

```
modules/
└── debug-app/
    └── src/
        └── main/
            └── assets/
                └── settings.json
```

The file uses JSON Schema Draft-07 and defines all available preferences for your module.

## Basic Schema Structure

```json
{
  "$schema": "https://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "enableFeature": {
      "type": "boolean",
      "title": "Enable Feature",
      "description": "Toggle the main functionality on/off",
      "default": true
    },
    "refreshInterval": {
      "type": "integer",
      "title": "Refresh Interval",
      "description": "How often to refresh data (in seconds)",
      "minimum": 1,
      "maximum": 3600,
      "default": 60
    }
  }
}
```

## Supported Data Types

### Boolean Settings

```json
"isEnabled": {
  "type": "boolean",
  "title": "Enable Module",
  "description": "Turn the module functionality on or off",
  "default": true
}
```

### Integer Settings

```json
"maxConnections": {
  "type": "integer",
  "title": "Maximum Connections",
  "description": "Maximum number of simultaneous connections",
  "minimum": 1,
  "maximum": 100,
  "default": 10
}
```

### String Settings

```json
"username": {
  "type": "string",
  "title": "Username",
  "description": "Your account username",
  "minLength": 3,
  "maxLength": 50,
  "pattern": "^[a-zA-Z0-9_]+$",
  "default": ""
}
```

### Enum (Dropdown) Settings

```json
"logLevel": {
  "type": "string",
  "title": "Log Level",
  "description": "Verbosity of logging",
  "enum": ["OFF", "ERROR", "WARNING", "INFO", "DEBUG", "VERBOSE"],
  "default": "INFO"
}
```

### Array Settings

```json
"blockedDomains": {
  "type": "array",
  "title": "Blocked Domains",
  "description": "List of domains to block",
  "items": {
    "type": "string",
    "pattern": "^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$"
  },
  "default": ["ads.example.com"]
}
```

### Object Settings

```json
"proxyConfig": {
  "type": "object",
  "title": "Proxy Configuration",
  "description": "Settings for proxy server",
  "properties": {
    "enabled": {
      "type": "boolean",
      "title": "Enable Proxy",
      "default": false
    },
    "host": {
      "type": "string",
      "title": "Proxy Host",
      "default": "127.0.0.1"
    },
    "port": {
      "type": "integer",
      "title": "Proxy Port",
      "minimum": 1,
      "maximum": 65535,
      "default": 8080
    }
  },
  "default": {
    "enabled": false,
    "host": "127.0.0.1",
    "port": 8080
  }
}
```

## Complex Examples

### Filter Configuration with Multiple Types

```json
{
  "filterConfig": {
    "type": "object",
    "title": "Content Filter Configuration",
    "properties": {
      "enableFiltering": {
        "type": "boolean",
        "title": "Enable Content Filtering",
        "default": true
      },
      "filterMode": {
        "type": "string",
        "title": "Filter Mode",
        "enum": ["BLACKLIST", "WHITELIST", "MONITOR"],
        "default": "BLACKLIST"
      },
      "filterRules": {
        "type": "array",
        "title": "Filter Rules",
        "items": {
          "type": "object",
          "properties": {
            "pattern": {
              "type": "string",
              "title": "Match Pattern"
            },
            "action": {
              "type": "string",
              "enum": ["BLOCK", "ALLOW", "LOG"],
              "title": "Action"
            },
            "priority": {
              "type": "integer",
              "title": "Priority",
              "minimum": 1,
              "maximum": 100
            }
          }
        },
        "default": []
      }
    },
    "default": {
      "enableFiltering": true,
      "filterMode": "BLACKLIST",
      "filterRules": []
    }
  }
}
```

### Network Configuration with Validation

```json
{
  "networkSettings": {
    "type": "object",
    "title": "Network Settings",
    "properties": {
      "timeout": {
        "type": "integer",
        "title": "Connection Timeout",
        "description": "Timeout in milliseconds",
        "minimum": 100,
        "maximum": 30000,
        "default": 5000
      },
      "retryCount": {
        "type": "integer",
        "title": "Retry Count",
        "minimum": 0,
        "maximum": 10,
        "default": 3
      },
      "allowedProtocols": {
        "type": "array",
        "title": "Allowed Protocols",
        "items": {
          "type": "string",
          "enum": ["HTTP", "HTTPS", "FTP", "FTPS"]
        },
        "default": ["HTTPS"]
      },
      "userAgent": {
        "type": "string",
        "title": "Custom User Agent",
        "description": "Leave empty to use default",
        "default": ""
      }
    },
    "default": {
      "timeout": 5000,
      "retryCount": 3,
      "allowedProtocols": ["HTTPS"],
      "userAgent": ""
    }
  }
}
```

## UI Elements and Constraints

### Input Validation

| Property       | Type      | Description                                         |
|----------------|-----------|-----------------------------------------------------|
| `minimum`      | number    | Minimum value for numeric types                     |
| `maximum`      | number    | Maximum value for numeric types                     |
| `multipleOf`   | number    | Value must be a multiple of this number             |
| `minLength`    | integer   | Minimum length for strings                          |
| `maxLength`    | integer   | Maximum length for strings                          |
| `pattern`      | string    | Regular expression pattern for string validation    |
| `minItems`     | integer   | Minimum items in array                              |
| `maxItems`     | integer   | Maximum items in array                              |
| `uniqueItems`  | boolean   | Whether array items must be unique                  |

### Display Customization

| Property       | Type      | Description                                         |
|----------------|-----------|-----------------------------------------------------|
| `title`        | string    | User-friendly name shown in settings UI             |
| `description`  | string    | Additional explanation shown below the setting      |
| `format`       | string    | Hints for special input types (e.g., "color", "date") |
| `readOnly`     | boolean   | Setting cannot be modified by user                  |
| `order`        | integer   | Custom ordering for display (lower shows first)     |

## Accessing Settings in Code

### Using SettingsProvider Directly

```kotlin
val settings = SettingsProvider.of(context)

// Access basic types
val isEnabled = settings.bool("enableFeature")
val refreshInterval = settings.int("refreshInterval") 
val username = settings.string("username")

// Access complex types
val blockedDomains = settings.stringList("blockedDomains")
val proxyEnabled = settings.bool("proxyConfig.enabled")
val proxyHost = settings.string("proxyConfig.host")
val proxyPort = settings.int("proxyConfig.port")

// Access with defaults
val customSetting = settings.string("customSetting", "default value")
```

### Using @SettingsKey Annotation

Define a class with annotated properties:

```kotlin
class DebugAppSettings {
    @SettingsKey("networkSettings.timeout")
    val timeout: Int = 5000
    
    @SettingsKey("networkSettings.retryCount")
    val retryCount: Int = 3
    
    @SettingsKey("networkSettings.allowedProtocols")
    val allowedProtocols: List<String> = listOf("HTTPS")
    
    @SettingsKey("networkSettings.userAgent")
    val userAgent: String = ""
}
```

And then bind it:

```kotlin
val settings = SettingsProvider.of(context)
val networkSettings = settings.bind(DebugAppSettings::class.java)

// Use the bound object
if (networkSettings.timeout > 10000) {
    // Do something
}
```

## Listening for Settings Changes

```kotlin
val settings = SettingsProvider.of(context)

// Register listener
val listener = settings.addOnSettingsChangedListener { key ->
    when (key) {
        "enableFeature" -> {
            val newValue = settings.bool(key)
            updateFeatureState(newValue)
        }
        "refreshInterval" -> {
            val newValue = settings.int(key)
            updateRefreshSchedule(newValue)
        }
    }
}

// Later, remove the listener when not needed
settings.removeOnSettingsChangedListener(listener)
```

## Best Practices

1. **Group Related Settings**: Use object types to group related settings
2. **Provide Sensible Defaults**: Always define default values
3. **Validate Inputs**: Set minimum/maximum values and validation patterns
4. **Use Descriptive Titles**: Make settings easy for users to understand
5. **Keep Schema Simple**: Avoid deeply nested objects when possible
6. **Separate UX Concerns**: Place commonly used settings at the top
7. **Handle Missing Settings**: Use defaults in code when accessing potentially missing settings
8. **Version Your Schema**: Include a version field if you plan to change the schema structure

## Migrating Settings Between Versions

When changing your settings schema, consider backward compatibility:

```kotlin
// Example migration code in your module initialization
fun migrateSettings(settings: SettingsProvider) {
    // Check if we need to migrate (e.g., by checking a version number)
    val schemaVersion = settings.int("schemaVersion", 0)
    
    if (schemaVersion < 1) {
        // Migrate from version 0 to 1
        val oldSetting = settings.string("deprecatedSetting")
        if (oldSetting.isNotEmpty()) {
            // Map to new setting
            settings.edit().putString("newSetting", oldSetting).apply()
        }
        
        // Update schema version
        settings.edit().putInt("schemaVersion", 1).apply()
    }
}
```

## Advanced: Custom Settings UI

While LSPosedKit automatically generates a functional UI from your `settings.json` schema, you can provide a fully custom settings activity for more complex needs or a unique look and feel. This allows for richer interactions, custom UI elements, and dynamic content that goes beyond what the schema-based generation can offer.

To use a custom settings UI:

1.  **Create a Custom Activity**: Develop an Android `Activity` (typically extending `androidx.appcompat.app.AppCompatActivity` or `android.preference.PreferenceActivity` if using traditional preference frameworks) that will serve as your module's settings screen.
    *   Within this activity, you will use `SettingsProvider.of(this)` to get an instance of the settings provider.
    *   Load current settings values using the provider's getter methods (e.g., `settings.bool("myKey")`).
    *   Save changes using `settings.edit().putBoolean("myKey", newValue).apply()` or `.commit()`.

2.  **Declare in `AndroidManifest.xml`**: Ensure your custom settings activity is properly declared in your module's `AndroidManifest.xml`.
    ```xml
    <activity
        android:name=".ui.MyCustomSettingsActivity"
        android:label="@string/module_settings_title"
        android:exported="true">
        <!-- For LSPosed Manager to find it, it might need a specific intent filter
             or rely on the direct class name launch. Check LSPosed documentation
             for specifics if direct integration is desired beyond LSPosedKit handling. -->
    </activity>
    ```

3.  **Specify in `module-info.json`**: Set the `customSettingsActivity` field in your module's `module-info.json` file to the fully qualified class name of your custom activity.
    ```json
    {
      // ... other module info fields ...
      "customSettingsActivity": "com.example.mymodule.ui.MyCustomSettingsActivity"
    }
    ```

4.  **(Optional) Pass Data to Custom Activity**: The LSPosed Manager (or the entity launching your settings) might pass the `moduleId` or other relevant data via the `Intent` that starts your custom activity. Your activity should check for these extras in `onCreate`.

**When to Use a Custom UI:**
*   You need UI elements not supported by standard Android preferences (e.g., complex graphs, custom pickers).
*   Settings have intricate dependencies or require dynamic updates based on other system states.
*   You want a very specific branding or user experience for your module's settings.
*   You need to perform actions beyond just changing a preference value (e.g., triggering a system scan, clearing caches from the settings screen).

**Considerations:**
*   **Complexity**: Building a custom UI is more work than relying on the automatic generation.
*   **Consistency**: Your custom UI might differ from the standard LSPosed Manager look and feel. Strive for a clean and intuitive design.
*   **Maintenance**: You are responsible for maintaining the UI and its interaction with `SettingsProvider`.
*   **Accessibility**: Ensure your custom UI adheres to Android accessibility guidelines.

If `customSettingsActivity` is specified, LSPosedKit (and the LSPosed Manager) will typically attempt to launch this activity directly instead of generating one from `settings.json`. If the custom activity cannot be launched, the behavior might fall back to the generated UI or show an error, depending on the host environment.

```kotlin
// Example custom settings activity
package com.example.mymodule.ui // Ensure package matches module-info.json

import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.wobbz.framework.settings.SettingsProvider // Assuming this is the correct import
// Import your R file for layout, e.g., import com.example.mymodule.R

class MyCustomSettingsActivity : AppCompatActivity() {
    private lateinit var settingsProvider: SettingsProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_custom_settings) // Your custom layout

        // It's good practice to get the module's specific context if possible,
        // though `this` (the Activity context) often works for SharedPreferences.
        settingsProvider = SettingsProvider.of(this)

        // Example: Setup for a Switch preference
        // val enableFeatureSwitch: Switch = findViewById(R.id.enable_feature_switch)
        // enableFeatureSwitch.isChecked = settingsProvider.bool("enableFeature", true) // Load with default

        // enableFeatureSwitch.setOnCheckedChangeListener { _, isChecked ->
        //    settingsProvider.edit().putBoolean("enableFeature", isChecked).apply()
        // }
        
        // Other UI setup...
        // Load other settings, set up listeners to save them back using settingsProvider.edit()...
    }
} 