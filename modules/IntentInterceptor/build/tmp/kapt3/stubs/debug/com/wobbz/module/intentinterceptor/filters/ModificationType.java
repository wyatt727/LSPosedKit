package com.wobbz.module.intentinterceptor.filters;

/**
 * Types of modifications that can be applied to Intents
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b\u00a8\u0006\t"}, d2 = {"Lcom/wobbz/module/intentinterceptor/filters/ModificationType;", "", "(Ljava/lang/String;I)V", "SET_ACTION", "ADD_EXTRA", "REMOVE_EXTRA", "SET_DATA", "ADD_CATEGORY", "REMOVE_CATEGORY", "IntentInterceptor_debug"})
public enum ModificationType {
    /*public static final*/ SET_ACTION /* = new SET_ACTION() */,
    /*public static final*/ ADD_EXTRA /* = new ADD_EXTRA() */,
    /*public static final*/ REMOVE_EXTRA /* = new REMOVE_EXTRA() */,
    /*public static final*/ SET_DATA /* = new SET_DATA() */,
    /*public static final*/ ADD_CATEGORY /* = new ADD_CATEGORY() */,
    /*public static final*/ REMOVE_CATEGORY /* = new REMOVE_CATEGORY() */;
    
    ModificationType() {
    }
    
    @org.jetbrains.annotations.NotNull
    public static kotlin.enums.EnumEntries<com.wobbz.module.intentinterceptor.filters.ModificationType> getEntries() {
        return null;
    }
}