package com.wobbz.module.intentinterceptor

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ScrollView

/**
 * Main activity for IntentInterceptor module.
 * This provides a simple settings and information interface for the module.
 */
class MainActivity : Activity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create a simple layout programmatically
        val scrollView = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        
        // Title
        val title = TextView(this).apply {
            text = "Intent Interceptor"
            textSize = 24f
            setPadding(0, 0, 0, 32)
        }
        layout.addView(title)
        
        // Description
        val description = TextView(this).apply {
            text = """
                Intent Interceptor Module
                
                This module provides comprehensive Intent monitoring and control:
                • Real-time Intent monitoring for activities, broadcasts, and services
                • Rule-based Intent filtering and modification
                • Intent history tracking and recording
                • Cross-module Intent history service
                
                Capabilities:
                • Hook startActivity, sendBroadcast, startService methods
                • Filter Intents based on action, component, data URI
                • Modify Intent extras and flags
                • Record Intent history for analysis
                • Export Intent data for debugging
                
                Security Features:
                • Privacy protection for sensitive data
                • Configurable filtering rules
                • Secure Intent modification
                
                Status: Module is active when enabled in LSPosed Manager.
                
                To configure this module:
                1. Open LSPosed Manager
                2. Go to Modules tab
                3. Find "Intent Interceptor" and enable it
                4. Select which apps to apply it to
                5. Reboot or restart the target app
                
                Intent history is stored locally and can be accessed by other modules.
            """.trimIndent()
            textSize = 16f
            lineHeight = (textSize * 1.5).toInt()
        }
        layout.addView(description)
        
        scrollView.addView(layout)
        setContentView(scrollView)
    }
} 