package com.wobbz.module.intentinterceptor.services;

/**
 * Service interface for accessing Intent history
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0016\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0007\u001a\u00020\bH&J\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\n\u001a\u00020\bH&J\u0018\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\f\u001a\u00020\rH&J\b\u0010\u000e\u001a\u00020\u000fH&J\u0016\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0011\u001a\u00020\bH&\u00a8\u0006\u0012"}, d2 = {"Lcom/wobbz/module/intentinterceptor/services/IIntentHistoryService;", "", "clearHistory", "", "getIntentsByPackage", "", "Lcom/wobbz/module/intentinterceptor/services/IntentRecord;", "packageName", "", "getIntentsByType", "type", "getRecentIntents", "limit", "", "getStatistics", "Lcom/wobbz/module/intentinterceptor/services/IntentStatistics;", "searchIntents", "query", "IntentInterceptor_debug"})
public abstract interface IIntentHistoryService {
    
    @org.jetbrains.annotations.NotNull
    public abstract java.util.List<com.wobbz.module.intentinterceptor.services.IntentRecord> getRecentIntents(int limit);
    
    @org.jetbrains.annotations.NotNull
    public abstract java.util.List<com.wobbz.module.intentinterceptor.services.IntentRecord> getIntentsByType(@org.jetbrains.annotations.NotNull
    java.lang.String type);
    
    @org.jetbrains.annotations.NotNull
    public abstract java.util.List<com.wobbz.module.intentinterceptor.services.IntentRecord> getIntentsByPackage(@org.jetbrains.annotations.NotNull
    java.lang.String packageName);
    
    @org.jetbrains.annotations.NotNull
    public abstract java.util.List<com.wobbz.module.intentinterceptor.services.IntentRecord> searchIntents(@org.jetbrains.annotations.NotNull
    java.lang.String query);
    
    public abstract void clearHistory();
    
    @org.jetbrains.annotations.NotNull
    public abstract com.wobbz.module.intentinterceptor.services.IntentStatistics getStatistics();
    
    /**
     * Service interface for accessing Intent history
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}