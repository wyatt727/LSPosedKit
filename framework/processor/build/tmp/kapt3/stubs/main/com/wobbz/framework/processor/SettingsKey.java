package com.wobbz.framework.processor;

/**
 * Annotation for binding fields to settings keys.
 * Fields annotated with this will be automatically populated from module settings.
 */
@kotlin.annotation.Target(allowedTargets = {kotlin.annotation.AnnotationTarget.FIELD})
@kotlin.annotation.Retention(value = kotlin.annotation.AnnotationRetention.RUNTIME)
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.FIELD})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0087\u0002\u0018\u00002\u00020\u0001B\b\u0012\u0006\u0010\u0002\u001a\u00020\u0003R\u000f\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0006\u001a\u0004\b\u0002\u0010\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/wobbz/framework/processor/SettingsKey;", "", "value", "", "()Ljava/lang/String;", "processor"})
public abstract @interface SettingsKey {
    
    /**
     * The settings key to bind to this field.
     * Must match a key in the module's settings.json schema.
     */
    public abstract java.lang.String value();
}