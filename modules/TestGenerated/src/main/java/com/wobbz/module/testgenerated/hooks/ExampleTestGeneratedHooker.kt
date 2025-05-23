package com.wobbz.module.testgenerated.hooks

import com.wobbz.framework.core.HookParam
import com.wobbz.framework.core.Hooker

/**
 * Example hooker for TestGenerated.
 * This is a template - replace with your actual hooking logic.
 */
class ExampleTestGeneratedHooker : Hooker {
    
    override fun beforeHook(param: HookParam) {
        // Called before the original method executes
        // Note: Use android.util.Log directly since HookParam doesn't provide logging
        android.util.Log.d("TestGenerated", "ExampleTestGeneratedHooker: beforeHook called")
        
        // Example: Log method parameters
        param.args.forEachIndexed { index, arg ->
            android.util.Log.d("TestGenerated", "Param[$index]: ${arg?.toString() ?: "null"}")
        }
        
        // Example: Modify parameters
        // param.args[0] = "modified_value"
        
        // Example: Block method execution
        // if (shouldBlockMethod()) {
        //     param.setResult(null)
        // }
    }
    
    override fun afterHook(param: HookParam) {
        // Called after the original method executes (or after beforeHook if result was set)
        android.util.Log.d("TestGenerated", "ExampleTestGeneratedHooker: afterHook called")
        
        // Example: Log the result
        val result = param.getResult<Any>()
        android.util.Log.d("TestGenerated", "Method result: ${result?.toString() ?: "null"}")
        
        // Example: Modify the result
        // param.setResult("modified_result")
    }
    
    private fun shouldBlockMethod(): Boolean {
        // Add your logic here
        return false
    }
}
