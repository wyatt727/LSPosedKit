# API Reference

> Complete reference guide to LSPosedKit's core interfaces, classes, and APIs. Use this document as a quick lookup for available methods, parameters, and return types.

## Core Interfaces

### IModulePlugin

The primary interface that all modules must implement.

```kotlin
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

### IHotReloadable

Interface for modules that support hot-reload capability.

```kotlin
interface IHotReloadable {
    /**
     * Called when a hot-reload is triggered for this module.
     * Implementations should clean up any active hooks.
     */
    fun onHotReload()
}
```

### PackageLoadedParam

Contains information about a loaded package and provides access to Xposed functionality.

```kotlin
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

### XposedInterface

Provides access to Xposed functionality with an improved API.

```kotlin
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
}
```

### Hooker

Base interface for hook implementations.

```kotlin
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

### HookParam

Contains information about a hooked method call.

```kotlin
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

### MethodUnhooker

Used to unhook a method.

```kotlin
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

## Settings Management

### SettingsProvider

Provides type-safe access to module settings.

```kotlin
class SettingsProvider(context: Context) {
    companion object {
        /**
         * Gets a SettingsProvider instance for the given context.
         * @param context Android context
         * @return A SettingsProvider instance
         */
        fun of(context: Context): SettingsProvider
    }
    
    /**
     * Gets a boolean value from settings.
     * @param key The setting key
     * @param defaultValue Default value if setting doesn't exist
     * @return The boolean value
     */
    fun bool(key: String, defaultValue: Boolean = false): Boolean
    
    /**
     * Gets an integer value from settings.
     * @param key The setting key
     * @param defaultValue Default value if setting doesn't exist
     * @return The integer value
     */
    fun int(key: String, defaultValue: Int = 0): Int
    
    /**
     * Gets a long value from settings.
     * @param key The setting key
     * @param defaultValue Default value if setting doesn't exist
     * @return The long value
     */
    fun long(key: String, defaultValue: Long = 0L): Long
    
    /**
     * Gets a float value from settings.
     * @param key The setting key
     * @param defaultValue Default value if setting doesn't exist
     * @return The float value
     */
    fun float(key: String, defaultValue: Float = 0f): Float
    
    /**
     * Gets a string value from settings.
     * @param key The setting key
     * @param defaultValue Default value if setting doesn't exist
     * @return The string value
     */
    fun string(key: String, defaultValue: String = ""): String
    
    /**
     * Gets a string list from settings.
     * @param key The setting key
     * @param defaultValue Default value if setting doesn't exist
     * @return The string list
     */
    fun stringList(key: String, defaultValue: List<String> = emptyList()): List<String>
    
    /**
     * Gets a string set from settings.
     * @param key The setting key
     * @param defaultValue Default value if setting doesn't exist
     * @return The string set
     */
    fun stringSet(key: String, defaultValue: Set<String> = emptySet()): Set<String>
    
    /**
     * Binds a settings class using @SettingsKey annotations.
     * @param clazz The class to bind
     * @return An instance of the class with properties bound to settings
     */
    fun <T : Any> bind(clazz: Class<T>): T
    
    /**
     * Gets an editor for modifying settings.
     * @return A SettingsEditor
     */
    fun edit(): SettingsEditor
    
    /**
     * Adds a settings change listener.
     * @param listener Callback function that receives the changed key
     * @return A token that can be used to remove the listener
     */
    fun addOnSettingsChangedListener(listener: (String) -> Unit): Any
    
    /**
     * Removes a settings change listener.
     * @param token The token returned from addOnSettingsChangedListener
     */
    fun removeOnSettingsChangedListener(token: Any)
}
```

### SettingsEditor

Used to modify settings values.

```kotlin
interface SettingsEditor {
    /**
     * Sets a boolean value.
     * @param key The setting key
     * @param value The boolean value
     * @return This editor for chaining
     */
    fun putBoolean(key: String, value: Boolean): SettingsEditor
    
    /**
     * Sets an integer value.
     * @param key The setting key
     * @param value The integer value
     * @return This editor for chaining
     */
    fun putInt(key: String, value: Int): SettingsEditor
    
    /**
     * Sets a long value.
     * @param key The setting key
     * @param value The long value
     * @return This editor for chaining
     */
    fun putLong(key: String, value: Long): SettingsEditor
    
    /**
     * Sets a float value.
     * @param key The setting key
     * @param value The float value
     * @return This editor for chaining
     */
    fun putFloat(key: String, value: Float): SettingsEditor
    
    /**
     * Sets a string value.
     * @param key The setting key
     * @param value The string value
     * @return This editor for chaining
     */
    fun putString(key: String, value: String): SettingsEditor
    
    /**
     * Sets a string list.
     * @param key The setting key
     * @param value The string list
     * @return This editor for chaining
     */
    fun putStringList(key: String, value: List<String>): SettingsEditor
    
    /**
     * Sets a string set.
     * @param key The setting key
     * @param value The string set
     * @return This editor for chaining
     */
    fun putStringSet(key: String, value: Set<String>): SettingsEditor
    
    /**
     * Removes a setting.
     * @param key The setting key
     * @return This editor for chaining
     */
    fun remove(key: String): SettingsEditor
    
    /**
     * Applies the changes.
     */
    fun apply()
    
    /**
     * Applies the changes immediately and returns success.
     * @return true if changes were committed successfully
     */
    fun commit(): Boolean
}
```

## Service Discovery

### FeatureManager

Provides a service discovery mechanism for modules.

```kotlin
object FeatureManager {
    /**
     * Registers a service implementation.
     * @param serviceClass The service interface class
     * @param implementation The service implementation
     */
    fun <T : Any> register(serviceClass: KClass<T>, implementation: T)
    
    /**
     * Registers a service implementation.
     * @param serviceClass The service interface class
     * @param implementation The service implementation
     */
    fun <T : Any> register(serviceClass: Class<T>, implementation: T)
    
    /**
     * Gets a service implementation.
     * @param serviceClass The service interface class
     * @return The service implementation or null if not found
     */
    fun <T : Any> get(serviceClass: KClass<T>): T?
    
    /**
     * Gets a service implementation.
     * @param serviceClass The service interface class
     * @return The service implementation or null if not found
     */
    fun <T : Any> get(serviceClass: Class<T>): T?
    
    /**
     * Checks if a feature is available.
     * @param featureId The feature identifier
     * @return true if the feature is available
     */
    fun hasFeature(featureId: String): Boolean
    
    /**
     * Gets extensions for a specific extension point.
     * @param extensionPoint The extension point identifier
     * @return A list of extension classes
     */
    fun getExtensions(extensionPoint: String): List<Class<*>>
}
```

## Logging

### LogUtil

Provides a unified logging interface.

```kotlin
class LogUtil(private val tag: String) {
    /**
     * Logs a message at VERBOSE level.
     * @param message The message to log
     */
    fun v(message: String)
    
    /**
     * Logs a message at DEBUG level.
     * @param message The message to log
     */
    fun d(message: String)
    
    /**
     * Logs a message at INFO level.
     * @param message The message to log
     */
    fun i(message: String)
    
    /**
     * Logs a message at WARN level.
     * @param message The message to log
     */
    fun w(message: String)
    
    /**
     * Logs a message at ERROR level.
     * @param message The message to log
     */
    fun e(message: String)
    
    /**
     * Logs an error message with an exception.
     * @param message The message to log
     * @param throwable The exception to log
     */
    fun e(message: String, throwable: Throwable)
}
```

### LogLevel

Enumeration of log levels.

```kotlin
enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR
}
```

## Resource Helpers

### ResourceHelper

Helps with resource modifications.

```kotlin
class ResourceHelper(private val resparam: XC_InitPackageResources.InitPackageResourcesParam) {
    /**
     * Replaces a resource with another resource.
     * @param type The resource type (e.g., "drawable", "layout")
     * @param name The resource name
     * @param replacement The replacement resource ID
     */
    fun replaceResource(type: String, name: String, replacement: Int)
    
    /**
     * Replaces a layout resource with another layout.
     * @param layoutId The layout resource ID to replace
     * @param newLayoutResId The replacement layout resource ID
     */
    fun replaceLayout(layoutId: Int, newLayoutResId: Int)
    
    /**
     * Replaces a drawable resource with another drawable.
     * @param drawableId The drawable resource ID to replace
     * @param newDrawableResId The replacement drawable resource ID
     */
    fun replaceDrawable(drawableId: Int, newDrawableResId: Int)
    
    /**
     * Adds a resource hook.
     * @param type The resource type (e.g., "drawable", "layout")
     * @param name The resource name
     * @param callback The callback to execute when the resource is loaded
     */
    fun addResourceHook(type: String, name: String, callback: ResourceCallback)
}
```

### ResourceCallback

Callback for resource hooks.

```kotlin
interface ResourceCallback {
    /**
     * Called when a resource is loaded.
     * @param resparam The resource parameters
     * @param res The resource to modify
     * @return The modified resource
     */
    fun onResourceLoaded(resparam: XC_InitPackageResources.InitPackageResourcesParam, res: XResources.ResourceName): Any?
}
```

## Utility Classes

### ReflectionHelper

Provides helper methods for reflection operations.

```kotlin
object ReflectionHelper {
    /**
     * Finds a method in a class by name and parameter types.
     * @param clazz The class to search in
     * @param methodName The method name
     * @param parameterTypes The parameter types
     * @return The method
     * @throws NoSuchMethodException if the method is not found
     */
    fun findMethod(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Method
    
    /**
     * Finds a field in a class by name.
     * @param clazz The class to search in
     * @param fieldName The field name
     * @return The field
     * @throws NoSuchFieldException if the field is not found
     */
    fun findField(clazz: Class<*>, fieldName: String): Field
    
    /**
     * Gets the value of a field from an object.
     * @param obj The object (or null for static fields)
     * @param field The field
     * @return The field value
     */
    fun <T> getFieldValue(obj: Any?, field: Field): T
    
    /**
     * Sets the value of a field on an object.
     * @param obj The object (or null for static fields)
     * @param field The field
     * @param value The new value
     */
    fun setFieldValue(obj: Any?, field: Field, value: Any?)
}
```

### CollectionUtils

Provides helper methods for collections.

```kotlin
object CollectionUtils {
    /**
     * Creates a thread-safe list.
     * @return A thread-safe list
     */
    fun <T> threadSafeList(): MutableList<T>
    
    /**
     * Creates a thread-safe map.
     * @return A thread-safe map
     */
    fun <K, V> threadSafeMap(): MutableMap<K, V>
    
    /**
     * Creates a thread-safe set.
     * @return A thread-safe set
     */
    fun <T> threadSafeSet(): MutableSet<T>
}
```

## Type Definitions

### XposedHookInfo

Information about a hook setup.

```kotlin
data class XposedHookInfo(
    /**
     * The method that was hooked.
     */
    val method: Method,
    
    /**
     * The hooker class.
     */
    val hookerClass: Class<out Hooker>,
    
    /**
     * The hooker instance.
     */
    val hooker: Hooker,
    
    /**
     * When the hook was installed.
     */
    val timestamp: Long = System.currentTimeMillis()
)
```

### ModuleInfo

Information about a module.

```kotlin
data class ModuleInfo(
    /**
     * The module ID.
     */
    val id: String,
    
    /**
     * The module name.
     */
    val name: String,
    
    /**
     * The module version.
     */
    val version: String,
    
    /**
     * The module description.
     */
    val description: String,
    
    /**
     * The module author.
     */
    val author: String?,
    
    /**
     * The target package scopes.
     */
    val scope: List<String>,
    
    /**
     * The module plugin instance.
     */
    val plugin: IModulePlugin
)
```

## Exception Classes

### HookFailedException

Thrown when a hook operation fails.

```kotlin
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

### ModuleInitializationException

Thrown when a module fails to initialize.

```kotlin
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

## Constant Definitions

### IntentActions

Intent actions used by the framework.

```kotlin
object IntentActions {
    /**
     * Intent action for triggering a module reload.
     */
    const val ACTION_LSPK_RELOAD = "com.wobbz.lspk.ACTION_LSPK_RELOAD"
    
    /**
     * Intent action for module settings.
     */
    const val ACTION_LSPK_SETTINGS = "com.wobbz.lspk.ACTION_LSPK_SETTINGS"
}
```

### IntentExtras

Intent extras used by the framework.

```kotlin
object IntentExtras {
    /**
     * Extra for specifying the module ID.
     */
    const val EXTRA_MODULE_ID = "module_id"
    
    /**
     * Extra for specifying whether to force reload.
     */
    const val EXTRA_FORCE = "force"
    
    /**
     * Extra for specifying whether to use selective reload.
     */
    const val EXTRA_SELECTIVE = "selective"
}
``` 