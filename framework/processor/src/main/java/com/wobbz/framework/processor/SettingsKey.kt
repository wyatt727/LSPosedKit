package com.wobbz.framework.processor

/**
 * Annotation for binding fields to settings keys.
 * Fields annotated with this will be automatically populated from module settings.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class SettingsKey(
    /**
     * The settings key to bind to this field.
     * Must match a key in the module's settings.json schema.
     */
    val value: String
) 