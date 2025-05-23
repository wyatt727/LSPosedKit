import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.AppExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

/**
 * Common Android configuration for LSPosedKit framework libraries.
 */
fun LibraryExtension.configureAndroidLibrary() {
    compileSdkVersion(35)
    
    defaultConfig {
        minSdk = 31
        targetSdk = 35
        consumerProguardFiles("consumer-rules.pro")
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    // Configure Kotlin options
    (this as ExtensionAware).extensions.configure<KotlinJvmOptions>("kotlinOptions") {
        jvmTarget = "17"
    }
}

/**
 * Common Android configuration for LSPosed module applications.
 */
fun AppExtension.configureAndroidApplication() {
    compileSdkVersion(35)
    
    defaultConfig {
        // applicationId will be set by the plugin based on namespace
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = false // LSPosed modules typically don't minify
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    // Configure Kotlin options
    (this as ExtensionAware).extensions.configure<KotlinJvmOptions>("kotlinOptions") {
        jvmTarget = "17"
    }
}

/**
 * Add common framework dependencies for LSPosedKit modules.
 */
fun Project.addFrameworkDependencies() {
    dependencies {
        "implementation"(project(":framework"))
        "kapt"(project(":framework:processor"))
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
        "implementation"("androidx.annotation:annotation:1.5.0")
    }
}

/**
 * Add common testing dependencies.
 */
fun Project.addTestingDependencies() {
    dependencies {
        "testImplementation"("junit:junit:4.13.2")
        "testImplementation"("org.mockito:mockito-core:4.0.0")
        "testImplementation"("androidx.test:core:1.4.0")
    }
}

/**
 * Add UI-related dependencies for modules that need UI components.
 */
fun Project.addUIDependencies() {
    dependencies {
        "implementation"("androidx.core:core-ktx:1.9.0")
        "implementation"("androidx.constraintlayout:constraintlayout:2.1.4")
        "implementation"("com.google.android.material:material:1.8.0")
    }
}

/**
 * Add JSON processing dependencies.
 */
fun Project.addJSONDependencies() {
    dependencies {
        "implementation"("com.squareup.moshi:moshi:1.14.0")
        "implementation"("com.squareup.moshi:moshi-kotlin:1.14.0")
    }
}

/**
 * Add coroutines dependencies for async operations.
 */
fun Project.addCoroutinesDependencies() {
    dependencies {
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    }
} 