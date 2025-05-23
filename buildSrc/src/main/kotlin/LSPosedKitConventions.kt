import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin for LSPosedKit framework components (libraries).
 * These are shared libraries used by modules.
 */
class LSPosedKitFrameworkPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply required plugins
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")
            
            // Configure Android library
            extensions.configure<LibraryExtension> {
                configureAndroidLibrary()
                namespace = "com.wobbz.framework.${project.name}"
            }
            
            // Add testing dependencies
            addTestingDependencies()
        }
    }
}

/**
 * Convention plugin for standalone LSPosed module applications.
 * These produce installable APKs containing the LSPosedKit framework.
 */
class LSPosedKitApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply required plugins for applications
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.kapt")
            
            // Configure Android application
            extensions.configure<AppExtension> {
                configureAndroidApplication()
                namespace = "com.wobbz.module.${project.name.lowercase()}"
                defaultConfig {
                    applicationId = "com.wobbz.module.${project.name.lowercase()}"
                }
            }
            
            // Add standard dependencies
            addFrameworkDependencies()
            addTestingDependencies()
        }
    }
}

/**
 * Convention plugin for UI-enabled LSPosed module applications.
 * Extends the base application plugin with UI dependencies.
 */
class LSPosedKitUIApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply base application plugin
            pluginManager.apply(LSPosedKitApplicationPlugin::class.java)
            
            // Add UI dependencies
            addUIDependencies()
            addJSONDependencies()
        }
    }
}

/**
 * Legacy convention plugin for LSPosedKit modules (deprecated).
 * Use LSPosedKitApplicationPlugin instead for standalone modules.
 */
@Deprecated("Use LSPosedKitApplicationPlugin for installable modules")
class LSPosedKitModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply required plugins
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.kapt")
            
            // Configure Android library
            extensions.configure<LibraryExtension> {
                configureAndroidLibrary()
                namespace = "com.wobbz.module.${project.name.lowercase()}"
            }
            
            // Add standard dependencies
            addFrameworkDependencies()
            addTestingDependencies()
        }
    }
}

/**
 * Legacy convention plugin for UI-enabled LSPosedKit modules (deprecated).
 * Use LSPosedKitUIApplicationPlugin instead.
 */
@Deprecated("Use LSPosedKitUIApplicationPlugin for UI-enabled modules")
class LSPosedKitUIModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply base module plugin
            pluginManager.apply(LSPosedKitModulePlugin::class.java)
            
            // Add UI dependencies
            addUIDependencies()
            addJSONDependencies()
        }
    }
} 