package com.wobbz.module.testgenerated.hooks;

/**
 * Example hooker for TestGenerated.
 * This is a template - replace with your actual hooking logic.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\b\u0010\b\u001a\u00020\tH\u0002\u00a8\u0006\n"}, d2 = {"Lcom/wobbz/module/testgenerated/hooks/ExampleTestGeneratedHooker;", "Lcom/wobbz/framework/core/Hooker;", "()V", "afterHook", "", "param", "Lcom/wobbz/framework/core/HookParam;", "beforeHook", "shouldBlockMethod", "", "TestGenerated_debug"})
public final class ExampleTestGeneratedHooker implements com.wobbz.framework.core.Hooker {
    
    public ExampleTestGeneratedHooker() {
        super();
    }
    
    @java.lang.Override
    public void beforeHook(@org.jetbrains.annotations.NotNull
    com.wobbz.framework.core.HookParam param) {
    }
    
    @java.lang.Override
    public void afterHook(@org.jetbrains.annotations.NotNull
    com.wobbz.framework.core.HookParam param) {
    }
    
    private final boolean shouldBlockMethod() {
        return false;
    }
}