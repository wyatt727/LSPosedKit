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
    
    override fun loadClass(name: String): Class<*> {
        return XposedHelpers.findClass(name, lpparam.classLoader)
    }
    
    override fun <T : Hooker> hook(method: Method, hooker: Class<T>): MethodUnhooker<T> {
        try {
            val instance = hooker.getDeclaredConstructor().newInstance()
            val hookCallback = createMethodHook(instance)
            val unhook = XposedBridge.hookMethod(method, hookCallback)
            return MethodUnhookerImpl(instance, unhook)
        } catch (e: Exception) {
            throw HookFailedException("Failed to hook method ${method.name}", e)
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
            return hook(method, hooker)
        } catch (e: Exception) {
            throw HookFailedException("Failed to hook method $methodName in ${clazz.name}", e)
        }
    }
    
    override fun <T : Hooker> hook(getter: Field, hooker: Class<T>): MethodUnhooker<T> {
        try {
            val instance = hooker.getDeclaredConstructor().newInstance()
            val hookCallback = createMethodHook(instance)
            val unhook = XposedBridge.hookMethod(getFieldGetter(getter), hookCallback)
            return MethodUnhookerImpl(instance, unhook)
        } catch (e: Exception) {
            throw HookFailedException("Failed to hook getter for field ${getter.name}", e)
        }
    }
    
    override fun <T : Hooker> hookSetter(field: Field, hooker: Class<T>): MethodUnhooker<T> {
        try {
            val instance = hooker.getDeclaredConstructor().newInstance()
            val hookCallback = createMethodHook(instance)
            val unhook = XposedBridge.hookMethod(getFieldSetter(field), hookCallback)
            return MethodUnhookerImpl(instance, unhook)
        } catch (e: Exception) {
            throw HookFailedException("Failed to hook setter for field ${field.name}", e)
        }
    }
    
    override fun log(level: LogLevel, message: String) {
        when (level) {
            LogLevel.VERBOSE -> XposedBridge.log("[V] $message")
            LogLevel.DEBUG -> XposedBridge.log("[D] $message")
            LogLevel.INFO -> XposedBridge.log("[I] $message")
            LogLevel.WARN -> XposedBridge.log("[W] $message")
            LogLevel.ERROR -> XposedBridge.log("[E] $message")
        }
    }
    
    override fun logError(message: String, throwable: Throwable) {
        XposedBridge.log("[E] $message")
        XposedBridge.log(throwable)
    }
    
    override fun getSystemContext(): Context {
        return XposedHelpers.findAndHookMethod(
            "android.app.ActivityThread",
            null,
            "currentApplication",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    // Do nothing, this is just to get the context
                }
            }
        ).result as Context
    }
    
    private fun createMethodHook(hooker: Hooker): XC_MethodHook {
        return object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val hookParam = HookParamImpl(param, this@XposedInterfaceImpl)
                    hooker.beforeHook(hookParam)
                } catch (e: Throwable) {
                    logError("Error in beforeHook", e)
                }
            }
            
            override fun afterHookedMethod(param: MethodHookParam) {
                try {
                    val hookParam = HookParamImpl(param, this@XposedInterfaceImpl)
                    hooker.afterHook(hookParam)
                } catch (e: Throwable) {
                    logError("Error in afterHook", e)
                }
            }
        }
    }
    
    private fun getFieldGetter(field: Field): Method {
        // This is a simplified implementation - actual implementation
        // would need to handle different field types properly
        field.isAccessible = true
        return field.declaringClass.getDeclaredMethod("get${field.name.capitalize()}")
    }
    
    private fun getFieldSetter(field: Field): Method {
        // This is a simplified implementation - actual implementation
        // would need to handle different field types properly
        field.isAccessible = true
        return field.declaringClass.getDeclaredMethod("set${field.name.capitalize()}", field.type)
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
 */
class LogUtil(private val tag: String) {
    /**
     * Logs a message at VERBOSE level.
     * @param message The message to log
     */
    fun v(message: String) {
        log(LogLevel.VERBOSE, message)
    }
    
    /**
     * Logs a message at DEBUG level.
     * @param message The message to log
     */
    fun d(message: String) {
        log(LogLevel.DEBUG, message)
    }
    
    /**
     * Logs a message at INFO level.
     * @param message The message to log
     */
    fun i(message: String) {
        log(LogLevel.INFO, message)
    }
    
    /**
     * Logs a message at WARN level.
     * @param message The message to log
     */
    fun w(message: String) {
        log(LogLevel.WARN, message)
    }
    
    /**
     * Logs a message at ERROR level.
     * @param message The message to log
     */
    fun e(message: String) {
        log(LogLevel.ERROR, message)
    }
    
    /**
     * Logs an error message with an exception.
     * @param message The message to log
     * @param throwable The exception to log
     */
    fun e(message: String, throwable: Throwable) {
        logError(message, throwable)
    }
    
    private fun log(level: LogLevel, message: String) {
        android.util.Log.println(
            when (level) {
                LogLevel.VERBOSE -> android.util.Log.VERBOSE
                LogLevel.DEBUG -> android.util.Log.DEBUG
                LogLevel.INFO -> android.util.Log.INFO
                LogLevel.WARN -> android.util.Log.WARN
                LogLevel.ERROR -> android.util.Log.ERROR
            },
            tag,
            message
        )
    }
    
    private fun logError(message: String, throwable: Throwable) {
        android.util.Log.e(tag, message, throwable)
    }
    
    companion object {
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