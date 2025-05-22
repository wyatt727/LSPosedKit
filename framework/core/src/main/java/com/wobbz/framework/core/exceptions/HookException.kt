package com.wobbz.framework.core.exceptions

/**
 * Exception thrown when hook operations fail.
 */
class HookException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * Exception thrown when module initialization fails.
 */
class ModuleInitializationException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * Exception thrown when there are issues with module configuration.
 */
class ModuleConfigurationException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) 