package com.wobbz.module.networkguard.services

import com.wobbz.module.networkguard.rules.NetworkRule

/**
 * Service interface for accessing network rules.
 * This service can be used by other modules to access network filtering rules.
 */
interface INetworkRuleService {
    /**
     * Gets the current list of network rules.
     */
    fun getRules(): List<NetworkRule>
    
    /**
     * Checks if a URL should be allowed.
     */
    fun shouldAllowUrl(url: String): Boolean
    
    /**
     * Gets the version of the rule service.
     */
    fun getVersion(): String
} 