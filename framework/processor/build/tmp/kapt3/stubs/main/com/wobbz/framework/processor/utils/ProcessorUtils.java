package com.wobbz.framework.processor.utils;

/**
 * Utility functions for annotation processing.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u0016\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\n\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\"\u0010\u000b\u001a\u00020\f2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u00042\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u000eJ\"\u0010\u000f\u001a\u00020\f2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u00042\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u000eJ\"\u0010\u0010\u001a\u00020\f2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u00042\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u000eJ\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0012\u001a\u00020\u0004J\u0018\u0010\u0013\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0014\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u0004J$\u0010\u0016\u001a\u0004\u0018\u00010\u0004\"\u0004\b\u0000\u0010\u00172\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u0002H\u00170\u00192\u0006\u0010\u0015\u001a\u00020\u0004J \u0010\u001a\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0014\u001a\u00020\u00042\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0015\u001a\u00020\u0004J\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u001e\u001a\u00020\u0004\u00a8\u0006\u001f"}, d2 = {"Lcom/wobbz/framework/processor/utils/ProcessorUtils;", "", "()V", "getFullyQualifiedName", "", "element", "Ljavax/lang/model/element/TypeElement;", "getPackageName", "processingEnv", "Ljavax/annotation/processing/ProcessingEnvironment;", "getSimpleClassName", "logError", "", "message", "Ljavax/lang/model/element/Element;", "logInfo", "logWarning", "validateModuleId", "id", "validateNotBlank", "value", "fieldName", "validateNotEmpty", "T", "list", "", "validatePattern", "pattern", "Lkotlin/text/Regex;", "validateSemVer", "version", "processor"})
public final class ProcessorUtils {
    @org.jetbrains.annotations.NotNull
    public static final com.wobbz.framework.processor.utils.ProcessorUtils INSTANCE = null;
    
    private ProcessorUtils() {
        super();
    }
    
    /**
     * Logs an info message with the processor.
     */
    public final void logInfo(@org.jetbrains.annotations.NotNull
    javax.annotation.processing.ProcessingEnvironment processingEnv, @org.jetbrains.annotations.NotNull
    java.lang.String message, @org.jetbrains.annotations.Nullable
    javax.lang.model.element.Element element) {
    }
    
    /**
     * Logs a warning message with the processor.
     */
    public final void logWarning(@org.jetbrains.annotations.NotNull
    javax.annotation.processing.ProcessingEnvironment processingEnv, @org.jetbrains.annotations.NotNull
    java.lang.String message, @org.jetbrains.annotations.Nullable
    javax.lang.model.element.Element element) {
    }
    
    /**
     * Logs an error message with the processor.
     */
    public final void logError(@org.jetbrains.annotations.NotNull
    javax.annotation.processing.ProcessingEnvironment processingEnv, @org.jetbrains.annotations.NotNull
    java.lang.String message, @org.jetbrains.annotations.Nullable
    javax.lang.model.element.Element element) {
    }
    
    /**
     * Gets the fully qualified class name for an element.
     */
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getFullyQualifiedName(@org.jetbrains.annotations.NotNull
    javax.lang.model.element.TypeElement element) {
        return null;
    }
    
    /**
     * Gets the package name for an element.
     */
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getPackageName(@org.jetbrains.annotations.NotNull
    javax.annotation.processing.ProcessingEnvironment processingEnv, @org.jetbrains.annotations.NotNull
    javax.lang.model.element.TypeElement element) {
        return null;
    }
    
    /**
     * Gets the simple class name for an element.
     */
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSimpleClassName(@org.jetbrains.annotations.NotNull
    javax.lang.model.element.TypeElement element) {
        return null;
    }
    
    /**
     * Validates that a string matches a given regex pattern.
     */
    @org.jetbrains.annotations.Nullable
    public final java.lang.String validatePattern(@org.jetbrains.annotations.NotNull
    java.lang.String value, @org.jetbrains.annotations.NotNull
    kotlin.text.Regex pattern, @org.jetbrains.annotations.NotNull
    java.lang.String fieldName) {
        return null;
    }
    
    /**
     * Validates that a string is not blank.
     */
    @org.jetbrains.annotations.Nullable
    public final java.lang.String validateNotBlank(@org.jetbrains.annotations.NotNull
    java.lang.String value, @org.jetbrains.annotations.NotNull
    java.lang.String fieldName) {
        return null;
    }
    
    /**
     * Validates that a list is not empty.
     */
    @org.jetbrains.annotations.Nullable
    public final <T extends java.lang.Object>java.lang.String validateNotEmpty(@org.jetbrains.annotations.NotNull
    java.util.List<? extends T> list, @org.jetbrains.annotations.NotNull
    java.lang.String fieldName) {
        return null;
    }
    
    /**
     * Validates semantic version format.
     */
    @org.jetbrains.annotations.Nullable
    public final java.lang.String validateSemVer(@org.jetbrains.annotations.NotNull
    java.lang.String version) {
        return null;
    }
    
    /**
     * Validates module ID format.
     */
    @org.jetbrains.annotations.Nullable
    public final java.lang.String validateModuleId(@org.jetbrains.annotations.NotNull
    java.lang.String id) {
        return null;
    }
}