package com.wobbz.framework.service

import android.content.Context
import android.util.Log
import kotlin.reflect.KClass

/**
 * Utility for bootstrapping the service system.
 */
object ServiceBootstrap {
    
    private const val TAG = "ServiceBootstrap"
    
    /**
     * Initializes the service system for a module.
     * @param context The module's context
     * @param moduleClass The module's main class
     */
    fun initialize(context: Context, moduleClass: KClass<*>) {
        initialize(context, moduleClass.java)
    }
    
    /**
     * Initializes the service system for a module using Java class.
     * @param context The module's context
     * @param moduleClass The module's main class
     */
    fun initialize(context: Context, moduleClass: Class<*>) {
        // Register module features
        val (moduleId, features) = FeatureFinder.loadFeatures(context)
        FeatureManager.registerModuleFeatures(moduleId, features)
        
        // Register extension points
        val extensions = FeatureFinder.loadExtensionPoints(context)
        for ((extensionPoint, className) in extensions) {
            try {
                val extensionClass = moduleClass.classLoader?.loadClass(className)
                if (extensionClass != null) {
                    FeatureManager.registerExtension(extensionPoint, extensionClass)
                }
            } catch (e: ClassNotFoundException) {
                Log.e(TAG, "Failed to load extension class: $className", e)
            }
        }
        
        // If module implements ServiceProvider, call registerServices
        try {
            val moduleInstance = moduleClass.kotlin.objectInstance
                ?: moduleClass.getDeclaredConstructor().newInstance()
            
            if (moduleInstance is ServiceProvider) {
                moduleInstance.registerServices(context)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not instantiate module for service registration: ${moduleClass.name}", e)
        }
    }
} 