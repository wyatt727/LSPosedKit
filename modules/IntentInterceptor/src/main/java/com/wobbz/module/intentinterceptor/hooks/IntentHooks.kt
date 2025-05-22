package com.wobbz.module.intentinterceptor.hooks

import android.content.Intent
import android.os.Bundle
import com.wobbz.framework.core.HookParam
import com.wobbz.framework.core.Hooker
import com.wobbz.framework.core.LogLevel
import com.wobbz.framework.core.PackageLoadedParam
import com.wobbz.module.intentinterceptor.filters.IntentFilterManager
import com.wobbz.module.intentinterceptor.monitor.IntentMonitor
import com.wobbz.module.intentinterceptor.services.IntentHistoryService

/**
 * Comprehensive Intent hooking system for monitoring Intent communications.
 */
class IntentHooks(
    private val param: PackageLoadedParam,
    private val intentMonitor: IntentMonitor,
    private val filterManager: IntentFilterManager,
    private val historyService: IntentHistoryService
) {
    
    private val hooks = mutableListOf<Any>()
    
    fun applyHooks(): List<Any> {
        // Hook Activity Intent methods
        hookActivityIntents()
        
        // Hook Context Intent methods
        hookContextIntents()
        
        // Hook Service Intent methods
        hookServiceIntents()
        
        // Hook Broadcast Intent methods
        hookBroadcastIntents()
        
        // Hook Intent constructor and setters
        hookIntentModification()
        
        return hooks
    }
    
    private fun hookActivityIntents() {
        try {
            val activityClass = param.xposed.loadClass("android.app.Activity")
            
            // Hook startActivity methods
            val startActivityMethod = activityClass.getDeclaredMethod("startActivity", Intent::class.java)
            hooks += param.xposed.hook(startActivityMethod, StartActivityHooker::class.java)
            
            val startActivityWithOptionsMethod = activityClass.getDeclaredMethod(
                "startActivity", Intent::class.java, Bundle::class.java
            )
            hooks += param.xposed.hook(startActivityWithOptionsMethod, StartActivityHooker::class.java)
            
            // Hook startActivityForResult methods
            val startActivityForResultMethod = activityClass.getDeclaredMethod(
                "startActivityForResult", Intent::class.java, Int::class.javaPrimitiveType
            )
            hooks += param.xposed.hook(startActivityForResultMethod, StartActivityForResultHooker::class.java)
            
            param.xposed.log(LogLevel.INFO, "Hooked Activity Intent methods")
        } catch (e: Exception) {
            param.xposed.logError("Failed to hook Activity Intent methods", e)
        }
    }
    
    private fun hookContextIntents() {
        try {
            val contextClass = param.xposed.loadClass("android.content.Context")
            
            // Hook sendBroadcast methods
            val sendBroadcastMethod = contextClass.getDeclaredMethod("sendBroadcast", Intent::class.java)
            hooks += param.xposed.hook(sendBroadcastMethod, SendBroadcastHooker::class.java)
            
            // Hook startService methods
            val startServiceMethod = contextClass.getDeclaredMethod("startService", Intent::class.java)
            hooks += param.xposed.hook(startServiceMethod, StartServiceHooker::class.java)
            
            // Hook bindService methods
            val bindServiceMethod = contextClass.getDeclaredMethod(
                "bindService", 
                Intent::class.java, 
                android.content.ServiceConnection::class.java, 
                Int::class.javaPrimitiveType
            )
            hooks += param.xposed.hook(bindServiceMethod, BindServiceHooker::class.java)
            
            param.xposed.log(LogLevel.INFO, "Hooked Context Intent methods")
        } catch (e: Exception) {
            param.xposed.logError("Failed to hook Context Intent methods", e)
        }
    }
    
    private fun hookServiceIntents() {
        try {
            val serviceClass = param.xposed.loadClass("android.app.Service")
            
            // Hook onStartCommand
            val onStartCommandMethod = serviceClass.getDeclaredMethod(
                "onStartCommand", Intent::class.java, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType
            )
            hooks += param.xposed.hook(onStartCommandMethod, ServiceStartHooker::class.java)
            
            param.xposed.log(LogLevel.INFO, "Hooked Service Intent methods")
        } catch (e: Exception) {
            param.xposed.logError("Failed to hook Service Intent methods", e)
        }
    }
    
    private fun hookBroadcastIntents() {
        try {
            val broadcastReceiverClass = param.xposed.loadClass("android.content.BroadcastReceiver")
            
            // Hook onReceive
            val onReceiveMethod = broadcastReceiverClass.getDeclaredMethod(
                "onReceive", android.content.Context::class.java, Intent::class.java
            )
            hooks += param.xposed.hook(onReceiveMethod, BroadcastReceiveHooker::class.java)
            
            param.xposed.log(LogLevel.INFO, "Hooked BroadcastReceiver Intent methods")
        } catch (e: Exception) {
            param.xposed.logError("Failed to hook BroadcastReceiver Intent methods", e)
        }
    }
    
    private fun hookIntentModification() {
        try {
            val intentClass = param.xposed.loadClass("android.content.Intent")
            
            // Hook Intent setAction
            val setActionMethod = intentClass.getDeclaredMethod("setAction", String::class.java)
            hooks += param.xposed.hook(setActionMethod, IntentModificationHooker::class.java)
            
            // Hook Intent putExtra methods
            val putStringExtraMethod = intentClass.getDeclaredMethod(
                "putExtra", String::class.java, String::class.java
            )
            hooks += param.xposed.hook(putStringExtraMethod, IntentModificationHooker::class.java)
            
            param.xposed.log(LogLevel.INFO, "Hooked Intent modification methods")
        } catch (e: Exception) {
            param.xposed.logError("Failed to hook Intent modification methods", e)
        }
    }
    
    /**
     * Hooker for Activity.startActivity() methods
     */
    inner class StartActivityHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            val intent = param.args[0] as? Intent ?: return
            
            // Check if intent should be intercepted
            if (filterManager.shouldBlock(intent)) {
                this@IntentHooks.param.xposed.log(LogLevel.WARN, "Blocking startActivity: ${intent.action}")
                param.setThrowable(SecurityException("Intent blocked by IntentInterceptor"))
                return
            }
            
            // Monitor and log the intent
            intentMonitor.onActivityStart(intent, param.thisObject?.javaClass?.name ?: "Unknown")
            historyService.recordIntent("START_ACTIVITY", intent, this@IntentHooks.param.packageName)
            
            // Allow filtering/modification
            val modifiedIntent = filterManager.processIntent(intent)
            if (modifiedIntent != intent) {
                param.args[0] = modifiedIntent
                this@IntentHooks.param.xposed.log(LogLevel.INFO, "Modified startActivity intent")
            }
        }
    }
    
    /**
     * Hooker for Activity.startActivityForResult() methods
     */
    inner class StartActivityForResultHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            val intent = param.args[0] as? Intent ?: return
            val requestCode = param.args[1] as? Int ?: -1
            
            // Check if intent should be intercepted
            if (filterManager.shouldBlock(intent)) {
                this@IntentHooks.param.xposed.log(LogLevel.WARN, "Blocking startActivityForResult: ${intent.action}")
                param.setThrowable(SecurityException("Intent blocked by IntentInterceptor"))
                return
            }
            
            // Monitor and log the intent
            intentMonitor.onActivityStartForResult(intent, requestCode, param.thisObject?.javaClass?.name ?: "Unknown")
            historyService.recordIntent("START_ACTIVITY_FOR_RESULT", intent, this@IntentHooks.param.packageName, requestCode)
        }
    }
    
    /**
     * Hooker for Context.sendBroadcast() methods
     */
    inner class SendBroadcastHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            val intent = param.args[0] as? Intent ?: return
            
            // Check if intent should be intercepted
            if (filterManager.shouldBlock(intent)) {
                this@IntentHooks.param.xposed.log(LogLevel.WARN, "Blocking sendBroadcast: ${intent.action}")
                param.setThrowable(SecurityException("Broadcast blocked by IntentInterceptor"))
                return
            }
            
            // Monitor and log the intent
            intentMonitor.onBroadcast(intent, param.thisObject?.javaClass?.name ?: "Unknown")
            historyService.recordIntent("SEND_BROADCAST", intent, this@IntentHooks.param.packageName)
        }
    }
    
    /**
     * Hooker for Context.startService() methods
     */
    inner class StartServiceHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            val intent = param.args[0] as? Intent ?: return
            
            // Check if intent should be intercepted
            if (filterManager.shouldBlock(intent)) {
                this@IntentHooks.param.xposed.log(LogLevel.WARN, "Blocking startService: ${intent.action}")
                param.setResult(null) // Return null to indicate service not started
                return
            }
            
            // Monitor and log the intent
            intentMonitor.onServiceStart(intent, param.thisObject?.javaClass?.name ?: "Unknown")
            historyService.recordIntent("START_SERVICE", intent, this@IntentHooks.param.packageName)
        }
    }
    
    /**
     * Hooker for Context.bindService() methods
     */
    inner class BindServiceHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            val intent = param.args[0] as? Intent ?: return
            
            // Check if intent should be intercepted
            if (filterManager.shouldBlock(intent)) {
                this@IntentHooks.param.xposed.log(LogLevel.WARN, "Blocking bindService: ${intent.action}")
                param.setResult(false) // Return false to indicate binding failed
                return
            }
            
            // Monitor and log the intent
            intentMonitor.onServiceBind(intent, param.thisObject?.javaClass?.name ?: "Unknown")
            historyService.recordIntent("BIND_SERVICE", intent, this@IntentHooks.param.packageName)
        }
    }
    
    /**
     * Hooker for Service.onStartCommand() methods
     */
    inner class ServiceStartHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            val intent = param.args[0] as? Intent
            val flags = param.args[1] as? Int ?: 0
            val startId = param.args[2] as? Int ?: -1
            
            if (intent != null) {
                intentMonitor.onServiceCommand(intent, flags, startId, param.thisObject?.javaClass?.name ?: "Unknown")
                historyService.recordIntent("SERVICE_COMMAND", intent, this@IntentHooks.param.packageName, startId)
            }
        }
    }
    
    /**
     * Hooker for BroadcastReceiver.onReceive() methods
     */
    inner class BroadcastReceiveHooker : Hooker {
        override fun beforeHook(param: HookParam) {
            val context = param.args[0] as? android.content.Context
            val intent = param.args[1] as? Intent ?: return
            
            // Monitor and log the intent
            intentMonitor.onBroadcastReceive(intent, param.thisObject?.javaClass?.name ?: "Unknown")
            historyService.recordIntent("BROADCAST_RECEIVE", intent, context?.packageName ?: "Unknown")
        }
    }
    
    /**
     * Hooker for Intent modification methods
     */
    inner class IntentModificationHooker : Hooker {
        override fun afterHook(param: HookParam) {
            val intent = param.thisObject as? Intent ?: return
            
            // Log intent modifications
            intentMonitor.onIntentModified(intent, param.method.name)
        }
    }
} 