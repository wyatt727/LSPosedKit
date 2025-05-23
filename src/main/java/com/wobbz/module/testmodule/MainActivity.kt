package com.wobbz.module.testmodule

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ScrollView

/**
 * Main activity for TestModule.
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
            text = "Test Module"
            textSize = 24f
            setPadding(0, 0, 0, 32)
        }
        layout.addView(title)
        
        // Description
        val description = TextView(this).apply {
            text = """
                Test Module
                
                This is a simple test module for basic functionality testing and framework validation.
                
                Purpose:
                • Test LSPosedKit framework integration
                • Validate basic hooking functionality
                • Ensure proper module lifecycle
                • Test hot-reload capabilities
                
                Features:
                • Basic method hooking examples
                • Framework component testing
                • Simple logging and debugging
                • Module lifecycle validation
                
                Testing Capabilities:
                • Hook installation verification
                • Parameter passing tests
                • Return value modification tests
                • Exception handling tests
                • Framework API validation
                
                Status: Module is active when enabled in LSPosed Manager.
                
                To configure this module:
                1. Open LSPosed Manager
                2. Go to Modules tab
                3. Find "Test Module" and enable it
                4. Select which apps to apply it to
                5. Reboot or restart the target app
                
                This module is primarily for development and testing purposes.
            """.trimIndent()
            textSize = 16f
            lineHeight = (textSize * 1.5).toInt()
        }
        layout.addView(description)
        
        scrollView.addView(layout)
        setContentView(scrollView)
    }
} 