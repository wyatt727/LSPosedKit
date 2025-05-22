package com.wobbz.module.uienhancer.services

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import com.wobbz.framework.core.Releasable
import com.wobbz.framework.service.ReloadAware
import com.wobbz.module.uienhancer.ui.ThemeManager

/**
 * Implementation of IUIThemeService that provides theming capabilities to other modules.
 */
class UIThemeProvider(
    private val themeManager: ThemeManager
) : IUIThemeService, Releasable, ReloadAware {
    
    private var isReleased = false
    
    override fun getCurrentTheme(): IUIThemeService.ThemeConfig {
        checkNotReleased()
        return themeManager.getCurrentTheme()
    }
    
    override fun getPrimaryColor(): Int {
        checkNotReleased()
        return themeManager.getPrimaryColor()
    }
    
    override fun getAccentColor(): Int {
        checkNotReleased()
        return themeManager.getAccentColor()
    }
    
    override fun getTextSizeMultiplier(): Float {
        checkNotReleased()
        return themeManager.getTextSizeMultiplier()
    }
    
    override fun getCornerRadius(): Int {
        checkNotReleased()
        return themeManager.getCornerRadius()
    }
    
    override fun isDarkThemeEnabled(): Boolean {
        checkNotReleased()
        return themeManager.isDarkThemeEnabled()
    }
    
    override fun areAnimationsEnabled(): Boolean {
        checkNotReleased()
        return themeManager.areAnimationsEnabled()
    }
    
    override fun applyThemeToView(view: View, applyBackground: Boolean, applyText: Boolean) {
        checkNotReleased()
        
        try {
            if (applyBackground) {
                applyBackgroundTheme(view)
            }
            
            if (applyText && view is TextView) {
                applyTextTheme(view)
            }
        } catch (e: Exception) {
            // Silently ignore theming errors to avoid crashes
        }
    }
    
    override fun createThemedDrawable(backgroundColor: Int?, cornerRadius: Int?): Drawable {
        checkNotReleased()
        
        val drawable = GradientDrawable()
        drawable.setColor(backgroundColor ?: getPrimaryColor())
        drawable.cornerRadius = (cornerRadius ?: getCornerRadius()).toFloat()
        return drawable
    }
    
    override fun getContrastColor(backgroundColor: Int): Int {
        checkNotReleased()
        
        // Calculate luminance using relative luminance formula
        val r = Color.red(backgroundColor) / 255.0
        val g = Color.green(backgroundColor) / 255.0
        val b = Color.blue(backgroundColor) / 255.0
        
        val luminance = 0.299 * r + 0.587 * g + 0.114 * b
        
        // Return white for dark backgrounds, black for light backgrounds
        return if (luminance > 0.5) Color.BLACK else Color.WHITE
    }
    
    private fun applyBackgroundTheme(view: View) {
        val drawable = createThemedDrawable()
        view.background = drawable
    }
    
    private fun applyTextTheme(textView: TextView) {
        // Apply text size multiplier
        val currentSize = textView.textSize
        textView.textSize = currentSize * getTextSizeMultiplier()
        
        // Apply theme-appropriate text color
        if (isDarkThemeEnabled()) {
            textView.setTextColor(Color.WHITE)
        } else {
            // For light theme, use a color that contrasts with primary color
            val textColor = getContrastColor(getPrimaryColor())
            textView.setTextColor(textColor)
        }
    }
    
    override fun release() {
        isReleased = true
        // Clean up any resources if needed
    }
    
    override fun onReload() {
        // Refresh theme configuration on hot reload
        themeManager.refreshConfiguration()
    }
    
    private fun checkNotReleased() {
        if (isReleased) {
            throw IllegalStateException("UIThemeProvider has been released")
        }
    }
} 