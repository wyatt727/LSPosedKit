package com.wobbz.module.intentinterceptor.filters;

/**
 * Manages Intent filtering and modification rules.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\nJ\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u0016\u001a\u00020\nH\u0002J\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\n0\tJ\b\u0010\u001b\u001a\u00020\u0015H\u0002J\u0006\u0010\u001c\u001a\u00020\u0015J\u0018\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020 H\u0002J\u0018\u0010\"\u001a\u00020\u001e2\u0006\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u0016\u001a\u00020\nH\u0002J\u000e\u0010#\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u0018J\u000e\u0010$\u001a\u00020\u00152\u0006\u0010%\u001a\u00020 J\u0006\u0010&\u001a\u00020\u0015J\u000e\u0010\'\u001a\u00020\u001e2\u0006\u0010\u0019\u001a\u00020\u0018J\u000e\u0010(\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\nRJ\u0010\u0007\u001a>\u0012\u0018\u0012\u0016\u0012\u0004\u0012\u00020\n \u000b*\n\u0012\u0004\u0012\u00020\n\u0018\u00010\t0\t \u000b*\u001e\u0012\u0018\u0012\u0016\u0012\u0004\u0012\u00020\n \u000b*\n\u0012\u0004\u0012\u00020\n\u0018\u00010\t0\t\u0018\u00010\b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\f\u001a\n \u000b*\u0004\u0018\u00010\r0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\n0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0012\u001a\n \u000b*\u0004\u0018\u00010\u00130\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006)"}, d2 = {"Lcom/wobbz/module/intentinterceptor/filters/IntentFilterManager;", "", "context", "Landroid/content/Context;", "settings", "Lcom/wobbz/framework/settings/SettingsProvider;", "(Landroid/content/Context;Lcom/wobbz/framework/settings/SettingsProvider;)V", "adapter", "Lcom/squareup/moshi/JsonAdapter;", "", "Lcom/wobbz/module/intentinterceptor/filters/IntentFilterRule;", "kotlin.jvm.PlatformType", "moshi", "Lcom/squareup/moshi/Moshi;", "rules", "", "rulesFile", "Ljava/io/File;", "type", "Ljava/lang/reflect/ParameterizedType;", "addRule", "", "rule", "applyModification", "Landroid/content/Intent;", "intent", "getRules", "loadDefaultRules", "loadFilters", "matchesPattern", "", "text", "", "pattern", "matchesRule", "processIntent", "removeRule", "ruleId", "saveFilters", "shouldBlock", "updateRule", "IntentInterceptor_debug"})
public final class IntentFilterManager {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull
    private final com.wobbz.framework.settings.SettingsProvider settings = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.wobbz.module.intentinterceptor.filters.IntentFilterRule> rules = null;
    private final com.squareup.moshi.Moshi moshi = null;
    private final java.lang.reflect.ParameterizedType type = null;
    private final com.squareup.moshi.JsonAdapter<java.util.List<com.wobbz.module.intentinterceptor.filters.IntentFilterRule>> adapter = null;
    @org.jetbrains.annotations.NotNull
    private final java.io.File rulesFile = null;
    
    public IntentFilterManager(@org.jetbrains.annotations.NotNull
    android.content.Context context, @org.jetbrains.annotations.NotNull
    com.wobbz.framework.settings.SettingsProvider settings) {
        super();
    }
    
    /**
     * Loads filter rules from storage
     */
    public final void loadFilters() {
    }
    
    /**
     * Saves current rules to storage
     */
    public final void saveFilters() {
    }
    
    /**
     * Adds a new filter rule
     */
    public final void addRule(@org.jetbrains.annotations.NotNull
    com.wobbz.module.intentinterceptor.filters.IntentFilterRule rule) {
    }
    
    /**
     * Removes a filter rule by ID
     */
    public final void removeRule(@org.jetbrains.annotations.NotNull
    java.lang.String ruleId) {
    }
    
    /**
     * Updates an existing rule
     */
    public final void updateRule(@org.jetbrains.annotations.NotNull
    com.wobbz.module.intentinterceptor.filters.IntentFilterRule rule) {
    }
    
    /**
     * Gets all current rules
     */
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.wobbz.module.intentinterceptor.filters.IntentFilterRule> getRules() {
        return null;
    }
    
    /**
     * Checks if an Intent should be blocked
     */
    public final boolean shouldBlock(@org.jetbrains.annotations.NotNull
    android.content.Intent intent) {
        return false;
    }
    
    /**
     * Processes an Intent, potentially modifying it based on rules
     */
    @org.jetbrains.annotations.NotNull
    public final android.content.Intent processIntent(@org.jetbrains.annotations.NotNull
    android.content.Intent intent) {
        return null;
    }
    
    /**
     * Checks if an Intent matches a filter rule
     */
    private final boolean matchesRule(android.content.Intent intent, com.wobbz.module.intentinterceptor.filters.IntentFilterRule rule) {
        return false;
    }
    
    /**
     * Applies a modification rule to an Intent
     */
    private final android.content.Intent applyModification(android.content.Intent intent, com.wobbz.module.intentinterceptor.filters.IntentFilterRule rule) {
        return null;
    }
    
    /**
     * Checks if a string matches a pattern (supports basic wildcards)
     */
    private final boolean matchesPattern(java.lang.String text, java.lang.String pattern) {
        return false;
    }
    
    /**
     * Loads default filter rules
     */
    private final void loadDefaultRules() {
    }
}