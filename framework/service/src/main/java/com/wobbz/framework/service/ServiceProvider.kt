package com.wobbz.framework.service

import android.content.Context

/**
 * Interface for modules that provide services.
 * Optional interface that modules can implement to register
 * services during module initialization.
 */
interface ServiceProvider {
    /**
     * Called to register services with the FeatureManager.
     * @param context The module's context
     */
    fun registerServices(context: Context)
} 