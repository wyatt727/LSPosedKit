package com.wobbz.module.uienhancer

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.wobbz.framework.settings.SettingsProvider
import com.wobbz.framework.settings.SettingsUIGenerator

/**
 * Main activity for UIEnhancer module.
 * Provides a settings interface for configuring UI enhancements.
 */
class MainActivity : AppCompatActivity() {
    
    private companion object {
        const val TAG = "UIEnhancer.MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "Starting UIEnhancer MainActivity")
        
        try {
            // Create settings fragment
            val settingsFragment = UIEnhancerSettingsFragment()
            
            // Show the settings fragment
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit()
                
            Log.d(TAG, "Settings fragment created successfully")
                
        } catch (e: Exception) {
            // Fallback to info dialog if settings generation fails
            Log.e(TAG, "Failed to create settings fragment", e)
            showModuleInfo(e)
        }
    }
    
    private fun showModuleInfo(error: Exception? = null) {
        val message = buildString {
            appendLine("UIEnhancer Module")
            appendLine()
            
            if (error != null) {
                appendLine("⚠️ Settings Error: ${error.message}")
                appendLine()
            }
            
            appendLine("This module enhances UI elements with custom themes and styling.")
            appendLine()
            appendLine("Configuration:")
            appendLine("• Primary/Accent Colors: Set custom theme colors")
            appendLine("• Text Size: Adjust text scaling (0.5x to 3.0x)")
            appendLine("• Corner Radius: Customize button roundness")
            appendLine("• Target Packages: Choose which apps to enhance")
            appendLine("• Feature Toggles: Enable/disable specific enhancements")
            appendLine()
            appendLine("System Framework Targeting:")
            appendLine("• For system-wide themes: Enable for 'android' and 'com.android.systemui'")
            appendLine("• For app-specific themes: Target individual apps")
            appendLine("• Default targets: Settings and SystemUI")
            appendLine()
            appendLine("In LSPosed Manager:")
            appendLine("1. Enable this module")
            appendLine("2. Select scope: specific apps OR system framework")
            appendLine("3. Reboot or restart target apps")
        }
        
        AlertDialog.Builder(this)
            .setTitle("UIEnhancer Settings")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> finish() }
            .setOnCancelListener { finish() }
            .show()
    }
    
    /**
     * Settings fragment that generates UI from settings.json schema
     */
    class UIEnhancerSettingsFragment : PreferenceFragmentCompat() {
        
        private companion object {
            const val TAG = "UIEnhancer.SettingsFragment"
        }
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            val context = preferenceManager.context
            val screen = preferenceManager.createPreferenceScreen(context)
            preferenceScreen = screen
            
            Log.d(TAG, "Creating preferences...")
            
            try {
                // Get settings provider for this module
                Log.d(TAG, "Creating settings provider...")
                val settingsProvider = SettingsProvider.of(context)
                Log.d(TAG, "Settings provider created successfully")
                
                // Generate preferences from settings.json schema
                Log.d(TAG, "Generating preferences from schema...")
                SettingsUIGenerator.generatePreferences(context, settingsProvider, screen)
                Log.d(TAG, "Preferences generated successfully")
                
                // Add helpful note about targeting
                addTargetingHelpPreference(screen)
                Log.d(TAG, "Help preference added")
                
            } catch (e: Exception) {
                // Add error preference if generation fails
                Log.e(TAG, "Failed to generate preferences", e)
                addErrorPreference(screen, e.message ?: "Unknown error")
                
                // Also try to add basic manual preferences as fallback
                try {
                    addFallbackPreferences(screen)
                } catch (fallbackError: Exception) {
                    Log.e(TAG, "Failed to add fallback preferences", fallbackError)
                }
            }
        }
        
        private fun addTargetingHelpPreference(screen: PreferenceScreen) {
            val helpPreference = androidx.preference.Preference(requireContext()).apply {
                key = "targeting_help"
                title = "💡 Targeting Guide"
                summary = "Tap for help with LSPosed scope vs module target packages"
                
                setOnPreferenceClickListener {
                    val helpMessage = buildString {
                        appendLine("📱 LSPosed Scope vs Module Target Packages")
                        appendLine()
                        appendLine("🔧 LSPosed Manager Scope:")
                        appendLine("• Controls which apps the module gets loaded into")
                        appendLine("• Set in LSPosed Manager → Modules → UIEnhancer → Scope")
                        appendLine("• Should typically include 'System Framework' or specific apps")
                        appendLine()
                        appendLine("🎯 Module Target Packages (this setting):")
                        appendLine("• Controls which apps the module actually modifies")
                        appendLine("• More granular control within the module")
                        appendLine("• Can be more restrictive than LSPosed scope")
                        appendLine()
                        appendLine("⭐ Recommended Setup:")
                        appendLine("• LSPosed Scope: 'System Framework' (loads into all apps)")
                        appendLine("• Target Packages: '*' (affect all loaded apps)")
                        appendLine("OR")
                        appendLine("• LSPosed Scope: Specific apps")
                        appendLine("• Target Packages: Same specific apps")
                        appendLine()
                        appendLine("🔸 Target Package Options:")
                        appendLine("• '*' = All apps (when scope allows)")
                        appendLine("• 'com.android.systemui' = System UI only")
                        appendLine("• 'com.android.settings' = Settings app only")
                        appendLine("• Specific package names for targeted theming")
                        appendLine()
                        appendLine("💡 Tip: Start with '*' for global theming, then restrict if needed")
                    }
                    
                    AlertDialog.Builder(context)
                        .setTitle("LSPosed Scope vs Target Packages")
                        .setMessage(helpMessage)
                        .setPositiveButton("OK", null)
                        .show()
                    
                    true
                }
            }
            
            screen.addPreference(helpPreference)
        }
        
        private fun addErrorPreference(screen: PreferenceScreen, error: String) {
            val errorPreference = androidx.preference.Preference(requireContext()).apply {
                key = "settings_error"
                title = "⚠️ Settings Error"
                summary = "Failed to load settings: $error"
                isEnabled = false
            }
            
            screen.addPreference(errorPreference)
        }
        
        private fun addFallbackPreferences(screen: PreferenceScreen) {
            Log.d(TAG, "Adding fallback preferences...")
            
            // Add basic enable/disable switch
            val enablePref = androidx.preference.SwitchPreference(requireContext()).apply {
                key = "enabled"
                title = "Enable UI Enhancement"
                summary = "Master switch for all UI enhancements"
                isChecked = true
            }
            screen.addPreference(enablePref)
            
            // Add primary color preference
            val primaryColorPref = androidx.preference.EditTextPreference(requireContext()).apply {
                key = "primary_color"
                title = "Primary Color"
                summary = "Primary theme color in hex format (e.g., #3F51B5)"
                text = "#3F51B5"
            }
            screen.addPreference(primaryColorPref)
            
            // Add accent color preference
            val accentColorPref = androidx.preference.EditTextPreference(requireContext()).apply {
                key = "accent_color"
                title = "Accent Color"
                summary = "Accent color for buttons and highlights"
                text = "#FF4081"
            }
            screen.addPreference(accentColorPref)
            
            // Add text size preference
            val textSizePref = androidx.preference.EditTextPreference(requireContext()).apply {
                key = "text_size_multiplier"
                title = "Text Size Multiplier"
                summary = "Multiplier for text sizes (0.5 to 3.0)"
                text = "1.0"
            }
            screen.addPreference(textSizePref)
            
            Log.d(TAG, "Fallback preferences added successfully")
        }
    }
} 