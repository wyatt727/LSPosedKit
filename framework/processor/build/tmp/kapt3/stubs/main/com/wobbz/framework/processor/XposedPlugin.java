package com.wobbz.framework.processor;

/**
 * Annotation for marking LSPosedKit module classes.
 * This annotation provides metadata about the module and is processed at compile time
 * to generate module.prop, xposed_init, and module-info.json files.
 */
@kotlin.annotation.Target(allowedTargets = {kotlin.annotation.AnnotationTarget.CLASS})
@kotlin.annotation.Retention(value = kotlin.annotation.AnnotationRetention.SOURCE)
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.SOURCE)
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\b\u0087\u0002\u0018\u00002\u00020\u0001BL\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u000bR\u000f\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\u0006\u001a\u0004\b\u0007\u0010\rR\u000f\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\rR\u000f\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0006\u001a\u0004\b\u0002\u0010\rR\u000f\u0010\f\u001a\u00020\u000b\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\u000eR\u000f\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\u000eR\u000f\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0006\u001a\u0004\b\u0004\u0010\rR\u0015\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\t\u00a2\u0006\u0006\u001a\u0004\b\b\u0010\u000fR\u000f\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\r\u00a8\u0006\u0010"}, d2 = {"Lcom/wobbz/framework/processor/XposedPlugin;", "", "id", "", "name", "version", "description", "author", "scope", "", "minApi", "", "maxApi", "()Ljava/lang/String;", "()I", "()[Ljava/lang/String;", "processor"})
public abstract @interface XposedPlugin {
    
    /**
     * The unique identifier for this module.
     * Must be unique across all modules.
     */
    public abstract java.lang.String id();
    
    /**
     * The human-readable name of the module.
     */
    public abstract java.lang.String name();
    
    /**
     * The version of the module in semver format (e.g., "1.0.0").
     */
    public abstract java.lang.String version();
    
    /**
     * A brief description of what the module does.
     */
    public abstract java.lang.String description();
    
    /**
     * The author or organization that created the module.
     */
    public abstract java.lang.String author() default "";
    
    /**
     * Array of package names that this module should be applied to.
     * Use ["*"] to apply to all packages.
     */
    public abstract java.lang.String[] scope();
    
    /**
     * Minimum Android API level required for this module.
     * Defaults to the project's minSdk.
     */
    public abstract int minApi() default -1;
    
    /**
     * Maximum Android API level supported by this module.
     * Defaults to no maximum.
     */
    public abstract int maxApi() default -1;
}