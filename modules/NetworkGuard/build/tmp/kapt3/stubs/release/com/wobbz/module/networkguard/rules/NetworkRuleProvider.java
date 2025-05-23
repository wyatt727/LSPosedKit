package com.wobbz.module.networkguard.rules;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003B\r\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\f\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0016J\b\u0010\r\u001a\u00020\u000eH\u0016J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\b\u0010\u0011\u001a\u00020\u0010H\u0016J\b\u0010\u0012\u001a\u00020\u0010H\u0016J\u0010\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\u0014\u001a\u00020\u000eH\u0016R\u0016\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/wobbz/module/networkguard/rules/NetworkRuleProvider;", "Lcom/wobbz/module/networkguard/services/INetworkRuleService;", "Lcom/wobbz/framework/core/Releasable;", "Lcom/wobbz/framework/service/ReloadAware;", "ruleManager", "Lcom/wobbz/module/networkguard/rules/RuleManager;", "(Lcom/wobbz/module/networkguard/rules/RuleManager;)V", "cachedRules", "", "Lcom/wobbz/module/networkguard/rules/NetworkRule;", "isReleased", "", "getRules", "getVersion", "", "onAfterReload", "", "onBeforeReload", "release", "shouldAllowUrl", "url", "NetworkGuard_release"})
public final class NetworkRuleProvider implements com.wobbz.module.networkguard.services.INetworkRuleService, com.wobbz.framework.core.Releasable, com.wobbz.framework.service.ReloadAware {
    @org.jetbrains.annotations.NotNull
    private final com.wobbz.module.networkguard.rules.RuleManager ruleManager = null;
    @org.jetbrains.annotations.Nullable
    private java.util.List<com.wobbz.module.networkguard.rules.NetworkRule> cachedRules;
    private boolean isReleased = false;
    
    public NetworkRuleProvider(@org.jetbrains.annotations.NotNull
    com.wobbz.module.networkguard.rules.RuleManager ruleManager) {
        super();
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public java.util.List<com.wobbz.module.networkguard.rules.NetworkRule> getRules() {
        return null;
    }
    
    @java.lang.Override
    public boolean shouldAllowUrl(@org.jetbrains.annotations.NotNull
    java.lang.String url) {
        return false;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public java.lang.String getVersion() {
        return null;
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
}