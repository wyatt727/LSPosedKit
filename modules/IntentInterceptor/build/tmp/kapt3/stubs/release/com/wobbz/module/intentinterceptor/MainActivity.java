package com.wobbz.module.intentinterceptor;

/**
 * Main activity for IntentInterceptor module.
 * Provides a settings interface for configuring intent monitoring and filtering.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0001\bB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0014J\b\u0010\u0007\u001a\u00020\u0004H\u0002\u00a8\u0006\t"}, d2 = {"Lcom/wobbz/module/intentinterceptor/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "showModuleInfo", "IntentInterceptorSettingsFragment", "IntentInterceptor_release"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    
    public MainActivity() {
        super();
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void showModuleInfo() {
    }
    
    /**
     * Settings fragment that generates UI from settings.json schema
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0002J\u0010\u0010\t\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u0010\u0010\n\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u001c\u0010\u000b\u001a\u00020\u00042\b\u0010\f\u001a\u0004\u0018\u00010\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\bH\u0016\u00a8\u0006\u000f"}, d2 = {"Lcom/wobbz/module/intentinterceptor/MainActivity$IntentInterceptorSettingsFragment;", "Landroidx/preference/PreferenceFragmentCompat;", "()V", "addErrorPreference", "", "screen", "Landroidx/preference/PreferenceScreen;", "error", "", "addPrivacyWarningPreference", "addTargetingHelpPreference", "onCreatePreferences", "savedInstanceState", "Landroid/os/Bundle;", "rootKey", "IntentInterceptor_release"})
    public static final class IntentInterceptorSettingsFragment extends androidx.preference.PreferenceFragmentCompat {
        
        public IntentInterceptorSettingsFragment() {
            super();
        }
        
        @java.lang.Override
        public void onCreatePreferences(@org.jetbrains.annotations.Nullable
        android.os.Bundle savedInstanceState, @org.jetbrains.annotations.Nullable
        java.lang.String rootKey) {
        }
        
        private final void addTargetingHelpPreference(androidx.preference.PreferenceScreen screen) {
        }
        
        private final void addPrivacyWarningPreference(androidx.preference.PreferenceScreen screen) {
        }
        
        private final void addErrorPreference(androidx.preference.PreferenceScreen screen, java.lang.String error) {
        }
    }
}