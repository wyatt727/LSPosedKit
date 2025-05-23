package com.wobbz.module.uienhancer;

/**
 * Main activity for UIEnhancer module.
 * Provides a settings interface for configuring UI enhancements.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u000b2\u00020\u0001:\u0002\u000b\fB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0014J\u001a\u0010\u0007\u001a\u00020\u00042\u0010\b\u0002\u0010\b\u001a\n\u0018\u00010\tj\u0004\u0018\u0001`\nH\u0002\u00a8\u0006\r"}, d2 = {"Lcom/wobbz/module/uienhancer/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "showModuleInfo", "error", "Ljava/lang/Exception;", "Lkotlin/Exception;", "Companion", "UIEnhancerSettingsFragment", "UIEnhancer_debug"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.NotNull
    @java.lang.Deprecated
    public static final java.lang.String TAG = "UIEnhancer.MainActivity";
    @org.jetbrains.annotations.NotNull
    private static final com.wobbz.module.uienhancer.MainActivity.Companion Companion = null;
    
    public MainActivity() {
        super();
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void showModuleInfo(java.lang.Exception error) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/wobbz/module/uienhancer/MainActivity$Companion;", "", "()V", "TAG", "", "UIEnhancer_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    /**
     * Settings fragment that generates UI from settings.json schema
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u000f2\u00020\u0001:\u0001\u000fB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0002J\u0010\u0010\t\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u0010\u0010\n\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u001c\u0010\u000b\u001a\u00020\u00042\b\u0010\f\u001a\u0004\u0018\u00010\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\bH\u0016\u00a8\u0006\u0010"}, d2 = {"Lcom/wobbz/module/uienhancer/MainActivity$UIEnhancerSettingsFragment;", "Landroidx/preference/PreferenceFragmentCompat;", "()V", "addErrorPreference", "", "screen", "Landroidx/preference/PreferenceScreen;", "error", "", "addFallbackPreferences", "addTargetingHelpPreference", "onCreatePreferences", "savedInstanceState", "Landroid/os/Bundle;", "rootKey", "Companion", "UIEnhancer_debug"})
    public static final class UIEnhancerSettingsFragment extends androidx.preference.PreferenceFragmentCompat {
        @org.jetbrains.annotations.NotNull
        @java.lang.Deprecated
        public static final java.lang.String TAG = "UIEnhancer.SettingsFragment";
        @org.jetbrains.annotations.NotNull
        private static final com.wobbz.module.uienhancer.MainActivity.UIEnhancerSettingsFragment.Companion Companion = null;
        
        public UIEnhancerSettingsFragment() {
            super();
        }
        
        @java.lang.Override
        public void onCreatePreferences(@org.jetbrains.annotations.Nullable
        android.os.Bundle savedInstanceState, @org.jetbrains.annotations.Nullable
        java.lang.String rootKey) {
        }
        
        private final void addTargetingHelpPreference(androidx.preference.PreferenceScreen screen) {
        }
        
        private final void addErrorPreference(androidx.preference.PreferenceScreen screen, java.lang.String error) {
        }
        
        private final void addFallbackPreferences(androidx.preference.PreferenceScreen screen) {
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/wobbz/module/uienhancer/MainActivity$UIEnhancerSettingsFragment$Companion;", "", "()V", "TAG", "", "UIEnhancer_debug"})
        static final class Companion {
            
            private Companion() {
                super();
            }
        }
    }
}