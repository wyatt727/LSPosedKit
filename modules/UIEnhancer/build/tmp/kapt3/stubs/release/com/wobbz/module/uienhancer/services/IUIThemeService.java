package com.wobbz.module.uienhancer.services;

/**
 * Service interface for UI theming capabilities.
 * Other modules can use this service to apply consistent theming.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001:\u0001\u0019J$\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007H&J\b\u0010\t\u001a\u00020\u0007H&J%\u0010\n\u001a\u00020\u000b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\rH&\u00a2\u0006\u0002\u0010\u000fJ\b\u0010\u0010\u001a\u00020\rH&J\u0010\u0010\u0011\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\rH&J\b\u0010\u0012\u001a\u00020\rH&J\b\u0010\u0013\u001a\u00020\u0014H&J\b\u0010\u0015\u001a\u00020\rH&J\b\u0010\u0016\u001a\u00020\u0017H&J\b\u0010\u0018\u001a\u00020\u0007H&\u00a8\u0006\u001a"}, d2 = {"Lcom/wobbz/module/uienhancer/services/IUIThemeService;", "", "applyThemeToView", "", "view", "Landroid/view/View;", "applyBackground", "", "applyText", "areAnimationsEnabled", "createThemedDrawable", "Landroid/graphics/drawable/Drawable;", "backgroundColor", "", "cornerRadius", "(Ljava/lang/Integer;Ljava/lang/Integer;)Landroid/graphics/drawable/Drawable;", "getAccentColor", "getContrastColor", "getCornerRadius", "getCurrentTheme", "Lcom/wobbz/module/uienhancer/services/IUIThemeService$ThemeConfig;", "getPrimaryColor", "getTextSizeMultiplier", "", "isDarkThemeEnabled", "ThemeConfig", "UIEnhancer_release"})
public abstract interface IUIThemeService {
    
    /**
     * Gets the current theme configuration.
     */
    @org.jetbrains.annotations.NotNull
    public abstract com.wobbz.module.uienhancer.services.IUIThemeService.ThemeConfig getCurrentTheme();
    
    /**
     * Gets the primary color as an integer.
     */
    public abstract int getPrimaryColor();
    
    /**
     * Gets the accent color as an integer.
     */
    public abstract int getAccentColor();
    
    /**
     * Gets the text size multiplier.
     */
    public abstract float getTextSizeMultiplier();
    
    /**
     * Gets the corner radius in pixels.
     */
    public abstract int getCornerRadius();
    
    /**
     * Checks if dark theme is enabled.
     */
    public abstract boolean isDarkThemeEnabled();
    
    /**
     * Checks if animations are enabled.
     */
    public abstract boolean areAnimationsEnabled();
    
    /**
     * Applies the current theme to a view dynamically.
     * @param view The view to apply theming to
     * @param applyBackground Whether to apply background styling
     * @param applyText Whether to apply text styling
     */
    public abstract void applyThemeToView(@org.jetbrains.annotations.NotNull
    android.view.View view, boolean applyBackground, boolean applyText);
    
    /**
     * Creates a themed drawable with the current theme settings.
     * @param backgroundColor Background color (null to use primary color)
     * @param cornerRadius Corner radius (null to use default)
     */
    @org.jetbrains.annotations.NotNull
    public abstract android.graphics.drawable.Drawable createThemedDrawable(@org.jetbrains.annotations.Nullable
    java.lang.Integer backgroundColor, @org.jetbrains.annotations.Nullable
    java.lang.Integer cornerRadius);
    
    /**
     * Gets a color that contrasts well with the given background color.
     * Useful for determining text color based on background.
     */
    public abstract int getContrastColor(int backgroundColor);
    
    /**
     * Service interface for UI theming capabilities.
     * Other modules can use this service to apply consistent theming.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
    
    /**
     * Theme configuration data class.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0015\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\tH\u00c6\u0003J\t\u0010\u0019\u001a\u00020\tH\u00c6\u0003JE\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\tH\u00c6\u0001J\u0013\u0010\u001b\u001a\u00020\t2\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u001e\u001a\u00020\u001fH\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\n\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006 "}, d2 = {"Lcom/wobbz/module/uienhancer/services/IUIThemeService$ThemeConfig;", "", "primaryColor", "", "accentColor", "textSizeMultiplier", "", "cornerRadius", "isDarkTheme", "", "animationsEnabled", "(IIFIZZ)V", "getAccentColor", "()I", "getAnimationsEnabled", "()Z", "getCornerRadius", "getPrimaryColor", "getTextSizeMultiplier", "()F", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "toString", "", "UIEnhancer_release"})
    public static final class ThemeConfig {
        private final int primaryColor = 0;
        private final int accentColor = 0;
        private final float textSizeMultiplier = 0.0F;
        private final int cornerRadius = 0;
        private final boolean isDarkTheme = false;
        private final boolean animationsEnabled = false;
        
        public ThemeConfig(int primaryColor, int accentColor, float textSizeMultiplier, int cornerRadius, boolean isDarkTheme, boolean animationsEnabled) {
            super();
        }
        
        public final int getPrimaryColor() {
            return 0;
        }
        
        public final int getAccentColor() {
            return 0;
        }
        
        public final float getTextSizeMultiplier() {
            return 0.0F;
        }
        
        public final int getCornerRadius() {
            return 0;
        }
        
        public final boolean isDarkTheme() {
            return false;
        }
        
        public final boolean getAnimationsEnabled() {
            return false;
        }
        
        public final int component1() {
            return 0;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final float component3() {
            return 0.0F;
        }
        
        public final int component4() {
            return 0;
        }
        
        public final boolean component5() {
            return false;
        }
        
        public final boolean component6() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.wobbz.module.uienhancer.services.IUIThemeService.ThemeConfig copy(int primaryColor, int accentColor, float textSizeMultiplier, int cornerRadius, boolean isDarkTheme, boolean animationsEnabled) {
            return null;
        }
        
        @java.lang.Override
        public boolean equals(@org.jetbrains.annotations.Nullable
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override
        @org.jetbrains.annotations.NotNull
        public java.lang.String toString() {
            return null;
        }
    }
}