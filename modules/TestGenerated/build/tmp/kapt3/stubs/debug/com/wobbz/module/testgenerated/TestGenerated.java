package com.wobbz.module.testgenerated;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003B\u0005\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\b\u0010\u0010\u001a\u00020\u000bH\u0016J\u0010\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\b\u0010\u0014\u001a\u00020\u000bH\u0016J\b\u0010\u0015\u001a\u00020\u000bH\u0016J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/wobbz/module/testgenerated/TestGenerated;", "Lcom/wobbz/framework/core/IModulePlugin;", "Lcom/wobbz/framework/hot/IHotReloadable;", "Lcom/wobbz/framework/core/ModuleLifecycle;", "()V", "hooks", "", "", "settings", "Lcom/wobbz/framework/settings/SettingsProvider;", "initialize", "", "context", "Landroid/content/Context;", "xposed", "Lcom/wobbz/framework/core/XposedInterface;", "onHotReload", "onPackageLoaded", "param", "Lcom/wobbz/framework/core/PackageLoadedParam;", "onStart", "onStop", "shouldHookPackage", "", "packageName", "", "TestGenerated_debug"})
@com.wobbz.framework.processor.XposedPlugin(id = "test-generated", name = "TestGenerated", version = "1.0.0", description = "Test module to verify generation works", author = "LSPosedKit Developer", scope = {"*"})
@com.wobbz.framework.processor.HotReloadable
public final class TestGenerated implements com.wobbz.framework.core.IModulePlugin, com.wobbz.framework.hot.IHotReloadable, com.wobbz.framework.core.ModuleLifecycle {
    @org.jetbrains.annotations.NotNull
    private final java.util.List<java.lang.Object> hooks = null;
    private com.wobbz.framework.settings.SettingsProvider settings;
    
    public TestGenerated() {
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
    
    private final boolean shouldHookPackage(java.lang.String packageName) {
        return false;
    }
    
    @java.lang.Override
    public void onHotReload() {
    }
}