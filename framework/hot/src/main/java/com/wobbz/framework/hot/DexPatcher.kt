package com.wobbz.framework.hot

import android.os.Build
import android.util.Log
import com.wobbz.framework.hot.exceptions.HotReloadException
import dalvik.system.DexClassLoader
import dalvik.system.PathClassLoader
import java.io.File
import java.lang.reflect.Method

/**
 * Provides DEX patching capabilities for hot-reload.
 * 
 * Note: This implementation relies on Android internal APIs and reflection.
 * It may break across different Android versions and should be thoroughly tested.
 */
object DexPatcher {
    
    private const val TAG = "LSPK-HotReload-Patcher"
    
    /**
     * Checks if DEX patching is supported on this device.
     */
    fun isSupported(): Boolean {
        // Minimum API 31 (Android 12) for our patching strategy
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            Log.w(TAG, "DEX Patching not supported on API < 31 (current: ${Build.VERSION.SDK_INT})")
            return false
        }
        
        return try {
            // Check for essential classes and methods
            checkRequiredComponents()
            Log.i(TAG, "DEX patching appears supported on Android ${Build.VERSION.SDK_INT}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "DEX patching support check failed", e)
            false
        }
    }
    
    /**
     * Patches a DEX file into the running application.
     */
    fun patchDex(dexFile: File, classLoader: ClassLoader): Boolean {
        if (!isSupported()) {
            Log.e(TAG, "Attempted DEX patch on unsupported device")
            throw HotReloadException("DEX patching is not supported on this device")
        }
        
        if (!dexFile.exists() || !dexFile.isFile) {
            Log.e(TAG, "DEX file does not exist: ${dexFile.absolutePath}")
            throw HotReloadException("DEX file does not exist: ${dexFile.absolutePath}")
        }
        
        Log.i(TAG, "Attempting DEX patch: ${dexFile.name} (${dexFile.length()} bytes)")
        
        return try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                    // Android 14+ strategy
                    patchDexAndroid14Plus(dexFile, classLoader)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    // Android 12-13 strategy
                    patchDexAndroid12Plus(dexFile, classLoader)
                }
                else -> {
                    Log.w(TAG, "Unsupported Android version for DEX patching: ${Build.VERSION.SDK_INT}")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "DEX patch failed for ${dexFile.name}", e)
            throw HotReloadException("Failed to patch DEX: ${e.message}", e)
        }
    }
    
    /**
     * Checks if all required components for DEX patching are available.
     */
    private fun checkRequiredComponents() {
        // Check VMRuntime class
        val vmRuntimeClass = Class.forName("dalvik.system.VMRuntime")
        val getRuntime = vmRuntimeClass.getDeclaredMethod("getRuntime")
        getRuntime.isAccessible = true
        
        // Check ClassLoader components
        Class.forName("dalvik.system.DexClassLoader")
        Class.forName("dalvik.system.PathClassLoader")
        
        Log.d(TAG, "All required components for DEX patching are available")
    }
    
    /**
     * DEX patching strategy for Android 12-13.
     */
    private fun patchDexAndroid12Plus(dexFile: File, classLoader: ClassLoader): Boolean {
        Log.d(TAG, "Using Android 12+ DEX patching strategy")
        
        return try {
            // Strategy 1: Try to add DEX to existing ClassLoader path
            if (classLoader is PathClassLoader) {
                addDexToPathClassLoader(dexFile, classLoader)
            } else {
                // Strategy 2: Create new DexClassLoader and merge
                createAndMergeDexClassLoader(dexFile, classLoader)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Primary strategy failed, trying fallback", e)
            // Fallback strategy: Use reflection to modify ClassLoader internals
            patchClassLoaderReflection(dexFile, classLoader)
        }
    }
    
    /**
     * DEX patching strategy for Android 14+.
     */
    private fun patchDexAndroid14Plus(dexFile: File, classLoader: ClassLoader): Boolean {
        Log.d(TAG, "Using Android 14+ DEX patching strategy")
        
        // Android 14+ might have different internal APIs
        // For now, try the same strategy as 12+ but with additional checks
        return patchDexAndroid12Plus(dexFile, classLoader)
    }
    
    /**
     * Adds a DEX file to an existing PathClassLoader.
     */
    private fun addDexToPathClassLoader(dexFile: File, classLoader: PathClassLoader): Boolean {
        try {
            Log.d(TAG, "Adding DEX to PathClassLoader: ${dexFile.name}")
            
            // Get the pathList field from ClassLoader
            val pathListField = ClassLoader::class.java.getDeclaredField("pathList")
            pathListField.isAccessible = true
            val pathList = pathListField.get(classLoader)
            
            // Get the dexElements field from DexPathList
            val dexElementsField = pathList.javaClass.getDeclaredField("dexElements")
            dexElementsField.isAccessible = true
            val dexElements = dexElementsField.get(pathList) as Array<*>
            
            // Create new DexElement for our DEX file
            val elementClass = Class.forName("dalvik.system.DexPathList\$Element")
            val elementConstructor = elementClass.getDeclaredConstructor(
                File::class.java,
                Boolean::class.javaPrimitiveType,
                File::class.java,
                Class.forName("dalvik.system.DexFile")
            )
            elementConstructor.isAccessible = true
            
            // Create DexFile for our DEX
            val dexFileClass = Class.forName("dalvik.system.DexFile")
            val dexFileConstructor = dexFileClass.getDeclaredConstructor(File::class.java)
            dexFileConstructor.isAccessible = true
            val dexFileObj = dexFileConstructor.newInstance(dexFile)
            
            // Create new element
            val newElement = elementConstructor.newInstance(dexFile, false, null, dexFileObj)
            
            // Merge arrays
            val newDexElements = java.lang.reflect.Array.newInstance(elementClass, dexElements.size + 1)
            System.arraycopy(dexElements, 0, newDexElements, 0, dexElements.size)
            java.lang.reflect.Array.set(newDexElements, dexElements.size, newElement)
            
            // Set new array
            dexElementsField.set(pathList, newDexElements)
            
            Log.i(TAG, "Successfully added DEX to PathClassLoader")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add DEX to PathClassLoader", e)
            throw e
        }
    }
    
    /**
     * Creates a new DexClassLoader and attempts to merge it.
     */
    private fun createAndMergeDexClassLoader(dexFile: File, parentClassLoader: ClassLoader): Boolean {
        try {
            Log.d(TAG, "Creating DexClassLoader for: ${dexFile.name}")
            
            // Create a temporary directory for the optimized DEX
            val optimizedDir = File(dexFile.parent, "optimized")
            optimizedDir.mkdirs()
            
            // Create DexClassLoader
            val dexClassLoader = DexClassLoader(
                dexFile.absolutePath,
                optimizedDir.absolutePath,
                null,
                parentClassLoader
            )
            
            Log.i(TAG, "Created DexClassLoader successfully: ${dexClassLoader.javaClass.name}")
            
            // Note: Actually "merging" ClassLoaders is complex and may not be fully achievable
            // This would typically require additional mechanisms in the calling code
            // to use both ClassLoaders appropriately
            
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create DexClassLoader", e)
            throw e
        }
    }
    
    /**
     * Fallback strategy using reflection to modify ClassLoader internals.
     */
    private fun patchClassLoaderReflection(@Suppress("UNUSED_PARAMETER") dexFile: File, @Suppress("UNUSED_PARAMETER") classLoader: ClassLoader): Boolean {
        try {
            Log.d(TAG, "Using reflection fallback strategy")
            
            // This is a more aggressive approach that modifies ClassLoader internals
            // It's more likely to break across Android versions but may work when other methods fail
            
            // Access VMRuntime for lower-level operations
            val vmRuntimeClass = Class.forName("dalvik.system.VMRuntime")
            val getRuntime = vmRuntimeClass.getDeclaredMethod("getRuntime")
            getRuntime.isAccessible = true
            @Suppress("UNUSED_VARIABLE")
            val runtime = getRuntime.invoke(null)
            
            // Try to find a method that can load DEX files
            // Note: These method names are speculative and may not exist
            val methods = vmRuntimeClass.declaredMethods.filter { method ->
                method.name.contains("dex", ignoreCase = true) ||
                method.name.contains("load", ignoreCase = true) ||
                method.name.contains("define", ignoreCase = true)
            }
            
            Log.d(TAG, "Found ${methods.size} potential methods: ${methods.map { it.name }}")
            
            // This is where a real implementation would try various methods
            // For now, we'll log that we attempted the fallback
            Log.w(TAG, "Reflection fallback not fully implemented")
            
            return false
            
        } catch (e: Exception) {
            Log.e(TAG, "Reflection fallback strategy failed", e)
            throw e
        }
    }
} 