package com.wobbz.module.intentinterceptor.filters;

/**
 * Represents an Intent filter rule
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b$\b\u0086\b\u0018\u00002\u00020\u0001B{\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\r\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012\u00a2\u0006\u0002\u0010\u0014J\t\u0010&\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\'\u001a\u00020\u0003H\u00c6\u0003J\t\u0010(\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012H\u00c6\u0003J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\t\u0010+\u001a\u00020\u0003H\u00c6\u0003J\t\u0010,\u001a\u00020\u0007H\u00c6\u0003J\t\u0010-\u001a\u00020\tH\u00c6\u0003J\t\u0010.\u001a\u00020\u000bH\u00c6\u0003J\t\u0010/\u001a\u00020\u0003H\u00c6\u0003J\t\u00100\u001a\u00020\u0003H\u00c6\u0003J\t\u00101\u001a\u00020\u0003H\u00c6\u0003J\u0087\u0001\u00102\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u00032\b\b\u0002\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u00032\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012H\u00c6\u0001J\u0013\u00103\u001a\u00020\u00072\b\u00104\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00105\u001a\u00020\u000bH\u00d6\u0001J\t\u00106\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0010\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0018R\u0011\u0010\r\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0018R\u0011\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0018R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0018R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0018R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0018R\u0011\u0010\u000e\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0018R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010%\u00a8\u00067"}, d2 = {"Lcom/wobbz/module/intentinterceptor/filters/IntentFilterRule;", "", "id", "", "name", "description", "enabled", "", "action", "Lcom/wobbz/module/intentinterceptor/filters/IntentFilterAction;", "priority", "", "actionPattern", "componentPattern", "packagePattern", "dataPattern", "categoryPattern", "modifications", "", "Lcom/wobbz/module/intentinterceptor/filters/IntentModification;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/wobbz/module/intentinterceptor/filters/IntentFilterAction;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V", "getAction", "()Lcom/wobbz/module/intentinterceptor/filters/IntentFilterAction;", "getActionPattern", "()Ljava/lang/String;", "getCategoryPattern", "getComponentPattern", "getDataPattern", "getDescription", "getEnabled", "()Z", "getId", "getModifications", "()Ljava/util/List;", "getName", "getPackagePattern", "getPriority", "()I", "component1", "component10", "component11", "component12", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "IntentInterceptor_release"})
public final class IntentFilterRule {
    @org.jetbrains.annotations.NotNull
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String name = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String description = null;
    private final boolean enabled = false;
    @org.jetbrains.annotations.NotNull
    private final com.wobbz.module.intentinterceptor.filters.IntentFilterAction action = null;
    private final int priority = 0;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String actionPattern = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String componentPattern = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String packagePattern = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String dataPattern = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String categoryPattern = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.wobbz.module.intentinterceptor.filters.IntentModification> modifications = null;
    
    public IntentFilterRule(@org.jetbrains.annotations.NotNull
    java.lang.String id, @org.jetbrains.annotations.NotNull
    java.lang.String name, @org.jetbrains.annotations.NotNull
    java.lang.String description, boolean enabled, @org.jetbrains.annotations.NotNull
    com.wobbz.module.intentinterceptor.filters.IntentFilterAction action, int priority, @org.jetbrains.annotations.NotNull
    java.lang.String actionPattern, @org.jetbrains.annotations.NotNull
    java.lang.String componentPattern, @org.jetbrains.annotations.NotNull
    java.lang.String packagePattern, @org.jetbrains.annotations.NotNull
    java.lang.String dataPattern, @org.jetbrains.annotations.NotNull
    java.lang.String categoryPattern, @org.jetbrains.annotations.NotNull
    java.util.List<com.wobbz.module.intentinterceptor.filters.IntentModification> modifications) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getDescription() {
        return null;
    }
    
    public final boolean getEnabled() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.wobbz.module.intentinterceptor.filters.IntentFilterAction getAction() {
        return null;
    }
    
    public final int getPriority() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getActionPattern() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getComponentPattern() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getPackagePattern() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getDataPattern() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getCategoryPattern() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.wobbz.module.intentinterceptor.filters.IntentModification> getModifications() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component10() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component11() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.wobbz.module.intentinterceptor.filters.IntentModification> component12() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component3() {
        return null;
    }
    
    public final boolean component4() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.wobbz.module.intentinterceptor.filters.IntentFilterAction component5() {
        return null;
    }
    
    public final int component6() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.wobbz.module.intentinterceptor.filters.IntentFilterRule copy(@org.jetbrains.annotations.NotNull
    java.lang.String id, @org.jetbrains.annotations.NotNull
    java.lang.String name, @org.jetbrains.annotations.NotNull
    java.lang.String description, boolean enabled, @org.jetbrains.annotations.NotNull
    com.wobbz.module.intentinterceptor.filters.IntentFilterAction action, int priority, @org.jetbrains.annotations.NotNull
    java.lang.String actionPattern, @org.jetbrains.annotations.NotNull
    java.lang.String componentPattern, @org.jetbrains.annotations.NotNull
    java.lang.String packagePattern, @org.jetbrains.annotations.NotNull
    java.lang.String dataPattern, @org.jetbrains.annotations.NotNull
    java.lang.String categoryPattern, @org.jetbrains.annotations.NotNull
    java.util.List<com.wobbz.module.intentinterceptor.filters.IntentModification> modifications) {
        return null;
    }
    
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public java.lang.String toString() {
        return null;
    }
}