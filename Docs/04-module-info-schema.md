# Module Info Schema Reference

> Comprehensive guide to the `module-info.json` schema that enables version management, module dependencies, and LSPosedKit integration features.

## Overview

The `module-info.json` file defines metadata and dependency information for your module. This configuration enables:

1. **Semantic versioning** with automatic compatibility checks
2. **Module dependencies** with version constraints
3. **Feature declarations** for runtime capability discovery
4. **Extension points** for module composition
5. **Custom UI integrations** with LSPosed Manager

## File Location

Each module should include a `module-info.json` file in its `src/main/assets` directory:

```
modules/
└── debug-app/
    └── src/
        └── main/
            └── assets/
                ├── module-info.json
                └── settings.json
```

## Basic Structure

```json
{
  "$schema": "https://json-schema.org/draft-07/schema#",
  "id": "debug-app",
  "version": "2.0.0",
  "dependencies": [
    { "id": "another-module", "version": ">=1.0.0" }
  ],
  "features": [
    "debug.flags",
    "app.logging"
  ],
  "extensions": {
    "debug.provider": "com.example.debugapp.DebugProvider"
  },
  "customSettingsActivity": "com.example.debugapp.SettingsActivity"
}
```

## Required Fields

| Field     | Type   | Description                                              | Example             |
|-----------|--------|----------------------------------------------------------|---------------------|
| `id`      | String | Module identifier (must match annotation ID)             | `"debug-app"`   |
| `version` | String | SemVer format (major.minor.patch[-label])                | `"2.0.0"`           |

## Optional Fields

| Field                    | Type     | Description                                           | Example                          |
|--------------------------|----------|-------------------------------------------------------|----------------------------------|
| `dependencies`           | Array    | List of required or optional module dependencies      | (see Dependencies section)       |
| `features`               | Array    | Capabilities provided by this module                  | `["network.inspection"]`         |
| `extensions`             | Object   | Extension points with implementation classes          | (see Extensions section)         |
| `customSettingsActivity` | String   | Fully qualified class name for a custom settings UI. If provided, LSPosed Manager will launch this activity instead of generating one from `settings.json`. | `"com.example.MySettingsActivity"` |
| `minHostVersion`         | String   | Minimum required LSPosedKit host version              | `"1.0.0"`                       |
| `maxHostVersion`         | String   | Maximum compatible LSPosedKit host version            | `"2.0.0"`                       |

## Dependency Management

### Specifying Dependencies

Each dependency object requires:

| Field       | Type    | Required | Description                                     | Example             |
|-------------|---------|----------|-------------------------------------------------|---------------------|
| `id`        | String  | Yes      | Target module ID                                | `"debug-all"`       |
| `version`   | String  | Yes      | SemVer range constraint                         | `">=2.1.0"`         |
| `optional`  | Boolean | No       | Whether dependency is required (default: false) | `true`              |

### Version Range Syntax

LSPosedKit uses npm-style semantic versioning ranges:

| Pattern        | Description                                  | Example                        |
|----------------|----------------------------------------------|--------------------------------|
| `x.y.z`        | Exact version match                          | `"2.1.0"` (exactly 2.1.0)      |
| `^x.y.z`       | Compatible with version, same major          | `"^2.1.0"` (≥2.1.0, <3.0.0)    |
| `~x.y.z`       | Patch-level changes allowed                  | `"~2.1.0"` (≥2.1.0, <2.2.0)    |
| `>x.y.z`       | Greater than version                         | `">2.1.0"` (>2.1.0)            |
| `>=x.y.z`      | Greater than or equal to                     | `">=2.1.0"` (≥2.1.0)           |
| `<x.y.z`       | Less than version                            | `"<3.0.0"` (<3.0.0)            |
| `<=x.y.z`      | Less than or equal to                        | `"<=2.9.9"` (≤2.9.9)           |
| `x.y.z - a.b.c`| Inclusive range                             | `"2.1.0 - 2.9.9"` (2.1.0 to 2.9.9) |
| `*`            | Any version                                  | `"*"` (any version)            |

### Dependency Resolution Process

When LSPosedKit loads modules:

1. Each module's dependencies are evaluated
2. Version constraints are validated against available modules
3. Required dependencies must be present and meet version constraints
4. Optional dependencies are used if available and compatible
5. Dependency cycles are detected and reported as errors

### Example Dependency Patterns

#### Basic Hard Dependency

```json
"dependencies": [
  { "id": "debug-all", "version": ">=2.1.0" }
]
```

This requires the `debug-all` module to be installed with version 2.1.0 or higher.

#### Optional Dependency

```json
"dependencies": [
  { "id": "intent-master", "version": "^1.0.0", "optional": true }
]
```

This will use the `intent-master` module if available at version 1.x.x, but will not fail if missing.

#### Multiple Dependencies with Mixed Requirements

```json
"dependencies": [
  { "id": "debug-all", "version": ">=2.1.0" },
  { "id": "intent-master", "version": "^1.0.0", "optional": true },
  { "id": "logger", "version": "1.0.0 - 1.5.0" }
]
```

This requires:
- `debug-all` 2.1.0 or higher (required)
- `intent-master` 1.x.x if available (optional)
- `logger` between versions 1.0.0 and 1.5.0 inclusive (required)

## Feature Declarations

Features are string identifiers that describe capabilities provided by your module. They enable runtime discovery and interaction.

### Common Feature Namespaces

| Namespace             | Description                                        | Example Features                                |
|-----------------------|----------------------------------------------------|------------------------------------------------|
| `network.*`           | Network-related capabilities                       | `network.inspection`, `network.blocking`        |
| `ui.*`                | User interface modifications                       | `ui.theme`, `ui.layout.customization`           |
| `system.*`            | System-level modifications                         | `system.battery.optimization`, `system.performance` |
| `app.*`               | Application-specific features                     | `app.patch`, `app.automation`                   |
| `security.*`          | Security-related features                         | `security.permission.control`, `security.encryption` |
| `media.*`             | Media handling features                           | `media.recorder`, `media.playback.control`      |

### Using Features in Code

```kotlin
// In a dependent module, check if a feature exists
val hasNetworkBlocking = FeatureManager.hasFeature("network.blocking")

// Conditionally use the feature
if (hasNetworkBlocking) {
    // Use network blocking functionality
}
```

## Extension Points

Extensions allow modules to provide implementations of specific interfaces that other modules can discover and use.

### Declaring Extensions

```json
"extensions": {
  "debug.provider": "com.example.debugapp.DebugProvider",
  "app.logger": "com.example.debugapp.AppLogger"
}
```

### Implementing an Extension Point

```kotlin
// The implementation class must be public and have a no-arg constructor
package com.example.debugapp

class DebugProvider : IDebugFlagsProvider {
    override fun getFlags(): List<DebugFlag> {
        return listOf(
            DebugFlag("ENABLE_LOGGING", true),
            DebugFlag("SHOW_OVERLAY", false)
        )
    }
}
```

### Discovering Extensions

```kotlin
// In another module, discover and use extensions
val flagProviders = FeatureManager.getExtensions("debug.provider")
flagProviders.forEach { providerClass ->
    val provider = providerClass.getDeclaredConstructor().newInstance() as IDebugFlagsProvider
    val flags = provider.getFlags()
    // Use the flags
}
```

## Version Management

### Minimum Host Version

Specify the minimum LSPosedKit host version required:

```json
"minHostVersion": "1.5.0"
```

This will prevent the module from loading on incompatible host versions.

### Maximum Host Version

Optionally specify the maximum compatible host version:

```json
"maxHostVersion": "2.0.0"
```

This can prevent loading on future potentially incompatible host versions.

## Custom Settings UI

To provide a custom settings UI instead of the auto-generated one from `settings.json`:

```json
"customSettingsActivity": "com.example.debugapp.SettingsActivity"
```

The specified activity must:
1. Be declared in your module's `AndroidManifest.xml`.
2. Typically extend `androidx.appcompat.app.AppCompatActivity` or `android.preference.PreferenceActivity`.
3. Be public and accessible (exported, if necessary, depending on how LSPosed Manager launches it).
4. Use `SettingsProvider.of(context)` to load and save preferences defined in your `settings.json` or other storage managed by your module.

If this field is present and valid, LSPosed Manager (or a similar host application integrating LSPosedKit modules) will attempt to launch this activity when the user tries to configure the module. If it's not specified, or if the activity fails to launch, the system will fall back to generating a UI from `settings.json` if that file exists.

## Complete Example

Here's a comprehensive module-info.json example:

```json
{
  "$schema": "https://json-schema.org/draft-07/schema#",
  "id": "debug-app",
  "version": "2.1.0",
  "dependencies": [
    { "id": "logger-module", "version": ">=1.0.0" },
    { "id": "utils-module", "version": "^1.2.0", "optional": true }
  ],
  "features": [
    "debug.flags.read",
    "debug.flags.write",
    "app.logging.verbose"
  ],
  "extensions": {
    "debug.provider": "com.example.debugapp.DebugProvider",
    "app.logger": "com.example.debugapp.AppLogger",
    "ui.settings.extra": "com.example.debugapp.ExtraSettings"
  },
  "customSettingsActivity": "com.example.debugapp.SettingsActivity",
  "minHostVersion": "1.5.0",
  "maxHostVersion": "3.0.0"
}
```

## Best Practices

1. **Use Semantic Versioning** for your module's version field
2. **Be Specific with Dependencies** to avoid compatibility issues
3. **Mark Optional Dependencies** to allow your module to work even if they're missing
4. **Follow Namespace Conventions** for features and extension points
5. **Document Your Extension Points** for other module developers
6. **Test Different Dependency Scenarios** to ensure your module is robust
7. **Update Version Constraints** when you've verified compatibility with new versions

## Common Errors and Solutions

| Error                                     | Possible Solutions                                     |
|-------------------------------------------|--------------------------------------------------------|
| `Dependency not found`                    | Ensure required modules are installed and enabled      |
| `Version constraint not satisfied`        | Update version constraint or use a compatible module version |
| `Cyclic dependency detected`              | Redesign module dependencies to avoid circular references |
| `Extension implementation not found`      | Verify class name and package, ensure class is public   |
| `Host version incompatible`               | Update minHostVersion/maxHostVersion or use compatible host |

## Migration from Legacy LSPosed Modules

When migrating from traditional LSPosed modules to LSPosedKit:

1. Create a `module-info.json` file with your module's ID and version
2. Define any dependencies on other modules
3. If your module provides services for other modules, declare features and extensions
4. Test with both dependent and standalone configurations 