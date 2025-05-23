package com.wobbz.module.debugapp;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\u0018\u00002\u00020\u00012\u00020\u0002:\u0003\u001a\u001b\u001cB\u0005\u00a2\u0006\u0002\u0010\u0003J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0002J\u0018\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\b\u0010\u0014\u001a\u00020\fH\u0016J\u0010\u0010\u0015\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002R\u0018\u0010\u0004\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lcom/wobbz/module/debugapp/DebugApp;", "Lcom/wobbz/framework/core/IModulePlugin;", "Lcom/wobbz/framework/hot/IHotReloadable;", "()V", "hooks", "", "Lcom/wobbz/framework/core/MethodUnhooker;", "moduleSettings", "Lcom/wobbz/module/debugapp/DebugApp$DebugAppSettings;", "settings", "Lcom/wobbz/framework/settings/SettingsProvider;", "hookSystemProperties", "", "param", "Lcom/wobbz/framework/core/PackageLoadedParam;", "initialize", "context", "Landroid/content/Context;", "xposed", "Lcom/wobbz/framework/core/XposedInterface;", "onHotReload", "onPackageLoaded", "shouldEnableDebug", "", "packageName", "", "ApplicationInfoFlagsHooker", "DebugAppSettings", "SystemPropertiesHooker", "DebugApp_debug"})
@com.wobbz.framework.processor.XposedPlugin(id = "debug-app", name = "Debug App", version = "1.0.0", description = "Forces applications to run in debug mode", author = "LSPosedKit", scope = {"*"})
@com.wobbz.framework.processor.HotReloadable
public final class DebugApp implements com.wobbz.framework.core.IModulePlugin, com.wobbz.framework.hot.IHotReloadable {
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.wobbz.framework.core.MethodUnhooker<?>> hooks = null;
    private com.wobbz.framework.settings.SettingsProvider settings;
    private com.wobbz.module.debugapp.DebugApp.DebugAppSettings moduleSettings;
    
    public DebugApp() {
        super();
    }
    
    @java.lang.Override
    public void initialize(@org.jetbrains.annotations.NotNull
    android.content.Context context, @org.jetbrains.annotations.NotNull
    com.wobbz.framework.core.XposedInterface xposed) {
    }
    
    @java.lang.Override
    public void onPackageLoaded(@org.jetbrains.annotations.NotNull
    com.wobbz.framework.core.PackageLoadedParam param) {
    }
    
    private final void hookSystemProperties(com.wobbz.framework.core.PackageLoadedParam param) {
    }
    
    private final boolean shouldEnableDebug(java.lang.String packageName) {
        return false;
    }
    
    @java.lang.Override
    public void onHotReload() {
    }
    
    /**
     * Hooker that modifies ApplicationInfo.flags to add FLAG_DEBUGGABLE.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0007"}, d2 = {"Lcom/wobbz/module/debugapp/DebugApp$ApplicationInfoFlagsHooker;", "Lcom/wobbz/framework/core/Hooker;", "()V", "afterHook", "", "param", "Lcom/wobbz/framework/core/HookParam;", "DebugApp_debug"})
    public static final class ApplicationInfoFlagsHooker implements com.wobbz.framework.core.Hooker {
        
        public ApplicationInfoFlagsHooker() {
            super();
        }
        
        @java.lang.Override
        public void afterHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
        
        @java.lang.Override
        public void beforeHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
    }
    
    /**
     * Settings class demonstrating @SettingsKey annotation usage.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\b\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002R\u0016\u0010\u0003\u001a\u00020\u00048\u0006X\u0087D\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u001c\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001c\u0010\f\u001a\b\u0012\u0004\u0012\u00020\t0\b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0016\u0010\u000e\u001a\u00020\t8\u0006X\u0087D\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u0011"}, d2 = {"Lcom/wobbz/module/debugapp/DebugApp$DebugAppSettings;", "", "()V", "enableForAll", "", "getEnableForAll", "()Z", "excludedPackages", "", "", "getExcludedPackages", "()Ljava/util/Set;", "includedPackages", "getIncludedPackages", "logLevel", "getLogLevel", "()Ljava/lang/String;", "DebugApp_debug"})
    public static final class DebugAppSettings {
        @com.wobbz.framework.processor.SettingsKey(value = "enable_for_all")
        private final boolean enableForAll = true;
        @com.wobbz.framework.processor.SettingsKey(value = "excluded_packages")
        @org.jetbrains.annotations.NotNull
        private final java.util.Set<java.lang.String> excludedPackages = null;
        @com.wobbz.framework.processor.SettingsKey(value = "included_packages")
        @org.jetbrains.annotations.NotNull
        private final java.util.Set<java.lang.String> includedPackages = null;
        @com.wobbz.framework.processor.SettingsKey(value = "log_level")
        @org.jetbrains.annotations.NotNull
        private final java.lang.String logLevel = "INFO";
        
        public DebugAppSettings() {
            super();
        }
        
        public final boolean getEnableForAll() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.Set<java.lang.String> getExcludedPackages() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.Set<java.lang.String> getIncludedPackages() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getLogLevel() {
            return null;
        }
    }
    
    /**
     * Hooker that modifies system properties to return debug-friendly values.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\b"}, d2 = {"Lcom/wobbz/module/debugapp/DebugApp$SystemPropertiesHooker;", "Lcom/wobbz/framework/core/Hooker;", "()V", "afterHook", "", "param", "Lcom/wobbz/framework/core/HookParam;", "Companion", "DebugApp_debug"})
    public static final class SystemPropertiesHooker implements com.wobbz.framework.core.Hooker {
        @org.jetbrains.annotations.NotNull
        private static final java.util.Map<java.lang.String, java.lang.String> debugProperties = null;
        @org.jetbrains.annotations.NotNull
        public static final com.wobbz.module.debugapp.DebugApp.SystemPropertiesHooker.Companion Companion = null;
        
        public SystemPropertiesHooker() {
            super();
        }
        
        @java.lang.Override
        public void afterHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
        
        @java.lang.Override
        public void beforeHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/wobbz/module/debugapp/DebugApp$SystemPropertiesHooker$Companion;", "", "()V", "debugProperties", "", "", "DebugApp_debug"})
        public static final class Companion {
            
            private Companion() {
                super();
            }
        }
    }
}