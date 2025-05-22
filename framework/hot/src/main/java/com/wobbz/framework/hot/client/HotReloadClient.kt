package com.wobbz.framework.hot.client

import android.content.Context
import android.util.Log
import com.wobbz.framework.hot.exceptions.HotReloadException
import com.wobbz.framework.hot.utils.NetworkUtils
import java.io.*
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Client for communicating with the development server.
 * Receives hot-reload updates and DEX files.
 */
class HotReloadClient(private val context: Context) {
    
    private val TAG = "LSPK-HotReload-Client"
    private val scheduledExecutor: ScheduledExecutorService = Executors.newScheduledThreadPool(2)
    
    private var devServerHost: String? = null
    private var devServerPort: Int = 5795
    private var isListening = false
    private var connectionCheckInterval = 30L // seconds
    
    /**
     * Starts listening for hot-reload updates.
     */
    fun startListening(onDexReceived: (moduleId: String, dexBytes: ByteArray) -> Unit) {
        if (isListening) {
            Log.w(TAG, "Already listening for hot-reload updates")
            return
        }
        
        // Check network availability
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.w(TAG, "Network not available, cannot start hot-reload client")
            return
        }
        
        // Detect development server
        detectDevelopmentServer()
        
        if (devServerHost == null) {
            Log.w(TAG, "Could not detect development server, hot-reload will not be available")
            return
        }
        
        isListening = true
        Log.i(TAG, "Starting hot-reload client, connecting to $devServerHost:$devServerPort")
        
        // Start periodic connection check
        scheduledExecutor.scheduleWithFixedDelay({
            checkForUpdates(onDexReceived)
        }, 0, connectionCheckInterval, TimeUnit.SECONDS)
    }
    
    /**
     * Stops listening for hot-reload updates.
     */
    fun stopListening() {
        isListening = false
        scheduledExecutor.shutdownNow()
        Log.i(TAG, "Stopped hot-reload client")
    }
    
    /**
     * Detects the development server on the local network.
     */
    private fun detectDevelopmentServer() {
        try {
            // Try common development server locations
            val commonHosts = listOf(
                "10.0.2.2",      // Android emulator host
                "192.168.1.100", // Common dev machine IP
                "192.168.1.101",
                "192.168.0.100",
                "192.168.0.101",
                NetworkUtils.detectDevelopmentMachineIp()
            ).filterNotNull()
            
            for (host in commonHosts) {
                if (NetworkUtils.isHostReachable(host, devServerPort, 2000)) {
                    // Test connection with ping
                    if (pingServer(host)) {
                        devServerHost = host
                        Log.i(TAG, "Found development server at: $host:$devServerPort")
                        return
                    }
                }
            }
            
            Log.w(TAG, "No development server found on common hosts")
        } catch (e: Exception) {
            Log.e(TAG, "Error detecting development server", e)
        }
    }
    
    /**
     * Pings the development server to verify it's running.
     */
    private fun pingServer(host: String): Boolean {
        return try {
            Socket(host, devServerPort).use { socket ->
                val writer = PrintWriter(socket.getOutputStream(), true)
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                
                writer.println("PING")
                val response = reader.readLine()
                
                response == "PONG"
            }
        } catch (e: Exception) {
            Log.d(TAG, "Ping failed for $host:$devServerPort: ${e.message}")
            false
        }
    }
    
    /**
     * Checks for updates from the development server.
     */
    private fun checkForUpdates(onDexReceived: (moduleId: String, dexBytes: ByteArray) -> Unit) {
        if (!isListening) return
        
        val host = devServerHost ?: return
        
        try {
            // For now, we'll implement a simple polling mechanism
            // In a real implementation, this might use WebSockets or Server-Sent Events
            
            Socket(host, devServerPort).use { socket ->
                val writer = PrintWriter(socket.getOutputStream(), true)
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                
                // Request updates for all modules
                writer.println("CHECK_UPDATES")
                
                val response = reader.readLine()
                if (response?.startsWith("UPDATE_AVAILABLE") == true) {
                    val parts = response.split(" ")
                    if (parts.size >= 2) {
                        val moduleId = parts[1]
                        Log.i(TAG, "Update available for module: $moduleId")
                        requestModuleUpdate(moduleId, onDexReceived)
                    }
                }
            }
        } catch (e: Exception) {
            // Connection errors are expected when dev server is not running
            Log.d(TAG, "Connection check failed: ${e.message}")
        }
    }
    
    /**
     * Requests a module update from the development server.
     */
    private fun requestModuleUpdate(moduleId: String, onDexReceived: (moduleId: String, dexBytes: ByteArray) -> Unit) {
        val host = devServerHost ?: return
        
        try {
            Socket(host, devServerPort).use { socket ->
                val writer = PrintWriter(socket.getOutputStream(), true)
                val inputStream = socket.getInputStream()
                val reader = BufferedReader(InputStreamReader(inputStream))
                
                // Request DEX for specific module
                writer.println("GET_DEX $moduleId")
                
                val response = reader.readLine()
                when {
                    response?.startsWith("DEX") == true -> {
                        val parts = response.split(" ")
                        if (parts.size >= 2) {
                            val size = parts[1].toIntOrNull()
                            if (size != null && size > 0) {
                                // Read DEX bytes
                                val dexBytes = ByteArray(size)
                                var totalRead = 0
                                while (totalRead < size) {
                                    val read = inputStream.read(dexBytes, totalRead, size - totalRead)
                                    if (read == -1) break
                                    totalRead += read
                                }
                                
                                if (totalRead == size) {
                                    Log.i(TAG, "Received DEX for module $moduleId ($size bytes)")
                                    onDexReceived(moduleId, dexBytes)
                                } else {
                                    Log.e(TAG, "Incomplete DEX received for module $moduleId")
                                }
                            } else {
                                Log.w(TAG, "Invalid DEX size for module $moduleId")
                            }
                        } else {
                            Log.w(TAG, "Invalid DEX response format for module $moduleId")
                        }
                    }
                    response?.startsWith("ERROR") == true -> {
                        Log.e(TAG, "Server error: $response")
                    }
                    else -> {
                        Log.w(TAG, "Unexpected response: $response")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting module update for $moduleId", e)
        }
    }
    
    /**
     * Manually triggers a check for updates (for testing/debugging).
     */
    fun triggerUpdateCheck(moduleId: String, onDexReceived: (moduleId: String, dexBytes: ByteArray) -> Unit) {
        if (devServerHost == null) {
            Log.w(TAG, "No development server configured")
            return
        }
        
        Log.i(TAG, "Manual update check triggered for module: $moduleId")
        scheduledExecutor.submit {
            requestModuleUpdate(moduleId, onDexReceived)
        }
    }
    
    /**
     * Sets a custom development server address.
     */
    fun setDevelopmentServer(host: String, port: Int = 5795) {
        devServerHost = host
        devServerPort = port
        Log.i(TAG, "Development server set to: $host:$port")
    }
} 