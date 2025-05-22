package com.wobbz.framework.hot.server

import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * Development server for hot-reload functionality.
 * This server runs on the development machine and sends updated DEX files to devices.
 */
class DevServer(private val port: Int = 5795) {
    
    private val TAG = "LSPK-DevServer"
    private val executorService = Executors.newCachedThreadPool()
    private var serverSocket: ServerSocket? = null
    private var isRunning = false
    
    // Track connected clients
    private val connectedClients = ConcurrentHashMap<String, ClientInfo>()
    
    // Module DEX file tracking
    private val moduleDexFiles = ConcurrentHashMap<String, File>()
    private val moduleVersions = ConcurrentHashMap<String, Long>()
    
    /**
     * Starts the development server.
     */
    fun start() {
        if (isRunning) {
            println("[$TAG] Server is already running on port $port")
            return
        }
        
        try {
            serverSocket = ServerSocket(port)
            isRunning = true
            
            println("[$TAG] 🚀 Hot-reload development server started on port $port")
            println("[$TAG] Waiting for device connections...")
            
            executorService.submit {
                acceptConnections()
            }
            
        } catch (e: Exception) {
            println("[$TAG] ❌ Failed to start server: ${e.message}")
            isRunning = false
        }
    }
    
    /**
     * Stops the development server.
     */
    fun stop() {
        if (!isRunning) {
            println("[$TAG] Server is not running")
            return
        }
        
        isRunning = false
        
        try {
            serverSocket?.close()
            executorService.shutdownNow()
            connectedClients.clear()
            println("[$TAG] 🛑 Development server stopped")
        } catch (e: Exception) {
            println("[$TAG] Error stopping server: ${e.message}")
        }
    }
    
    /**
     * Registers a DEX file for a module.
     */
    fun registerModuleDex(moduleId: String, dexFile: File) {
        if (!dexFile.exists() || !dexFile.isFile) {
            println("[$TAG] ⚠️ DEX file does not exist: ${dexFile.absolutePath}")
            return
        }
        
        val version = dexFile.lastModified()
        val previousVersion = moduleVersions[moduleId]
        
        moduleDexFiles[moduleId] = dexFile
        moduleVersions[moduleId] = version
        
        if (previousVersion != null && previousVersion != version) {
            println("[$TAG] 📦 Module $moduleId updated (${dexFile.length()} bytes)")
            notifyClientsOfUpdate(moduleId)
        } else {
            println("[$TAG] 📦 Module $moduleId registered (${dexFile.length()} bytes)")
        }
    }
    
    /**
     * Gets server statistics.
     */
    fun getStatistics(): Map<String, Any> {
        return mapOf(
            "isRunning" to isRunning,
            "port" to port,
            "connectedClients" to connectedClients.size,
            "registeredModules" to moduleDexFiles.size,
            "modules" to moduleDexFiles.keys.toList()
        )
    }
    
    /**
     * Accepts incoming connections.
     */
    private fun acceptConnections() {
        while (isRunning) {
            try {
                val clientSocket = serverSocket?.accept() ?: break
                val clientId = "${clientSocket.inetAddress.hostAddress}:${clientSocket.port}"
                
                println("[$TAG] 📱 Device connected: $clientId")
                
                executorService.submit {
                    handleClient(clientId, clientSocket)
                }
                
            } catch (e: SocketTimeoutException) {
                // Timeout is normal, continue
            } catch (e: Exception) {
                if (isRunning) {
                    println("[$TAG] Error accepting client: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Handles a client connection.
     */
    private fun handleClient(clientId: String, clientSocket: Socket) {
        try {
            clientSocket.soTimeout = 10000 // 10 second timeout
            
            val clientInfo = ClientInfo(clientId, System.currentTimeMillis())
            connectedClients[clientId] = clientInfo
            
            val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            val output = clientSocket.getOutputStream()
            
            // Read command
            val command = input.readLine()
            
            when {
                command == "PING" -> {
                    handlePing(output)
                }
                command == "CHECK_UPDATES" -> {
                    handleCheckUpdates(output)
                }
                command?.startsWith("GET_DEX ") == true -> {
                    val moduleId = command.substring(8).trim()
                    handleGetDex(moduleId, output)
                }
                else -> {
                    println("[$TAG] Unknown command from $clientId: $command")
                    output.write("ERROR UNKNOWN_COMMAND\n".toByteArray())
                    output.flush()
                }
            }
            
        } catch (e: Exception) {
            println("[$TAG] Error handling client $clientId: ${e.message}")
        } finally {
            try {
                clientSocket.close()
                connectedClients.remove(clientId)
                println("[$TAG] 📱 Device disconnected: $clientId")
            } catch (e: Exception) {
                // Ignore cleanup errors
            }
        }
    }
    
    /**
     * Handles a ping request.
     */
    private fun handlePing(output: OutputStream) {
        output.write("PONG\n".toByteArray())
        output.flush()
    }
    
    /**
     * Handles a check for updates request.
     */
    private fun handleCheckUpdates(output: OutputStream) {
        // For simplicity, we'll just respond with "no updates" for now
        // In a real implementation, this would track what the client has seen
        output.write("NO_UPDATES\n".toByteArray())
        output.flush()
    }
    
    /**
     * Handles a GET_DEX request.
     */
    private fun handleGetDex(moduleId: String, output: OutputStream) {
        val dexFile = moduleDexFiles[moduleId]
        
        if (dexFile == null || !dexFile.exists()) {
            println("[$TAG] ⚠️ DEX not found for module: $moduleId")
            output.write("ERROR DEX_NOT_FOUND\n".toByteArray())
            output.flush()
            return
        }
        
        try {
            val dexBytes = dexFile.readBytes()
            
            // Send success header
            output.write("DEX ${dexBytes.size}\n".toByteArray())
            output.flush()
            
            // Send DEX bytes
            output.write(dexBytes)
            output.flush()
            
            println("[$TAG] 📤 Sent DEX for module $moduleId (${dexBytes.size} bytes)")
            
        } catch (e: Exception) {
            println("[$TAG] Error sending DEX for module $moduleId: ${e.message}")
            output.write("ERROR ${e.message}\n".toByteArray())
            output.flush()
        }
    }
    
    /**
     * Notifies connected clients of a module update.
     */
    private fun notifyClientsOfUpdate(moduleId: String) {
        println("[$TAG] 🔄 Notifying clients of update for module: $moduleId")
        
        // In a real implementation, this would maintain persistent connections
        // or use a push notification mechanism to inform clients
        // For now, clients will detect updates on their next polling cycle
    }
    
    /**
     * Information about a connected client.
     */
    private data class ClientInfo(
        val id: String,
        val connectTime: Long
    )
    
    companion object {
        /**
         * Creates and starts a development server.
         */
        fun createAndStart(port: Int = 5795): DevServer {
            val server = DevServer(port)
            server.start()
            return server
        }
    }
} 