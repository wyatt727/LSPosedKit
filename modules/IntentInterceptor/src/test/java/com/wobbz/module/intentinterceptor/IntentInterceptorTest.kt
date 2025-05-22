package com.wobbz.module.intentinterceptor

import com.wobbz.framework.core.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for IntentInterceptor.
 */
class IntentInterceptorTest {
    
    private lateinit var mockXposed: MockXposedInterface
    private lateinit var module: IntentInterceptor
    
    @Before
    fun setup() {
        mockXposed = MockXposedInterface()
        module = IntentInterceptor()
    }
    
    @Test
    fun testModuleInitialization() {
        val mockContext = HookTestUtil.createMockModuleContext("com.test.package")
        
        // Test that module initializes without throwing
        module.initialize(mockContext as android.content.Context, mockXposed)
        
        // Verify initialization logging
        val logs = mockXposed.getLogs(LogLevel.INFO)
        assertTrue("Module should log initialization", 
            logs.any { it.message.contains("IntentInterceptor initialized") })
    }
    
    @Test
    fun testPackageFiltering() {
        val mockContext = HookTestUtil.createMockModuleContext("com.test.package")
        module.initialize(mockContext as android.content.Context, mockXposed)
        
        val param = MockPackageLoadedParam.forPackageWithXposed("com.test.app", mockXposed)
        
        // Test package loading
        module.onPackageLoaded(param)
        
        // Verify hooks were applied (or not, depending on settings)
        val hookedMethods = mockXposed.getHookedMethods()
        // Add specific assertions based on your module logic
    }
    
    @Test
    fun testHotReload() {
        val mockContext = HookTestUtil.createMockModuleContext("com.test.package")
        module.initialize(mockContext as android.content.Context, mockXposed)
        
        // Simulate some hooks being created
        val param = MockPackageLoadedParam.forPackageWithXposed("com.test.app", mockXposed)
        module.onPackageLoaded(param)
        
        val initialHookCount = mockXposed.getHookedMethods().size
        
        // Test hot reload
        module.onHotReload()
        
        // Verify cleanup occurred (you may need to adjust this based on implementation)
        // This is a basic template - customize based on your module's behavior
    }
    
    @Test
    fun testLifecycleMethods() {
        // Test that lifecycle methods don't throw exceptions
        module.onStart()
        module.onStop()
        
        // Add more specific tests based on your module's lifecycle behavior
    }
}
