package com.wobbz.module.networkguard.services;

/**
 * Service interface for accessing network rules.
 * This service can be used by other modules to access network filtering rules.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H&J\b\u0010\u0005\u001a\u00020\u0006H&J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0006H&\u00a8\u0006\n"}, d2 = {"Lcom/wobbz/module/networkguard/services/INetworkRuleService;", "", "getRules", "", "Lcom/wobbz/module/networkguard/rules/NetworkRule;", "getVersion", "", "shouldAllowUrl", "", "url", "NetworkGuard_release"})
public abstract interface INetworkRuleService {
    
    /**
     * Gets the current list of network rules.
     */
    @org.jetbrains.annotations.NotNull
    public abstract java.util.List<com.wobbz.module.networkguard.rules.NetworkRule> getRules();
    
    /**
     * Checks if a URL should be allowed.
     */
    public abstract boolean shouldAllowUrl(@org.jetbrains.annotations.NotNull
    java.lang.String url);
    
    /**
     * Gets the version of the rule service.
     */
    @org.jetbrains.annotations.NotNull
    public abstract java.lang.String getVersion();
}