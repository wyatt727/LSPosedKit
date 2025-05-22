package com.wobbz.framework.hot

/**
 * Interface for modules that support hot-reload functionality.
 * Modules implementing this interface can have their code updated without restarting the target application.
 */
interface IHotReloadable {
    
    /**
     * Called before a hot-reload is performed.
     * Modules should clean up their hooks and temporary state here.
     */
    fun onHotReload()
} 