# TestModule - Definitive Proof of LSPosed Hooking

This module provides **definitive proof** that LSPosed hooks are actively working by implementing hooks that can only function through the Xposed framework. Unlike simple logging hooks, these modifications demonstrate real system-level interception and modification.

## 🎯 Definitive Proof Features

### 1. **System Properties Modification** 
- **Hook**: `android.os.Build.MODEL` and `android.os.SystemProperties.get()`
- **Proof**: Device model name changes to include "LSPosedKit-TestModule-PROOF"
- **Verification**: Check device info in Settings → About Phone
- **Why it's definitive**: Only Xposed can modify static Build properties at runtime

### 2. **Settings App Toast Injection**
- **Hook**: `com.android.settings.Settings.onCreate()`
- **Proof**: Custom toast appears when opening Settings app
- **Message**: "🎯 LSPosedKit TestModule is ACTIVE! 🎯\nHooks are working perfectly!"
- **Why it's definitive**: UI injection into system apps requires system-level hooking

### 3. **OnePlus SystemUI Interception**
- **Hook**: OnePlus-specific SystemUI components (`com.oneplus.systemui`, `com.oplus.systemui`)
- **Proof**: Detailed logging with OnePlus SystemUI version information
- **Verification**: Check logcat for OnePlus-specific hook confirmations
- **Why it's definitive**: Accesses vendor-specific system components

### 4. **Toast Message Modification**
- **Hook**: `android.widget.Toast.makeText()`
- **Proof**: All toast messages get prefixed with "🎯 HOOKED:" and LSPosedKit identifier
- **Why it's definitive**: Global message interception across all apps

### 5. **Activity Title Modification**
- **Hook**: `android.app.Activity.setTitle()`
- **Proof**: Activity titles get prefixed with "🎯" and LSPosedKit identifier
- **Why it's definitive**: Modifies UI elements across all applications

## 🔍 How to Verify the Module is Working

### Method 1: Check Device Model (Most Definitive)
1. Open **Settings** → **About Phone** → **Device Information**
2. Look for device model name containing "LSPosedKit-TestModule-PROOF"
3. If you see this, the module is **definitely working**

### Method 2: Settings App Toast
1. Open the **Settings** app
2. Look for a toast message: "🎯 LSPosedKit TestModule is ACTIVE! 🎯"
3. Toast appears for 5+ seconds with confirmation message

### Method 3: Check Logcat (Technical Verification)
```bash
adb logcat -s LSPK-TestModule:V | grep "PROOF"
```

Look for these definitive proof messages:
- `🚀 CRITICAL PROOF: System server hooks active`
- `🚀 CRITICAL PROOF: Settings app hooks active`
- `🚀 CRITICAL PROOF: SystemUI hooks active`
- `PROOF TRIGGERED: Build.MODEL hooked and modified!`
- `PROOF TRIGGERED: Settings app toast displayed!`

### Method 4: Toast Modifications
1. Trigger any toast message in any app
2. Observe if messages are prefixed with "🎯 HOOKED:"
3. This proves global message interception is working

## 🏗️ Technical Implementation

### Target Packages
The module specifically targets these critical system packages:
- `android` - System server for Build property hooks
- `com.android.settings` - Settings app for visible proof
- `com.android.systemui` - Standard Android SystemUI
- `com.oneplus.systemui` - OnePlus-specific SystemUI
- `com.oplus.systemui` - Alternative OnePlus SystemUI package
- `*` - All other apps for general hooks

### Hook Types Implemented
1. **Field Hooks**: Modifying static `Build.MODEL` field
2. **Method Hooks**: Intercepting `SystemProperties.get()` calls
3. **UI Hooks**: Injecting toasts and modifying titles
4. **Platform-specific Hooks**: OnePlus SystemUI components

### OnePlus Architecture Compatibility
Specifically designed for OnePlus 12 (CPH2583) running OxygenOS 15.0 with:
- Snapdragon 8 Gen 3 Mobile Platform
- Android 15 (API level 35)
- OnePlus-specific system paths and components

## 🚀 Why This Provides Definitive Proof

Unlike simple logging modules that just record method calls, this module:

1. **Modifies System Behavior**: Changes how the system reports device information
2. **Injects UI Elements**: Adds visual proof through toasts and title modifications  
3. **Intercepts Global APIs**: Hooks system-wide APIs like Toast and SystemProperties
4. **Targets Vendor Components**: Specifically hooks OnePlus SystemUI components
5. **Provides Multiple Verification Methods**: Offers both technical and user-visible proof

If any of these modifications are visible, it's **mathematically certain** that:
- LSPosed is properly installed and active
- The TestModule is loaded and functioning
- Xposed hooks are successfully intercepting system calls
- The module has system-level access and permissions

This eliminates any doubt about whether the hooking framework is actually working.

## 🛠️ Development Details

- **Module ID**: `test-module`
- **Version**: 1.0.0
- **Scope**: Targets critical system packages plus wildcard for general apps
- **Hot Reload**: Supported with proper cleanup
- **Settings**: Configurable package inclusion/exclusion (defaults to proof mode)

## 📋 Installation & Usage

1. Build the module APK
2. Install via LSPosed Manager
3. Enable the module for system packages
4. Reboot device
5. Verify proof using any of the methods above

The module is designed to work immediately upon activation without any configuration required.
