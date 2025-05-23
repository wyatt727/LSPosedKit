package com.wobbz.module.intentinterceptor.hooks;

/**
 * Comprehensive Intent hooking system for monitoring Intent communications.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0002\n\u0002\b\r\u0018\u00002\u00020\u0001:\b\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001cB%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\u000eJ\b\u0010\u000f\u001a\u00020\u0010H\u0002J\b\u0010\u0011\u001a\u00020\u0010H\u0002J\b\u0010\u0012\u001a\u00020\u0010H\u0002J\b\u0010\u0013\u001a\u00020\u0010H\u0002J\b\u0010\u0014\u001a\u00020\u0010H\u0002R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks;", "", "param", "Lcom/wobbz/framework/core/PackageLoadedParam;", "intentMonitor", "Lcom/wobbz/module/intentinterceptor/monitor/IntentMonitor;", "filterManager", "Lcom/wobbz/module/intentinterceptor/filters/IntentFilterManager;", "historyService", "Lcom/wobbz/module/intentinterceptor/services/IntentHistoryService;", "(Lcom/wobbz/framework/core/PackageLoadedParam;Lcom/wobbz/module/intentinterceptor/monitor/IntentMonitor;Lcom/wobbz/module/intentinterceptor/filters/IntentFilterManager;Lcom/wobbz/module/intentinterceptor/services/IntentHistoryService;)V", "hooks", "", "applyHooks", "", "hookActivityIntents", "", "hookBroadcastIntents", "hookContextIntents", "hookIntentModification", "hookServiceIntents", "BindServiceHooker", "BroadcastReceiveHooker", "IntentModificationHooker", "SendBroadcastHooker", "ServiceStartHooker", "StartActivityForResultHooker", "StartActivityHooker", "StartServiceHooker", "IntentInterceptor_release"})
public final class IntentHooks {
    @org.jetbrains.annotations.NotNull
    private final com.wobbz.framework.core.PackageLoadedParam param = null;
    @org.jetbrains.annotations.NotNull
    private final com.wobbz.module.intentinterceptor.monitor.IntentMonitor intentMonitor = null;
    @org.jetbrains.annotations.NotNull
    private final com.wobbz.module.intentinterceptor.filters.IntentFilterManager filterManager = null;
    @org.jetbrains.annotations.NotNull
    private final com.wobbz.module.intentinterceptor.services.IntentHistoryService historyService = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<java.lang.Object> hooks = null;
    
    public IntentHooks(@org.jetbrains.annotations.NotNull
    com.wobbz.framework.core.PackageLoadedParam param, @org.jetbrains.annotations.NotNull
    com.wobbz.module.intentinterceptor.monitor.IntentMonitor intentMonitor, @org.jetbrains.annotations.NotNull
    com.wobbz.module.intentinterceptor.filters.IntentFilterManager filterManager, @org.jetbrains.annotations.NotNull
    com.wobbz.module.intentinterceptor.services.IntentHistoryService historyService) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<java.lang.Object> applyHooks() {
        return null;
    }
    
    private final void hookActivityIntents() {
    }
    
    private final void hookContextIntents() {
    }
    
    private final void hookServiceIntents() {
    }
    
    private final void hookBroadcastIntents() {
    }
    
    private final void hookIntentModification() {
    }
    
    /**
     * Hooker for Context.bindService() methods
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0007"}, d2 = {"Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks$BindServiceHooker;", "Lcom/wobbz/framework/core/Hooker;", "(Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks;)V", "beforeHook", "", "param", "Lcom/wobbz/framework/core/HookParam;", "IntentInterceptor_release"})
    public final class BindServiceHooker implements com.wobbz.framework.core.Hooker {
        
        public BindServiceHooker() {
            super();
        }
        
        @java.lang.Override
        public void beforeHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
        
        @java.lang.Override
        public void afterHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
    }
    
    /**
     * Hooker for BroadcastReceiver.onReceive() methods
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0007"}, d2 = {"Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks$BroadcastReceiveHooker;", "Lcom/wobbz/framework/core/Hooker;", "(Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks;)V", "beforeHook", "", "param", "Lcom/wobbz/framework/core/HookParam;", "IntentInterceptor_release"})
    public final class BroadcastReceiveHooker implements com.wobbz.framework.core.Hooker {
        
        public BroadcastReceiveHooker() {
            super();
        }
        
        @java.lang.Override
        public void beforeHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
        
        @java.lang.Override
        public void afterHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
    }
    
    /**
     * Hooker for Intent modification methods
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0007"}, d2 = {"Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks$IntentModificationHooker;", "Lcom/wobbz/framework/core/Hooker;", "(Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks;)V", "afterHook", "", "param", "Lcom/wobbz/framework/core/HookParam;", "IntentInterceptor_release"})
    public final class IntentModificationHooker implements com.wobbz.framework.core.Hooker {
        
        public IntentModificationHooker() {
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
     * Hooker for Context.sendBroadcast() methods
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0007"}, d2 = {"Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks$SendBroadcastHooker;", "Lcom/wobbz/framework/core/Hooker;", "(Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks;)V", "beforeHook", "", "param", "Lcom/wobbz/framework/core/HookParam;", "IntentInterceptor_release"})
    public final class SendBroadcastHooker implements com.wobbz.framework.core.Hooker {
        
        public SendBroadcastHooker() {
            super();
        }
        
        @java.lang.Override
        public void beforeHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
        
        @java.lang.Override
        public void afterHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
    }
    
    /**
     * Hooker for Service.onStartCommand() methods
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0007"}, d2 = {"Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks$ServiceStartHooker;", "Lcom/wobbz/framework/core/Hooker;", "(Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks;)V", "beforeHook", "", "param", "Lcom/wobbz/framework/core/HookParam;", "IntentInterceptor_release"})
    public final class ServiceStartHooker implements com.wobbz.framework.core.Hooker {
        
        public ServiceStartHooker() {
            super();
        }
        
        @java.lang.Override
        public void beforeHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
        
        @java.lang.Override
        public void afterHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
    }
    
    /**
     * Hooker for Activity.startActivityForResult() methods
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0007"}, d2 = {"Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks$StartActivityForResultHooker;", "Lcom/wobbz/framework/core/Hooker;", "(Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks;)V", "beforeHook", "", "param", "Lcom/wobbz/framework/core/HookParam;", "IntentInterceptor_release"})
    public final class StartActivityForResultHooker implements com.wobbz.framework.core.Hooker {
        
        public StartActivityForResultHooker() {
            super();
        }
        
        @java.lang.Override
        public void beforeHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
        
        @java.lang.Override
        public void afterHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
    }
    
    /**
     * Hooker for Activity.startActivity() methods
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0007"}, d2 = {"Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks$StartActivityHooker;", "Lcom/wobbz/framework/core/Hooker;", "(Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks;)V", "beforeHook", "", "param", "Lcom/wobbz/framework/core/HookParam;", "IntentInterceptor_release"})
    public final class StartActivityHooker implements com.wobbz.framework.core.Hooker {
        
        public StartActivityHooker() {
            super();
        }
        
        @java.lang.Override
        public void beforeHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
        
        @java.lang.Override
        public void afterHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
    }
    
    /**
     * Hooker for Context.startService() methods
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0007"}, d2 = {"Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks$StartServiceHooker;", "Lcom/wobbz/framework/core/Hooker;", "(Lcom/wobbz/module/intentinterceptor/hooks/IntentHooks;)V", "beforeHook", "", "param", "Lcom/wobbz/framework/core/HookParam;", "IntentInterceptor_release"})
    public final class StartServiceHooker implements com.wobbz.framework.core.Hooker {
        
        public StartServiceHooker() {
            super();
        }
        
        @java.lang.Override
        public void beforeHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
        
        @java.lang.Override
        public void afterHook(@org.jetbrains.annotations.NotNull
        com.wobbz.framework.core.HookParam param) {
        }
    }
}