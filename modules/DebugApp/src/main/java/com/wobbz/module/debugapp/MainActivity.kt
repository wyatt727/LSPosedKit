package com.wobbz.module.debugapp

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle

/**
 * Main activity for DebugApp module.
 * Provides debug information and development guidance.
 */
class MainActivity : Activity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        showDebugInfo()
    }
    
    private fun showDebugInfo() {
        val message = buildString {
            appendLine("DebugApp Module")
            appendLine()
            appendLine("This is a development and debugging module for LSPosedKit.")
            appendLine()
            appendLine("Purpose:")
            appendLine("• Test LSPosedKit framework functionality")
            appendLine("• Debug hook implementations")
            appendLine("• Validate module scaffolding")
            appendLine("• Development reference implementation")
            appendLine()
            appendLine("Features:")
            appendLine("• Basic hook examples")
            appendLine("• Settings integration testing")
            appendLine("• Hot-reload capability testing")
            appendLine("• Framework API validation")
            appendLine()
            appendLine("Targeting Guidance:")
            appendLine("• Use specific test apps for focused debugging")
            appendLine("• Enable 'System Framework' for framework testing")
            appendLine("• Avoid production use")
            appendLine()
            appendLine("Development Tips:")
            appendLine("• Check LSPosed logs for hook activity")
            appendLine("• Use hot-reload for rapid iteration")
            appendLine("• Modify settings.json for configuration testing")
            appendLine("• Example hooks are in hooks/ directory")
            appendLine()
            appendLine("This module is safe to enable for testing purposes.")
        }
        
        AlertDialog.Builder(this)
            .setTitle("DebugApp - Development Module")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> finish() }
            .setOnCancelListener { finish() }
            .show()
    }
} 