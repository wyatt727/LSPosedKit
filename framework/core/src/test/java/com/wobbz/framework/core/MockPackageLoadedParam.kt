package com.wobbz.framework.core

/**
 * Mock implementation for testing package loading behavior.
 * Provides a similar interface to PackageLoadedParam without inheritance.
 */
class MockPackageLoadedParam(
    val packageName: String,
    val classLoader: ClassLoader = MockPackageLoadedParam::class.java.classLoader!!,
    val xposed: XposedInterface = MockXposedInterface(),
    val isSystemApp: Boolean = false,
    val appInfo: android.content.pm.ApplicationInfo? = null
) {
    
    companion object {
        /**
         * Creates a mock param for testing with a specific package name.
         */
        fun forPackage(packageName: String): MockPackageLoadedParam {
            return MockPackageLoadedParam(packageName)
        }
        
        /**
         * Creates a mock param for testing with a custom XposedInterface.
         */
        fun forPackageWithXposed(packageName: String, xposed: XposedInterface): MockPackageLoadedParam {
            return MockPackageLoadedParam(packageName, xposed = xposed)
        }
        
        /**
         * Creates a mock param for testing with a custom ClassLoader.
         */
        fun forPackageWithClassLoader(packageName: String, classLoader: ClassLoader): MockPackageLoadedParam {
            return MockPackageLoadedParam(packageName, classLoader = classLoader)
        }
        
        /**
         * Creates a mock param that matches a real PackageLoadedParam for testing.
         */
        fun fromReal(param: PackageLoadedParam): MockPackageLoadedParam {
            return MockPackageLoadedParam(
                packageName = param.packageName,
                classLoader = param.classLoader,
                xposed = param.xposed,
                isSystemApp = param.isSystemApp,
                appInfo = param.appInfo
            )
        }
    }
} 