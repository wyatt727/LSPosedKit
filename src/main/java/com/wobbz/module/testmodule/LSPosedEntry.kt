package com.wobbz.module.testmodule

import io.github.libxposed.api.XposedModuleInterface
import com.wobbz.framework.core.PackageLoadedParam
import com.wobbz.framework.core.XposedInterfaceImpl

/**
 * LSPosed entry point for TestModule.
 * This class implements the standard LSPosed interfaces and bridges to our LSPosedKit framework.
 */
class LSPosedEntry : XposedModuleInterface {
    
    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        try {
            // Create our framework module
            val module = TestModule()
            
            // Create a minimal XposedInterface implementation for the bridge
            val xposedInterface = XposedInterfaceImpl(
                libXposedInterface = object : io.github.libxposed.api.XposedInterface {
                    override fun getFrameworkName(): String = "LSPosedKit"
                    override fun getFrameworkVersion(): String = "1.0.0"
                    override fun getFrameworkVersionCode(): Long = 100
                    override fun getFrameworkPrivilege(): Int = io.github.libxposed.api.XposedInterface.FRAMEWORK_PRIVILEGE_APP
                    override fun hook(origin: java.lang.reflect.Method, hooker: Class<out io.github.libxposed.api.XposedInterface.Hooker>) = throw UnsupportedOperationException("Not implemented")
                    override fun hookClassInitializer(origin: Class<*>, hooker: Class<out io.github.libxposed.api.XposedInterface.Hooker>) = throw UnsupportedOperationException("Not implemented")
                    override fun hookClassInitializer(origin: Class<*>, priority: Int, hooker: Class<out io.github.libxposed.api.XposedInterface.Hooker>) = throw UnsupportedOperationException("Not implemented")
                    override fun hook(origin: java.lang.reflect.Method, priority: Int, hooker: Class<out io.github.libxposed.api.XposedInterface.Hooker>) = throw UnsupportedOperationException("Not implemented")
                    override fun hook(origin: java.lang.reflect.Constructor<*>, hooker: Class<out io.github.libxposed.api.XposedInterface.Hooker>) = throw UnsupportedOperationException("Not implemented")
                    override fun hook(origin: java.lang.reflect.Constructor<*>, priority: Int, hooker: Class<out io.github.libxposed.api.XposedInterface.Hooker>) = throw UnsupportedOperationException("Not implemented")
                    override fun deoptimize(method: java.lang.reflect.Method): Boolean = false
                    override fun deoptimize(constructor: java.lang.reflect.Constructor<*>): Boolean = false
                    override fun invokeOrigin(method: java.lang.reflect.Method, thisObject: Any?, vararg args: Any?): Any? = throw UnsupportedOperationException("Not implemented")
                    override fun invokeOrigin(constructor: java.lang.reflect.Constructor<*>, thisObject: Any, vararg args: Any?) = throw UnsupportedOperationException("Not implemented")
                    override fun invokeSpecial(method: java.lang.reflect.Method, thisObject: Any, vararg args: Any?): Any? = throw UnsupportedOperationException("Not implemented")
                    override fun invokeSpecial(constructor: java.lang.reflect.Constructor<*>, thisObject: Any, vararg args: Any?) = throw UnsupportedOperationException("Not implemented")
                    override fun newInstanceOrigin(constructor: java.lang.reflect.Constructor<*>, vararg args: Any?): Any = throw UnsupportedOperationException("Not implemented")
                    override fun newInstanceSpecial(constructor: java.lang.reflect.Constructor<*>, subClass: Class<*>, vararg args: Any?): Any = throw UnsupportedOperationException("Not implemented")
                    override fun log(message: String) = android.util.Log.i("LSPosedKit-TestModule", message)
                    override fun log(message: String, throwable: Throwable) = android.util.Log.e("LSPosedKit-TestModule", message, throwable)
                    override fun parseDex(dexData: java.nio.ByteBuffer, includeAnnotations: Boolean) = null
                    override fun getApplicationInfo() = param.getApplicationInfo()
                    override fun getRemotePreferences(group: String) = throw UnsupportedOperationException("Not implemented")
                    override fun listRemoteFiles(): Array<String> = emptyArray()
                    override fun openRemoteFile(name: String) = throw UnsupportedOperationException("Not implemented")
                },
                packageName = param.getPackageName()
            )
            
            // Create LSPosedKit PackageLoadedParam from libxposed param
            val lspkParam = PackageLoadedParam(
                packageName = param.getPackageName(),
                classLoader = param.getClassLoader(),
                xposed = xposedInterface,
                isSystemApp = param.getApplicationInfo().flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM != 0,
                appInfo = param.getApplicationInfo()
            )
            
            // Initialize and call the module directly
            try {
                module.initialize(
                    context = android.app.Application(), // Placeholder context
                    xposed = xposedInterface
                )
            } catch (e: Exception) {
                android.util.Log.w("LSPosedKit-TestModule", "Module initialization failed, continuing anyway", e)
            }
            
            module.onPackageLoaded(lspkParam)
            
        } catch (e: Exception) {
            android.util.Log.e("LSPosedKit-TestModule", "Error in onPackageLoaded", e)
        }
    }
} 