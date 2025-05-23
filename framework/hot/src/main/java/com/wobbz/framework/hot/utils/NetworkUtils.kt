package com.wobbz.framework.hot.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import java.io.IOException
import java.net.InetAddress
import java.net.Socket

/**
 * Network utilities for hot-reload functionality.
 */
object NetworkUtils {
    private const val TAG = "LSPK-HotReload-Network"
    
    /**
     * Checks if the device has an active network connection.
     */
    @SuppressLint("MissingPermission")
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        return try {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking network availability", e)
            false
        }
    }
    
    /**
     * Checks if a specific host and port are reachable.
     */
    fun isHostReachable(host: String, port: Int, timeoutMs: Int = 5000): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(java.net.InetSocketAddress(host, port), timeoutMs)
                true
            }
        } catch (e: IOException) {
            Log.d(TAG, "Host $host:$port not reachable: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking host reachability", e)
            false
        }
    }
    
    /**
     * Gets the local device IP address.
     */
    fun getLocalIpAddress(): String? {
        return try {
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                if (networkInterface.isLoopback || !networkInterface.isUp) continue
                
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is java.net.Inet4Address) {
                        return address.hostAddress
                    }
                }
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting local IP address", e)
            null
        }
    }
    
    /**
     * Detects the development machine IP address by checking common patterns.
     */
    fun detectDevelopmentMachineIp(): String? {
        val localIp = getLocalIpAddress()
        if (localIp == null) {
            Log.w(TAG, "Could not determine local IP address")
            return null
        }
        
        // For development, the dev machine is often on the same subnet
        val ipParts = localIp.split(".")
        if (ipParts.size == 4) {
            val baseIp = "${ipParts[0]}.${ipParts[1]}.${ipParts[2]}"
            
            // Common development machine IPs
            val commonDevIps = listOf(
                "$baseIp.1",   // Often the router/gateway
                "$baseIp.100", // Common static IP
                "$baseIp.101",
                "$baseIp.2"
            )
            
            for (devIp in commonDevIps) {
                if (isHostReachable(devIp, 5795, 1000)) {
                    Log.i(TAG, "Found development machine at: $devIp")
                    return devIp
                }
            }
        }
        
        Log.w(TAG, "Could not detect development machine IP")
        return null
    }
} 