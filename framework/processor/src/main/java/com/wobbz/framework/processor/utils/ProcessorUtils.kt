package com.wobbz.framework.processor.utils

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Utility functions for annotation processing.
 */
object ProcessorUtils {
    
    /**
     * Logs an info message with the processor.
     */
    fun logInfo(processingEnv: ProcessingEnvironment, message: String, element: Element? = null) {
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, message, element)
    }
    
    /**
     * Logs a warning message with the processor.
     */
    fun logWarning(processingEnv: ProcessingEnvironment, message: String, element: Element? = null) {
        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, message, element)
    }
    
    /**
     * Logs an error message with the processor.
     */
    fun logError(processingEnv: ProcessingEnvironment, message: String, element: Element? = null) {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, message, element)
    }
    
    /**
     * Gets the fully qualified class name for an element.
     */
    fun getFullyQualifiedName(element: TypeElement): String {
        return element.qualifiedName.toString()
    }
    
    /**
     * Gets the package name for an element.
     */
    fun getPackageName(processingEnv: ProcessingEnvironment, element: TypeElement): String {
        return processingEnv.elementUtils.getPackageOf(element).qualifiedName.toString()
    }
    
    /**
     * Gets the simple class name for an element.
     */
    fun getSimpleClassName(element: TypeElement): String {
        return element.simpleName.toString()
    }
    
    /**
     * Validates that a string matches a given regex pattern.
     */
    fun validatePattern(value: String, pattern: Regex, fieldName: String): String? {
        return if (value.matches(pattern)) {
            null
        } else {
            "$fieldName does not match required pattern: $pattern"
        }
    }
    
    /**
     * Validates that a string is not blank.
     */
    fun validateNotBlank(value: String, fieldName: String): String? {
        return if (value.isNotBlank()) {
            null
        } else {
            "$fieldName cannot be blank"
        }
    }
    
    /**
     * Validates that a list is not empty.
     */
    fun <T> validateNotEmpty(list: List<T>, fieldName: String): String? {
        return if (list.isNotEmpty()) {
            null
        } else {
            "$fieldName cannot be empty"
        }
    }
    
    /**
     * Validates semantic version format.
     */
    fun validateSemVer(version: String): String? {
        val semVerPattern = Regex("\\d+\\.\\d+\\.\\d+(-[a-zA-Z0-9.-]+)?")
        return validatePattern(version, semVerPattern, "Version")
    }
    
    /**
     * Validates module ID format.
     */
    fun validateModuleId(id: String): String? {
        val moduleIdPattern = Regex("[a-z0-9-_]+")
        return validatePattern(id, moduleIdPattern, "Module ID")
    }
} 