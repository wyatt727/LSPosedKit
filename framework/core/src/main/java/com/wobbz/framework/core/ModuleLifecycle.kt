package com.wobbz.framework.core

/**
 * Interface for modules that need lifecycle management.
 * Provides hooks for startup and shutdown operations.
 */
interface ModuleLifecycle {
    
    /**
     * Called when all module dependencies are satisfied and the module is fully operational.
     * This is a good place for tasks that require other modules' services.
     */
    fun onStart()
    
    /**
     * Called when the module is being unloaded or the application is shutting down.
     * This is where modules should unregister services and release resources.
     */
    fun onStop()
} 