import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

/**
 * Custom Gradle plugin for LSPosedKit module development.
 * Provides common configuration and utilities for framework and modules.
 */
class LSPosedKitPlugin : Plugin<Project> {
    
    override fun apply(project: Project) {
        project.configureCommonSettings()
    }
    
    private fun Project.configureCommonSettings() {
        // Apply common plugins
        plugins.apply("com.android.library")
        plugins.apply("org.jetbrains.kotlin.android")
        plugins.apply("org.jetbrains.kotlin.kapt")
        
        // Configure Android settings using centralized configuration
        configure<LibraryExtension> {
            configureAndroidLibrary()
            
            defaultConfig {
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }
            
            buildTypes {
                getByName("release") {
                    isMinifyEnabled = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
            }
            
            // Configure Kotlin options
            (this as ExtensionAware).extensions.configure<KotlinJvmOptions>("kotlinOptions") {
                jvmTarget = DependencyVersions.javaVersion
                freeCompilerArgs = listOf(
                    "-Xopt-in=kotlin.RequiresOptIn",
                    "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
                )
            }
            
            // Configure test options
            testOptions {
                unitTests.isReturnDefaultValues = true
            }
        }
        
        // Add common dependencies using centralized versions
        dependencies {
            "implementation"("org.jetbrains.kotlin:kotlin-stdlib:${DependencyVersions.kotlin}")
            "implementation"("androidx.annotation:annotation:${DependencyVersions.androidxAnnotation}")
            
            // Test dependencies
            "testImplementation"("junit:junit:${DependencyVersions.junit}")
            "testImplementation"("org.mockito:mockito-core:${DependencyVersions.mockito}")
            "testImplementation"("androidx.test:core:${DependencyVersions.androidxTest}")
            "androidTestImplementation"("androidx.test.ext:junit:1.1.5")
            "androidTestImplementation"("androidx.test.espresso:espresso-core:3.5.1")
        }
    }
}

/**
 * Extension for configuring LSPosedKit module-specific settings.
 */
open class LSPosedKitExtension {
    var moduleId: String = ""
    var moduleName: String = ""
    var moduleDescription: String = ""
    var moduleAuthor: String = "LSPosedKit"
    var moduleVersion: String = "1.0.0"
    var scope: List<String> = listOf("*")
    var hotReloadEnabled: Boolean = true
    var serviceEnabled: Boolean = false
    var settingsEnabled: Boolean = false
}

/**
 * Configures a project as an LSPosedKit framework module.
 */
fun Project.configureFrameworkModule() {
    dependencies {
        "implementation"(project(":libxposed-api:api"))
        
        // Framework-specific dependencies using centralized versions
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-android:${DependencyVersions.coroutines}")
        "implementation"("com.squareup.moshi:moshi:${DependencyVersions.moshi}")
        "implementation"("com.squareup.moshi:moshi-kotlin:${DependencyVersions.moshi}")
        "kapt"("com.squareup.moshi:moshi-kotlin-codegen:${DependencyVersions.moshi}")
    }
}

/**
 * Configures a project as an LSPosedKit module.
 */
fun Project.configureModule(configure: LSPosedKitExtension.() -> Unit = {}) {
    val extension = extensions.create<LSPosedKitExtension>("lspkit")
    extension.configure()
    
    dependencies {
        "implementation"(project(":framework"))
        "kapt"(project(":framework:processor"))
        
        if (extension.serviceEnabled) {
            "implementation"(project(":framework:service"))
        }
        
        if (extension.settingsEnabled) {
            "implementation"(project(":framework:settings"))
        }
        
        if (extension.hotReloadEnabled) {
            "implementation"(project(":framework:hot"))
        }
    }
    
    // Configure tasks
    tasks.register("validateModule") {
        group = "verification"
        description = "Validates module configuration and dependencies"
        
        doLast {
            validateModuleConfiguration(extension)
        }
    }
}

/**
 * Validates module configuration.
 */
private fun validateModuleConfiguration(extension: LSPosedKitExtension) {
    require(extension.moduleId.isNotEmpty()) { "Module ID must be specified" }
    require(extension.moduleName.isNotEmpty()) { "Module name must be specified" }
    require(extension.moduleId.matches(Regex("[a-z0-9-]+"))) { 
        "Module ID must contain only lowercase letters, numbers, and hyphens" 
    }
}

/**
 * Configures signing for LSPosedKit modules.
 */
fun Project.configureSigning() {
    configure<LibraryExtension> {
        signingConfigs {
            create("lspkit") {
                storeFile = file(findProperty("lspkit.signing.storeFile") ?: "$rootDir/keystore.jks")
                storePassword = findProperty("lspkit.signing.storePassword") as String?
                keyAlias = findProperty("lspkit.signing.keyAlias") as String? ?: "lspkit"
                keyPassword = findProperty("lspkit.signing.keyPassword") as String?
            }
        }
        
        buildTypes {
            getByName("release") {
                if (project.hasProperty("lspkit.signing.storeFile")) {
                    signingConfig = signingConfigs.getByName("lspkit")
                }
            }
        }
    }
} 