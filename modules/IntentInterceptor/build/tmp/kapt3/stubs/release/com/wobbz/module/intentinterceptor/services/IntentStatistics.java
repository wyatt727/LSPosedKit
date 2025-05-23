package com.wobbz.module.intentinterceptor.services;

/**
 * Statistics about Intent usage
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B]\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00030\u0005\u0012\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00030\u0005\u0012\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00030\u0005\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\u0015\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00030\u0005H\u00c6\u0003J\u0015\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00030\u0005H\u00c6\u0003J\u0015\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00030\u0005H\u00c6\u0003J\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJ\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJr\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0014\b\u0002\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00030\u00052\u0014\b\u0002\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00030\u00052\u0014\b\u0002\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00030\u00052\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010\u001dJ\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010!\u001a\u00020\"H\u00d6\u0001J\t\u0010#\u001a\u00020\u0006H\u00d6\u0001R\u001d\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00030\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0015\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u000e\u0010\u000fR\u0015\u0010\t\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u0011\u0010\u000fR\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00030\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u001d\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00030\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015\u00a8\u0006$"}, d2 = {"Lcom/wobbz/module/intentinterceptor/services/IntentStatistics;", "", "totalIntents", "", "operationCounts", "", "", "packageCounts", "actionCounts", "oldestRecord", "newestRecord", "(JLjava/util/Map;Ljava/util/Map;Ljava/util/Map;Ljava/lang/Long;Ljava/lang/Long;)V", "getActionCounts", "()Ljava/util/Map;", "getNewestRecord", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getOldestRecord", "getOperationCounts", "getPackageCounts", "getTotalIntents", "()J", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "(JLjava/util/Map;Ljava/util/Map;Ljava/util/Map;Ljava/lang/Long;Ljava/lang/Long;)Lcom/wobbz/module/intentinterceptor/services/IntentStatistics;", "equals", "", "other", "hashCode", "", "toString", "IntentInterceptor_release"})
public final class IntentStatistics {
    private final long totalIntents = 0L;
    @org.jetbrains.annotations.NotNull
    private final java.util.Map<java.lang.String, java.lang.Long> operationCounts = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.Map<java.lang.String, java.lang.Long> packageCounts = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.Map<java.lang.String, java.lang.Long> actionCounts = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.Long oldestRecord = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.Long newestRecord = null;
    
    public IntentStatistics(long totalIntents, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.Long> operationCounts, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.Long> packageCounts, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.Long> actionCounts, @org.jetbrains.annotations.Nullable
    java.lang.Long oldestRecord, @org.jetbrains.annotations.Nullable
    java.lang.Long newestRecord) {
        super();
    }
    
    public final long getTotalIntents() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.String, java.lang.Long> getOperationCounts() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.String, java.lang.Long> getPackageCounts() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.String, java.lang.Long> getActionCounts() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Long getOldestRecord() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Long getNewestRecord() {
        return null;
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.String, java.lang.Long> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.String, java.lang.Long> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.String, java.lang.Long> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Long component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Long component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.wobbz.module.intentinterceptor.services.IntentStatistics copy(long totalIntents, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.Long> operationCounts, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.Long> packageCounts, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.Long> actionCounts, @org.jetbrains.annotations.Nullable
    java.lang.Long oldestRecord, @org.jetbrains.annotations.Nullable
    java.lang.Long newestRecord) {
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