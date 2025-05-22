package com.wobbz.framework.core

/**
 * Enumeration of available log levels for LSPosedKit modules.
 */
enum class LogLevel(val priority: Int, val tag: String) {
    VERBOSE(2, "V"),
    DEBUG(3, "D"),
    INFO(4, "I"),
    WARN(5, "W"),
    ERROR(6, "E")
} 