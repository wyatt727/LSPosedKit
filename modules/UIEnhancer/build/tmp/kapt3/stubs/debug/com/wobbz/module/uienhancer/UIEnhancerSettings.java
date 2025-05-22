package com.wobbz.module.uienhancer;

/**
 * Settings class for UI enhancement configuration.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\b\u0015\u0018\u00002\u00020\u0001Bo\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00050\f\u0012\b\b\u0002\u0010\r\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0011R\u0016\u0010\u0006\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0010\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0016\u0010\u000f\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0015R\u0016\u0010\t\u001a\u00020\n8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0015R\u0016\u0010\r\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0015R\u0016\u0010\u000e\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0015R\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0013R\u001c\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00050\f8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 \u00a8\u0006!"}, d2 = {"Lcom/wobbz/module/uienhancer/UIEnhancerSettings;", "", "enabled", "", "primaryColor", "", "accentColor", "textSizeMultiplier", "", "cornerRadius", "", "targetPackages", "", "enhanceButtons", "enhanceTextViews", "applyDarkTheme", "animateTransitions", "(ZLjava/lang/String;Ljava/lang/String;FILjava/util/List;ZZZZ)V", "getAccentColor", "()Ljava/lang/String;", "getAnimateTransitions", "()Z", "getApplyDarkTheme", "getCornerRadius", "()I", "getEnabled", "getEnhanceButtons", "getEnhanceTextViews", "getPrimaryColor", "getTargetPackages", "()Ljava/util/List;", "getTextSizeMultiplier", "()F", "UIEnhancer_debug"})
public final class UIEnhancerSettings {
    @com.wobbz.framework.processor.SettingsKey(value = "enabled")
    private final boolean enabled = false;
    @com.wobbz.framework.processor.SettingsKey(value = "primary_color")
    @org.jetbrains.annotations.NotNull
    private final java.lang.String primaryColor = null;
    @com.wobbz.framework.processor.SettingsKey(value = "accent_color")
    @org.jetbrains.annotations.NotNull
    private final java.lang.String accentColor = null;
    @com.wobbz.framework.processor.SettingsKey(value = "text_size_multiplier")
    private final float textSizeMultiplier = 0.0F;
    @com.wobbz.framework.processor.SettingsKey(value = "corner_radius")
    private final int cornerRadius = 0;
    @com.wobbz.framework.processor.SettingsKey(value = "target_packages")
    @org.jetbrains.annotations.NotNull
    private final java.util.List<java.lang.String> targetPackages = null;
    @com.wobbz.framework.processor.SettingsKey(value = "enhance_buttons")
    private final boolean enhanceButtons = false;
    @com.wobbz.framework.processor.SettingsKey(value = "enhance_text_views")
    private final boolean enhanceTextViews = false;
    @com.wobbz.framework.processor.SettingsKey(value = "apply_dark_theme")
    private final boolean applyDarkTheme = false;
    @com.wobbz.framework.processor.SettingsKey(value = "animate_transitions")
    private final boolean animateTransitions = false;
    
    public UIEnhancerSettings(boolean enabled, @org.jetbrains.annotations.NotNull
    java.lang.String primaryColor, @org.jetbrains.annotations.NotNull
    java.lang.String accentColor, float textSizeMultiplier, int cornerRadius, @org.jetbrains.annotations.NotNull
    java.util.List<java.lang.String> targetPackages, boolean enhanceButtons, boolean enhanceTextViews, boolean applyDarkTheme, boolean animateTransitions) {
        super();
    }
    
    public final boolean getEnabled() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getPrimaryColor() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getAccentColor() {
        return null;
    }
    
    public final float getTextSizeMultiplier() {
        return 0.0F;
    }
    
    public final int getCornerRadius() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<java.lang.String> getTargetPackages() {
        return null;
    }
    
    public final boolean getEnhanceButtons() {
        return false;
    }
    
    public final boolean getEnhanceTextViews() {
        return false;
    }
    
    public final boolean getApplyDarkTheme() {
        return false;
    }
    
    public final boolean getAnimateTransitions() {
        return false;
    }
    
    public UIEnhancerSettings() {
        super();
    }
}