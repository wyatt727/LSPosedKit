package com.wobbz.framework.hot.exceptions

/**
 * Exception thrown when hot-reload operations fail.
 */
class HotReloadException : Exception {
    
    constructor(message: String) : super(message)
    
    constructor(message: String, cause: Throwable) : super(message, cause)
    
    constructor(cause: Throwable) : super(cause)
} 