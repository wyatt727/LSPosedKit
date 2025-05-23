package com.wobbz.module.networkguard

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ScrollView

/**
 * Main activity for NetworkGuard module.
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
            text = "Network Guard"
            textSize = 24f
            setPadding(0, 0, 0, 32)
        }
        layout.addView(title)
        
        // Description
        val description = TextView(this).apply {
            text = """
                Network Guard Module
                
                This module provides network traffic monitoring and blocking with:
                • Rule-based filtering system
                • Service Bus integration for cross-module communication
                • Real-time network monitoring
                • Configurable allow/block rules
                
                Features:
                • NetworkRuleProvider service for cross-module access
                • Hot-reload support for dynamic updates
                • JSON-based rule persistence
                • URL and HttpURLConnection hooks
                
                Status: Module is active when enabled in LSPosed Manager.
                
                To configure this module:
                1. Open LSPosed Manager
                2. Go to Modules tab
                3. Find "Network Guard" and enable it
                4. Select which apps to apply it to
                5. Reboot or restart the target app
                
                Configuration files are stored in the app's data directory.
            """.trimIndent()
            textSize = 16f
            lineHeight = (textSize * 1.5).toInt()
        }
        layout.addView(description)
        
        scrollView.addView(layout)
        setContentView(scrollView)
    }
} 