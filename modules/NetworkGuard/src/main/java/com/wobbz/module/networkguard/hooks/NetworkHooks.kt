package com.wobbz.module.networkguard.hooks

import com.wobbz.framework.core.PackageLoadedParam
import com.wobbz.framework.core.LogLevel
import com.wobbz.framework.core.Hooker
import com.wobbz.framework.core.HookParam
import com.wobbz.module.networkguard.rules.RuleManager
import java.net.URL

class NetworkHooks(
    private val param: PackageLoadedParam,
    private val ruleManager: RuleManager
) {
    
    private val hooks = mutableListOf<Any>()
    
    fun applyHooks(): List<Any> {
        // Hook URL.openConnection
        hookUrlOpenConnection()
        
        // Hook HttpURLConnection methods
        hookHttpUrlConnection()
        
        // Try to hook OkHttp if present
        tryHookOkHttp()
        
        return hooks
    }
    
    private fun hookUrlOpenConnection() {
        try {
            val urlClass = param.xposed.loadClass("java.net.URL")
            val openConnectionMethod = urlClass.getDeclaredMethod("openConnection")
            
            val unhooker = param.xposed.hook(
                openConnectionMethod,
                UrlOpenConnectionHooker::class.java
            )
            
            hooks.add(unhooker)
            param.xposed.log(LogLevel.INFO, "Hooked URL.openConnection for network filtering")
        } catch (e: Exception) {
            param.xposed.logError("Failed to hook URL.openConnection", e)
        }
    }
    
    private fun hookHttpUrlConnection() {
        try {
            // Hook HttpURLConnection.connect() method
            val httpUrlConnectionClass = param.xposed.loadClass("java.net.HttpURLConnection")
            val connectMethod = httpUrlConnectionClass.getDeclaredMethod("connect")
            
            val unhooker = param.xposed.hook(
                connectMethod,
                HttpUrlConnectionHooker::class.java
            )
            
            hooks.add(unhooker)
            param.xposed.log(LogLevel.INFO, "Hooked HttpURLConnection.connect for network filtering")
        } catch (e: Exception) {
            param.xposed.logError("Failed to hook HttpURLConnection.connect", e)
        }
    }
    
    private fun tryHookOkHttp() {
        try {
            // Try to find OkHttp classes
            val okHttpClientClass = param.classLoader.loadClass("okhttp3.OkHttpClient")
            
            // If OkHttp is available, we could hook its methods here
            // For now, just log that we detected it
            param.xposed.log(LogLevel.INFO, "OkHttp detected in ${param.packageName} - hooks could be implemented")
            
        } catch (e: ClassNotFoundException) {
            // OkHttp not present in this app, that's fine
            param.xposed.log(LogLevel.DEBUG, "OkHttp not found in ${param.packageName}, skipping OkHttp hooks")
        } catch (e: Exception) {
            param.xposed.logError("Failed to detect/hook OkHttp", e)
        }
    }
    
    /**
     * Hooker for URL.openConnection() method
     */
    inner class UrlOpenConnectionHooker : Hooker {
        override fun beforeHook(hookParam: HookParam) {
            try {
                val url = hookParam.thisObject as? URL ?: return
                val urlString = url.toString()
                
                // Check if the URL is allowed by our rules
                if (!ruleManager.shouldAllowConnection(urlString)) {
                    // Block the connection by throwing an exception
                    hookParam.setThrowable(java.io.IOException("Connection blocked by NetworkGuard: $urlString"))
                    
                    // Log the blocked connection
                    param.xposed.log(LogLevel.INFO, "NetworkGuard blocked connection to: $urlString")
                } else {
                    // Log allowed connections (only for debugging)
                    param.xposed.log(LogLevel.DEBUG, "NetworkGuard allowed connection to: $urlString")
                }
            } catch (e: Exception) {
                param.xposed.logError("Error in UrlOpenConnectionHooker", e)
            }
        }
    }
    
    /**
     * Hooker for HttpURLConnection.connect() method
     */
    inner class HttpUrlConnectionHooker : Hooker {
        override fun beforeHook(hookParam: HookParam) {
            try {
                val connection = hookParam.thisObject ?: return
                
                // Use reflection to get the URL from the connection
                val urlField = connection.javaClass.getDeclaredField("url")
                urlField.isAccessible = true
                val url = urlField.get(connection) as? URL ?: return
                
                val urlString = url.toString()
                
                // Check if the URL is allowed by our rules
                if (!ruleManager.shouldAllowConnection(urlString)) {
                    // Block the connection by throwing an exception
                    hookParam.setThrowable(java.io.IOException("Connection blocked by NetworkGuard: $urlString"))
                    
                    // Log the blocked connection
                    param.xposed.log(LogLevel.INFO, "NetworkGuard blocked HttpURLConnection to: $urlString")
                } else {
                    // Log allowed connections (only for debugging)
                    param.xposed.log(LogLevel.DEBUG, "NetworkGuard allowed HttpURLConnection to: $urlString")
                }
            } catch (e: Exception) {
                param.xposed.logError("Error in HttpUrlConnectionHooker", e)
            }
        }
    }
} 