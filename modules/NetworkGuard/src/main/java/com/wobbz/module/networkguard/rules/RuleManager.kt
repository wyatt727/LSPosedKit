package com.wobbz.module.networkguard.rules

import android.content.Context
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types
import java.io.File

class RuleManager(private val context: Context) {
    
    private val rules = mutableListOf<NetworkRule>()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val type = Types.newParameterizedType(List::class.java, NetworkRule::class.java)
    private val adapter = moshi.adapter<List<NetworkRule>>(type)
    private val rulesFile: File
    
    init {
        rulesFile = File(context.filesDir, "network_rules.json")
        loadRules()
    }
    
    fun getRules(): List<NetworkRule> {
        return rules.toList()
    }
    
    fun addRule(rule: NetworkRule) {
        rules.add(rule)
        saveRules()
    }
    
    fun removeRule(ruleId: String) {
        rules.removeIf { it.id == ruleId }
        saveRules()
    }
    
    fun updateRule(rule: NetworkRule) {
        val index = rules.indexOfFirst { it.id == rule.id }
        if (index >= 0) {
            rules[index] = rule
            saveRules()
        }
    }
    
    fun shouldAllowConnection(url: String): Boolean {
        // Default to allowing connections
        var shouldAllow = true
        
        // Find the most specific rule that applies
        val applicableRules = rules
            .filter { it.enabled }
            .filter { url.contains(it.target) }
            .sortedByDescending { it.target.length } // Longer patterns are more specific
        
        // Apply the most specific rule if any
        if (applicableRules.isNotEmpty()) {
            shouldAllow = applicableRules[0].action == "ALLOW"
        }
        
        return shouldAllow
    }
    
    fun saveRules() {
        try {
            val json = adapter.toJson(rules)
            rulesFile.writeText(json)
        } catch (e: Exception) {
            Log.e("RuleManager", "Error saving rules: ${e.message}")
        }
    }
    
    private fun loadRules() {
        try {
            if (rulesFile.exists()) {
                val json = rulesFile.readText()
                val loadedRules = adapter.fromJson(json) ?: listOf()
                rules.clear()
                rules.addAll(loadedRules)
            } else {
                initializeDefaultRules()
            }
        } catch (e: Exception) {
            Log.e("RuleManager", "Error loading rules: ${e.message}")
            // Initialize with default rules if loading fails
            initializeDefaultRules()
        }
    }
    
    private fun initializeDefaultRules() {
        rules.clear()
        rules.add(
            NetworkRule(
                id = "default-block-ads",
                action = "BLOCK",
                target = "ads.example.com",
                description = "Block example ad server",
                enabled = true
            )
        )
        rules.add(
            NetworkRule(
                id = "default-block-tracking",
                action = "BLOCK", 
                target = "analytics.google.com",
                description = "Block Google Analytics",
                enabled = false
            )
        )
        saveRules()
    }
} 