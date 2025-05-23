package com.wobbz.module.networkguard

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.wobbz.framework.settings.SettingsProvider
import com.wobbz.framework.settings.SettingsUIGenerator

/**
 * Main activity for NetworkGuard module.
 * Provides a settings interface for configuring network monitoring and blocking.
 */
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Create settings fragment
            val settingsFragment = NetworkGuardSettingsFragment()
            
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
            appendLine("NetworkGuard Module")
            appendLine()
            appendLine("This module monitors and controls network access for applications.")
            appendLine()
            appendLine("Key Features:")
            appendLine("• Monitor all network connections")
            appendLine("• Block connections by default or with custom rules")
            appendLine("• Log allowed and blocked connections")
            appendLine("• Package-based filtering and rules")
            appendLine()
            appendLine("System Framework Targeting:")
            appendLine("• RECOMMENDED: Enable 'System Framework' in LSPosed scope")
            appendLine("• Required for monitoring system network calls")
            appendLine("• App-specific targeting only monitors selected apps")
            appendLine()
            appendLine("Security Note:")
            appendLine("• 'Block by default' can break system functionality")
            appendLine("• Test rules carefully before enabling")
            appendLine("• Ensure critical system apps are excluded")
            appendLine()
            appendLine("In LSPosed Manager:")
            appendLine("1. Enable this module")
            appendLine("2. Select 'System Framework' for system-wide monitoring")
            appendLine("3. Reboot device")
        }
        
        AlertDialog.Builder(this)
            .setTitle("NetworkGuard Settings")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> finish() }
            .setOnCancelListener { finish() }
            .show()
    }
    
    /**
     * Settings fragment that generates UI from settings.json schema
     */
    class NetworkGuardSettingsFragment : PreferenceFragmentCompat() {
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            val context = preferenceManager.context
            val screen = preferenceManager.createPreferenceScreen(context)
            preferenceScreen = screen
            
            try {
                // Get settings provider for this module
                val settingsProvider = SettingsProvider.of(context)
                
                // Generate preferences from settings.json schema
                SettingsUIGenerator.generatePreferences(context, settingsProvider, screen)
                
                // Add helpful notes about targeting and security
                addTargetingHelpPreference(screen)
                addSecurityWarningPreference(screen)
                
            } catch (e: Exception) {
                // Add error preference if generation fails
                addErrorPreference(screen, e.message ?: "Unknown error")
            }
        }
        
        private fun addTargetingHelpPreference(screen: PreferenceScreen) {
            val helpPreference = androidx.preference.Preference(requireContext()).apply {
                key = "targeting_help"
                title = "🌐 Network Monitoring Scope"
                summary = "Tap for help with system framework vs app-specific monitoring"
                
                setOnPreferenceClickListener {
                    val helpMessage = buildString {
                        appendLine("Network Monitoring Scope:")
                        appendLine()
                        appendLine("System Framework (RECOMMENDED):")
                        appendLine("• Enable 'System Framework' in LSPosed scope")
                        appendLine("• Monitors ALL network calls system-wide")
                        appendLine("• Required for system app network control")
                        appendLine("• Catches low-level network operations")
                        appendLine()
                        appendLine("App-Specific Targeting:")
                        appendLine("• Only monitors selected applications")
                        appendLine("• Limited system network visibility")
                        appendLine("• Good for testing specific apps")
                        appendLine()
                        appendLine("Package Filtering:")
                        appendLine("• Use 'Monitor all apps' for comprehensive coverage")
                        appendLine("• Exclude critical system packages")
                        appendLine("• Default excludes: SystemUI, Settings, LSPosed")
                        appendLine()
                        appendLine("Performance Note:")
                        appendLine("• System-wide monitoring may impact performance")
                        appendLine("• Use logging carefully in production")
                    }
                    
                    AlertDialog.Builder(context)
                        .setTitle("Network Monitoring Guide")
                        .setMessage(helpMessage)
                        .setPositiveButton("OK", null)
                        .show()
                    
                    true
                }
            }
            
            screen.addPreference(helpPreference)
        }
        
        private fun addSecurityWarningPreference(screen: PreferenceScreen) {
            val securityPreference = androidx.preference.Preference(requireContext()).apply {
                key = "security_warning"
                title = "⚠️ Security Warning"
                summary = "Important information about blocking network access"
                
                setOnPreferenceClickListener {
                    val securityMessage = buildString {
                        appendLine("Security & Stability Considerations:")
                        appendLine()
                        appendLine("Block by Default:")
                        appendLine("• Can break essential system functions")
                        appendLine("• May prevent updates and time sync")
                        appendLine("• Can cause app crashes and instability")
                        appendLine("• Use only for testing or specific needs")
                        appendLine()
                        appendLine("Critical System Packages:")
                        appendLine("• Always exclude system UI and settings")
                        appendLine("• Exclude LSPosed manager itself")
                        appendLine("• Consider excluding core system services")
                        appendLine()
                        appendLine("Testing Rules:")
                        appendLine("• Test with non-critical apps first")
                        appendLine("• Have a way to disable the module")
                        appendLine("• Monitor system stability")
                        appendLine("• Check logs for blocked system calls")
                        appendLine()
                        appendLine("Recovery:")
                        appendLine("• Disable module in LSPosed if issues occur")
                        appendLine("• Safe mode can bypass module loading")
                        appendLine("• Keep rule updates conservative")
                    }
                    
                    AlertDialog.Builder(context)
                        .setTitle("Security Warning")
                        .setMessage(securityMessage)
                        .setPositiveButton("OK", null)
                        .show()
                    
                    true
                }
            }
            
            screen.addPreference(securityPreference)
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