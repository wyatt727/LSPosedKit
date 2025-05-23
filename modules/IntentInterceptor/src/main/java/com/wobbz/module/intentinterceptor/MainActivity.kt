package com.wobbz.module.intentinterceptor

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.wobbz.framework.settings.SettingsProvider
import com.wobbz.framework.settings.SettingsUIGenerator

/**
 * Main activity for IntentInterceptor module.
 * Provides a settings interface for configuring intent monitoring and filtering.
 */
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Create settings fragment
            val settingsFragment = IntentInterceptorSettingsFragment()
            
            // Show the settings fragment
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit()
                
        } catch (e: Exception) {
            // Fallback to info dialog if settings generation fails
            showModuleInfo()
        }
    }
    
    private fun showModuleInfo() {
        val message = buildString {
            appendLine("IntentInterceptor Module")
            appendLine()
            appendLine("This module monitors and intercepts Intent activities across the system.")
            appendLine()
            appendLine("Key Features:")
            appendLine("• Monitor activity starts, broadcasts, and services")
            appendLine("• Block or modify malicious intents")
            appendLine("• Export intent logs for analysis")
            appendLine("• Track intent history and statistics")
            appendLine()
            appendLine("System Framework Targeting:")
            appendLine("• RECOMMENDED: Enable 'System Framework' in LSPosed scope")
            appendLine("• This allows monitoring cross-app intent flows")
            appendLine("• App-specific targeting limits visibility")
            appendLine()
            appendLine("Privacy Note:")
            appendLine("• Intent extras may contain sensitive data")
            appendLine("• Configure logging options carefully")
            appendLine("• Consider export implications")
            appendLine()
            appendLine("In LSPosed Manager:")
            appendLine("1. Enable this module")
            appendLine("2. Select 'System Framework' for full monitoring")
            appendLine("3. Reboot device")
        }
        
        AlertDialog.Builder(this)
            .setTitle("IntentInterceptor Settings")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> finish() }
            .setOnCancelListener { finish() }
            .show()
    }
    
    /**
     * Settings fragment that generates UI from settings.json schema
     */
    class IntentInterceptorSettingsFragment : PreferenceFragmentCompat() {
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            val context = preferenceManager.context
            val screen = preferenceManager.createPreferenceScreen(context)
            preferenceScreen = screen
            
            try {
                // Get settings provider for this module
                val settingsProvider = SettingsProvider.of(context)
                
                // Generate preferences from settings.json schema
                SettingsUIGenerator.generatePreferences(context, settingsProvider, screen)
                
                // Add helpful notes about targeting and privacy
                addTargetingHelpPreference(screen)
                addPrivacyWarningPreference(screen)
                
            } catch (e: Exception) {
                // Add error preference if generation fails
                addErrorPreference(screen, e.message ?: "Unknown error")
            }
        }
        
        private fun addTargetingHelpPreference(screen: PreferenceScreen) {
            val helpPreference = androidx.preference.Preference(requireContext()).apply {
                key = "targeting_help"
                title = "🎯 Targeting Guide"
                summary = "Tap for help with system framework vs app-specific targeting"
                
                setOnPreferenceClickListener {
                    val helpMessage = buildString {
                        appendLine("Intent Monitoring Scope:")
                        appendLine()
                        appendLine("System Framework (RECOMMENDED):")
                        appendLine("• Enable 'System Framework' in LSPosed scope")
                        appendLine("• Monitors ALL intent flows across the system")
                        appendLine("• Required for cross-app intent tracking")
                        appendLine("• Catches system-initiated intents")
                        appendLine()
                        appendLine("App-Specific Targeting:")
                        appendLine("• Only monitors intents within selected apps")
                        appendLine("• Limited visibility of system intents")
                        appendLine("• Useful for debugging specific apps")
                        appendLine()
                        appendLine("Package Filtering:")
                        appendLine("• Use 'Monitor all apps' with exclusions for broad coverage")
                        appendLine("• Use included packages for focused monitoring")
                        appendLine("• Default excludes system UI to reduce noise")
                    }
                    
                    AlertDialog.Builder(context)
                        .setTitle("Targeting Guide")
                        .setMessage(helpMessage)
                        .setPositiveButton("OK", null)
                        .show()
                    
                    true
                }
            }
            
            screen.addPreference(helpPreference)
        }
        
        private fun addPrivacyWarningPreference(screen: PreferenceScreen) {
            val privacyPreference = androidx.preference.Preference(requireContext()).apply {
                key = "privacy_warning"
                title = "🔒 Privacy Notice"
                summary = "Important information about intent data and logging"
                
                setOnPreferenceClickListener {
                    val privacyMessage = buildString {
                        appendLine("Privacy & Security Considerations:")
                        appendLine()
                        appendLine("Intent Extras Logging:")
                        appendLine("• Intent extras may contain sensitive data")
                        appendLine("• Usernames, passwords, personal info")
                        appendLine("• Consider disabling in production")
                        appendLine()
                        appendLine("Log Export:")
                        appendLine("• Exported logs are stored on device")
                        appendLine("• May be accessible to other apps")
                        appendLine("• Review data before sharing")
                        appendLine()
                        appendLine("Sensitive Intent Actions:")
                        appendLine("• Payment, authentication, and location intents")
                        appendLine("• Can be excluded from logging")
                        appendLine("• Configure based on your needs")
                        appendLine()
                        appendLine("Best Practices:")
                        appendLine("• Disable logging in production")
                        appendLine("• Regularly clear intent history")
                        appendLine("• Review exported data carefully")
                    }
                    
                    AlertDialog.Builder(context)
                        .setTitle("Privacy Notice")
                        .setMessage(privacyMessage)
                        .setPositiveButton("OK", null)
                        .show()
                    
                    true
                }
            }
            
            screen.addPreference(privacyPreference)
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
    }
} 