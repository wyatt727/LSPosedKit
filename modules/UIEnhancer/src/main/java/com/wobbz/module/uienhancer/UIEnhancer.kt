package com.wobbz.module.uienhancer

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.wobbz.framework.core.*
import com.wobbz.framework.hot.IHotReloadable
import com.wobbz.framework.processor.HotReloadable
import com.wobbz.framework.processor.SettingsKey
import com.wobbz.framework.processor.XposedPlugin
import com.wobbz.framework.service.FeatureManager
import com.wobbz.framework.settings.SettingsProvider
import com.wobbz.module.uienhancer.services.IUIThemeService
import com.wobbz.module.uienhancer.services.UIThemeProvider
import com.wobbz.module.uienhancer.ui.ThemeManager

/**
 * Settings class for UI enhancement configuration.
 */
class UIEnhancerSettings(
    @SettingsKey("enabled") val enabled: Boolean = true,
    @SettingsKey("primary_color") val primaryColor: String = "Material Blue",
    @SettingsKey("accent_color") val accentColor: String = "Pink",
    @SettingsKey("text_size_multiplier") val textSizeMultiplier: Float = 1.0f,
    @SettingsKey("corner_radius") val cornerRadius: Int = 8,
    @SettingsKey("target_packages") val targetPackages: List<String> = listOf("com.android.settings", "com.android.systemui"),
    @SettingsKey("enhance_buttons") val enhanceButtons: Boolean = true,
    @SettingsKey("enhance_text_views") val enhanceTextViews: Boolean = true,
    @SettingsKey("apply_dark_theme") val applyDarkTheme: Boolean = false,
    @SettingsKey("animate_transitions") val animateTransitions: Boolean = true
)

/**
 * Color mapping from human-readable names to hex values
 */
object ColorMapping {
    private val PRIMARY_COLORS = mapOf(
        "Material Blue" to "#3F51B5",
        "Material Red" to "#F44336", 
        "Material Green" to "#4CAF50",
        "Material Purple" to "#9C27B0",
        "Material Orange" to "#FF9800",
        "Material Teal" to "#009688",
        "Material Pink" to "#E91E63",
        "Material Indigo" to "#3F51B5",
        "Material Dark Blue" to "#1976D2",
        "Material Dark Green" to "#388E3C"
    )
    
    private val ACCENT_COLORS = mapOf(
        "Pink" to "#FF4081",
        "Teal" to "#1DE9B6", 
        "Orange" to "#FF6D00",
        "Light Blue" to "#40C4FF",
        "Green" to "#00E676",
        "Purple" to "#E040FB",
        "Red" to "#FF5252",
        "Amber" to "#FFAB00",
        "Deep Orange" to "#FF3D00",
        "Light Green" to "#76FF03"
    )
    
    fun getPrimaryColorHex(colorName: String): String {
        return PRIMARY_COLORS[colorName] ?: "#3F51B5" // Default to Material Blue
    }
    
    fun getAccentColorHex(colorName: String): String {
        return ACCENT_COLORS[colorName] ?: "#FF4081" // Default to Pink
    }
}

/**
 * UIEnhancer module that demonstrates UI customization and theming.
 * This module shows advanced patterns like view hooking, theme management,
 * and cross-module service provision for UI themes.
 */
@XposedPlugin(
    id = "ui-enhancer",
    name = "UI Enhancer",
    version = "1.0.0",
    scope = ["com.android.settings", "com.android.systemui", "*"],
    description = "Enhance UI elements with custom styling and theming",
    author = "LSPosedKit Team"
)
@HotReloadable
class UIEnhancer : IModulePlugin, IHotReloadable, ModuleLifecycle {
    
    companion object {
        private const val TAG = "LSPK-UIEnhancer"
    }
    
    private val hooks = mutableListOf<MethodUnhooker<*>>()
    private lateinit var settings: UIEnhancerSettings
    private lateinit var themeManager: ThemeManager
    private var themeService: UIThemeProvider? = null
    
    override fun initialize(context: Context, xposed: XposedInterface) {
        xposed.log(LogLevel.INFO, "UIEnhancer initializing...")
        
        // Load settings
        val provider = SettingsProvider.of(context)
        settings = provider.bind(UIEnhancerSettings::class.java)
        
        // Initialize theme manager
        themeManager = ThemeManager(settings)
        
        xposed.log(LogLevel.INFO, "UIEnhancer initialized with settings: enabled=${settings.enabled}")
    }
    
    override fun onPackageLoaded(param: PackageLoadedParam) {
        param.xposed.log(LogLevel.INFO, "UIEnhancer: Package loaded callback for: ${param.packageName}")
        
        if (!settings.enabled) {
            param.xposed.log(LogLevel.DEBUG, "UIEnhancer disabled, skipping package: ${param.packageName}")
            return
        }
        
        param.xposed.log(LogLevel.INFO, "UIEnhancer enabled, checking if package should be enhanced...")
        param.xposed.log(LogLevel.INFO, "Target packages: ${settings.targetPackages}")
        
        // Check if this package should be enhanced
        if (!shouldEnhancePackage(param.packageName)) {
            param.xposed.log(LogLevel.DEBUG, "Package not in target list: ${param.packageName}")
            return
        }
        
        param.xposed.log(LogLevel.INFO, "Enhancing UI for package: ${param.packageName}")
        param.xposed.log(LogLevel.INFO, "Settings: primary=${settings.primaryColor}, accent=${settings.accentColor}, textSize=${settings.textSizeMultiplier}")
        
        try {
            // Hook view inflation
            hookViewInflation(param)
            
            // Hook specific view types
            if (settings.enhanceTextViews) {
                param.xposed.log(LogLevel.INFO, "Hooking text views...")
                hookTextViews(param)
            }
            
            if (settings.enhanceButtons) {
                param.xposed.log(LogLevel.INFO, "Hooking buttons...")
                hookButtons(param)
            }
            
            param.xposed.log(LogLevel.INFO, "Successfully hooked UI components for ${param.packageName}")
        } catch (e: Exception) {
            param.xposed.logError("Failed to hook UI components for ${param.packageName}", e)
        }
    }
    
    override fun onHotReload() {
        LogUtil.logInfo(TAG, "Hot reload triggered, cleaning up hooks...")
        hooks.forEach { it.unhook() }
        hooks.clear()
        
        // Re-register service if it exists
        themeService?.let { service ->
            FeatureManager.registerService(
                IUIThemeService::class.java,
                service,
                version = "1.0.0",
                moduleId = "ui-enhancer"
            )
        }
    }
    
    override fun onStart() {
        LogUtil.logInfo(TAG, "UIEnhancer module starting...")
        
        // Register theme service for other modules to use
        themeService = UIThemeProvider(themeManager)
        
        FeatureManager.registerService(
            IUIThemeService::class.java,
            themeService!!,
            version = "1.0.0",
            moduleId = "ui-enhancer"
        )
        
        LogUtil.logInfo(TAG, "UIEnhancer theme service registered")
    }
    
    override fun onStop() {
        LogUtil.logInfo(TAG, "UIEnhancer module stopping...")
        
        // Clean up hooks
        hooks.forEach { it.unhook() }
        hooks.clear()
        
        // Release theme service
        themeService?.release()
        themeService = null
        
        LogUtil.logInfo(TAG, "UIEnhancer stopped and cleaned up")
    }
    
    private fun shouldEnhancePackage(packageName: String): Boolean {
        return settings.targetPackages.contains(packageName) || 
               settings.targetPackages.contains("*")
    }
    
    private fun hookViewInflation(param: PackageLoadedParam) {
        try {
            // Hook View.onFinishInflate to apply styling after view inflation
            val viewClass = param.xposed.loadClass("android.view.View")
            val onFinishInflateMethod = viewClass.getDeclaredMethod("onFinishInflate")
            
            val unhooker = param.xposed.hook(onFinishInflateMethod, ViewInflateHooker::class.java)
            hooks.add(unhooker)
            
            param.xposed.log(LogLevel.DEBUG, "Hooked View.onFinishInflate")
        } catch (e: Exception) {
            param.xposed.logError("Failed to hook view inflation", e)
        }
    }
    
    private fun hookTextViews(param: PackageLoadedParam) {
        try {
            // Hook TextView.setText method instead of constructor 
            val textViewClass = param.xposed.loadClass("android.widget.TextView")
            val setTextMethod = textViewClass.getDeclaredMethod("setText", CharSequence::class.java)
            
            val unhooker = param.xposed.hook(setTextMethod, TextViewHooker::class.java)
            hooks.add(unhooker)
            
            param.xposed.log(LogLevel.DEBUG, "Hooked TextView.setText")
        } catch (e: Exception) {
            param.xposed.logError("Failed to hook TextView", e)
        }
    }
    
    private fun hookButtons(param: PackageLoadedParam) {
        try {
            // Hook Button constructor to apply button styling
            val buttonClass = param.xposed.loadClass("android.widget.Button")
            val constructor = buttonClass.getDeclaredConstructor(
                Context::class.java,
                param.xposed.loadClass("android.util.AttributeSet"),
                Int::class.javaPrimitiveType
            )
            
            // Hook the constructor directly using the constructor hook method
            val unhooker = param.xposed.hook(constructor, ButtonHooker::class.java)
            hooks.add(unhooker)
            
            param.xposed.log(LogLevel.DEBUG, "Hooked Button constructor")
        } catch (e: Exception) {
            param.xposed.logError("Failed to hook Button", e)
        }
    }
    
    /**
     * Hooker for view inflation to apply general styling.
     */
    class ViewInflateHooker : Hooker {
        override fun afterHook(param: HookParam) {
            val view = param.thisObject as? View ?: return
            
            try {
                val context = view.context
                val provider = SettingsProvider.of(context)
                val settings = provider.bind(UIEnhancerSettings::class.java)
                
                if (!settings.enabled) return
                
                // Apply corner radius to views that support background
                if (settings.cornerRadius > 0) {
                    applyCornerRadius(view, settings.cornerRadius)
                }
                
                // Apply dark theme if enabled
                if (settings.applyDarkTheme) {
                    applyDarkTheme(view)
                }
                
            } catch (e: Exception) {
                // Silently ignore errors in view styling to avoid crashes
            }
        }
        
        private fun applyCornerRadius(view: View, radius: Int) {
            try {
                if (view.background != null) {
                    val drawable = GradientDrawable()
                    drawable.cornerRadius = radius.toFloat()
                    drawable.setColor(Color.TRANSPARENT)
                    view.background = drawable
                }
            } catch (e: Exception) {
                // Ignore if we can't apply corner radius
            }
        }
        
        private fun applyDarkTheme(view: View) {
            try {
                when (view) {
                    is ViewGroup -> {
                        // Apply dark background to containers
                        view.setBackgroundColor(Color.parseColor("#1E1E1E"))
                    }
                    is TextView -> {
                        // Apply light text color for dark theme
                        view.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                }
            } catch (e: Exception) {
                // Ignore theming errors
            }
        }
    }
    
    /**
     * Hooker for TextView styling.
     */
    class TextViewHooker : Hooker {
        override fun afterHook(param: HookParam) {
            val textView = param.thisObject as? TextView ?: return
            
            try {
                val context = textView.context
                val provider = SettingsProvider.of(context)
                val settings = provider.bind(UIEnhancerSettings::class.java)
                
                // Log hook firing
                LogUtil.logDebug("UIEnhancer.TextViewHooker", "Hook fired - enabled: ${settings.enabled}, enhanceTextViews: ${settings.enhanceTextViews}")
                
                if (!settings.enabled || !settings.enhanceTextViews) return
                
                // Apply text size multiplier
                if (settings.textSizeMultiplier != 1.0f) {
                    val currentSize = textView.textSize
                    val newSize = currentSize * settings.textSizeMultiplier
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize)
                    LogUtil.logDebug("UIEnhancer.TextViewHooker", "Applied text size: $currentSize -> $newSize (multiplier: ${settings.textSizeMultiplier})")
                }
                
                // Apply primary color to important text views
                if (isImportantTextView(textView)) {
                    try {
                        val colorHex = ColorMapping.getPrimaryColorHex(settings.primaryColor)
                        val color = Color.parseColor(colorHex)
                        textView.setTextColor(color)
                        LogUtil.logDebug("UIEnhancer.TextViewHooker", "Applied color: ${settings.primaryColor} -> $colorHex")
                    } catch (e: Exception) {
                        LogUtil.logError("UIEnhancer.TextViewHooker", "Color parsing failed: ${settings.primaryColor}", e)
                    }
                }
                
            } catch (e: Exception) {
                LogUtil.logError("UIEnhancer.TextViewHooker", "TextView styling failed", e)
            }
        }
        
        private fun isImportantTextView(textView: TextView): Boolean {
            val id = textView.id
            return id == android.R.id.text1 || 
                   id == android.R.id.title ||
                   textView.contentDescription?.contains("title", ignoreCase = true) == true
        }
    }
    
    /**
     * Hooker for Button styling.
     */
    class ButtonHooker : Hooker {
        override fun afterHook(param: HookParam) {
            val button = param.thisObject as? Button ?: return
            
            try {
                val context = button.context
                val provider = SettingsProvider.of(context)
                val settings = provider.bind(UIEnhancerSettings::class.java)
                
                // Log hook firing
                LogUtil.logDebug("UIEnhancer.ButtonHooker", "Hook fired - enabled: ${settings.enabled}, enhanceButtons: ${settings.enhanceButtons}")
                
                if (!settings.enabled || !settings.enhanceButtons) return
                
                // Apply accent color background
                try {
                    val accentColorHex = ColorMapping.getAccentColorHex(settings.accentColor)
                    val accentColor = Color.parseColor(accentColorHex)
                    val drawable = GradientDrawable()
                    drawable.setColor(accentColor)
                    drawable.cornerRadius = settings.cornerRadius.toFloat()
                    button.background = drawable
                    
                    // Set white text for better contrast
                    button.setTextColor(Color.WHITE)
                    LogUtil.logDebug("UIEnhancer.ButtonHooker", "Applied button style: ${settings.accentColor} -> $accentColorHex, radius: ${settings.cornerRadius}")
                } catch (e: Exception) {
                    LogUtil.logError("UIEnhancer.ButtonHooker", "Button color styling failed: ${settings.accentColor}", e)
                }
                
                // Apply text size multiplier
                if (settings.textSizeMultiplier != 1.0f) {
                    val currentSize = button.textSize
                    val newSize = currentSize * settings.textSizeMultiplier
                    button.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize)
                    LogUtil.logDebug("UIEnhancer.ButtonHooker", "Applied button text size: $currentSize -> $newSize")
                }
                
            } catch (e: Exception) {
                LogUtil.logError("UIEnhancer.ButtonHooker", "Button styling failed", e)
            }
        }
    }
} 