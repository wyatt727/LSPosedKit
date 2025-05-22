# Core Framework Implementation Guide

This document provides detailed implementation guidelines for the core components of the LSPosedKit framework. These components form the foundation upon which all modules are built.

## Directory Structure

Create the following files in the `framework/core/src/main/java/com/wobbz/framework/core` directory:

```
framework/core/src/main/java/com/wobbz/framework/core/
├── IModulePlugin.kt
├── PackageLoadedParam.kt
├── XposedInterface.kt
├── XposedInterfaceImpl.kt
├── Hooker.kt
├── HookParam.kt
├── HookParamImpl.kt
├── MethodUnhooker.kt
├── LogLevel.kt
├── LogUtil.kt
└── exceptions/
    ├── HookFailedException.kt
    └── ModuleInitializationException.kt
```

## Interface and Class Implementations

### IModulePlugin.kt

```kotlin
package com.wobbz.framework.core

import android.content.Context

/**
 * Primary interface that all modules must implement.
 * This interface defines the entry point for LSPosed modules.
 * For more fine-grained lifecycle control, modules can also implement [ModuleLifecycle].
 */
interface IModulePlugin {
    /**
     * Called when a module is first initialized.
     * @param context Android context from the host application
     * @param xposed Interface for Xposed functionality
     */
    fun initialize(context: Context, xposed: XposedInterface) {
        // Optional implementation
    }
    
    /**
     * Called when a package within your module's scope is loaded.
     * @param param Contains package information and Xposed hooks
     */
    fun onPackageLoaded(param: PackageLoadedParam)
}
```

### PackageLoadedParam.kt

```kotlin
package com.wobbz.framework.core

import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Contains information about a loaded package and provides access to Xposed functionality.
 */
class PackageLoadedParam(
    /**
     * Original LoadPackageParam from LSPosed
     */
    val lpparam: XC_LoadPackage.LoadPackageParam
) {
    /**
     * Name of the loaded package
     */
    val packageName: String = lpparam.packageName
    
    /**
     * ClassLoader for the loaded package
     */
    val classLoader: ClassLoader = lpparam.classLoader
    
    /**
     * Access to Xposed API for this package
     */
    val xposed: XposedInterface = XposedInterfaceImpl(lpparam)
    
    /**
     * Whether this is the first load of the package
     */
    val isFirstLoad: Boolean = lpparam.isFirstApplication
}
```

### XposedInterface.kt

```kotlin
package com.wobbz.framework.core

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Provides access to Xposed functionality with an improved API.
 */
interface XposedInterface {
    /**
     * Loads a class from the target application's ClassLoader.
     * @param name Fully qualified class name
     * @return The loaded Class object
     * @throws ClassNotFoundException if the class cannot be found
     */
    fun loadClass(name: String): Class<*>
    
    /**
     * Hooks a method in the target application.
     * @param method The method to hook
     * @param hooker The hooker class that contains the hook logic
     * @return A MethodUnhooker that can be used to unhook the method
     */
    fun <T : Hooker> hook(method: Method, hooker: Class<T>): MethodUnhooker<T>
    
    /**
     * Hooks a method in the target application.
     * @param clazz The class containing the method
     * @param methodName The name of the method
     * @param parameterTypes The parameter types of the method
     * @param hooker The hooker class that contains the hook logic
     * @return A MethodUnhooker that can be used to unhook the method
     */
    fun <T : Hooker> hook(
        clazz: Class<*>, 
        methodName: String, 
        parameterTypes: Array<Class<*>>, 
        hooker: Class<T>
    ): MethodUnhooker<T>
    
    /**
     * Hooks the getter for a field.
     * @param field The field to hook
     * @param hooker The hooker class that contains the hook logic
     * @return A MethodUnhooker that can be used to unhook the getter
     */
    fun <T : Hooker> hook(getter: Field, hooker: Class<T>): MethodUnhooker<T>
    
    /**
     * Hooks the setter for a field.
     * @param field The field to hook
     * @param hooker The hooker class that contains the hook logic
     * @return A MethodUnhooker that can be used to unhook the setter
     */
    fun <T : Hooker> hookSetter(field: Field, hooker: Class<T>): MethodUnhooker<T>
    
    /**
     * Logs a message with the specified level.
     * @param level Log level
     * @param message The message to log
     */
    fun log(level: LogLevel, message: String)
    
    /**
     * Logs an error message with an exception.
     * @param message The message to log
     * @param throwable The exception to log
     */
    fun logError(message: String, throwable: Throwable)
    
    /**
     * Gets the system context.
     * @return The Android system context
     */
    fun getSystemContext(): Context
}
```

### XposedInterfaceImpl.kt

```kotlin
package com.wobbz.framework.core

import android.content.Context
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Field
import java.lang.reflect.Method
import com.wobbz.framework.core.exceptions.HookFailedException

/**
 * Implementation of XposedInterface that bridges to the underlying LSPosed API.
 */
class XposedInterfaceImpl(private val lpparam: XC_LoadPackage.LoadPackageParam) : XposedInterface {
    
    private val logUtil = LogUtil.create("LSPK-Framework") // Centralized LogUtil

    override fun loadClass(name: String): Class<*> {
        try {
            return XposedHelpers.findClass(name, lpparam.classLoader)
        } catch (e: XposedHelpers.ClassNotFoundError) {
            logUtil.e("Failed to load class: $name in package: ${lpparam.packageName}", e)
            throw ClassNotFoundException("LSPosedKit: Class $name not found in ${lpparam.packageName}", e)
        }
    }
    
    override fun <T : Hooker> hook(method: Method, hooker: Class<T>): MethodUnhooker<T> {
        try {
            val instance = hooker.getDeclaredConstructor().newInstance()
            val hookCallback = createMethodHook(instance, method.name)
            val unhook = XposedBridge.hookMethod(method, hookCallback)
            logUtil.d("Successfully hooked method: ${method.declaringClass.name}#${method.name}")
            return MethodUnhookerImpl(instance, unhook)
        } catch (e: Exception) {
            logUtil.e("Failed to hook method ${method.declaringClass.name}#${method.name}", e)
            throw HookFailedException("Failed to hook method ${method.declaringClass.name}#${method.name} in package ${lpparam.packageName}", e)
        }
    }
    
    override fun <T : Hooker> hook(
        clazz: Class<*>,
        methodName: String,
        parameterTypes: Array<Class<*>>,
        hooker: Class<T>
    ): MethodUnhooker<T> {
        try {
            val method = clazz.getDeclaredMethod(methodName, *parameterTypes)
            return hook(method, hooker) // Delegates to the above hook method for logging
        } catch (e: NoSuchMethodException) {
            logUtil.e("Method $methodName not found in ${clazz.name} with specified parameters.", e)
            throw HookFailedException("Method $methodName not found in ${clazz.name} in package ${lpparam.packageName}", e)
        } catch (e: Exception) {
            logUtil.e("Failed to hook method $methodName in ${clazz.name}", e)
            throw HookFailedException("Failed to hook method $methodName in ${clazz.name} in package ${lpparam.packageName}", e)
        }
    }
    
    override fun <T : Hooker> hook(getter: Field, hooker: Class<T>): MethodUnhooker<T> {
        try {
            val instance = hooker.getDeclaredConstructor().newInstance()
            val hookCallback = createMethodHook(instance, "get" + getter.name.capitalize())
            val unhook = XposedBridge.hookMethod(XposedHelpers.getObjectField(null, "") as Member, hookCallback) // This needs proper getter method resolution
            logUtil.d("Successfully hooked getter for field: ${getter.declaringClass.name}#${getter.name}")
            return MethodUnhookerImpl(instance, unhook)
        } catch (e: Exception) {
            logUtil.e("Failed to hook getter for field ${getter.declaringClass.name}#${getter.name}", e)
            // This part is problematic: XposedBridge.hookMethod needs a Member (Method/Constructor).
            // XposedHelpers.getObjectField is for getting field values, not the getter method itself for hooking.
            // A proper implementation would find or generate a getter method.
            // For now, placeholder for the concept or assumes a utility that resolves the getter Member.
            throw HookFailedException("Failed to hook getter for field ${getter.declaringClass.name}#${getter.name} in package ${lpparam.packageName}. Getter resolution needs implementation.", e)
        }
    }
    
    override fun <T : Hooker> hookSetter(field: Field, hooker: Class<T>): MethodUnhooker<T> {
        try {
            val instance = hooker.getDeclaredConstructor().newInstance()
            val hookCallback = createMethodHook(instance, "set" + field.name.capitalize())
            val unhook = XposedBridge.hookMethod(XposedHelpers.getObjectField(null, "") as Member, hookCallback) // This needs proper setter method resolution
            logUtil.d("Successfully hooked setter for field: ${getter.declaringClass.name}#${getter.name}")
            return MethodUnhookerImpl(instance, unhook)
        } catch (e: Exception) {
            logUtil.e("Failed to hook setter for field ${field.declaringClass.name}#${field.name}", e)
            // Similar to getter, direct hooking of field setters like this is not standard XposedBridge API.
            // One would typically find the actual setter method.
            throw HookFailedException("Failed to hook setter for field ${field.declaringClass.name}#${field.name} in package ${lpparam.packageName}. Setter resolution needs implementation.", e)
        }
    }
    
    override fun log(level: LogLevel, message: String) {
        logUtil.log(level, message, lpparam.packageName) // Pass packageName for context
    }
    
    override fun logError(message: String, throwable: Throwable) {
        logUtil.e(message, throwable, lpparam.packageName) // Pass packageName for context
    }
    
    override fun getSystemContext(): Context {
        // Simplified: XposedBridge.getSystemContext() is usually available in Zygote context.
        // If called from app context, param.lpparam.appInfo.dataDir or similar could get an app context.
        // AndroidAppHelper.currentApplication() is also an option if available.
        // The original reflection was problematic and might not always work or be necessary.
        try {
            val activityThreadClass = XposedHelpers.findClass("android.app.ActivityThread", null)
            val currentApplicationMethod = XposedHelpers.findMethodExact(activityThreadClass, "currentApplication")
            val application = currentApplicationMethod.invoke(null) as android.app.Application?
            if (application != null) return application.applicationContext
        } catch (e: Exception) {
            logUtil.w("Could not get application context via ActivityThread, falling back. Error: ${e.message}")
        }
        // Fallback or more direct method depending on LSPosed version/availability
        return XposedBridge.getInstanteneousContext() ?: lpparam.classLoaderContext
    }
    
    private fun createMethodHook(hooker: Hooker, methodName: String): XC_MethodHook {
        return object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val hookParam = HookParamImpl(param, this@XposedInterfaceImpl)
                    hooker.beforeHook(hookParam)
                } catch (e: Throwable) {
                    logUtil.e("Error in beforeHook for $methodName in package ${lpparam.packageName}", e)
                    // Optionally, re-throw to crash the hooked app if that's desired on critical errors,
                    // or handle it to prevent module errors from crashing the host app.
                    // param.throwable = e // Example of propagating the error to the hooked method
                }
            }
            
            override fun afterHookedMethod(param: MethodHookParam) {
                try {
                    val hookParam = HookParamImpl(param, this@XposedInterfaceImpl)
                    hooker.afterHook(hookParam)
                } catch (e: Throwable) {
                    logUtil.e("Error in afterHook for $methodName in package ${lpparam.packageName}", e)
                    // param.throwable = e // Example
                }
            }
        }
    }
}
```

### Hooker.kt

```kotlin
package com.wobbz.framework.core

/**
 * Base interface for hook implementations.
 */
interface Hooker {
    /**
     * Called before the hooked method is executed.
     * @param param Contains method information and allows modifying arguments
     * @throws Throwable Any exception can be thrown
     */
    fun beforeHook(param: HookParam) { }
    
    /**
     * Called after the hooked method is executed.
     * @param param Contains method information and allows modifying the result
     * @throws Throwable Any exception can be thrown
     */
    fun afterHook(param: HookParam) { }
}
```

### HookParam.kt

```kotlin
package com.wobbz.framework.core

/**
 * Contains information about a hooked method call.
 */
interface HookParam {
    /**
     * The object instance on which the method was called (null for static methods).
     */
    val thisObject: Any?
    
    /**
     * The arguments passed to the method.
     */
    val args: Array<Any?>
    
    /**
     * The method that was hooked.
     */
    val method: java.lang.reflect.Method
    
    /**
     * XposedInterface for logging and other operations.
     */
    val xposed: XposedInterface
    
    /**
     * Sets a method argument.
     * @param index Argument index
     * @param value New argument value
     */
    fun setArg(index: Int, value: Any?)
    
    /**
     * Gets the result of the method call.
     * Only valid in afterHook.
     * @return The method result
     */
    fun <T> getResult(): T?
    
    /**
     * Sets the result of the method call.
     * This will prevent the original method from being called in beforeHook.
     * @param result The new result
     */
    fun setResult(result: Any?)
    
    /**
     * Throws an exception from the method.
     * This will prevent the original method from being called in beforeHook.
     * @param throwable The exception to throw
     */
    fun setThrowable(throwable: Throwable)
    
    /**
     * Gets the exception thrown by the method.
     * Only valid in afterHook.
     * @return The thrown exception or null
     */
    fun getThrowable(): Throwable?
}
```

### HookParamImpl.kt

```kotlin
package com.wobbz.framework.core

import de.robv.android.xposed.XC_MethodHook

/**
 * Implementation of HookParam that bridges to the underlying LSPosed API.
 */
class HookParamImpl(
    private val param: XC_MethodHook.MethodHookParam,
    override val xposed: XposedInterface
) : HookParam {
    
    override val thisObject: Any?
        get() = param.thisObject
    
    override val args: Array<Any?>
        get() = param.args
    
    override val method: java.lang.reflect.Method
        get() = param.method
    
    override fun setArg(index: Int, value: Any?) {
        param.args[index] = value
    }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T> getResult(): T? {
        return param.result as? T
    }
    
    override fun setResult(result: Any?) {
        param.result = result
    }
    
    override fun setThrowable(throwable: Throwable) {
        param.throwable = throwable
    }
    
    override fun getThrowable(): Throwable? {
        return param.throwable
    }
}
```

### MethodUnhooker.kt

```kotlin
package com.wobbz.framework.core

/**
 * Used to unhook a method.
 */
interface MethodUnhooker<T : Hooker> {
    /**
     * The hooker instance.
     */
    val hooker: T
    
    /**
     * Unhooks the method.
     */
    fun unhook()
}
```

### MethodUnhookerImpl.kt

```kotlin
package com.wobbz.framework.core

import de.robv.android.xposed.XC_MethodHook

/**
 * Implementation of MethodUnhooker that bridges to the underlying LSPosed API.
 */
class MethodUnhookerImpl<T : Hooker>(
    override val hooker: T,
    private val unhook: XC_MethodHook.Unhook
) : MethodUnhooker<T> {
    
    override fun unhook() {
        unhook.unhook()
    }
}
```

### LogLevel.kt

```kotlin
package com.wobbz.framework.core

/**
 * Enumeration of log levels.
 */
enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR
}
```

### LogUtil.kt

```kotlin
package com.wobbz.framework.core

/**
 * Provides a unified logging interface.
 * Logs will be prefixed with "LSPK-[tag]".
 */
class LogUtil(private val tag: String) {
    
    private fun formatMessage(message: String, packageName: String?): String {
        return if (packageName != null) "[$packageName] $message" else message
    }

    /**
     * Logs a message at VERBOSE level.
     * @param message The message to log
     * @param packageName Optional package name to include in the log
     */
    fun v(message: String, packageName: String? = null) {
        log(LogLevel.VERBOSE, message, packageName)
    }
    
    /**
     * Logs a message at DEBUG level.
     * @param message The message to log
     * @param packageName Optional package name to include in the log
     */
    fun d(message: String, packageName: String? = null) {
        log(LogLevel.DEBUG, message, packageName)
    }
    
    /**
     * Logs a message at INFO level.
     * @param message The message to log
     * @param packageName Optional package name to include in the log
     */
    fun i(message: String, packageName: String? = null) {
        log(LogLevel.INFO, message, packageName)
    }
    
    /**
     * Logs a message at WARN level.
     * @param message The message to log
     * @param packageName Optional package name to include in the log
     */
    fun w(message: String, packageName: String? = null) {
        log(LogLevel.WARN, message, packageName)
    }
    
    /**
     * Logs a message at ERROR level.
     * @param message The message to log
     * @param packageName Optional package name to include in the log
     */
    fun e(message: String, packageName: String? = null) {
        log(LogLevel.ERROR, message, packageName)
    }
    
    /**
     * Logs an error message with an exception.
     * @param message The message to log
     * @param throwable The exception to log
     * @param packageName Optional package name to include in the log
     */
    fun e(message: String, throwable: Throwable, packageName: String? = null) {
        logErrorInternal(formatMessage(message, packageName), throwable)
    }

    /**
     * Internal log method to bridge to XposedBridge or android.util.Log.
     * It is recommended to use XposedBridge.log for consistency within the Xposed environment.
     */
    internal fun log(level: LogLevel, message: String, packageName: String? = null) {
        val finalMessage = "[LSPK-$tag] ${formatMessage(message, packageName)}"
        // XposedBridge.log is preferred as it integrates with LSPosed Manager's logging
        // and often handles logcat permissions/visibility better than direct android.util.Log from modules.
        when (level) {
            LogLevel.VERBOSE -> XposedBridge.log("[V] $finalMessage")
            LogLevel.DEBUG -> XposedBridge.log("[D] $finalMessage")
            LogLevel.INFO -> XposedBridge.log("[I] $finalMessage")
            LogLevel.WARN -> XposedBridge.log("[W] $finalMessage")
            LogLevel.ERROR -> XposedBridge.log("[E] $finalMessage")
        }
        // Fallback or alternative for testing outside Xposed environment:
        // android.util.Log.println(levelToAndroidLogPriority(level), "LSPK-$tag", formatMessage(message, packageName))
    }
    
    /**
     * Internal error log method.
     */
    private fun logErrorInternal(formattedMessage: String, throwable: Throwable) {
        val finalMessage = "[LSPK-$tag] [E] $formattedMessage"
        XposedBridge.log(finalMessage)
        XposedBridge.log(throwable) // XposedBridge can log throwables directly
        // Fallback or alternative for testing outside Xposed environment:
        // android.util.Log.e("LSPK-$tag", formattedMessage, throwable)
    }

    // private fun levelToAndroidLogPriority(level: LogLevel): Int {
    //     return when (level) {
    //         LogLevel.VERBOSE -> android.util.Log.VERBOSE
    //         LogLevel.DEBUG -> android.util.Log.DEBUG
    //         LogLevel.INFO -> android.util.Log.INFO
    //         LogLevel.WARN -> android.util.Log.WARN
    //         LogLevel.ERROR -> android.util.Log.ERROR
    //     }
    // }
    
    companion object {
        /**
         * Creates a LogUtil instance with a specific tag.
         * @param tag The log tag, will be prefixed with "LSPK-".
         * @return A new LogUtil instance.
         */
        fun create(tag: String): LogUtil {
            return LogUtil(tag)
        }
    }
}
```

### HookFailedException.kt

```kotlin
package com.wobbz.framework.core.exceptions

/**
 * Thrown when a hook operation fails.
 */
class HookFailedException(
    /**
     * The message explaining why the hook failed.
     */
    message: String,
    
    /**
     * The underlying cause of the failure.
     */
    cause: Throwable? = null
) : Exception(message, cause)
```

### ModuleInitializationException.kt

```kotlin
package com.wobbz.framework.core.exceptions

/**
 * Thrown when a module fails to initialize.
 */
class ModuleInitializationException(
    /**
     * The module ID.
     */
    val moduleId: String,
    
    /**
     * The message explaining why initialization failed.
     */
    message: String,
    
    /**
     * The underlying cause of the failure.
     */
    cause: Throwable? = null
) : Exception("Failed to initialize module $moduleId: $message", cause)
```

### ModuleLifecycle.kt

```kotlin
package com.wobbz.framework.core

/**
 * Provides extended lifecycle callbacks for modules, complementing [IModulePlugin].
 * This allows modules to manage resources and state more effectively during
 * their operational lifetime, including startup and shutdown phases.
 */
interface ModuleLifecycle {
    /**
     * Called when the module is effectively started and its services (if any)
     * are expected to be available or dependencies met. This is a good place
     * for initial setup that depends on the module being fully operational.
     */
    fun onStart()

    /**
     * Called when the module is being stopped or unloaded.
     * This is the designated place to release resources, unregister services,
     * and perform any necessary cleanup to prevent resource leaks.
     * Implementations should ensure this method is idempotent.
     */
    fun onStop()
}
```

### Releasable.kt

```kotlin
package com.wobbz.framework.core

/**
 * Interface for components (typically services) that hold resources
 * that need to be explicitly released when the component is no longer needed.
 * This promotes better resource management within the framework.
 *
 * It is similar in purpose to [java.io.Closeable] or [java.lang.AutoCloseable]
 * but is specifically intended for LSPosedKit components.
 */
interface Releasable {
    /**
     * Releases any resources held by this component.
     * This method should be idempotent, meaning calling it multiple
     * times should not cause errors.
     * After calling release, the component may no longer be usable.
     */
    fun release()
}
```

## Testing

Create unit tests for each component to ensure they function correctly:

```kotlin
// Example test for XposedInterfaceImpl
class XposedInterfaceImplTest {
    @Test
    fun hook_shouldCreateMethodUnhooker() {
        // Test implementation
    }
    
    @Test
    fun loadClass_shouldFindClass() {
        // Test implementation
    }
    
    // More tests...
}
```

## Integration

After implementing the core components, update the framework module's build.gradle to export these components:

```groovy
dependencies {
    api project(':libxposed-api:api')
    // Other dependencies
}
```

## Next Steps

After implementing the core framework components:

1. Implement the annotation processor for module metadata generation
2. Create the hot-reload system
3. Implement the settings management system
4. Build the FeatureManager service bus

These components will build upon the core framework interfaces and classes defined here. 