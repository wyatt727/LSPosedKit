package com.wobbz.module.intentinterceptor

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for IntentInterceptor.
 */
class IntentInterceptorTest {
    
    private lateinit var module: IntentInterceptor
    
    @Before
    fun setup() {
        module = IntentInterceptor()
    }
    
    @Test
    fun testModuleCreation() {
        // Test that module can be created without throwing
        assertNotNull("Module should be created successfully", module)
    }
    
    @Test
    fun testLifecycleMethods() {
        // Test that lifecycle methods don't throw exceptions
        try {
            module.onStart()
            module.onStop()
            // If we get here, the methods executed without throwing
            assertTrue("Lifecycle methods should execute without exceptions", true)
        } catch (e: Exception) {
            fail("Lifecycle methods should not throw exceptions: ${e.message}")
        }
    }
    
    @Test
    fun testHotReload() {
        // Test that hot reload method doesn't throw exceptions
        try {
            module.onHotReload()
            // If we get here, the method executed without throwing
            assertTrue("Hot reload should execute without exceptions", true)
        } catch (e: Exception) {
            fail("Hot reload should not throw exceptions: ${e.message}")
        }
    }
    
    @Test
    fun testModuleImplementsInterfaces() {
        // Test that module implements required interfaces
        assertTrue("Module should implement IModulePlugin", 
            module is com.wobbz.framework.core.IModulePlugin)
        assertTrue("Module should implement IHotReloadable", 
            module is com.wobbz.framework.hot.IHotReloadable)
        assertTrue("Module should implement ModuleLifecycle", 
            module is com.wobbz.framework.core.ModuleLifecycle)
    }
}
