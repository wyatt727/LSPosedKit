package com.wobbz.framework.core

/**
 * Mock implementation of PackageLoadedParam for unit testing.
 * Allows testing of module package loading behavior without requiring the actual Xposed framework.
 */
class MockPackageLoadedParam(
    override val packageName: String,
    override val classLoader: ClassLoader = MockPackageLoadedParam::class.java.classLoader,
    override val xposed: XposedInterface = MockXposedInterface()
) : PackageLoadedParam {
    
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
    }
} 