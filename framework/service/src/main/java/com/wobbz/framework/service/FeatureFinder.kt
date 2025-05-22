package com.wobbz.framework.service

import android.content.Context
import org.json.JSONObject
import java.io.IOException

/**
 * Utility for discovering features from module-info.json files.
 */
internal object FeatureFinder {
    
    /**
     * Loads features from a module's module-info.json.
     * @param context The module's context
     * @return A pair of module ID and list of features
     */
    fun loadFeatures(context: Context): Pair<String, List<String>> {
        try {
            val inputStream = context.assets.open("module-info.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(jsonString)
            
            val moduleId = json.getString("id")
            val featureArray = json.optJSONArray("features") ?: return Pair(moduleId, emptyList())
            
            val features = mutableListOf<String>()
            for (i in 0 until featureArray.length()) {
                features.add(featureArray.getString(i))
            }
            
            return Pair(moduleId, features)
        } catch (e: IOException) {
            android.util.Log.e("FeatureFinder", "Failed to load features: ${e.message}", e)
            return Pair("unknown", emptyList())
        } catch (e: Exception) {
            android.util.Log.e("FeatureFinder", "Failed to parse module-info.json: ${e.message}", e)
            return Pair("unknown", emptyList())
        }
    }
    
    /**
     * Loads extension points from a module's module-info.json.
     * @param context The module's context
     * @return A map of extension point to implementation class name
     */
    fun loadExtensionPoints(context: Context): Map<String, String> {
        try {
            val inputStream = context.assets.open("module-info.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(jsonString)
            
            val extensionsObj = json.optJSONObject("extensions") ?: return emptyMap()
            
            val extensions = mutableMapOf<String, String>()
            for (key in extensionsObj.keys()) {
                extensions[key] = extensionsObj.getString(key)
            }
            
            return extensions
        } catch (e: IOException) {
            android.util.Log.e("FeatureFinder", "Failed to load extensions: ${e.message}", e)
            return emptyMap()
        } catch (e: Exception) {
            android.util.Log.e("FeatureFinder", "Failed to parse module-info.json: ${e.message}", e)
            return emptyMap()
        }
    }
} 