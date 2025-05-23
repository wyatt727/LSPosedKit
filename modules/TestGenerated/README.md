# TestGenerated

Test module to verify generation works

## Module Information

- **ID**: `test-generated`
- **Version**: 1.0.0
- **Target**: Android 12+ (API 31+)

## Features

- Hot-reload support for development
- Configurable package filtering
- Debug logging support

## Settings

The module can be configured through LSPosed Manager or by editing the settings:

- **Enable for all apps**: Apply hooks to all applications (default: false)
- **Excluded packages**: Apps to exclude when "Enable for all apps" is on
- **Included packages**: Apps to include when "Enable for all apps" is off
- **Enable logging**: Enable detailed debug logging

## Development

### Building

```bash
./gradlew :modules:TestGenerated:assembleDebug
```

### Installing

```bash
./gradlew :modules:TestGenerated:installDebug
```

### Testing

```bash
./gradlew :modules:TestGenerated:test
```

### Hot Reload

During development, you can use hot-reload to quickly test changes:

1. Start the development server: `./gradlew runDevServer`
2. Make your changes
3. Rebuild and install: `./gradlew :modules:TestGenerated:installDebug`

## Implementation Notes

This module was generated using the LSPosedKit scaffolding system. The main implementation points are:

1. **Main Module Class**: `TestGenerated.kt` - Entry point and lifecycle management
2. **Hooks**: `hooks/` directory - Contains the actual hooking implementations
3. **Settings**: `assets/settings.json` - Configuration schema
4. **Tests**: `test/` directory - Unit tests for the module

## Customization

To customize this module for your needs:

1. Replace the example hooks in `hooks/ExampleTestGeneratedHooker.kt` with your implementation
2. Update the settings schema in `assets/settings.json` to match your configuration needs
3. Modify the package filtering logic in the main module class
4. Add additional hook classes as needed
5. Update tests to cover your specific functionality

## License

This module is part of the LSPosedKit project and follows the same license terms.
