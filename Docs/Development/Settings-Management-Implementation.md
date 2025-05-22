# Settings Management Implementation Guide

> Technical implementation details for the LSPosedKit's settings management system, which provides type-safe preferences, automatic UI generation, and live updates.

## System Overview

The settings management system consists of these components:

1. **SettingsProvider** - Core class that provides access to module preferences
2. **settings.json Schema** - JSON schema for defining module settings
3. **Annotation Processor** - For `@SettingsKey` binding
4. **UI Generator** - Automatic preference screen creation
5. **Storage Backend** - Persistent preferences storage

## Directory Structure

```text
framework/
├── settings/                           # Settings management component
│   ├── src/main/
│   │   └── java/com/wobbz/framework/settings/
│   │       ├── SettingsProvider.kt     # Core settings access API
│   │       ├── SettingsEditor.kt       # Interface for modifying settings
│   │       ├── SettingsSchema.kt       # Schema model classes
│   │       ├── SettingsStorage.kt      # Storage implementation
│   │       ├── SettingsBinding.kt      # Annotation binding utilities
│   │       ├── SettingsUIGenerator.kt  # UI generation utilities
│   │       └── validation/             # Schema validation components
│   ├── src/test/                       # Unit tests
│   └── build.gradle
└── processor/
    └── src/main/java/com/wobbz/framework/processor/
        ├── SettingsKeyProcessor.kt     # @SettingsKey processor
        └── SettingsSchemaProcessor.kt  # Schema validation processor
```

## Core Components Implementation

### 1. SettingsProvider

This class is the main entry point for accessing module settings:

```kotlin
/**
 * Provides type-safe access to module preferences.
 */
public class SettingsProvider(context: Context) {
    private val storage: SettingsStorage
    private val listeners = ConcurrentHashMap<Any, (String) -> Unit>()
    private val schema: SettingsSchema?
    
    init {
        // Initialize storage backend based on context
        storage = SettingsStorage.create(context)
        
        // Load settings schema if available
        schema = try {
            context.assets.open("settings.json").use { input ->
                SettingsSchema.parse(input.reader().readText())
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Factory method to get a SettingsProvider instance for the given context.
     */
    companion object {
        private val providers = WeakHashMap<Context, SettingsProvider>()
        
        @Synchronized
        fun of(context: Context): SettingsProvider {
            return providers.getOrPut(context) {
                SettingsProvider(context)
            }
        }
        
        /**
         * Creates a test instance for unit testing.
         */
        @VisibleForTesting
        fun createForTesting(context: Context): SettingsProvider {
            return SettingsProvider(context).apply {
                // Use in-memory storage for testing
                this.storage = SettingsStorage.createInMemory()
            }
        }
    }
    
    // Type-safe getters
    fun bool(key: String, defaultValue: Boolean = false): Boolean {
        val value = storage.get(key) ?: return getDefaultFromSchema(key, defaultValue)
        return try { value.toBoolean() } catch (e: Exception) { defaultValue }
    }
    
    fun int(key: String, defaultValue: Int = 0): Int {
        val value = storage.get(key) ?: return getDefaultFromSchema(key, defaultValue)
        return try { value.toInt() } catch (e: Exception) { defaultValue }
    }
    
    // Additional getter methods for other types...
    
    /**
     * Gets a editor for modifying settings.
     */
    fun edit(): SettingsEditor {
        return SettingsEditorImpl(storage, this::notifyListeners)
    }
    
    /**
     * Binds a settings class using @SettingsKey annotations.
     */
    fun <T : Any> bind(clazz: Class<T>): T {
        return SettingsBinding.bind(clazz, this)
    }
    
    /**
     * Retrieves default value from schema if available.
     */
    private inline fun <reified T> getDefaultFromSchema(key: String, hardcodedDefault: T): T {
        if (schema == null) return hardcodedDefault
        
        return try {
            schema.getDefaultValue(key) as? T ?: hardcodedDefault
        } catch (e: Exception) {
            hardcodedDefault
        }
    }
    
    /**
     * Adds a settings change listener.
     */
    fun addOnSettingsChangedListener(listener: (String) -> Unit): Any {
        val token = UUID.randomUUID()
        listeners[token] = listener
        return token
    }
    
    /**
     * Removes a settings change listener.
     */
    fun removeOnSettingsChangedListener(token: Any) {
        listeners.remove(token)
    }
    
    /**
     * Notifies all listeners that a setting has changed.
     */
    private fun notifyListeners(key: String) {
        listeners.values.forEach { listener ->
            try {
                listener.invoke(key)
            } catch (e: Exception) {
                // Log error but continue notifying other listeners
            }
        }
    }
}
```

### 2. SettingsEditor Implementation

```kotlin
/**
 * Interface for modifying settings values.
 */
public interface SettingsEditor {
    fun putBoolean(key: String, value: Boolean): SettingsEditor
    fun putInt(key: String, value: Int): SettingsEditor
    fun putLong(key: String, value: Long): SettingsEditor
    fun putFloat(key: String, value: Float): SettingsEditor
    fun putString(key: String, value: String): SettingsEditor
    fun putStringSet(key: String, value: Set<String>): SettingsEditor
    fun remove(key: String): SettingsEditor
    fun apply()
    fun commit(): Boolean
}

/**
 * Implementation of SettingsEditor.
 */
internal class SettingsEditorImpl(
    private val storage: SettingsStorage,
    private val notifyChange: (String) -> Unit
) : SettingsEditor {
    private val modifications = mutableMapOf<String, Any?>()
    
    override fun putBoolean(key: String, value: Boolean): SettingsEditor {
        modifications[key] = value.toString()
        return this
    }
    
    override fun putInt(key: String, value: Int): SettingsEditor {
        modifications[key] = value.toString()
        return this
    }
    
    // Additional setter implementations...
    
    override fun remove(key: String): SettingsEditor {
        modifications[key] = null
        return this
    }
    
    override fun apply() {
        // Apply changes asynchronously
        Thread {
            commitInternal()
        }.start()
    }
    
    override fun commit(): Boolean {
        return commitInternal()
    }
    
    private fun commitInternal(): Boolean {
        val result = storage.setAll(modifications)
        
        // Notify listeners of all changes
        modifications.keys.forEach { key ->
            notifyChange(key)
        }
        
        // Clear pending modifications
        modifications.clear()
        
        return result
    }
}
```

### 3. Settings Storage Implementation

```kotlin
/**
 * Storage backend for settings.
 */
internal interface SettingsStorage {
    fun get(key: String): String?
    fun getAll(): Map<String, String>
    fun set(key: String, value: String?): Boolean
    fun setAll(values: Map<String, Any?>): Boolean
    fun contains(key: String): Boolean
    fun remove(key: String): Boolean
    fun clear(): Boolean
    
    companion object {
        /**
         * Creates a storage implementation based on context.
         */
        fun create(context: Context): SettingsStorage {
            val packageName = context.packageName
            val prefsName = "$packageName.settings"
            
            return try {
                // Try to use SharedPreferences
                val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                SharedPreferencesStorage(prefs)
            } catch (e: Exception) {
                // Fall back to XSharedPreferences for module context
                try {
                    val xprefs = XSharedPreferences(packageName, prefsName)
                    if (xprefs.file.canRead()) {
                        XSharedPreferencesStorage(xprefs)
                    } else {
                        // Fall back to in-memory as last resort
                        InMemoryStorage()
                    }
                } catch (e: Exception) {
                    InMemoryStorage()
                }
            }
        }
        
        /**
         * Creates an in-memory storage for testing.
         */
        fun createInMemory(): SettingsStorage {
            return InMemoryStorage()
        }
    }
}

/**
 * SharedPreferences-based storage implementation.
 */
internal class SharedPreferencesStorage(
    private val prefs: SharedPreferences
) : SettingsStorage {
    override fun get(key: String): String? {
        return if (prefs.contains(key)) prefs.getString(key, null) else null
    }
    
    override fun getAll(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        for ((key, value) in prefs.all) {
            if (value != null) {
                result[key] = value.toString()
            }
        }
        return result
    }
    
    override fun set(key: String, value: String?): Boolean {
        val editor = prefs.edit()
        if (value == null) {
            editor.remove(key)
        } else {
            editor.putString(key, value)
        }
        return editor.commit()
    }
    
    override fun setAll(values: Map<String, Any?>): Boolean {
        val editor = prefs.edit()
        for ((key, value) in values) {
            if (value == null) {
                editor.remove(key)
            } else {
                editor.putString(key, value.toString())
            }
        }
        return editor.commit()
    }
    
    override fun contains(key: String): Boolean {
        return prefs.contains(key)
    }
    
    override fun remove(key: String): Boolean {
        return prefs.edit().remove(key).commit()
    }
    
    override fun clear(): Boolean {
        return prefs.edit().clear().commit()
    }
}

/**
 * XSharedPreferences-based storage implementation for module context.
 */
internal class XSharedPreferencesStorage(
    private val prefs: XSharedPreferences
) : SettingsStorage {
    // Similar implementation as SharedPreferencesStorage
    // but read-only since XSharedPreferences is read-only
    
    override fun set(key: String, value: String?): Boolean {
        return false // Cannot modify XSharedPreferences
    }
    
    override fun setAll(values: Map<String, Any?>): Boolean {
        return false // Cannot modify XSharedPreferences
    }
    
    // Other methods implemented similar to SharedPreferencesStorage
}

/**
 * In-memory storage implementation for testing.
 */
internal class InMemoryStorage : SettingsStorage {
    private val map = ConcurrentHashMap<String, String>()
    
    override fun get(key: String): String? {
        return map[key]
    }
    
    override fun getAll(): Map<String, String> {
        return map.toMap()
    }
    
    override fun set(key: String, value: String?): Boolean {
        if (value == null) {
            map.remove(key)
        } else {
            map[key] = value
        }
        return true
    }
    
    override fun setAll(values: Map<String, Any?>): Boolean {
        for ((key, value) in values) {
            if (value == null) {
                map.remove(key)
            } else {
                map[key] = value.toString()
            }
        }
        return true
    }
    
    override fun contains(key: String): Boolean {
        return map.containsKey(key)
    }
    
    override fun remove(key: String): Boolean {
        map.remove(key)
        return true
    }
    
    override fun clear(): Boolean {
        map.clear()
        return true
    }
}
```

### 4. Settings Schema Implementation

```kotlin
/**
 * Represents a parsed settings.json schema.
 */
internal class SettingsSchema(
    val properties: Map<String, SchemaProperty>
) {
    /**
     * Gets the default value for a setting.
     */
    fun getDefaultValue(key: String): Any? {
        return properties[key]?.default
    }
    
    /**
     * Gets the type of a property.
     */
    fun getPropertyType(key: String): SchemaPropertyType? {
        return properties[key]?.type
    }
    
    companion object {
        /**
         * Parses a settings schema from JSON.
         */
        fun parse(json: String): SettingsSchema {
            val jsonObject = JSONObject(json)
            val propertiesObj = jsonObject.optJSONObject("properties") ?: return SettingsSchema(emptyMap())
            
            val properties = mutableMapOf<String, SchemaProperty>()
            for (key in propertiesObj.keys()) {
                val propObj = propertiesObj.getJSONObject(key)
                val type = when (propObj.optString("type")) {
                    "boolean" -> SchemaPropertyType.BOOLEAN
                    "integer" -> SchemaPropertyType.INTEGER
                    "number" -> SchemaPropertyType.FLOAT
                    "string" -> SchemaPropertyType.STRING
                    "array" -> SchemaPropertyType.ARRAY
                    "object" -> SchemaPropertyType.OBJECT
                    else -> SchemaPropertyType.STRING
                }
                
                val default = propObj.opt("default")
                val title = propObj.optString("title", key)
                val description = propObj.optString("description", "")
                
                properties[key] = SchemaProperty(
                    type = type,
                    default = default,
                    title = title,
                    description = description
                )
            }
            
            return SettingsSchema(properties)
        }
    }
}

/**
 * Represents a property in the settings schema.
 */
internal data class SchemaProperty(
    val type: SchemaPropertyType,
    val default: Any?,
    val title: String,
    val description: String
)

/**
 * Enum of supported property types.
 */
internal enum class SchemaPropertyType {
    BOOLEAN,
    INTEGER,
    FLOAT,
    STRING,
    ARRAY,
    OBJECT
}
```

### 5. Settings Binding Implementation

```kotlin
/**
 * Utility for binding settings classes using annotations.
 */
internal object SettingsBinding {
    /**
     * Binds a settings class to a SettingsProvider.
     */
    fun <T : Any> bind(clazz: Class<T>, provider: SettingsProvider): T {
        // Create instance using no-arg constructor
        val instance = try {
            clazz.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            throw IllegalArgumentException("Settings class ${clazz.name} must have a no-arg constructor", e)
        }
        
        // Find all fields with @SettingsKey annotation
        for (field in clazz.declaredFields) {
            val annotation = field.getAnnotation(SettingsKey::class.java) ?: continue
            val key = annotation.value
            
            // Make field accessible
            field.isAccessible = true
            
            // Get default value from field
            val defaultValue = field.get(instance)
            
            // Get value from settings based on field type
            val value = when (field.type) {
                Boolean::class.java, java.lang.Boolean::class.java -> 
                    provider.bool(key, defaultValue as? Boolean ?: false)
                Int::class.java, java.lang.Integer::class.java -> 
                    provider.int(key, defaultValue as? Int ?: 0)
                Long::class.java, java.lang.Long::class.java -> 
                    provider.long(key, defaultValue as? Long ?: 0L)
                Float::class.java, java.lang.Float::class.java -> 
                    provider.float(key, defaultValue as? Float ?: 0f)
                String::class.java -> 
                    provider.string(key, defaultValue as? String ?: "")
                Set::class.java -> {
                    @Suppress("UNCHECKED_CAST")
                    provider.stringSet(key, defaultValue as? Set<String> ?: emptySet())
                }
                List::class.java -> {
                    @Suppress("UNCHECKED_CAST")
                    provider.stringList(key, defaultValue as? List<String> ?: emptyList())
                }
                else -> defaultValue
            }
            
            // Set the value on the instance
            field.set(instance, value)
        }
        
        return instance
    }
}
```

### 6. Settings UI Generator

```kotlin
/**
 * Generates preference screens from settings.json schema.
 */
internal object SettingsUIGenerator {
    /**
     * Generates preferences from a schema.
     */
    fun generatePreferences(
        context: Context,
        settingsProvider: SettingsProvider,
        preferenceScreen: PreferenceScreen
    ) {
        try {
            // Load schema from assets
            val schema = context.assets.open("settings.json").use { input ->
                SettingsSchema.parse(input.reader().readText())
            }
            
            // Generate preferences from schema
            for ((key, property) in schema.properties) {
                val preference = when (property.type) {
                    SchemaPropertyType.BOOLEAN -> createSwitchPreference(context, key, property, settingsProvider)
                    SchemaPropertyType.INTEGER -> createSeekBarPreference(context, key, property, settingsProvider)
                    SchemaPropertyType.STRING -> {
                        if (property is SchemaProperty.EnumProperty) {
                            createListPreference(context, key, property, settingsProvider)
                        } else {
                            createEditTextPreference(context, key, property, settingsProvider)
                        }
                    }
                    SchemaPropertyType.ARRAY -> createMultiSelectListPreference(context, key, property, settingsProvider)
                    else -> null
                }
                
                preference?.let {
                    preferenceScreen.addPreference(it)
                }
            }
        } catch (e: Exception) {
            Log.e("SettingsUIGenerator", "Failed to generate preferences", e)
        }
    }
    
    /**
     * Creates a SwitchPreference from a schema property.
     */
    private fun createSwitchPreference(
        context: Context,
        key: String,
        property: SchemaProperty,
        provider: SettingsProvider
    ): SwitchPreference {
        return SwitchPreference(context).apply {
            this.key = key
            title = property.title
            summary = property.description
            isChecked = provider.bool(key, property.default as? Boolean ?: false)
            
            setOnPreferenceChangeListener { _, newValue ->
                provider.edit().putBoolean(key, newValue as Boolean).apply()
                true
            }
        }
    }
    
    // Similar methods for other preference types...
}
```

## Annotation Processor Implementation

### SettingsKeyProcessor

```kotlin
/**
 * Processes @SettingsKey annotations.
 */
@AutoService(Processor::class)
@SupportedAnnotationTypes("com.wobbz.framework.processor.SettingsKey")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
class SettingsKeyProcessor : AbstractProcessor() {
    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // We don't need to generate code for @SettingsKey
        // It's used at runtime for binding, so just validate the usage
        
        for (element in roundEnv.getElementsAnnotatedWith(SettingsKey::class.java)) {
            if (element.kind != ElementKind.FIELD) {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "@SettingsKey can only be applied to fields",
                    element
                )
                continue
            }
            
            val annotation = element.getAnnotation(SettingsKey::class.java)
            val key = annotation.value
            
            if (key.isEmpty()) {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "@SettingsKey value cannot be empty",
                    element
                )
            }
        }
        
        return true
    }
}
```

### SettingsSchemaProcessor

```kotlin
/**
 * Validates settings.json schema files.
 */
@AutoService(Processor::class)
@SupportedAnnotationTypes("com.wobbz.framework.processor.XposedPlugin")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
class SettingsSchemaProcessor : AbstractProcessor() {
    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // Find all modules with @XposedPlugin annotation
        for (element in roundEnv.getElementsAnnotatedWith(XposedPlugin::class.java)) {
            if (element.kind != ElementKind.CLASS) continue
            
            val moduleElement = element as TypeElement
            val moduleName = moduleElement.simpleName.toString()
            
            // Check if the module has a settings.json file
            try {
                val settingsFile = getSettingsFile(moduleElement)
                if (settingsFile != null) {
                    validateSettingsSchema(settingsFile, moduleName)
                }
            } catch (e: Exception) {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Failed to validate settings.json: ${e.message}",
                    element
                )
            }
        }
        
        return true
    }
    
    /**
     * Gets the settings.json file for a module.
     */
    private fun getSettingsFile(moduleElement: TypeElement): FileObject? {
        val packageName = processingEnv.elementUtils
            .getPackageOf(moduleElement).qualifiedName.toString()
        
        return try {
            processingEnv.filer.getResource(
                StandardLocation.CLASS_OUTPUT,
                "",
                "assets/settings.json"
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Validates a settings schema file.
     */
    private fun validateSettingsSchema(file: FileObject, moduleName: String) {
        try {
            val content = file.openInputStream().use { it.reader().readText() }
            val jsonObject = JSONObject(content)
            
            // Check for required properties
            if (!jsonObject.has("type") || jsonObject.getString("type") != "object") {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "settings.json must have type: \"object\"",
                    null
                )
            }
            
            if (!jsonObject.has("properties")) {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "settings.json must have a properties object",
                    null
                )
            }
            
            // Validate each property
            val properties = jsonObject.getJSONObject("properties")
            for (key in properties.keys()) {
                val property = properties.getJSONObject(key)
                
                // Check for required property fields
                if (!property.has("type")) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Property '$key' must have a type",
                        null
                    )
                }
                
                // Validate property type
                val type = property.getString("type")
                if (type !in listOf("boolean", "integer", "number", "string", "array", "object")) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Property '$key' has invalid type: $type",
                        null
                    )
                }
                
                // Check if default value is present and matches type
                if (property.has("default")) {
                    val default = property.get("default")
                    val validType = when (type) {
                        "boolean" -> default is Boolean
                        "integer" -> default is Int
                        "number" -> default is Number
                        "string" -> default is String
                        "array" -> default is JSONArray
                        "object" -> default is JSONObject
                        else -> false
                    }
                    
                    if (!validType) {
                        processingEnv.messager.printMessage(
                            Diagnostic.Kind.WARNING,
                            "Property '$key' has default value that doesn't match its type",
                            null
                        )
                    }
                }
            }
        } catch (e: Exception) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Invalid settings.json: ${e.message}",
                null
            )
        }
    }
}
```

## Integration with Module System

### 1. Module Access to Settings

Modules access settings through the `SettingsProvider` class:

```kotlin
class MyModule : IModulePlugin {
    override fun initialize(context: Context, xposed: XposedInterface) {
        val settings = SettingsProvider.of(context)
        val isEnabled = settings.bool("enable_feature", true)
        
        if (isEnabled) {
            xposed.log(LogLevel.INFO, "Feature enabled")
        }
    }
}
```

### 2. Using @SettingsKey for Type-Safe Access

Modules can use annotated classes for type-safe settings access:

```kotlin
class MySettings(
    @SettingsKey("enable_feature") val isEnabled: Boolean = true,
    @SettingsKey("log_level") val logLevel: String = "INFO",
    @SettingsKey("retry_count") val retryCount: Int = 3
)

class MyModule : IModulePlugin {
    override fun initialize(context: Context, xposed: XposedInterface) {
        val provider = SettingsProvider.of(context)
        val settings = provider.bind(MySettings::class.java)
        
        if (settings.isEnabled) {
            xposed.log(LogLevel.INFO, "Module enabled with log level: ${settings.logLevel}")
        }
    }
}
```

### 3. Settings Change Notifications

Modules can listen for settings changes:

```kotlin
class MyModule : IModulePlugin {
    private var settingsListener: Any? = null
    
    override fun initialize(context: Context, xposed: XposedInterface) {
        val settings = SettingsProvider.of(context)
        
        // Register listener for settings changes
        settingsListener = settings.addOnSettingsChangedListener { key ->
            when (key) {
                "enable_feature" -> {
                    val enabled = settings.bool("enable_feature")
                    xposed.log(LogLevel.INFO, "Feature enabled changed to: $enabled")
                    // Update module behavior based on new setting
                }
            }
        }
    }
}
```

## UI Generation Implementation

To generate preference screens from settings.json:
LSPosedKit offers automatic UI generation from `settings.json`. However, if a module specifies a `customSettingsActivity` in its `module-info.json`, the `SettingsUIGenerator` will typically be bypassed for that module. The module then becomes responsible for presenting its own settings interface using the provided `SettingsProvider` to load and save values.

The `SettingsFragment` shown below is an example of how the **automatic generation** would be invoked. If a custom activity is defined, the LSPosed Manager or a similar host application would directly launch that activity instead of this fragment.

```kotlin
class ModuleSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        // Get module ID from intent
        val moduleId = intent.getStringExtra("module_id") ?: return
        val customActivityClassName = intent.getStringExtra("custom_settings_activity") // Optional: Passed by host

        if (customActivityClassName != null && customActivityClassName.isNotEmpty()) {
            try {
                // Attempt to launch the custom settings activity
                val customIntent = Intent()
                customIntent.setClassName(this, customActivityClassName)
                // Potentially pass module ID or other necessary info
                customIntent.putExtra("module_id", moduleId) 
                startActivity(customIntent)
                finish() // Close this placeholder activity
                return
            } catch (e: Exception) {
                // Log error, fall back to default UI or show error
                Log.e("SettingsLauncher", "Failed to launch custom settings activity: $customActivityClassName for module $moduleId", e)
                // Fallback to generated UI below
            }
        }
        
        // Default: Set up preference fragment for automatic UI generation or as fallback
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings_container, SettingsFragment.newInstance(moduleId))
            .commit()
    }
    
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            val context = preferenceManager.context
            val screen = preferenceManager.createPreferenceScreen(context)
            preferenceScreen = screen // Set it early
            
            // Get module ID from arguments
            val moduleId = arguments?.getString("module_id") ?: return
            
            // Create settings provider for the module context
            // This might require getting the module's specific context if not already correct
            val moduleContext = context // Placeholder: actual module context might be needed
            val settingsProvider = SettingsProvider.of(moduleContext) 
            
            // Logic to decide whether to generate UI or if custom activity is primary
            // This check might also live in the code that decides to show this fragment.
            // For this example, we assume this fragment is shown if custom activity is not set or failed.
            // val moduleInfo = loadModuleInfoFromAssets(context, moduleId) // Needs a real implementation
            // if (moduleInfo?.customSettingsActivity == null || moduleInfo.customSettingsActivity.isEmpty()) {
            //    Log.i("SettingsFragment", "Generating settings UI for module $moduleId")
            //    SettingsUIGenerator.generatePreferences(context, settingsProvider, preferenceScreen)
            // } else {
            //    Log.i("SettingsFragment", "Module $moduleId uses a custom settings activity. This fragment might be a fallback or show a message.")
            //    // Optionally, add a preference here indicating that a custom activity is defined
            //    // or if the custom activity launch failed from ModuleSettingsActivity.
            //    val customActivityFailedPref = Preference(context)
            //    customActivityFailedPref.title = "Custom Settings Note"
            //    customActivityFailedPref.summary = "This module uses a custom settings screen. If it didn't launch, please check module configuration."
            //    preferenceScreen.addPreference(customActivityFailedPref)
            // }

            // Simplified: Assume if this fragment is loaded, we attempt generation
            // The decision to launch custom activity vs this fragment should be made by the caller (e.g., LSPosed Manager)
            Log.i("SettingsFragment", "Attempting to generate settings UI for module $moduleId")
            SettingsUIGenerator.generatePreferences(context, settingsProvider, preferenceScreen)
        }

        // Placeholder for actually loading module-info.json data from module's assets
        // private fun loadModuleInfoFromAssets(context: Context, moduleId: String): ModuleInfo? { 
        //     // In a real scenario, this would involve finding the module's APK,
        //     // creating a context for it, and reading its assets/module-info.json.
        //     // This is complex and usually handled by the LSPosed Manager.
        //     return null 
        // }

        // Placeholder data class for module-info.json content
        // data class ModuleInfo(val id: String, val customSettingsActivity: String?)
        
        companion object {
            fun newInstance(moduleId: String): SettingsFragment {
                return SettingsFragment().apply {
                    arguments = Bundle().apply {
                        putString("module_id", moduleId)
                    }
                }
            }
        }
    }
}
```

## Settings Migration & Versioning

Settings migration can be handled using version tracking in settings.json:

```json
{
  "$schema": "https://json-schema.org/draft-07/schema#",
  "type": "object",
  "version": 2,
  "properties": {
    // Properties...
  },
  "migrationHandler": "com.example.mymodule.SettingsMigration"
}
```

Implementation of migration handler:

```kotlin
/**
 * Handles settings migration between versions.
 */
class SettingsMigration : SettingsMigrationHandler {
    override fun migrate(oldVersion: Int, newVersion: Int, settings: SettingsProvider) {
        when {
            oldVersion == 1 && newVersion >= 2 -> {
                // Migrate from version 1 to 2
                val oldValue = settings.string("old_key")
                if (oldValue != null) {
                    settings.edit()
                        .putString("new_key", oldValue)
                        .remove("old_key")
                        .apply()
                }
            }
        }
    }
}
```

## Testing Implementation

Unit tests for settings management:

```kotlin
class SettingsProviderTest {
    private lateinit var context: Context
    private lateinit var settingsProvider: SettingsProvider
    
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        settingsProvider = SettingsProvider.createForTesting(context)
    }
    
    @Test
    fun testBooleanSetting() {
        // Default should be false
        val defaultValue = settingsProvider.bool("test_key")
        assertFalse(defaultValue)
        
        // Set to true
        settingsProvider.edit().putBoolean("test_key", true).commit()
        
        // Should now be true
        val newValue = settingsProvider.bool("test_key")
        assertTrue(newValue)
    }
    
    @Test
    fun testSettingsBinding() {
        // Set values first
        settingsProvider.edit()
            .putBoolean("enabled", true)
            .putString("name", "Test")
            .putInt("count", 42)
            .commit()
        
        // Bind to class
        val settings = settingsProvider.bind(TestSettings::class.java)
        
        // Verify values
        assertTrue(settings.enabled)
        assertEquals("Test", settings.name)
        assertEquals(42, settings.count)
    }
    
    @Test
    fun testSettingsChangeListener() {
        var changedKey: String? = null
        
        // Add listener
        val token = settingsProvider.addOnSettingsChangedListener { key ->
            changedKey = key
        }
        
        // Change setting
        settingsProvider.edit().putString("test_key", "new_value").commit()
        
        // Verify listener was called
        assertEquals("test_key", changedKey)
        
        // Remove listener and change again
        settingsProvider.removeOnSettingsChangedListener(token)
        changedKey = null
        settingsProvider.edit().putString("another_key", "value").commit()
        
        // Verify listener was not called
        assertNull(changedKey)
    }
    
    class TestSettings(
        @SettingsKey("enabled") val enabled: Boolean = false,
        @SettingsKey("name") val name: String = "",
        @SettingsKey("count") val count: Int = 0
    )
}
```

## Conclusion

This comprehensive implementation of the settings management system provides LSPosedKit modules with:

1. **Type-safe access** to module settings
2. **Automatic UI generation** from JSON schema
3. **Live updates** for settings changes
4. **Persistence** across module restarts
5. **Migration** support for version changes

The system is designed to be:
- **Robust**: Handles edge cases and error conditions
- **Efficient**: Uses caching and minimizes disk access
- **Extensible**: Supports custom types and validation
- **Testable**: Includes mocks and test helpers

By implementing this system, module developers can focus on their core functionality without worrying about settings persistence and UI generation. 