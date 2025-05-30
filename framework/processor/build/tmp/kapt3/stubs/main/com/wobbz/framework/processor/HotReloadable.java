package com.wobbz.framework.processor;

/**
 * Annotation to mark a module as supporting hot-reload functionality.
 * Modules with this annotation must implement IHotReloadable interface.
 */
@kotlin.annotation.Target(allowedTargets = {kotlin.annotation.AnnotationTarget.CLASS})
@kotlin.annotation.Retention(value = kotlin.annotation.AnnotationRetention.SOURCE)
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.SOURCE)
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\n\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\b\u0087\u0002\u0018\u00002\u00020\u0001B\u0000\u00a8\u0006\u0002"}, d2 = {"Lcom/wobbz/framework/processor/HotReloadable;", "", "processor"})
public abstract @interface HotReloadable {
}