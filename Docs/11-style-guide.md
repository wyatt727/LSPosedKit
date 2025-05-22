# Style Guide & Project Conventions

> Comprehensive guide to code conventions, naming standards, and project structure for LSPosedKit module development.

## Code Style

### Kotlin Style

#### Naming Conventions

| Element          | Convention                 | Examples                                    |
|------------------|----------------------------|---------------------------------------------|
| Packages         | `lowerCamelCase`           | `com.example.debugapp`                      |
| Classes          | `UpperCamelCase`           | `NetworkGuard`, `DebugHooker`               |
| Interfaces       | `UpperCamelCase` with `I` prefix | `IModulePlugin`, `INetworkRuleProvider`    |
| Functions        | `lowerCamelCase`           | `onPackageLoaded()`, `getRules()`           |
| Properties       | `lowerCamelCase`           | `packageName`, `isEnabled`                  |
| Constants        | `UPPER_SNAKE_CASE`         | `MAX_RETRY_COUNT`, `DEFAULT_TIMEOUT`        |
| Extension functions | `lowerCamelCase`        | `String.toBase64()`, `Context.getXposed()`  |

#### File Organization

1. **Package Statement**: Always at the top
2. **Import Statements**: Alphabetically ordered with no wildcards
3. **Class Declaration**: Single class per file (except for small related classes)
4. **Function Order**:
   - Public before private
   - Properties before methods
   - Companion object at the beginning of the class

```kotlin
package com.example.debugapp

import android.content.Context
import com.wobbz.framework.core.IModulePlugin
import com.wobbz.framework.hot.IHotReloadable

@XposedPlugin(/* ... */)
class DebugApp : IModulePlugin, IHotReloadable {
    companion object {
        private const val TAG = "DebugApp"
        private const val MAX_HOOKS = 100
    }
    
    // Properties
    private val hooks = mutableListOf<MethodUnhooker<*>>()
    private var isInitialized = false
    
    // Public methods
    override fun initialize(context: Context, xposed: XposedInterface) {
        // Implementation
    }
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Implementation
    }
    
    override fun onHotReload() {
        // Implementation
    }
    
    // Private methods
    private fun setupHooks(param: PackageLoadedParam) {
        // Implementation
    }
    
    private fun clearHooks() {
        // Implementation
    }
}
```

#### Formatting

- **Indentation**: 4 spaces (no tabs)
- **Line length**: Maximum 100 characters
- **Line breaks**: After operators in long expressions
- **Braces**: Opening brace on the same line as the declaration
- **Comments**: Above the code they document
- **Whitespace**: Single blank line between methods
- **Imports**: No wildcard imports

### Java Style

If you need to use Java (Kotlin is preferred), follow these conventions:

- Use same naming conventions as Kotlin
- Add `@NonNull` and `@Nullable` annotations for better interop
- Use lambda expressions where appropriate
- Follow Oracle's Java Code Conventions for other aspects

## Project Structure

### Module Organization

```
modules/
└── debug-app/                      # Module root directory
    ├── src/
    │   ├── main/
    │   │   ├── java/               # Java/Kotlin source files
    │   │   │   └── com/example/debugapp/
    │   │   │       ├── DebugApp.kt # Main module class
    │   │   │       ├── hookers/    # Hook implementation classes
    │   │   │       ├── model/      # Data model classes
    │   │   │       ├── utils/      # Utility classes
    │   │   │       └── services/   # Service implementations
    │   │   ├── assets/             # Module assets
    │   │   │   ├── module-info.json
    │   │   │   └── settings.json
    │   │   └── res/                # Android resources
    │   └── test/                   # Unit tests
    │       └── java/
    │           └── com/example/debugapp/
    │               └── DebugAppTest.kt
    └── build.gradle                # Module build configuration
```

### Resource Naming

| Resource Type       | Prefix        | Example                        |
|---------------------|---------------|--------------------------------|
| Layout files        | `layout_`     | `layout_settings.xml`          |
| Drawable files      | `ic_`         | `ic_debug.xml`                 |
| Menu files          | `menu_`       | `menu_debug_options.xml`       |
| Strings             | (none)        | `debug_enable_title`           |
| Dimensions          | (none)        | `text_size_large`              |
| Colors              | (none)        | `color_primary`                |

## Documentation Standards

### Code Documentation

#### Class Headers

```kotlin
/**
 * A module that enables debugging for target applications.
 * 
 * This module sets the FLAG_DEBUGGABLE flag in ApplicationInfo
 * to allow debugging of any application, including system apps.
 * 
 * @author Jane Developer
 * @since 1.0.0
 */
class DebugApp : IModulePlugin {
    // Implementation
}
```

#### Method Documentation

```kotlin
/**
 * Hooks the ApplicationInfo flags field to enable debugging.
 * 
 * @param param The package loaded parameters
 * @return True if hooks were successfully applied
 * @throws HookFailedException if hooking fails
 */
private fun setupDebugHooks(param: PackageLoadedParam): Boolean {
    // Implementation
}
```

#### Property Documentation

```kotlin
/**
 * Stores references to all hooks for proper cleanup during hot-reload.
 */
private val hooks = mutableListOf<MethodUnhooker<*>>()
```

### File Headers

Add a license and file description header to each source file:

```kotlin
/*
 * Copyright (c) 2023 LSPosedKit Team
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package com.example.debugapp

// Rest of the file
```

## Logging Conventions

### Log Tags

- Module logs: `LSPK-{ModuleID}` (e.g., `LSPK-DebugApp`)
- Framework logs: `LSPK-Framework`
- Hot-reload logs: `LSPK-HotReload`

### Log Levels

| Level   | Usage                                      | Example                                        |
|---------|--------------------------------------------|-------------------------------------------------|
| VERBOSE | Detailed development information           | `xposed.log(LogLevel.VERBOSE, "Processing view: $view")` |
| DEBUG   | Debugging information                      | `xposed.log(LogLevel.DEBUG, "Hook applied to: $method")` |
| INFO    | General runtime information                | `xposed.log(LogLevel.INFO, "Module initialized")` |
| WARN    | Potential issues that aren't fatal         | `xposed.log(LogLevel.WARN, "Failed to hook method: $method")` |
| ERROR   | Errors that need attention                 | `xposed.logError("Hook failed", exception)` |

### Log Format

```
{Timestamp} {LogLevel} {Tag}: {Message}
```

Example:
```
05-22 12:34:56.789 I LSPK-DebugApp: Module initialized for package com.android.settings
```

## Annotations Usage

### Module Annotations

#### @XposedPlugin

Required for all module main classes:

```kotlin
@XposedPlugin(
    id = "debug-app",          // Required: Kebab-case unique identifier
    name = "Debug App",        // Required: Human-readable name
    version = "1.0.0",         // Required: SemVer format
    scope = ["*"],             // Required: Target packages
    description = "Enables debugging flags for applications", // Required
    author = "Jane Developer"  // Optional
)
class DebugApp : IModulePlugin {
    // Implementation
}
```

#### @HotReloadable

Add to modules that support hot-reload:

```kotlin
@HotReloadable
class DebugApp : IModulePlugin, IHotReloadable {
    // Must implement IHotReloadable interface
    override fun onHotReload() {
        // Clean up hooks
    }
}
```

#### @SettingsKey

For settings binding:

```kotlin
class DebugAppSettings {
    @SettingsKey("enableDebug")
    val isDebugEnabled: Boolean = true
    
    @SettingsKey("logLevel")
    val logLevel: String = "INFO"
}
```

## JSON Conventions

### settings.json

- Use `snake_case` for property names
- Include `title` and `description` for all properties
- Always provide `default` values
- Use proper validation constraints

```json
{
  "$schema": "https://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "enable_debug": {
      "type": "boolean",
      "title": "Enable Debugging",
      "description": "Turn on debug features",
      "default": true
    },
    "log_level": {
      "type": "string",
      "title": "Log Level",
      "description": "Verbosity of logging",
      "enum": ["OFF", "INFO", "DEBUG", "VERBOSE"],
      "default": "INFO"
    }
  }
}
```

### module-info.json

- Use proper versioning in SemVer format
- Include feature identifiers in dot notation
- Document extension points

```json
{
  "$schema": "https://json-schema.org/draft-07/schema#",
  "id": "debug-app",
  "version": "1.0.0",
  "features": [
    "app.debugging",
    "system.inspection"
  ],
  "extensions": {
    "debug.provider": "com.example.debugapp.DebugProvider"
  }
}
```

## Hook Implementation Patterns

### Hooker Classes

- Create separate classes for each hook implementation
- Use inner classes for simple hooks
- Use standalone classes for complex or reusable hooks
- Add clear comments explaining the hook's purpose

```kotlin
/**
 * Hooker that modifies ApplicationInfo flags to enable debugging.
 */
class DebugFlagHooker : Hooker {
    override fun afterHook(param: HookParam) {
        val currentFlags = param.getResult<Int>()
        val newFlags = currentFlags or ApplicationInfo.FLAG_DEBUGGABLE
        param.setResult(newFlags)
    }
}
```

### Hook Management

Always maintain references to hooks for proper cleanup during hot-reload:

```kotlin
class MyModule : IModulePlugin, IHotReloadable {
    // Store ALL hook references
    private val hooks = mutableListOf<MethodUnhooker<*>>()
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        // Store hook reference
        hooks += param.xposed.hook(method, MyHooker::class.java)
    }
    
    override fun onHotReload() {
        // Clean up ALL hooks
        hooks.forEach { it.unhook() }
        hooks.clear()
    }
}
```

## Error Handling

### Exception Handling

- Add try-catch blocks around risky operations
- Log exceptions with appropriate context
- Fail gracefully when possible
- Use custom exceptions for specific error cases

```kotlin
try {
    val clazz = param.xposed.loadClass("android.app.ApplicationInfo")
    val field = clazz.getDeclaredField("flags")
    field.isAccessible = true
    hooks += param.xposed.hook(getter = field, hooker = DebugFlagHooker::class.java)
} catch (e: ClassNotFoundException) {
    param.xposed.logError("Failed to find ApplicationInfo class", e)
} catch (e: NoSuchFieldException) {
    param.xposed.logError("Failed to find flags field", e)
} catch (e: HookFailedException) {
    param.xposed.logError("Failed to hook flags field", e)
}
```

### Custom Exceptions

Create specific exceptions for your module:

```kotlin
/**
 * Thrown when a rule validation fails.
 */
class RuleValidationException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
```

## Testing Practices

### Unit Tests

- Test each component in isolation
- Use descriptive test method names
- Follow Arrange-Act-Assert pattern
- Cover edge cases and failure scenarios

```kotlin
class RuleParserTest {
    @Test
    fun parseValidRule_shouldReturnCorrectRule() {
        // Arrange
        val json = """{"action":"VIEW","type":"BLOCK"}"""
        
        // Act
        val rule = RuleParser.parse(json)
        
        // Assert
        assertEquals("VIEW", rule.action)
        assertEquals("BLOCK", rule.type)
    }
    
    @Test
    fun parseInvalidRule_shouldThrowException() {
        // Arrange
        val json = """{"action":"VIEW"}"""
        
        // Act & Assert
        assertThrows<RuleValidationException> {
            RuleParser.parse(json)
        }
    }
}
```

### Integration Tests

- Test interactions between components
- Use mock objects for dependencies
- Verify correct behavior in realistic scenarios

```kotlin
class IntentInterceptorTest {
    @Mock
    lateinit var xposed: XposedInterface
    
    @Mock
    lateinit var context: Context
    
    private lateinit var interceptor: IntentInterceptor
    
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        interceptor = IntentInterceptor()
        interceptor.initialize(context, xposed)
    }
    
    @Test
    fun interceptIntent_withBlockRule_shouldBlockIntent() {
        // Test implementation
    }
}
```

## Build Configuration

### Gradle Best Practices

- Use the same compile and target SDK versions
- Declare dependencies with specific versions
- Organize dependencies by purpose
- Add comments for non-obvious dependencies

```groovy
android {
    compileSdk 35
    
    defaultConfig {
        minSdk 31
        targetSdk 35
        
        // Use consistent version code and name with @XposedPlugin
        versionCode 1
        versionName "1.0.0"
    }
}

dependencies {
    // Framework dependencies
    implementation project(':framework')
    kapt project(':framework:processor')
    
    // Third-party libraries
    implementation 'com.squareup.okhttp3:okhttp:4.10.0' // HTTP client
    
    // Testing dependencies
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.mockito:mockito-core:4.0.0'
}
```

## Git Workflow

### Commit Messages

Follow the Conventional Commits format:

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

Examples:
- `feat(network): add support for HTTPS inspection`
- `fix(ui): correct text size in settings screen`
- `docs(readme): update installation instructions`
- `test(debug): add unit tests for flag hooking`

### Branch Naming

- Feature branches: `feature/short-description`
- Bug fixes: `fix/issue-description`
- Documentation: `docs/topic-name`
- Releases: `release/version-number`

### Pull Requests

- Use descriptive titles
- Include detailed descriptions
- Reference issues being fixed
- Include testing steps
- Add screenshots for UI changes

## Publishing Guidelines

### Release Preparation

1. Update version in `@XposedPlugin` annotation
2. Update version in `module-info.json`
3. Update CHANGELOG.md
4. Run all tests
5. Build release artifacts

### Release Artifacts

For each release, provide:

1. `.lspkmod` bundle file
2. SHA-256 checksum
3. Release notes
4. Compatibility information

## Security Best Practices

### Code Security

- Don't store sensitive data in plain text
- Validate all inputs, especially from settings
- Use proper permission checks
- Handle exceptions securely (avoid leaking sensitive information)
- Don't execute arbitrary code from untrusted sources

### User Privacy

- Get consent before collecting any data
- Don't log sensitive information
- Provide clear privacy policy
- Allow users to disable features with privacy implications

## Accessibility Guidelines

- Support dark mode
- Use appropriate contrast ratios
- Provide content descriptions for UI elements
- Support screen readers
- Test with accessibility services enabled

## Performance Considerations

- Minimize hook usage (only hook what you need)
- Use efficient data structures
- Avoid expensive operations in frequently called hooks
- Implement proper cleanup in `onHotReload()`
- Use lazy initialization for expensive resources
- Consider battery and memory impact

## Compatibility Matrix

Clearly document your module's compatibility:

| Android Version | API Level | Support Status | Notes                                |
|-----------------|-----------|----------------|--------------------------------------|
| Android 15      | 35        | Full           | All features supported               |
| Android 14      | 34        | Full           | All features supported               |
| Android 13      | 33        | Partial        | Limited hot-reload support           |
| Android 12/12L  | 31-32     | Partial        | Limited hot-reload support           |
| Android 11-     | ≤30       | Not supported  | Module won't load                    |

## Community Contribution Guidelines

When contributing to the project:

1. Follow this style guide
2. Add proper documentation
3. Include tests
4. Update relevant documentation
5. Request reviews from maintainers
6. Be responsive to feedback

By following these guidelines, you'll create modules that are maintainable, performant, and consistent with the LSPosedKit ecosystem. 