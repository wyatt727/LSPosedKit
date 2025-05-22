package com.wobbz.module.uienhancer.services

import android.graphics.Color

/**
 * Service interface for UI theming capabilities.
 * Other modules can use this service to apply consistent theming.
 */
interface IUIThemeService {
    
    /**
     * Theme configuration data class.
     */
    data class ThemeConfig(
        val primaryColor: Int,
        val accentColor: Int,
        val textSizeMultiplier: Float,
        val cornerRadius: Int,
        val isDarkTheme: Boolean,
        val animationsEnabled: Boolean
    )
    
    /**
     * Gets the current theme configuration.
     */
    fun getCurrentTheme(): ThemeConfig
    
    /**
     * Gets the primary color as an integer.
     */
    fun getPrimaryColor(): Int
    
    /**
     * Gets the accent color as an integer.
     */
    fun getAccentColor(): Int
    
    /**
     * Gets the text size multiplier.
     */
    fun getTextSizeMultiplier(): Float
    
    /**
     * Gets the corner radius in pixels.
     */
    fun getCornerRadius(): Int
    
    /**
     * Checks if dark theme is enabled.
     */
    fun isDarkThemeEnabled(): Boolean
    
    /**
     * Checks if animations are enabled.
     */
    fun areAnimationsEnabled(): Boolean
    
    /**
     * Applies the current theme to a view dynamically.
     * @param view The view to apply theming to
     * @param applyBackground Whether to apply background styling
     * @param applyText Whether to apply text styling
     */
    fun applyThemeToView(view: android.view.View, applyBackground: Boolean = true, applyText: Boolean = true)
    
    /**
     * Creates a themed drawable with the current theme settings.
     * @param backgroundColor Background color (null to use primary color)
     * @param cornerRadius Corner radius (null to use default)
     */
    fun createThemedDrawable(backgroundColor: Int? = null, cornerRadius: Int? = null): android.graphics.drawable.Drawable
    
    /**
     * Gets a color that contrasts well with the given background color.
     * Useful for determining text color based on background.
     */
    fun getContrastColor(backgroundColor: Int): Int
} 