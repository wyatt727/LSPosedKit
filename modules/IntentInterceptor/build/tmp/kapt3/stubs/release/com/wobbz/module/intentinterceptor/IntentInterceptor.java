package com.wobbz.module.intentinterceptor;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003B\u0005\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\u001a\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\b\u0010\u001b\u001a\u00020\u0011H\u0016J\u0010\u0010\u001c\u001a\u00020\u00112\u0006\u0010\u001d\u001a\u00020\u001eH\u0016J\b\u0010\u001f\u001a\u00020\u0011H\u0016J\b\u0010 \u001a\u00020\u0011H\u0016J\u0010\u0010!\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Lcom/wobbz/module/intentinterceptor/IntentInterceptor;", "Lcom/wobbz/framework/core/IModulePlugin;", "Lcom/wobbz/framework/hot/IHotReloadable;", "Lcom/wobbz/framework/core/ModuleLifecycle;", "()V", "filterManager", "Lcom/wobbz/module/intentinterceptor/filters/IntentFilterManager;", "historyService", "Lcom/wobbz/module/intentinterceptor/services/IntentHistoryService;", "hooks", "", "", "intentMonitor", "Lcom/wobbz/module/intentinterceptor/monitor/IntentMonitor;", "settings", "Lcom/wobbz/framework/settings/SettingsProvider;", "initialize", "", "context", "Landroid/content/Context;", "xposed", "Lcom/wobbz/framework/core/XposedInterface;", "isExcluded", "", "packageName", "", "isIncluded", "onHotReload", "onPackageLoaded", "param", "Lcom/wobbz/framework/core/PackageLoadedParam;", "onStart", "onStop", "shouldMonitorPackage", "IntentInterceptor_release"})
@com.wobbz.framework.processor.XposedPlugin(id = "intent-interceptor", name = "Intent Interceptor", version = "1.0.0", description = "Monitors and intercepts Intent communications between apps and components", author = "LSPosedKit", scope = {"*"})
@com.wobbz.framework.processor.HotReloadable
public final class IntentInterceptor implements com.wobbz.framework.core.IModulePlugin, com.wobbz.framework.hot.IHotReloadable, com.wobbz.framework.core.ModuleLifecycle {
    @org.jetbrains.annotations.NotNull
    private final java.util.List<java.lang.Object> hooks = null;
    private com.wobbz.framework.settings.SettingsProvider settings;
    private com.wobbz.module.intentinterceptor.monitor.IntentMonitor intentMonitor;
    private com.wobbz.module.intentinterceptor.filters.IntentFilterManager filterManager;
    private com.wobbz.module.intentinterceptor.services.IntentHistoryService historyService;
    
    public IntentInterceptor() {
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