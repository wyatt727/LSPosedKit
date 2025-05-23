package com.wobbz.module.networkguard.rules;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\bJ\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\b0\u0007J\b\u0010\u0016\u001a\u00020\u0013H\u0002J\b\u0010\u0017\u001a\u00020\u0013H\u0002J\u000e\u0010\u0018\u001a\u00020\u00132\u0006\u0010\u0019\u001a\u00020\u001aJ\u0006\u0010\u001b\u001a\u00020\u0013J\u000e\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001aJ\u000e\u0010\u001f\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\bRJ\u0010\u0005\u001a>\u0012\u0018\u0012\u0016\u0012\u0004\u0012\u00020\b \t*\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u00070\u0007 \t*\u001e\u0012\u0018\u0012\u0016\u0012\u0004\u0012\u00020\b \t*\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u00070\u0007\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\n \t*\u0004\u0018\u00010\u000b0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\b0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0010\u001a\n \t*\u0004\u0018\u00010\u00110\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006 "}, d2 = {"Lcom/wobbz/module/networkguard/rules/RuleManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "adapter", "Lcom/squareup/moshi/JsonAdapter;", "", "Lcom/wobbz/module/networkguard/rules/NetworkRule;", "kotlin.jvm.PlatformType", "moshi", "Lcom/squareup/moshi/Moshi;", "rules", "", "rulesFile", "Ljava/io/File;", "type", "Ljava/lang/reflect/ParameterizedType;", "addRule", "", "rule", "getRules", "initializeDefaultRules", "loadRules", "removeRule", "ruleId", "", "saveRules", "shouldAllowConnection", "", "url", "updateRule", "NetworkGuard_debug"})
public final class RuleManager {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.wobbz.module.networkguard.rules.NetworkRule> rules = null;
    private final com.squareup.moshi.Moshi moshi = null;
    private final java.lang.reflect.ParameterizedType type = null;
    private final com.squareup.moshi.JsonAdapter<java.util.List<com.wobbz.module.networkguard.rules.NetworkRule>> adapter = null;
    @org.jetbrains.annotations.NotNull
    private final java.io.File rulesFile = null;
    
    public RuleManager(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.wobbz.module.networkguard.rules.NetworkRule> getRules() {
        return null;
    }
    
    public final void addRule(@org.jetbrains.annotations.NotNull
    com.wobbz.module.networkguard.rules.NetworkRule rule) {
    }
    
    public final void removeRule(@org.jetbrains.annotations.NotNull
    java.lang.String ruleId) {
    }
    
    public final void updateRule(@org.jetbrains.annotations.NotNull
    com.wobbz.module.networkguard.rules.NetworkRule rule) {
    }
    
    public final boolean shouldAllowConnection(@org.jetbrains.annotations.NotNull
    java.lang.String url) {
        return false;
    }
    
    public final void saveRules() {
    }
    
    private final void loadRules() {
    }
    
    private final void initializeDefaultRules() {
    }
}