package com.wobbz.module.networkguard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003B\u0005\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\u0010\u0010\u0018\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\b\u0010\u0019\u001a\u00020\u000fH\u0016J\u0010\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u001cH\u0016J\b\u0010\u001d\u001a\u00020\u000fH\u0016J\b\u0010\u001e\u001a\u00020\u000fH\u0016J\u0010\u0010\u001f\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0002R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006 "}, d2 = {"Lcom/wobbz/module/networkguard/NetworkGuard;", "Lcom/wobbz/framework/core/IModulePlugin;", "Lcom/wobbz/framework/hot/IHotReloadable;", "Lcom/wobbz/framework/core/ModuleLifecycle;", "()V", "hooks", "", "", "networkRuleService", "Lcom/wobbz/module/networkguard/rules/NetworkRuleProvider;", "ruleManager", "Lcom/wobbz/module/networkguard/rules/RuleManager;", "settings", "Lcom/wobbz/framework/settings/SettingsProvider;", "initialize", "", "context", "Landroid/content/Context;", "xposed", "Lcom/wobbz/framework/core/XposedInterface;", "isExcluded", "", "packageName", "", "isIncluded", "onHotReload", "onPackageLoaded", "param", "Lcom/wobbz/framework/core/PackageLoadedParam;", "onStart", "onStop", "shouldMonitorPackage", "NetworkGuard_release"})
@com.wobbz.framework.processor.XposedPlugin(id = "network-guard", name = "Network Guard", version = "1.0.0", description = "Monitors and controls network traffic using rule-based filtering", author = "LSPosedKit", scope = {"*"})
@com.wobbz.framework.processor.HotReloadable
public final class NetworkGuard implements com.wobbz.framework.core.IModulePlugin, com.wobbz.framework.hot.IHotReloadable, com.wobbz.framework.core.ModuleLifecycle {
    @org.jetbrains.annotations.NotNull
    private final java.util.List<java.lang.Object> hooks = null;
    private com.wobbz.framework.settings.SettingsProvider settings;
    private com.wobbz.module.networkguard.rules.RuleManager ruleManager;
    private com.wobbz.module.networkguard.rules.NetworkRuleProvider networkRuleService;
    
    public NetworkGuard() {
        super();
    }
    
    @java.lang.Override
    public void initialize(@org.jetbrains.annotations.NotNull
    android.content.Context context, @org.jetbrains.annotations.NotNull
    com.wobbz.framework.core.XposedInterface xposed) {
    }
    
    @java.lang.Override
    public void onStart() {
    }
    
    @java.lang.Override
    public void onStop() {
    }
    
    @java.lang.Override
    public void onPackageLoaded(@org.jetbrains.annotations.NotNull
    com.wobbz.framework.core.PackageLoadedParam param) {
    }
    
    private final boolean shouldMonitorPackage(java.lang.String packageName) {
        return false;
    }
    
    private final boolean isExcluded(java.lang.String packageName) {
        return false;
    }
    
    private final boolean isIncluded(java.lang.String packageName) {
        return false;
    }
    
    @java.lang.Override
    public void onHotReload() {
    }
}