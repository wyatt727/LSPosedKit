package com.wobbz.framework.core

import android.util.Log

/**
 * Unified logging utility class providing clear and consistent log messages 
 * across the framework and modules.
 */
object LogUtil {
    
    private const val DEFAULT_TAG = "LSPK-Framework"
    
    /**
     * Logs a message with the specified level and tag.
     * 
     * @param level The log level
     * @param tag The log tag (optional, defaults to LSPK-Framework)
     * @param message The message to log
     * @param throwable Optional throwable to include in the log
     */
    @JvmStatic
    @JvmOverloads
    fun log(level: LogLevel, tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        when (level) {
            LogLevel.VERBOSE -> Log.v(tag, message, throwable)
            LogLevel.DEBUG -> Log.d(tag, message, throwable)
            LogLevel.INFO -> Log.i(tag, message, throwable)
            LogLevel.WARN -> Log.w(tag, message, throwable)
            LogLevel.ERROR -> Log.e(tag, message, throwable)
        }
    }
    
    /**
     * Logs an error message with throwable.
     * 
     * @param tag The log tag
     * @param message The error message
     * @param throwable The throwable that caused the error
     */
    @JvmStatic
    fun logError(tag: String, message: String, throwable: Throwable) {
        log(LogLevel.ERROR, tag, message, throwable)
    }
    
    /**
     * Logs a debug message.
     * 
     * @param tag The log tag
     * @param message The debug message
     */
    @JvmStatic
    fun logDebug(tag: String, message: String) {
        log(LogLevel.DEBUG, tag, message)
    }
    
    /**
     * Logs an info message.
     * 
     * @param tag The log tag
     * @param message The info message
     */
    @JvmStatic
    fun logInfo(tag: String, message: String) {
        log(LogLevel.INFO, tag, message)
    }
    
    /**
     * Logs a warning message.
     * 
     * @param tag The log tag
     * @param message The warning message
     */
    @JvmStatic
    fun logWarn(tag: String, message: String) {
        log(LogLevel.WARN, tag, message)
    }
} 