package com.wobbz.module.uienhancer

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ScrollView

/**
 * Main activity for UIEnhancer module.
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
            text = "UI Enhancer"
            textSize = 24f
            setPadding(0, 0, 0, 32)
        }
        layout.addView(title)
        
        // Description
        val description = TextView(this).apply {
            text = """
                UI Enhancer Module
                
                This module provides UI customization and enhancement capabilities:
                • Dynamic theme modification
                • Custom UI element styling
                • Layout enhancements
                • Visual effect improvements
                
                Features:
                • Theme color customization
                • Typography enhancements
                • Animation improvements
                • Accessibility enhancements
                • Custom view styling
                
                Enhancement Categories:
                • Material Design improvements
                • Dark/light theme optimization
                • Font and sizing adjustments
                • Color scheme modifications
                • UI responsiveness improvements
                
                Status: Module is active when enabled in LSPosed Manager.
                
                To configure this module:
                1. Open LSPosed Manager
                2. Go to Modules tab
                3. Find "UI Enhancer" and enable it
                4. Select which apps to apply it to
                5. Reboot or restart the target app
                
                Note: UI changes may require app restart to take full effect.
            """.trimIndent()
            textSize = 16f
            lineHeight = (textSize * 1.5).toInt()
        }
        layout.addView(description)
        
        scrollView.addView(layout)
        setContentView(scrollView)
    }
} 