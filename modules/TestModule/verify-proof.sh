#!/bin/bash

# TestModule Proof Verification Script
# This script helps verify that the TestModule is providing definitive proof of working

echo "🎯 LSPosedKit TestModule - Proof Verification Script"
echo "=================================================="
echo ""

# Check if ADB is available
if ! command -v adb &> /dev/null; then
    echo "❌ ADB not found. Please install Android Debug Bridge."
    exit 1
fi

# Check if device is connected
if ! adb devices | grep -q "device$"; then
    echo "❌ No Android device detected. Please connect your device and enable USB debugging."
    exit 1
fi

echo "📱 Device connected. Running proof verification..."
echo ""

# Function to check device model
check_device_model() {
    echo "🔍 Checking device model for proof..."
    MODEL=$(adb shell getprop ro.product.model)
    echo "Current device model: $MODEL"
    
    if echo "$MODEL" | grep -q "LSPosedKit-TestModule-PROOF"; then
        echo "✅ PROOF CONFIRMED: Device model contains LSPosedKit proof string!"
        echo "   This is definitive evidence that Build property hooks are working."
        return 0
    else
        echo "❌ No proof found in device model."
        return 1
    fi
}

# Function to check logcat for proof
check_logcat_proof() {
    echo ""
    echo "🔍 Checking logcat for TestModule proof messages..."
    echo "   (This will show the last 50 relevant log entries)"
    
    # Get recent proof logs
    PROOF_LOGS=$(adb logcat -d -s LSPK-TestModule:V | grep "PROOF" | tail -50)
    
    if [ -n "$PROOF_LOGS" ]; then
        echo "✅ PROOF LOGS FOUND:"
        echo "$PROOF_LOGS"
        return 0
    else
        echo "❌ No TestModule proof logs found in logcat."
        echo "   The module might not be active or enabled."
        return 1
    fi
}

# Function to check system properties
check_system_properties() {
    echo ""
    echo "🔍 Checking for modified system properties..."
    
    # Check Android version property
    VERSION=$(adb shell getprop ro.build.version.release)
    echo "Android version: $VERSION"
    
    if echo "$VERSION" | grep -q "LSPosedKit-PROOF"; then
        echo "✅ PROOF CONFIRMED: Android version property is hooked!"
        return 0
    else
        echo "ℹ️  Android version property not modified (this is OK)."
        return 1
    fi
}

# Function to trigger Settings app for toast proof
trigger_settings_proof() {
    echo ""
    echo "🔍 Triggering Settings app to test toast proof..."
    echo "   Opening Settings app - watch for proof toast on device screen!"
    
    adb shell am start -n com.android.settings/.Settings
    echo "✅ Settings app launched. Check device screen for proof toast:"
    echo "   Expected: '🎯 LSPosedKit TestModule is ACTIVE! 🎯'"
    echo "   If you see this toast, the module is definitely working!"
}

# Function to show real-time logs
show_realtime_logs() {
    echo ""
    echo "🔍 Showing real-time TestModule logs (press Ctrl+C to stop)..."
    echo "   Perform actions on your device to trigger hooks..."
    
    adb logcat -s LSPK-TestModule:V | grep --line-buffered "PROOF"
}

# Main verification sequence
echo "Starting verification sequence..."
echo ""

# Step 1: Check device model (most definitive)
MODEL_PROOF=0
check_device_model && MODEL_PROOF=1

# Step 2: Check logcat for existing proof
LOGCAT_PROOF=0
check_logcat_proof && LOGCAT_PROOF=1

# Step 3: Check system properties
PROP_PROOF=0
check_system_properties && PROP_PROOF=1

# Step 4: Trigger Settings app for visual proof
trigger_settings_proof

# Summary
echo ""
echo "📊 PROOF VERIFICATION SUMMARY"
echo "============================="

if [ $MODEL_PROOF -eq 1 ]; then
    echo "✅ Device Model Proof: CONFIRMED (Definitive)"
elif [ $LOGCAT_PROOF -eq 1 ]; then
    echo "✅ Logcat Proof: CONFIRMED (Strong evidence)"
else
    echo "❌ No definitive proof found"
fi

if [ $PROP_PROOF -eq 1 ]; then
    echo "✅ System Properties Proof: CONFIRMED"
fi

echo ""
if [ $MODEL_PROOF -eq 1 ] || [ $LOGCAT_PROOF -eq 1 ]; then
    echo "🎉 CONCLUSION: LSPosedKit TestModule is WORKING!"
    echo "   The hooks are successfully intercepting and modifying system behavior."
    echo ""
    echo "📋 What this proves:"
    echo "   • LSPosed is properly installed and active"
    echo "   • TestModule is loaded and functioning" 
    echo "   • Xposed hooks have system-level access"
    echo "   • Framework can modify core Android components"
else
    echo "⚠️  CONCLUSION: No definitive proof found."
    echo ""
    echo "🔧 Troubleshooting steps:"
    echo "   1. Ensure LSPosed is installed and active"
    echo "   2. Check that TestModule is enabled in LSPosed Manager"
    echo "   3. Reboot device after enabling the module"
    echo "   4. Check LSPosed logs for any error messages"
fi

echo ""
echo "💡 TIP: Run '$0 --realtime' to monitor live proof events"

# Handle realtime monitoring option
if [ "$1" = "--realtime" ] || [ "$1" = "-r" ]; then
    show_realtime_logs
fi 