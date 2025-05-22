# Intent Interceptor Module

A comprehensive Intent monitoring and interception module for LSPosedKit that provides real-time analysis, filtering, and modification of Intent communications between Android apps and components.

## Features

### 🔍 Intent Monitoring
- **Real-time tracking** of all Intent operations across the system
- **Comprehensive coverage** of Activity starts, broadcasts, service operations, and more
- **Detailed logging** with emoji indicators for easy identification
- **Statistical analysis** with periodic reporting

### 🛡️ Intent Filtering & Security
- **Rule-based filtering** with pattern matching for actions, components, packages, and data
- **Intent blocking** capabilities for security and privacy protection
- **Intent modification** to alter Intent contents before delivery
- **Malicious pattern detection** for known attack vectors

### 📊 Intent History & Analysis
- **Persistent history** with configurable size limits (100-10,000 records)
- **Search and filtering** by package, action, component, or data content
- **Export capabilities** for external analysis tools
- **Service Bus integration** for cross-module access to Intent data

### ⚡ Hot-Reload Support
- **Live updates** without app restarts during development
- **State preservation** across hot-reload cycles
- **Service persistence** for continuous monitoring

## Architecture

```
IntentInterceptor/
├── IntentInterceptor.kt          # Main module class with lifecycle management
├── hooks/
│   └── IntentHooks.kt            # Comprehensive Intent API hooks
├── monitor/
│   └── IntentMonitor.kt          # Real-time monitoring and logging
├── filters/
│   └── IntentFilterManager.kt    # Rule-based filtering and modification
└── services/
    ├── IIntentHistoryService.kt  # Service interface for cross-module access
    └── IntentHistoryService.kt   # History recording and persistence
```

## Intent Types Monitored

| Type | Description | Hook Points |
|------|-------------|-------------|
| **Activity Start** | `startActivity()`, `startActivityForResult()` | Activity.startActivity* methods |
| **Broadcasts** | `sendBroadcast()`, `onReceive()` | Context.sendBroadcast*, BroadcastReceiver.onReceive |
| **Services** | `startService()`, `bindService()`, `onStartCommand()` | Context.*Service, Service.onStartCommand |
| **Intent Modification** | `setAction()`, `putExtra()`, etc. | Intent setter methods |

## Configuration

### Basic Settings

```json
{
  "monitor_all_apps": true,
  "excluded_packages": ["com.android.systemui", "com.android.settings"],
  "enabled_intent_types": ["activity_start", "broadcast", "service_start"],
  "log_intent_extras": false,
  "enable_intent_history": true,
  "max_history_size": 1000
}
```

### Advanced Security Settings

```json
{
  "enable_intent_blocking": false,
  "enable_intent_modification": false,
  "block_malicious_intents": false,
  "log_sensitive_intents": false
}
```

## Usage Examples

### Basic Intent Monitoring

The module automatically starts monitoring when activated. View logs with:

```bash
adb logcat -s LSPK-IntentInterceptor:V
```

Example log output:
```
🚀 START_ACTIVITY | Action: android.intent.action.VIEW | Component: com.android.browser.BrowserActivity | Caller: com.example.app.MainActivity
📡 SEND_BROADCAST | Action: android.intent.action.CONNECTIVITY_CHANGE | Caller: com.android.systemui.NetworkController
🔧 START_SERVICE | Action: null | Component: com.example.service.BackgroundService | Caller: com.example.app.MainActivity
```

### Accessing Intent History via Service Bus

Other modules can access Intent history through the Service Bus:

```kotlin
class MyAnalysisModule : IModulePlugin {
    override fun initialize(context: Context, xposed: XposedInterface) {
        // Get the Intent history service
        val historyService = FeatureManager.get(IIntentHistoryService::class.java)
        
        if (historyService != null) {
            // Get recent Intent activity
            val recentIntents = historyService.getRecentIntents(50)
            
            // Search for specific patterns
            val suspiciousIntents = historyService.searchIntents("INSTALL_PACKAGE")
            
            // Get statistics
            val stats = historyService.getStatistics()
            xposed.log(LogLevel.INFO, "Total Intents tracked: ${stats.totalIntents}")
        }
    }
}
```

### Custom Intent Filtering

Create custom filter rules by modifying the filter manager:

```kotlin
// Add a rule to block package installation Intents
val blockRule = IntentFilterRule(
    id = "block-installs",
    name = "Block Package Installs",
    enabled = true,
    action = IntentFilterAction.BLOCK,
    actionPattern = "android.intent.action.INSTALL_PACKAGE"
)

filterManager.addRule(blockRule)
```

### Intent Modification Example

```kotlin
// Redirect sensitive Intents to a logging component
val redirectRule = IntentFilterRule(
    id = "log-sensitive",
    name = "Log Sensitive Actions",
    enabled = true,
    action = IntentFilterAction.MODIFY,
    actionPattern = "android.intent.action.SEND*",
    modifications = listOf(
        IntentModification(
            type = ModificationType.ADD_EXTRA,
            key = "monitoring_timestamp",
            value = System.currentTimeMillis().toString()
        )
    )
)
```

## Security Considerations

### Privacy Protection
- **Intent extras logging is disabled by default** to prevent exposure of sensitive data
- **Configurable exclusion lists** for system packages
- **Optional sensitive Intent filtering** with explicit user consent

### Permission Requirements
- `android.permission.WRITE_EXTERNAL_STORAGE` - For exporting Intent logs
- LSPosed activation required for target applications

### Data Retention
- Intent history is stored locally on device
- Configurable retention limits (100-10,000 records)
- Manual history clearing available

## Development & Testing

### Building the Module

```bash
./gradlew :modules:IntentInterceptor:assembleDebug
```

### Running Unit Tests

```bash
./gradlew :modules:IntentInterceptor:test
```

### Hot-Reload Development

```bash
# Start development server
./gradlew runDevServer

# Make changes to Intent hooks and save
# Module will hot-reload automatically
```

### Testing Intent Interception

```bash
# Generate test Intents
adb shell am start -a android.intent.action.VIEW -d "https://example.com"
adb shell am broadcast -a android.intent.action.BATTERY_LOW
adb shell am startservice com.example/.TestService
```

## Integration with Other Modules

### Service Bus Features

The module provides these services for other modules:

| Service | Interface | Purpose |
|---------|-----------|---------|
| Intent History | `IIntentHistoryService` | Access to recorded Intent data |

### Feature Declarations

| Feature | Description |
|---------|-------------|
| `intent.monitoring` | Real-time Intent tracking |
| `intent.interception` | Intent blocking and modification |
| `intent.history` | Persistent Intent history |
| `intent.filtering` | Rule-based Intent processing |

## Troubleshooting

### Common Issues

**Issue**: No Intent logs appearing
- **Solution**: Check that the module is activated for target packages in LSPosed Manager
- **Verify**: Ensure `monitor_all_apps` is enabled or target package is in `included_packages`

**Issue**: Intent blocking not working  
- **Solution**: Enable `enable_intent_blocking` in settings
- **Verify**: Check filter rules are properly configured and enabled

**Issue**: History not persisting
- **Solution**: Verify write permissions and storage availability
- **Check**: Ensure `enable_intent_history` is enabled in settings

### Debug Logging

Enable verbose logging for troubleshooting:

```bash
# View all Intent Interceptor logs
adb logcat -s LSPK-IntentInterceptor:V

# View specific hook logs  
adb logcat | grep "Intent.*Hook"

# View filter manager logs
adb logcat | grep "IntentFilterManager"
```

## Contributing

When contributing to the Intent Interceptor module:

1. **Follow LSPosedKit coding standards** defined in the style guide
2. **Add comprehensive tests** for new filtering rules or hook implementations  
3. **Update documentation** for new features or configuration options
4. **Test hot-reload compatibility** for all changes
5. **Consider privacy implications** of new logging or monitoring features

## License

This module is part of LSPosedKit and is licensed under the WOBBZ License. See the main project LICENSE file for details.
