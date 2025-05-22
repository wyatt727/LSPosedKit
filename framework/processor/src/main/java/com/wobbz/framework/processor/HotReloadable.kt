package com.wobbz.framework.processor

/**
 * Annotation to mark a module as supporting hot-reload functionality.
 * Modules with this annotation must implement IHotReloadable interface.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class HotReloadable 