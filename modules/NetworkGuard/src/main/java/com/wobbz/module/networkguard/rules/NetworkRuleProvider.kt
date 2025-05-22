package com.wobbz.module.networkguard.rules

import com.wobbz.framework.core.Releasable
import com.wobbz.framework.service.ReloadAware
import com.wobbz.module.networkguard.services.INetworkRuleService

class NetworkRuleProvider(private val ruleManager: RuleManager) : INetworkRuleService, Releasable, ReloadAware {
    
    // Cached data to improve performance
    private var cachedRules: List<NetworkRule>? = null
    private var isReleased = false
    
    override fun getRules(): List<NetworkRule> {
        if (isReleased) {
            return emptyList()
        }
        
        // Use cached rules if available, otherwise get from rule manager
        return cachedRules ?: ruleManager.getRules().also { cachedRules = it }
    }
    
    override fun shouldAllowUrl(url: String): Boolean {
        if (isReleased) {
            return true // Default to allowing when released
        }
        
        return ruleManager.shouldAllowConnection(url)
    }
    
    override fun getVersion(): String {
        return "1.0.0"
    }
    
    override fun release() {
        // Release resources held by this service
        cachedRules = null
        isReleased = true
        
        // Ensure rules are persisted before releasing
        try {
            ruleManager.saveRules()
        } catch (e: Exception) {
            android.util.Log.e("NetworkRuleProvider", "Error saving rules during release", e)
        }
        
        android.util.Log.d("NetworkRuleProvider", "NetworkRuleProvider resources released")
    }
    
    override fun onBeforeReload() {
        // Called before the module is hot-reloaded
        // Save any state that needs to persist across reloads
        android.util.Log.d("NetworkRuleProvider", "Preparing for hot-reload")
        
        try {
            ruleManager.saveRules()
        } catch (e: Exception) {
            android.util.Log.e("NetworkRuleProvider", "Error saving rules before reload", e)
        }
        
        // Clear cache to ensure fresh data after reload
        cachedRules = null
    }
    
    override fun onAfterReload() {
        // Called after the module is hot-reloaded
        // Restore state or re-initialize as needed
        android.util.Log.d("NetworkRuleProvider", "Hot-reload completed, reinitializing")
        
        // Force rules to be reloaded from disk
        cachedRules = null
        isReleased = false
        
        // Verify rules are accessible
        try {
            val rules = getRules()
            android.util.Log.d("NetworkRuleProvider", "Reloaded ${rules.size} network rules")
        } catch (e: Exception) {
            android.util.Log.e("NetworkRuleProvider", "Error accessing rules after reload", e)
        }
    }
} 