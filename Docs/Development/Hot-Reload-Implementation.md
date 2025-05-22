# Hot-Reload System Implementation Guide

This document provides detailed implementation guidelines for the hot-reload system of LSPosedKit. This feature enables rapid development by allowing module changes to be applied without requiring a device reboot.

## Directory Structure

Create the following files in the `framework/hot/src/main/java/com/wobbz/framework/hot` directory:

```
framework/hot/src/main/java/com/wobbz/framework/hot/
├── IHotReloadable.kt
├── HotReloadManager.kt
├── DexPatcher.kt
├── server/
│   ├── DevServer.kt
│   └── DevServerService.kt
├── client/
│   ├── HotReloadClient.kt
│   └── DexReloader.kt
├── utils/
│   ├── ArtUtils.kt
│   └── NetworkUtils.kt
└── exceptions/
    └── HotReloadException.kt
```

## Gradle Configuration

First, configure the `framework/hot/build.gradle` file with the necessary dependencies:

```groovy
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.wobbz.framework.hot'
    compileSdk rootProject.ext.compileSdk
    
    defaultConfig {
        minSdk rootProject.ext.minSdk
        targetSdk rootProject.ext.targetSdk
        
        consumerProguardFiles "consumer-rules.pro"
    }
    
    compileOptions {
        sourceCompatibility rootProject.ext.javaVersion
        targetCompatibility rootProject.ext.javaVersion
    }
    
    kotlinOptions {
        jvmTarget = rootProject.ext.javaVersion.toString()
    }
}

dependencies {
    implementation project(':framework:core')
    implementation "org.jetbrains.kotlin:kotlin-stdlib:${rootProject.ext.kotlinVersion}"
    implementation 'androidx.core:core-ktx:1.10.0'
    implementation 'androidx.lifecycle:lifecycle-process:2.6.0'
    
    // Network
    implementation 'com.squareup.okio:okio:3.3.0'
    implementation 'com.squareup.okhttp3:okhttp:4.10.0'
}
```

## Core Components Implementation

### IHotReloadable.kt

This interface must be implemented by modules that support hot-reload:

```kotlin
package com.wobbz.framework.hot

/**
 * Interface for modules that support hot-reload capability.
 * Modules implementing this interface should be annotated with @HotReloadable.
 */
interface IHotReloadable {
    /**
     * Called when a hot-reload is triggered for this module.
     * Implementations should clean up any active hooks.
     */
    fun onHotReload()
}
```

### HotReloadManager.kt

Manages the hot-reload process:

```kotlin
package com.wobbz.framework.hot

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.wobbz.framework.core.LogLevel
import com.wobbz.framework.hot.client.DexReloader
import com.wobbz.framework.hot.client.HotReloadClient
import com.wobbz.framework.hot.exceptions.HotReloadException
import com.wobbz.framework.hot.utils.NetworkUtils
import java.io.File
import java.util.concurrent.Executors

/**
 * Manages the hot-reload process for LSPosed modules.
 */
class HotReloadManager private constructor(private val context: Context) {
    
    private val mainHandler = Handler(Looper.getMainLooper())
    private val executorService = Executors.newSingleThreadExecutor()
    private val client = HotReloadClient(context)
    private val dexReloader = DexReloader(context)
    
    private val modules = mutableMapOf<String, IHotReloadable>()
    
    /**
     * Registers a module for hot-reload capability.
     * @param moduleId The module identifier
     * @param module The module instance that supports hot-reload
     */
    fun registerModule(moduleId: String, module: IHotReloadable) {
        modules[moduleId] = module
    }
    
    /**
     * Starts the hot-reload monitoring service.
     */
    fun startMonitoring() {
        if (modules.isEmpty()) {
            logError("No hot-reloadable modules registered")
            return
        }
        
        executorService.submit {
            try {
                client.startListening { moduleId, dexBytes ->
                    handleHotReload(moduleId, dexBytes)
                }
            } catch (e: Exception) {
                logError("Failed to start hot-reload monitoring", e)
            }
        }
        
        log(LogLevel.INFO, "Hot-reload monitoring started")
    }
    
    /**
     * Stops the hot-reload monitoring service.
     */
    fun stopMonitoring() {
        client.stopListening()
        log(LogLevel.INFO, "Hot-reload monitoring stopped")
    }
    
    /**
     * Triggers a hot-reload for a specific module.
     * @param moduleId The module identifier
     * @param dexBytes The new DEX bytecode
     */
    private fun handleHotReload(moduleId: String, dexBytes: ByteArray) {
        val module = modules[moduleId]
        if (module == null) {
            logError("Module not found: $moduleId")
            return
        }
        
        try {
            // Save DEX to temporary file
            val dexFile = File(context.cacheDir, "$moduleId.dex")
            dexFile.writeBytes(dexBytes)
            
            // Patch the DEX in memory
            mainHandler.post {
                try {
                    // Call onHotReload to clean up existing hooks
                    module.onHotReload()
                    
                    // Reload the DEX
                    dexReloader.reloadDex(moduleId, dexFile)
                    
                    log(LogLevel.INFO, "Hot-reloaded module: $moduleId")
                } catch (e: Exception) {
                    logError("Failed to hot-reload module: $moduleId", e)
                } finally {
                    // Clean up temporary file
                    dexFile.delete()
                }
            }
        } catch (e: Exception) {
            logError("Error handling hot-reload for module: $moduleId", e)
        }
    }
    
    /**
     * Logs a message with the specified level.
     */
    private fun log(level: LogLevel, message: String) {
        // Implementation would use the logging framework
        android.util.Log.println(
            when (level) {
                LogLevel.VERBOSE -> android.util.Log.VERBOSE
                LogLevel.DEBUG -> android.util.Log.DEBUG
                LogLevel.INFO -> android.util.Log.INFO
                LogLevel.WARN -> android.util.Log.WARN
                LogLevel.ERROR -> android.util.Log.ERROR
            },
            "LSPK-HotReload",
            message
        )
    }
    
    /**
     * Logs an error message with an exception.
     */
    private fun logError(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            android.util.Log.e("LSPK-HotReload", message, throwable)
        } else {
            android.util.Log.e("LSPK-HotReload", message)
        }
    }
    
    companion object {
        @Volatile
        private var instance: HotReloadManager? = null
        
        /**
         * Gets the singleton instance of the HotReloadManager.
         */
        fun getInstance(context: Context): HotReloadManager {
            return instance ?: synchronized(this) {
                instance ?: HotReloadManager(context.applicationContext).also { instance = it }
            }
        }
        
        /**
         * Checks if hot-reload is supported on this device.
         */
        fun isSupported(): Boolean {
            return DexPatcher.isSupported()
        }
    }
}
```

### DexPatcher.kt

Handles the DEX patching process:

```kotlin
package com.wobbz.framework.hot

import android.os.Build
import com.wobbz.framework.hot.exceptions.HotReloadException
import com.wobbz.framework.hot.utils.ArtUtils
import dalvik.system.DexClassLoader
import java.io.File
import java.lang.reflect.Method

/**
 * Provides DEX patching capabilities for hot-reload.
 */
object DexPatcher {
    
    /**
     * Checks if DEX patching is supported on this device.
     * @return true if supported
     */
    fun isSupported(): Boolean {
        // Check Android version - minimum API 31 (Android 12)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return false
        }
        
        // Check if we can access the runtime patching API
        return try {
            Class.forName("dalvik.system.DexFile")
            Class.forName("dalvik.system.VMRuntime")
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Patches a DEX file into the running application.
     * @param dexFile The DEX file to patch
     * @param classLoader The ClassLoader to use
     */
    fun patchDex(dexFile: File, classLoader: ClassLoader) {
        if (!isSupported()) {
            throw HotReloadException("DEX patching is not supported on this device")
        }
        
        try {
            // For Android 12+, we can use a more direct approach
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                patchDexAndroid12Plus(dexFile, classLoader)
            } else {
                throw HotReloadException("Unsupported Android version")
            }
        } catch (e: Exception) {
            throw HotReloadException("Failed to patch DEX", e)
        }
    }
    
    /**
     * Patches a DEX file on Android 12 and newer.
     */
    private fun patchDexAndroid12Plus(dexFile: File, classLoader: ClassLoader) {
        // Implementation would use ART's internal APIs for DEX patching
        // This is a simplified representation - actual implementation would use
        // reflection to access ART's DEX patching APIs
        
        try {
            // Access ART runtime
            val vmRuntimeClass = Class.forName("dalvik.system.VMRuntime")
            val getRuntime = vmRuntimeClass.getDeclaredMethod("getRuntime")
            getRuntime.isAccessible = true
            val runtime = getRuntime.invoke(null)
            
            // Get the DEX patching method
            val patchDexMethod = vmRuntimeClass.getDeclaredMethod(
                "patchDex",
                File::class.java,
                ClassLoader::class.java
            )
            patchDexMethod.isAccessible = true
            
            // Patch the DEX
            patchDexMethod.invoke(runtime, dexFile, classLoader)
        } catch (e: Exception) {
            throw HotReloadException("Failed to patch DEX using Android 12+ API", e)
        }
    }
}
```

## Server Implementation

### DevServer.kt

Development server that runs on the developer's machine:

```kotlin
package com.wobbz.framework.hot.server

import java.io.File
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

/**
 * Development server for hot-reload functionality.
 * This server runs on the development machine and sends updated DEX files to the device.
 */
class DevServer(private val port: Int = 5795) {
    
    private val executorService = Executors.newCachedThreadPool()
    private var serverSocket: ServerSocket? = null
    private var isRunning = false
    
    /**
     * Starts the development server.
     */
    fun start() {
        if (isRunning) {
            println("[LSPK-DevServer] Server is already running")
            return
        }
        
        try {
            serverSocket = ServerSocket(port)
            isRunning = true
            
            println("[LSPK-DevServer] Server started on port $port")
            
            executorService.submit {
                while (isRunning) {
                    try {
                        val clientSocket = serverSocket?.accept() ?: break
                        handleClient(clientSocket)
                    } catch (e: Exception) {
                        if (isRunning) {
                            println("[LSPK-DevServer] Error accepting client: ${e.message}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("[LSPK-DevServer] Failed to start server: ${e.message}")
        }
    }
    
    /**
     * Stops the development server.
     */
    fun stop() {
        isRunning = false
        serverSocket?.close()
        executorService.shutdownNow()
        println("[LSPK-DevServer] Server stopped")
    }
    
    /**
     * Handles a client connection.
     */
    private fun handleClient(clientSocket: Socket) {
        executorService.submit {
            try {
                val input = clientSocket.getInputStream()
                val output = clientSocket.getOutputStream()
                
                // Read command
                val command = input.bufferedReader().readLine()
                
                when {
                    command.startsWith("GET_DEX ") -> {
                        val parts = command.split(" ")
                        if (parts.size >= 3) {
                            val moduleId = parts[1]
                            val dexPath = parts[2]
                            sendDexToClient(moduleId, dexPath, output)
                        }
                    }
                    command == "PING" -> {
                        output.write("PONG\n".toByteArray())
                        output.flush()
                    }
                    else -> {
                        println("[LSPK-DevServer] Unknown command: $command")
                    }
                }
                
                clientSocket.close()
            } catch (e: Exception) {
                println("[LSPK-DevServer] Error handling client: ${e.message}")
            }
        }
    }
    
    /**
     * Sends a DEX file to the client.
     */
    private fun sendDexToClient(moduleId: String, dexPath: String, output: java.io.OutputStream) {
        try {
            val dexFile = File(dexPath)
            if (!dexFile.exists() || !dexFile.isFile) {
                output.write("ERROR DEX_NOT_FOUND\n".toByteArray())
                output.flush()
                return
            }
            
            val dexBytes = dexFile.readBytes()
            
            // Send success header
            output.write("DEX ${dexBytes.size}\n".toByteArray())
            output.flush()
            
            // Send DEX bytes
            output.write(dexBytes)
            output.flush()
            
            println("[LSPK-DevServer] Sent DEX for module $moduleId (${dexBytes.size} bytes)")
        } catch (e: Exception) {
            println("[LSPK-DevServer] Error sending DEX: ${e.message}")
            output.write("ERROR ${e.message}\n".toByteArray())
            output.flush()
        }
    }
}
```

### DevServerService.kt

Service that manages the development server:

```kotlin
package com.wobbz.framework.hot.server

import java.io.File

/**
 * Service class for running the development server as a Gradle task.
 */
class DevServerService {
    
    private var devServer: DevServer? = null
    
    /**
     * Starts the development server.
     * @param port The port to use
     */
    fun startServer(port: Int = 5795) {
        devServer = DevServer(port)
        devServer?.start()
    }
    
    /**
     * Stops the development server.
     */
    fun stopServer() {
        devServer?.stop()
        devServer = null
    }
    
    /**
     * Main entry point when running as a standalone application.
     */
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val port = args.getOrNull(0)?.toIntOrNull() ?: 5795
            
            println("[LSPK-DevServer] Starting development server on port $port")
            println("[LSPK-DevServer] Press Ctrl+C to stop the server")
            
            val service = DevServerService()
            service.startServer(port)
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(Thread {
                service.stopServer()
            })
            
            // Keep the main thread alive
            try {
                Thread.currentThread().join()
            } catch (e: InterruptedException) {
                service.stopServer()
            }
        }
    }
}
```

## Client Implementation

### HotReloadClient.kt

Client for communicating with the development server:

```kotlin
package com.wobbz.framework.hot.client

import android.content.Context
import com.wobbz.framework.hot.exceptions.HotReloadException
import com.wobbz.framework.hot.utils.NetworkUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Client for communicating with the development server.
 */
class HotReloadClient(private val context: Context) {
    
    private val executorService = Executors.newSingleThreadExecutor()
    private val isListening = AtomicBoolean(false)
    
    /**
     * Starts listening for DEX updates from the development server.
     * @param onDexReceived Callback function that receives module ID and DEX bytes
     */
    fun startListening(onDexReceived: (String, ByteArray) -> Unit) {
        if (isListening.getAndSet(true)) {
            return
        }
        
        executorService.submit {
            var retryCount = 0
            val maxRetries = 3
            
            while (isListening.get() && retryCount < maxRetries) {
                try {
                    // Try to connect to dev server
                    val serverAddress = NetworkUtils.getDevServerAddress(context)
                    val serverPort = 5795 // Default dev server port
                    
                    pollForUpdates(serverAddress, serverPort, onDexReceived)
                    
                    // Reset retry count on successful connection
                    retryCount = 0
                } catch (e: Exception) {
                    retryCount++
                    android.util.Log.e(
                        "LSPK-HotReload",
                        "Error connecting to dev server (retry $retryCount/$maxRetries): ${e.message}"
                    )
                    
                    // Sleep before retry
                    Thread.sleep(5000)
                }
            }
            
            isListening.set(false)
        }
    }
    
    /**
     * Stops listening for DEX updates.
     */
    fun stopListening() {
        isListening.set(false)
        executorService.shutdownNow()
    }
    
    /**
     * Polls the development server for updates.
     */
    private fun pollForUpdates(
        serverAddress: String,
        serverPort: Int,
        onDexReceived: (String, ByteArray) -> Unit
    ) {
        while (isListening.get()) {
            try {
                // Check if server is available
                if (pingServer(serverAddress, serverPort)) {
                    // Get list of modules that support hot-reload
                    val modules = getHotReloadableModules()
                    
                    // Check for updates for each module
                    for (moduleId in modules) {
                        checkForUpdate(serverAddress, serverPort, moduleId, onDexReceived)
                    }
                }
                
                // Wait before polling again
                Thread.sleep(2000)
            } catch (e: Exception) {
                android.util.Log.e("LSPK-HotReload", "Error polling for updates: ${e.message}")
                Thread.sleep(5000)
            }
        }
    }
    
    /**
     * Pings the development server to check if it's available.
     */
    private fun pingServer(serverAddress: String, serverPort: Int): Boolean {
        try {
            Socket(serverAddress, serverPort).use { socket ->
                val output = PrintWriter(socket.getOutputStream(), true)
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                
                output.println("PING")
                val response = input.readLine()
                
                return response == "PONG"
            }
        } catch (e: Exception) {
            return false
        }
    }
    
    /**
     * Gets the list of modules that support hot-reload.
     */
    private fun getHotReloadableModules(): List<String> {
        // Implementation would query the module registry
        // For now, just return a sample list
        return listOf("debug-app", "network-guard")
    }
    
    /**
     * Checks for an update for a specific module.
     */
    private fun checkForUpdate(
        serverAddress: String,
        serverPort: Int,
        moduleId: String,
        onDexReceived: (String, ByteArray) -> Unit
    ) {
        try {
            Socket(serverAddress, serverPort).use { socket ->
                val output = PrintWriter(socket.getOutputStream(), true)
                val input = socket.getInputStream()
                
                // Get current module version hash
                val moduleHash = getModuleHash(moduleId)
                
                // Request DEX for module
                output.println("GET_DEX $moduleId $moduleHash")
                
                // Read response
                val response = BufferedReader(InputStreamReader(input)).readLine() ?: return
                
                if (response.startsWith("DEX ")) {
                    val dexSize = response.substring(4).toInt()
                    val dexBytes = ByteArray(dexSize)
                    
                    // Read DEX bytes
                    var totalRead = 0
                    while (totalRead < dexSize) {
                        val bytesRead = input.read(dexBytes, totalRead, dexSize - totalRead)
                        if (bytesRead == -1) {
                            break
                        }
                        totalRead += bytesRead
                    }
                    
                    if (totalRead == dexSize) {
                        // Process the DEX
                        onDexReceived(moduleId, dexBytes)
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e(
                "LSPK-HotReload",
                "Error checking for update for module $moduleId: ${e.message}"
            )
        }
    }
    
    /**
     * Gets the hash of the current module DEX.
     */
    private fun getModuleHash(moduleId: String): String {
        // Implementation would calculate a hash of the current module DEX
        // For now, just return a placeholder
        return "current_hash"
    }
}
```

### DexReloader.kt

Handles DEX reloading for modules:

```kotlin
package com.wobbz.framework.hot.client

import android.content.Context
import com.wobbz.framework.hot.DexPatcher
import com.wobbz.framework.hot.exceptions.HotReloadException
import dalvik.system.PathClassLoader
import java.io.File

/**
 * Handles DEX reloading for modules.
 */
class DexReloader(private val context: Context) {
    
    /**
     * Reloads a DEX file for a module.
     * @param moduleId The module identifier
     * @param dexFile The DEX file to reload
     */
    fun reloadDex(moduleId: String, dexFile: File) {
        try {
            // Get the module's ClassLoader
            val classLoader = getModuleClassLoader(moduleId)
            
            // Patch the DEX
            DexPatcher.patchDex(dexFile, classLoader)
            
            android.util.Log.i("LSPK-HotReload", "Reloaded DEX for module $moduleId")
        } catch (e: Exception) {
            throw HotReloadException("Failed to reload DEX for module $moduleId", e)
        }
    }
    
    /**
     * Gets the ClassLoader for a module.
     */
    private fun getModuleClassLoader(moduleId: String): ClassLoader {
        // Implementation would get the actual ClassLoader for the module
        // For now, just return the context ClassLoader
        return context.classLoader
    }
}
```

## Utility Classes

### ArtUtils.kt

Utilities for interacting with Android's ART runtime:

```kotlin
package com.wobbz.framework.hot.utils

import android.os.Build

/**
 * Utilities for interacting with Android's ART runtime.
 */
object ArtUtils {
    
    /**
     * Checks if DEX patching is supported on this device.
     */
    fun isDexPatchingSupported(): Boolean {
        // Check Android version - minimum API 31 (Android 12)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return false
        }
        
        // Check if we can access the runtime patching API
        return try {
            Class.forName("dalvik.system.DexFile")
            Class.forName("dalvik.system.VMRuntime")
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Gets the DEX path for a ClassLoader.
     */
    fun getDexPath(classLoader: ClassLoader): String? {
        return try {
            val pathClassLoaderClass = Class.forName("dalvik.system.PathClassLoader")
            val pathField = pathClassLoaderClass.getDeclaredField("path")
            pathField.isAccessible = true
            pathField.get(classLoader) as? String
        } catch (e: Exception) {
            null
        }
    }
}
```

### NetworkUtils.kt

Utilities for network operations:

```kotlin
package com.wobbz.framework.hot.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import java.net.InetAddress
import java.net.NetworkInterface

/**
 * Utilities for network operations.
 */
object NetworkUtils {
    
    /**
     * Gets the development server address.
     * This could be a local address or a remote address via ADB port forwarding.
     */
    fun getDevServerAddress(context: Context): String {
        // First, try localhost (assuming ADB port forwarding is set up)
        if (isPortOpen("127.0.0.1", 5795)) {
            return "127.0.0.1"
        }
        
        // Next, try direct Wi-Fi connection
        val wifiAddress = getWifiAddress(context)
        if (wifiAddress != null && isPortOpen(wifiAddress, 5795)) {
            return wifiAddress
        }
        
        // Fall back to localhost
        return "127.0.0.1"
    }
    
    /**
     * Gets the Wi-Fi address of the device.
     */
    private fun getWifiAddress(context: Context): String? {
        try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ipAddress = wifiInfo.ipAddress
            
            return String.format(
                "%d.%d.%d.%d",
                (ipAddress and 0xff),
                (ipAddress shr 8 and 0xff),
                (ipAddress shr 16 and 0xff),
                (ipAddress shr 24 and 0xff)
            )
        } catch (e: Exception) {
            return null
        }
    }
    
    /**
     * Checks if a port is open on a host.
     */
    private fun isPortOpen(host: String, port: Int): Boolean {
        return try {
            java.net.Socket(host, port).use { socket ->
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}
```

### HotReloadException.kt

Exception for hot-reload errors:

```kotlin
package com.wobbz.framework.hot.exceptions

/**
 * Exception thrown for hot-reload errors.
 */
class HotReloadException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
```

## Gradle Task Implementation

Create a Gradle task to run the development server:

```kotlin
// Add to framework/hot/build.gradle

// Create a task to run the development server
tasks.register("runDevServer", JavaExec::class) {
    group = "LSPosedKit"
    description = "Runs the hot-reload development server"
    
    mainClass.set("com.wobbz.framework.hot.server.DevServerService")
    classpath = sourceSets.main.get().runtimeClasspath
    
    // Allow command-line arguments to be passed
    args = listOfNotNull(project.findProperty("port")?.toString())
}
```

## Integration with the Framework

To integrate the hot-reload system with the rest of the framework:

1. Update the `framework/build.gradle` file to include the hot-reload module:

```groovy
dependencies {
    api project(':framework:core')
    api project(':framework:processor')
    api project(':framework:hot')
    api project(':framework:settings')
    api project(':libxposed-api:api')
}
```

2. Initialize the hot-reload system in the application's entry point:

```kotlin
// In the main module class or application class
if (HotReloadManager.isSupported()) {
    val hotReloadManager = HotReloadManager.getInstance(context)
    
    // Register hot-reloadable modules
    for (module in modules) {
        if (module is IHotReloadable) {
            hotReloadManager.registerModule(module.getId(), module)
        }
    }
    
    // Start monitoring for updates
    hotReloadManager.startMonitoring()
}
```

## Testing

To test the hot-reload system:

1. Start the development server:

```bash
./gradlew :framework:hot:runDevServer
```

2. Set up ADB port forwarding:

```bash
adb forward tcp:5795 tcp:5795
```

3. Build and install a module with hot-reload support:

```bash
./gradlew :modules:debug-app:installDebug
```

4. Make changes to the module and rebuild:

```bash
./gradlew :modules:debug-app:assembleDebug
```

5. Verify that the changes are hot-reloaded on the device by checking the logs:

```bash
adb logcat -s LSPK-HotReload:*
```

## Next Steps

After implementing the hot-reload system:

1. Implement the settings management system
2. Build the FeatureManager service bus
3. Create sample modules that utilize hot-reload
4. Implement unit tests to verify the hot-reload functionality 