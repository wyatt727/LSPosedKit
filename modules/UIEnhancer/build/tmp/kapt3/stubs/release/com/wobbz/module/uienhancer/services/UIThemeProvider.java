package com.wobbz.module.uienhancer.services;

/**
 * Implementation of IUIThemeService that provides theming capabilities to other modules.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0005\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003B\r\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0010\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J \u0010\u0010\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\bH\u0016J\b\u0010\u0013\u001a\u00020\bH\u0016J\b\u0010\u0014\u001a\u00020\nH\u0002J!\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0018H\u0016\u00a2\u0006\u0002\u0010\u001aJ\b\u0010\u001b\u001a\u00020\u0018H\u0016J\u0010\u0010\u001c\u001a\u00020\u00182\u0006\u0010\u0017\u001a\u00020\u0018H\u0016J\b\u0010\u001d\u001a\u00020\u0018H\u0016J\b\u0010\u001e\u001a\u00020\u001fH\u0016J\b\u0010 \u001a\u00020\u0018H\u0016J\b\u0010!\u001a\u00020\"H\u0016J\b\u0010#\u001a\u00020\bH\u0016J\b\u0010$\u001a\u00020\nH\u0016J\b\u0010%\u001a\u00020\nH\u0016J\b\u0010&\u001a\u00020\nH\u0016R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\'"}, d2 = {"Lcom/wobbz/module/uienhancer/services/UIThemeProvider;", "Lcom/wobbz/module/uienhancer/services/IUIThemeService;", "Lcom/wobbz/framework/core/Releasable;", "Lcom/wobbz/framework/service/ReloadAware;", "themeManager", "Lcom/wobbz/module/uienhancer/ui/ThemeManager;", "(Lcom/wobbz/module/uienhancer/ui/ThemeManager;)V", "isReleased", "", "applyBackgroundTheme", "", "view", "Landroid/view/View;", "applyTextTheme", "textView", "Landroid/widget/TextView;", "applyThemeToView", "applyBackground", "applyText", "areAnimationsEnabled", "checkNotReleased", "createThemedDrawable", "Landroid/graphics/drawable/Drawable;", "backgroundColor", "", "cornerRadius", "(Ljava/lang/Integer;Ljava/lang/Integer;)Landroid/graphics/drawable/Drawable;", "getAccentColor", "getContrastColor", "getCornerRadius", "getCurrentTheme", "Lcom/wobbz/module/uienhancer/services/IUIThemeService$ThemeConfig;", "getPrimaryColor", "getTextSizeMultiplier", "", "isDarkThemeEnabled", "onAfterReload", "onBeforeReload", "release", "UIEnhancer_release"})
public final class UIThemeProvider implements com.wobbz.module.uienhancer.services.IUIThemeService, com.wobbz.framework.core.Releasable, com.wobbz.framework.service.ReloadAware {
    @org.jetbrains.annotations.NotNull
    private final com.wobbz.module.uienhancer.ui.ThemeManager themeManager = null;
    private boolean isReleased = false;
    
    public UIThemeProvider(@org.jetbrains.annotations.NotNull
    com.wobbz.module.uienhancer.ui.ThemeManager themeManager) {
        super();
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public com.wobbz.module.uienhancer.services.IUIThemeService.ThemeConfig getCurrentTheme() {
        return null;
    }
    
    @java.lang.Override
    public int getPrimaryColor() {
        return 0;
    }
    
    @java.lang.Override
    public int getAccentColor() {
        return 0;
    }
    
    @java.lang.Override
    public float getTextSizeMultiplier() {
        return 0.0F;
    }
    
    @java.lang.Override
    public int getCornerRadius() {
        return 0;
    }
    
    @java.lang.Override
    public boolean isDarkThemeEnabled() {
        return false;
    }
    
    @java.lang.Override
    public boolean areAnimationsEnabled() {
        return false;
    }
    
    @java.lang.Override
    public void applyThemeToView(@org.jetbrains.annotations.NotNull
    android.view.View view, boolean applyBackground, boolean applyText) {
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public android.graphics.drawable.Drawable createThemedDrawable(@org.jetbrains.annotations.Nullable
    java.lang.Integer backgroundColor, @org.jetbrains.annotations.Nullable
    java.lang.Integer cornerRadius) {
        return null;
    }
    
    @java.lang.Override
    public int getContrastColor(int backgroundColor) {
        return 0;
    }
    
    private final void applyBackgroundTheme(android.view.View view) {
    }
    
    private final void applyTextTheme(android.widget.TextView textView) {
    }
    
    @java.lang.Override
    public void release() {
    }
    
    @java.lang.Override
    public void onBeforeReload() {
    }
    
    @java.lang.Override
    public void onAfterReload() {
    }
    
    private final void checkNotReleased() {
    }
}