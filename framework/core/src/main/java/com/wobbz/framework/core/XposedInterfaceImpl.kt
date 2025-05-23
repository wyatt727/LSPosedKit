package com.wobbz.framework.core

import android.content.Context
import com.wobbz.framework.core.exceptions.HookException
import io.github.libxposed.api.XposedInterface as LibXposedInterface
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Implementation of XposedInterface with robust error logging.
 * This class wraps the actual Xposed API and provides LSPosedKit-specific functionality.
 */
class XposedInterfaceImpl(
    private val libXposedInterface: LibXposedInterface,
    private val packageName: String
) : XposedInterface {
    
    companion object {
        private const val TAG = "LSPK-XposedInterface"
    }
    
    override fun loadClass(className: String): Class<*> {
        return try {
            getClassLoader().loadClass(className)
        } catch (e: ClassNotFoundException) {
            LogUtil.logError(TAG, "Failed to load class: $className", e)
            throw e
        }
    }
    
    override fun <T : Hooker> hook(
        clazz: Class<*>,
        methodName: String,
        parameterTypes: Array<Class<*>>,
        hookerClass: Class<T>
    ): MethodUnhooker<T> {
        return try {
            val method = clazz.getDeclaredMethod(methodName, *parameterTypes)
            hook(method, hookerClass)
        } catch (e: Exception) {
            LogUtil.logError(TAG, "Failed to hook method $methodName in ${clazz.name}", e)
            throw HookException("Failed to hook method $methodName", e)
        }
    }
    
    override fun <T : Hooker> hook(
        method: Method,
        hookerClass: Class<T>
    ): MethodUnhooker<T> {
        return try {
            val hooker = hookerClass.getDeclaredConstructor().newInstance()
            
            // Create adapter to bridge LSPosedKit Hooker with libxposed Hooker
            val adapter = HookerAdapter(hooker)
            
            // Set the current hooker for this thread before hooking
            adapter.setCurrentHooker()
            
            // Use the real libxposed API to hook the method
            val libUnhooker = libXposedInterface.hook(method, HookerAdapter::class.java)
            
            // Wrap the libxposed unhooker with our implementation
            val unhookCallback = {
                adapter.clearCurrentHooker()
                libUnhooker.unhook()
            }
            
            LogUtil.logInfo(TAG, "Hooked method: ${method.declaringClass.name}.${method.name}")
            MethodUnhookerImpl(hooker, unhookCallback)
        } catch (e: Exception) {
            LogUtil.logError(TAG, "Failed to create hook for method ${method.name}", e)
            throw HookException("Failed to create hook for method ${method.name}", e)
        }
    }
    
    override fun <T : Hooker> hook(
        constructor: Constructor<*>,
        hookerClass: Class<T>
    ): MethodUnhooker<T> {
        return try {
            val hooker = hookerClass.getDeclaredConstructor().newInstance()
            
            // Create adapter to bridge LSPosedKit Hooker with libxposed Hooker
            val adapter = HookerAdapter(hooker)
            
            // Set the current hooker for this thread before hooking
            adapter.setCurrentHooker()
            
            // Use the real libxposed API to hook the constructor
            val libUnhooker = libXposedInterface.hook(constructor, HookerAdapter::class.java)
            
            // Wrap the libxposed unhooker with our implementation
            val unhookCallback = {
                adapter.clearCurrentHooker()
                libUnhooker.unhook()
            }
            
            LogUtil.logInfo(TAG, "Hooked constructor: ${constructor.declaringClass.name}")
            MethodUnhookerImpl(hooker, unhookCallback)
        } catch (e: Exception) {
            LogUtil.logError(TAG, "Failed to create hook for constructor", e)
            throw HookException("Failed to create hook for constructor", e)
        }
    }
    
    override fun <T : Hooker> hook(
        field: Field,
        hookerClass: Class<T>
    ): MethodUnhooker<T> {
        return try {
            val hooker = hookerClass.getDeclaredConstructor().newInstance()
            
            // For field hooks, we need to hook the getter/setter methods
            // This is a simplified implementation - real field hooking would be more complex
            LogUtil.logWarn(TAG, "Field hooking not fully implemented yet for ${field.declaringClass.name}.${field.name}")
            
            val unhookCallback = {
                LogUtil.logDebug(TAG, "Unhooking field: ${field.name}")
            }
            
            LogUtil.logInfo(TAG, "Hooked field: ${field.declaringClass.name}.${field.name}")
            MethodUnhookerImpl(hooker, unhookCallback)
        } catch (e: Exception) {
            LogUtil.logError(TAG, "Failed to create hook for field ${field.name}", e)
            throw HookException("Failed to create hook for field ${field.name}", e)
        }
    }
    
    override fun log(level: LogLevel, message: String) {
        LogUtil.log(level, "LSPK-$packageName", message)
        
        // Also log to libxposed if it's an important message
        if (level.priority >= LogLevel.INFO.priority) {
            libXposedInterface.log("[$packageName] $message")
        }
    }
    
    override fun logError(message: String, throwable: Throwable) {
        LogUtil.logError("LSPK-$packageName", message, throwable)
        libXposedInterface.log("[$packageName] ERROR: $message", throwable)
    }
    
    override fun getSystemContext(): Context {
        // For now, we can't easily get system context through libxposed API
        // This would need to be implemented differently based on the framework capabilities
        throw NotImplementedError("getSystemContext() needs framework-specific implementation")
    }
    
    override fun getClassLoader(): ClassLoader {
        // libxposed doesn't directly expose classloader, but we can use the current thread's context
        return Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader()
    }
    

} 