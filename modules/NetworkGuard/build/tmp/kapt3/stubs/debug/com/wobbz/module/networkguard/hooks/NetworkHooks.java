package com.wobbz.module.networkguard.hooks;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001:\u0002\u000f\u0010B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\nJ\b\u0010\u000b\u001a\u00020\fH\u0002J\b\u0010\r\u001a\u00020\fH\u0002J\b\u0010\u000e\u001a\u00020\fH\u0002R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/wobbz/module/networkguard/hooks/NetworkHooks;", "", "param", "Lcom/wobbz/framework/core/PackageLoadedParam;", "ruleManager", "Lcom/wobbz/module/networkguard/rules/RuleManager;", "(Lcom/wobbz/framework/core/PackageLoadedParam;Lcom/wobbz/module/networkguard/rules/RuleManager;)V", "hooks", "", "applyHooks", "", "hookHttpUrlConnection", "", "hookUrlOpenConnection", "tryHookOkHttp", "HttpUrlConnectionHooker", "UrlOpenConnectionHooker", "NetworkGuard_debug"})
public final class NetworkHooks {
    @org.jetbrains.annotations.NotNull
    private final com.wobbz.framework.core.PackageLoadedParam param = null;
    @org.jetbrains.annotations.NotNull
    private final com.wobbz.module.networkguard.rules.RuleManager ruleManager = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<java.lang.Object> hooks = null;
    
    public NetworkHooks(@org.jetbrains.annotations.NotNull
    com.wobbz.framework.core.PackageLoadedParam param, @org.jetbrains.annotations.NotNull
    com.wobbz.module.networkguard.rules.RuleManager ruleManager) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<java.lang.Object> applyHooks() {
        return null;
    }
    
    private final void hookUrlOpenConnection() {
    }
    
    private final void hookHttpUrlConnection() {
    }
    
    private final void tryHookOkHttp() {
    }
    
    /**
     * Hooker for HttpURLConnection.connect() method
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0007"}, d2 = {"Lcom/wobbz/module/networkguard/hooks/NetworkHooks$HttpUrlConnectionHooker;", "Lcom/wobbz/framework/core/Hooker;", "(Lcom/wobbz/module/networkguard/hooks/NetworkHooks;)V", "beforeHook", "", "hookParam", "Lcom/wobbz/framework/core/HookParam;", "NetworkGuard_debug"})
    public final class HttpUrlConnectionHooker implements com.wobbz.framework.core.Hooker {
        
        public HttpUrlConnectionHooker() {
            super();
        }
        
        @java.lang.Override
        public void beforeHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam hookParam) {
        }
        
        @java.lang.Override
        public void afterHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
    }
    
    /**
     * Hooker for URL.openConnection() method
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0007"}, d2 = {"Lcom/wobbz/module/networkguard/hooks/NetworkHooks$UrlOpenConnectionHooker;", "Lcom/wobbz/framework/core/Hooker;", "(Lcom/wobbz/module/networkguard/hooks/NetworkHooks;)V", "beforeHook", "", "hookParam", "Lcom/wobbz/framework/core/HookParam;", "NetworkGuard_debug"})
    public final class UrlOpenConnectionHooker implements com.wobbz.framework.core.Hooker {
        
        public UrlOpenConnectionHooker() {
            super();
        }
        
        @java.lang.Override
        public void beforeHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam hookParam) {
        }
        
        @java.lang.Override
        public void afterHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
    }
}