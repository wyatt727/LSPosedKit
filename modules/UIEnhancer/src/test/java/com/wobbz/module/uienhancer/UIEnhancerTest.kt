package com.wobbz.module.uienhancer

import com.wobbz.framework.core.*
import com.wobbz.framework.service.FeatureManager
import com.wobbz.module.uienhancer.services.IUIThemeService
import com.wobbz.module.uienhancer.ui.ThemeManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for UIEnhancer module.
 */
class UIEnhancerTest {
    
    private lateinit var mockXposed: MockXposedInterface
    private lateinit var module: UIEnhancer
    
    @Before
    fun setup() {
        mockXposed = MockXposedInterface()
        module = UIEnhancer()
    }
    
    @Test
    fun testModuleInitialization() {
        val mockContext = HookTestUtil.createMockModuleContext("com.wobbz.module.uienhancer")
        
        // Test that module initializes without throwing
        module.initialize(mockContext as android.content.Context, mockXposed)
        
        // Verify initialization logging
        val logs = mockXposed.getLogs(LogLevel.INFO)
        assertTrue("Module should log initialization", 
            logs.any { it.message.contains("UIEnhancer initialized") })
    }
    
    @Test
    fun testSettingsConfiguration() {
        val settings = UIEnhancerSettings(
            enabled = true,
            primaryColor = "#FF0000",
            accentColor = "#00FF00",
            textSizeMultiplier = 1.2f,
            cornerRadius = 12,
            enhanceButtons = true,
            enhanceTextViews = false,
            applyDarkTheme = true,
            animateTransitions = false
        )
        
        assertEquals(true, settings.enabled)
        assertEquals("#FF0000", settings.primaryColor)
        assertEquals("#00FF00", settings.accentColor)
        assertEquals(1.2f, settings.textSizeMultiplier, 0.01f)
        assertEquals(12, settings.cornerRadius)
        assertTrue(settings.enhanceButtons)
        assertFalse(settings.enhanceTextViews)
        assertTrue(settings.applyDarkTheme)
        assertFalse(settings.animateTransitions)
    }
    
    @Test
    fun testPackageFiltering() {
        val mockContext = HookTestUtil.createMockModuleContext("com.wobbz.module.uienhancer")
        module.initialize(mockContext as android.content.Context, mockXposed)
        
        // Test with target package
        val targetParam = MockPackageLoadedParam.forPackageWithXposed("com.android.settings", mockXposed)
        module.onPackageLoaded(targetParam)
        
        // Test with non-target package (should be filtered out)
        val nonTargetParam = MockPackageLoadedParam.forPackageWithXposed("com.example.app", mockXposed)
        module.onPackageLoaded(nonTargetParam)
        
        // Verify appropriate logging
        val logs = mockXposed.getLogs()
        assertTrue("Should have logs for package processing", logs.isNotEmpty())
    }
    
    @Test
    fun testHotReload() {
        val mockContext = HookTestUtil.createMockModuleContext("com.wobbz.module.uienhancer")
        module.initialize(mockContext as android.content.Context, mockXposed)
        
        // Simulate some hooks being created
        val param = MockPackageLoadedParam.forPackageWithXposed("com.android.settings", mockXposed)
        module.onPackageLoaded(param)
        
        // Test hot reload
        module.onHotReload()
        
        // Verify cleanup occurred
        val logs = mockXposed.getLogs()
        assertTrue("Should have hot reload logs", 
            logs.any { it.message.contains("Hot reload") })
    }
    
    @Test
    fun testModuleLifecycle() {
        // Test lifecycle methods don't throw exceptions
        module.onStart()
        module.onStop()
        
        // In a real test environment, we could verify service registration
        // For now, just ensure no exceptions are thrown
    }
    
    @Test
    fun testThemeManager() {
        val settings = UIEnhancerSettings(
            primaryColor = "#3F51B5",
            accentColor = "#FF4081",
            textSizeMultiplier = 1.5f,
            cornerRadius = 10,
            applyDarkTheme = true,
            animateTransitions = true
        )
        
        val themeManager = ThemeManager(settings)
        
        // Test color parsing
        assertEquals("#3F51B5", themeManager.colorToHex(themeManager.getPrimaryColor()))
        
        // Test theme configuration
        val themeConfig = themeManager.getCurrentTheme()
        assertEquals(1.5f, themeConfig.textSizeMultiplier, 0.01f)
        assertEquals(10, themeConfig.cornerRadius)
        assertTrue(themeConfig.isDarkTheme)
        assertTrue(themeConfig.animationsEnabled)
    }
    
    @Test
    fun testColorValidation() {
        val themeManager = ThemeManager(UIEnhancerSettings())
        
        // Test valid colors
        assertTrue(themeManager.isValidColor("#FF0000"))
        assertTrue(themeManager.isValidColor("#00FF00"))
        assertTrue(themeManager.isValidColor("#0000FF"))
        
        // Test invalid colors
        assertFalse(themeManager.isValidColor("invalid"))
        assertFalse(themeManager.isValidColor("#GGGGGG"))
        assertFalse(themeManager.isValidColor("red"))
    }
    
    @Test
    fun testColorManipulation() {
        val themeManager = ThemeManager(UIEnhancerSettings())
        val red = android.graphics.Color.RED
        
        // Test darkening
        val darkerRed = themeManager.darkenColor(red, 0.5f)
        assertNotEquals(red, darkerRed)
        
        // Test lightening
        val lighterRed = themeManager.lightenColor(red, 1.5f)
        assertNotEquals(red, lighterRed)
        
        // Test blending
        val blue = android.graphics.Color.BLUE
        val blended = themeManager.blendColors(red, blue, 0.5f)
        assertNotEquals(red, blended)
        assertNotEquals(blue, blended)
    }
    
    @Test
    fun testServiceIntegration() {
        // This test would verify service registration in a real environment
        // For unit testing, we just verify the service interface exists
        val settings = UIEnhancerSettings()
        val themeManager = ThemeManager(settings)
        val themeService = com.wobbz.module.uienhancer.services.UIThemeProvider(themeManager)
        
        // Test service interface methods
        val theme = themeService.getCurrentTheme()
        assertNotNull(theme)
        
        val primaryColor = themeService.getPrimaryColor()
        assertTrue("Primary color should be valid", primaryColor != 0)
        
        val contrastColor = themeService.getContrastColor(android.graphics.Color.BLACK)
        assertEquals(android.graphics.Color.WHITE, contrastColor)
    }
} 