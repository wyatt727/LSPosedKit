package com.wobbz.module.debugapp

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ScrollView

/**
 * Main activity for DebugApp module.
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
            text = "Debug App Module"
            textSize = 24f
            setPadding(0, 0, 0, 32)
        }
        layout.addView(title)
        
        // Description
        val description = TextView(this).apply {
            text = """
                Debug App Module
                
                This module forces applications to run in debug mode by:
                • Setting ApplicationInfo.FLAG_DEBUGGABLE flag
                • Modifying system properties for debugging
                
                The module applies to all applications by default.
                
                Status: Module is active when enabled in LSPosed Manager.
                
                To configure this module:
                1. Open LSPosed Manager
                2. Go to Modules tab
                3. Find "Debug App" and enable it
                4. Select which apps to apply it to
                5. Reboot or restart the target app
            """.trimIndent()
            textSize = 16f
            lineHeight = (textSize * 1.5).toInt()
        }
        layout.addView(description)
        
        scrollView.addView(layout)
        setContentView(scrollView)
    }
} 