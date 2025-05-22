package com.wobbz.module.uienhancer.ui

import android.graphics.Color
import com.wobbz.module.uienhancer.UIEnhancerSettings
import com.wobbz.module.uienhancer.services.IUIThemeService

/**
 * Manages theme configuration and color operations for the UIEnhancer module.
 */
class ThemeManager(private var settings: UIEnhancerSettings) {
    
    /**
     * Gets the current theme configuration.
     */
    fun getCurrentTheme(): IUIThemeService.ThemeConfig {
        return IUIThemeService.ThemeConfig(
            primaryColor = getPrimaryColor(),
            accentColor = getAccentColor(),
            textSizeMultiplier = settings.textSizeMultiplier,
            cornerRadius = settings.cornerRadius,
            isDarkTheme = settings.applyDarkTheme,
            animationsEnabled = settings.animateTransitions
        )
    }
    
    /**
     * Gets the primary color as an integer, with fallback to default.
     */
    fun getPrimaryColor(): Int {
        return parseColorSafe(settings.primaryColor, Color.parseColor("#3F51B5"))
    }
    
    /**
     * Gets the accent color as an integer, with fallback to default.
     */
    fun getAccentColor(): Int {
        return parseColorSafe(settings.accentColor, Color.parseColor("#FF4081"))
    }
    
    /**
     * Gets the text size multiplier.
     */
    fun getTextSizeMultiplier(): Float {
        return settings.textSizeMultiplier.coerceIn(0.5f, 3.0f)
    }
    
    /**
     * Gets the corner radius in pixels.
     */
    fun getCornerRadius(): Int {
        return settings.cornerRadius.coerceIn(0, 50)
    }
    
    /**
     * Checks if dark theme is enabled.
     */
    fun isDarkThemeEnabled(): Boolean {
        return settings.applyDarkTheme
    }
    
    /**
     * Checks if animations are enabled.
     */
    fun areAnimationsEnabled(): Boolean {
        return settings.animateTransitions
    }
    
    /**
     * Refreshes the configuration (useful for hot reload).
     */
    fun refreshConfiguration() {
        // In a real implementation, this might reload settings from storage
        // For now, the settings object should be updated externally
    }
    
    /**
     * Updates the settings configuration.
     */
    fun updateSettings(newSettings: UIEnhancerSettings) {
        settings = newSettings
    }
    
    /**
     * Safely parses a color string, returning a fallback color if parsing fails.
     */
    private fun parseColorSafe(colorString: String, fallback: Int): Int {
        return try {
            Color.parseColor(colorString)
        } catch (e: Exception) {
            fallback
        }
    }
    
    /**
     * Validates if a color string is valid.
     */
    fun isValidColor(colorString: String): Boolean {
        return try {
            Color.parseColor(colorString)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Converts a color to its hex string representation.
     */
    fun colorToHex(color: Int): String {
        return String.format("#%06X", 0xFFFFFF and color)
    }
    
    /**
     * Creates a darker variant of the given color.
     */
    fun darkenColor(color: Int, factor: Float = 0.8f): Int {
        val r = (Color.red(color) * factor).toInt().coerceIn(0, 255)
        val g = (Color.green(color) * factor).toInt().coerceIn(0, 255)
        val b = (Color.blue(color) * factor).toInt().coerceIn(0, 255)
        return Color.rgb(r, g, b)
    }
    
    /**
     * Creates a lighter variant of the given color.
     */
    fun lightenColor(color: Int, factor: Float = 1.2f): Int {
        val r = (Color.red(color) * factor).toInt().coerceIn(0, 255)
        val g = (Color.green(color) * factor).toInt().coerceIn(0, 255)
        val b = (Color.blue(color) * factor).toInt().coerceIn(0, 255)
        return Color.rgb(r, g, b)
    }
    
    /**
     * Blends two colors together.
     */
    fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val clampedRatio = ratio.coerceIn(0f, 1f)
        val inverseRatio = 1f - clampedRatio
        
        val r = (Color.red(color1) * inverseRatio + Color.red(color2) * clampedRatio).toInt()
        val g = (Color.green(color1) * inverseRatio + Color.green(color2) * clampedRatio).toInt()
        val b = (Color.blue(color1) * inverseRatio + Color.blue(color2) * clampedRatio).toInt()
        
        return Color.rgb(r, g, b)
    }
} 