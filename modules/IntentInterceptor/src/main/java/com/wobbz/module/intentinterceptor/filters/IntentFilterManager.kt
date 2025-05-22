package com.wobbz.module.intentinterceptor.filters

import android.content.Context
import android.content.Intent
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types
import com.wobbz.framework.settings.SettingsProvider
import java.io.File

/**
 * Manages Intent filtering and modification rules.
 */
class IntentFilterManager(
    private val context: Context,
    private val settings: SettingsProvider
) {
    
    private val rules = mutableListOf<IntentFilterRule>()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val type = Types.newParameterizedType(List::class.java, IntentFilterRule::class.java)
    private val adapter = moshi.adapter<List<IntentFilterRule>>(type)
    private val rulesFile: File
    
    init {
        rulesFile = File(context.filesDir, "intent_filter_rules.json")
        loadDefaultRules()
    }
    
    /**
     * Loads filter rules from storage
     */
    fun loadFilters() {
        try {
            if (rulesFile.exists()) {
                val json = rulesFile.readText()
                val loadedRules = adapter.fromJson(json) ?: listOf()
                rules.clear()
                rules.addAll(loadedRules)
                Log.i("IntentFilterManager", "Loaded ${rules.size} intent filter rules")
            } else {
                loadDefaultRules()
            }
        } catch (e: Exception) {
            Log.e("IntentFilterManager", "Error loading rules: ${e.message}")
            loadDefaultRules()
        }
    }
    
    /**
     * Saves current rules to storage
     */
    fun saveFilters() {
        try {
            val json = adapter.toJson(rules)
            rulesFile.writeText(json)
            Log.i("IntentFilterManager", "Saved ${rules.size} intent filter rules")
        } catch (e: Exception) {
            Log.e("IntentFilterManager", "Error saving rules: ${e.message}")
        }
    }
    
    /**
     * Adds a new filter rule
     */
    fun addRule(rule: IntentFilterRule) {
        rules.add(rule)
        saveFilters()
    }
    
    /**
     * Removes a filter rule by ID
     */
    fun removeRule(ruleId: String) {
        rules.removeIf { it.id == ruleId }
        saveFilters()
    }
    
    /**
     * Updates an existing rule
     */
    fun updateRule(rule: IntentFilterRule) {
        val index = rules.indexOfFirst { it.id == rule.id }
        if (index >= 0) {
            rules[index] = rule
            saveFilters()
        }
    }
    
    /**
     * Gets all current rules
     */
    fun getRules(): List<IntentFilterRule> {
        return rules.toList()
    }
    
    /**
     * Checks if an Intent should be blocked
     */
    fun shouldBlock(intent: Intent): Boolean {
        if (!settings.bool("enable_intent_blocking", false)) {
            return false
        }
        
        return rules
            .filter { it.enabled && it.action == IntentFilterAction.BLOCK }
            .any { rule -> matchesRule(intent, rule) }
    }
    
    /**
     * Processes an Intent, potentially modifying it based on rules
     */
    fun processIntent(intent: Intent): Intent {
        if (!settings.bool("enable_intent_modification", false)) {
            return intent
        }
        
        var processedIntent = intent
        
        rules
            .filter { it.enabled && it.action == IntentFilterAction.MODIFY }
            .forEach { rule ->
                if (matchesRule(processedIntent, rule)) {
                    processedIntent = applyModification(processedIntent, rule)
                }
            }
        
        return processedIntent
    }
    
    /**
     * Checks if an Intent matches a filter rule
     */
    private fun matchesRule(intent: Intent, rule: IntentFilterRule): Boolean {
        // Check action pattern
        if (rule.actionPattern.isNotEmpty()) {
            val action = intent.action ?: ""
            if (!matchesPattern(action, rule.actionPattern)) {
                return false
            }
        }
        
        // Check component pattern
        if (rule.componentPattern.isNotEmpty()) {
            val component = intent.component?.className ?: ""
            if (!matchesPattern(component, rule.componentPattern)) {
                return false
            }
        }
        
        // Check package pattern
        if (rule.packagePattern.isNotEmpty()) {
            val packageName = intent.component?.packageName ?: ""
            if (!matchesPattern(packageName, rule.packagePattern)) {
                return false
            }
        }
        
        // Check data pattern
        if (rule.dataPattern.isNotEmpty()) {
            val data = intent.dataString ?: ""
            if (!matchesPattern(data, rule.dataPattern)) {
                return false
            }
        }
        
        // Check category patterns
        if (rule.categoryPattern.isNotEmpty()) {
            val categories = intent.categories ?: emptySet()
            val hasMatchingCategory = categories.any { category ->
                matchesPattern(category, rule.categoryPattern)
            }
            if (!hasMatchingCategory) {
                return false
            }
        }
        
        return true
    }
    
    /**
     * Applies a modification rule to an Intent
     */
    private fun applyModification(intent: Intent, rule: IntentFilterRule): Intent {
        // Create a copy of the intent for modification
        val modifiedIntent = Intent(intent)
        
        // Apply modifications based on rule
        rule.modifications.forEach { modification ->
            when (modification.type) {
                ModificationType.SET_ACTION -> {
                    modifiedIntent.action = modification.value
                }
                ModificationType.ADD_EXTRA -> {
                    modifiedIntent.putExtra(modification.key ?: "modified", modification.value)
                }
                ModificationType.REMOVE_EXTRA -> {
                    modification.key?.let { key ->
                        modifiedIntent.removeExtra(key)
                    }
                }
                ModificationType.SET_DATA -> {
                    try {
                        modifiedIntent.data = android.net.Uri.parse(modification.value)
                    } catch (e: Exception) {
                        Log.w("IntentFilterManager", "Invalid URI for SET_DATA: ${modification.value}")
                    }
                }
                ModificationType.ADD_CATEGORY -> {
                    modifiedIntent.addCategory(modification.value)
                }
                ModificationType.REMOVE_CATEGORY -> {
                    modifiedIntent.removeCategory(modification.value)
                }
            }
        }
        
        return modifiedIntent
    }
    
    /**
     * Checks if a string matches a pattern (supports basic wildcards)
     */
    private fun matchesPattern(text: String, pattern: String): Boolean {
        return when {
            pattern.isEmpty() -> true
            pattern == "*" -> true
            pattern.startsWith("*") && pattern.endsWith("*") -> {
                val middle = pattern.substring(1, pattern.length - 1)
                text.contains(middle)
            }
            pattern.startsWith("*") -> {
                val suffix = pattern.substring(1)
                text.endsWith(suffix)
            }
            pattern.endsWith("*") -> {
                val prefix = pattern.substring(0, pattern.length - 1)
                text.startsWith(prefix)
            }
            else -> text == pattern
        }
    }
    
    /**
     * Loads default filter rules
     */
    private fun loadDefaultRules() {
        rules.clear()
        
        // Add some default security-focused rules
        rules.add(
            IntentFilterRule(
                id = "block-install-packages",
                name = "Block Package Installation",
                description = "Blocks Intent.ACTION_INSTALL_PACKAGE actions",
                enabled = false,
                action = IntentFilterAction.BLOCK,
                actionPattern = "android.intent.action.INSTALL_PACKAGE",
                priority = 100
            )
        )
        
        rules.add(
            IntentFilterRule(
                id = "monitor-sensitive-actions",
                name = "Monitor Sensitive Actions",
                description = "Logs sensitive system actions",
                enabled = true,
                action = IntentFilterAction.LOG,
                actionPattern = "android.intent.action.*",
                priority = 50
            )
        )
        
        saveFilters()
    }
}

/**
 * Represents an Intent filter rule
 */
data class IntentFilterRule(
    val id: String,
    val name: String,
    val description: String,
    val enabled: Boolean = true,
    val action: IntentFilterAction,
    val priority: Int = 0,
    
    // Matching patterns
    val actionPattern: String = "",
    val componentPattern: String = "",
    val packagePattern: String = "",
    val dataPattern: String = "",
    val categoryPattern: String = "",
    
    // Modifications to apply (for MODIFY action)
    val modifications: List<IntentModification> = emptyList()
)

/**
 * Types of actions that can be taken on matching Intents
 */
enum class IntentFilterAction {
    BLOCK,      // Block the Intent completely
    MODIFY,     // Modify the Intent before allowing it
    LOG,        // Just log the Intent (no interference)
    REDIRECT    // Redirect to a different component
}

/**
 * Represents a modification to apply to an Intent
 */
data class IntentModification(
    val type: ModificationType,
    val key: String? = null,    // For extra keys
    val value: String = ""      // New value
)

/**
 * Types of modifications that can be applied to Intents
 */
enum class ModificationType {
    SET_ACTION,
    ADD_EXTRA,
    REMOVE_EXTRA,
    SET_DATA,
    ADD_CATEGORY,
    REMOVE_CATEGORY
} 