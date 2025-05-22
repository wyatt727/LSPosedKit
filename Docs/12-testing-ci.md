# Testing & CI Pipelines

> Comprehensive guide to testing strategies, test automation, and continuous integration workflows for LSPosedKit module development.

## Testing Strategy Overview

LSPosedKit supports a multi-layered testing approach:

1. **Unit Tests**: For individual components
2. **Integration Tests**: For component interactions
3. **Instrumented Tests**: For on-device validation
4. **Hot-Reload Testing**: For rapid development iterations
5. **CI/CD Pipeline Testing**: For automated verification

This layered approach ensures your modules are robust, reliable, and maintainable.

## Unit Testing

### Test Structure

Unit tests should be placed in the `src/test/java` directory, mirroring your main source structure:

```
modules/
└── debug-app/
    ├── src/
    │   ├── main/java/com/example/debugapp/
    │   │   └── DebugApp.kt
    │   └── test/java/com/example/debugapp/
    │       ├── DebugAppTest.kt
    │       ├── HookerTests.kt
    │       └── UtilTests.kt
    └── build.gradle
```

### Test Dependencies

Add these dependencies to your module's `build.gradle`:

```groovy
dependencies {
    // JUnit for test assertions
    testImplementation 'junit:junit:4.13.2'
    
    // Mockito for mocking dependencies
    testImplementation 'org.mockito:mockito-core:4.0.0'
    testImplementation 'org.mockito.kotlin:mockito-kotlin:4.0.0'
    
    // Robolectric for Android framework simulation
    testImplementation 'org.robolectric:robolectric:4.9'
    
    // Framework test helpers
    testImplementation project(':framework:testing')
}
```

### Writing Unit Tests

Follow these patterns for effective unit tests:

#### Test Class Structure

```kotlin
class DebugAppTest {
    // Test subject
    private lateinit var debugApp: DebugApp
    
    // Mocks
    @Mock
    private lateinit var xposed: XposedInterface
    
    @Mock
    private lateinit var context: Context
    
    @Mock
    private lateinit var packageLoadedParam: PackageLoadedParam
    
    @Before
    fun setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this)
        
        // Configure mock behavior
        whenever(packageLoadedParam.xposed).thenReturn(xposed)
        whenever(packageLoadedParam.packageName).thenReturn("com.android.settings")
        
        // Create test subject
        debugApp = DebugApp()
        debugApp.initialize(context, xposed)
    }
    
    @Test
    fun onPackageLoaded_shouldHookApplicationInfoFlags() {
        // Arrange
        val appInfoClass = mock(Class::class.java)
        val flagsField = mock(Field::class.java)
        
        whenever(xposed.loadClass("android.content.pm.ApplicationInfo")).thenReturn(appInfoClass)
        whenever(appInfoClass.getDeclaredField("flags")).thenReturn(flagsField)
        
        // Act
        debugApp.onPackageLoaded(packageLoadedParam)
        
        // Assert
        verify(xposed).hook(eq(flagsField), eq(DebugApp.DebugFlagHooker::class.java))
    }
    
    @After
    fun tearDown() {
        // Clean up resources
    }
}
```

#### Testing Hookers

```kotlin
class DebugFlagHookerTest {
    @Test
    fun afterHook_shouldAddDebugFlag() {
        // Arrange
        val hooker = DebugApp.DebugFlagHooker()
        val param = mock(HookParam::class.java)
        whenever(param.getResult<Int>()).thenReturn(0)
        
        // Act
        hooker.afterHook(param)
        
        // Assert
        verify(param).setResult(ApplicationInfo.FLAG_DEBUGGABLE)
    }
}
```

### Testing Framework Support

LSPosedKit provides test helpers in the `framework:testing` module:

```kotlin
// Create a mock PackageLoadedParam
val param = MockPackageLoadedParam.create(
    packageName = "com.android.settings",
    classLoader = javaClass.classLoader
)

// Mock Xposed interface
val xposed = MockXposedInterface()

// Test hook registration
val method = String::class.java.getDeclaredMethod("length")
val hookInfo = xposed.hook(method, TestHooker::class.java)

// Verify hook was registered
assertTrue(xposed.isHooked(method))
```

### Running Unit Tests

```bash
# Run all unit tests
./gradlew :modules:debug-app:test

# Run specific test class
./gradlew :modules:debug-app:test --tests "com.example.debugapp.DebugAppTest"

# Run with test coverage
./gradlew :modules:debug-app:testDebugUnitTestCoverage
```

## Integration Testing

Integration tests verify that components work together correctly.

### Setting Up Integration Tests

```kotlin
class SettingsIntegrationTest {
    private lateinit var settingsProvider: SettingsProvider
    private lateinit var debugApp: DebugApp
    
    @Before
    fun setup() {
        // Set up an in-memory settings provider
        val context = ApplicationProvider.getApplicationContext<Context>()
        settingsProvider = SettingsProvider.createForTesting(context)
        
        // Initialize settings with test values
        settingsProvider.edit()
            .putBoolean("enableDebug", true)
            .putString("logLevel", "DEBUG")
            .apply()
        
        // Create test subject with real components
        debugApp = DebugApp()
        debugApp.initialize(context, MockXposedInterface())
    }
    
    @Test
    fun moduleUsesSettingsCorrectly() {
        // Test that the module correctly uses settings values
    }
}
```

### Testing Cross-Module Interactions

```kotlin
class ModuleInteractionTest {
    @Test
    fun ruleProviderIsDiscoveredCorrectly() {
        // Register a rule provider
        val provider = TestRuleProvider()
        FeatureManager.register(IRuleProvider::class.java, provider)
        
        // Verify another module can discover it
        val discoveredProvider = FeatureManager.get(IRuleProvider::class.java)
        assertNotNull(discoveredProvider)
        assertEquals(provider, discoveredProvider)
    }
}
```

## Instrumented Testing

Instrumented tests run on a real Android device or emulator.

### Setting Up Instrumented Tests

Create an `androidTest` directory in your module:

```
modules/
└── debug-app/
    ├── src/
    │   ├── main/
    │   ├── test/
    │   └── androidTest/java/com/example/debugapp/
    │       ├── DebugAppInstrumentedTest.kt
    │       └── HookingTest.kt
    └── build.gradle
```

Add dependencies to your `build.gradle`:

```groovy
dependencies {
    // AndroidX Test
    androidTestImplementation 'androidx.test:runner:1.5.2'
    androidTestImplementation 'androidx.test:rules:1.5.0'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    
    // LSPosed Test Helpers
    androidTestImplementation project(':framework:instrumentation')
}
```

### Writing Instrumented Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class DebugAppInstrumentedTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(TestActivity::class.java)
    
    @Before
    fun setup() {
        // Initialize test environment
        XposedTestHelper.initializeForTests()
    }
    
    @Test
    fun debugFlag_isCorrectlySet() {
        // Get application info
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val appInfo = context.applicationInfo
        
        // Create and apply our hooker
        val hooker = DebugApp.DebugFlagHooker()
        val param = XposedTestHelper.createHookParam(
            method = ApplicationInfo::class.java.getDeclaredField("flags").getter,
            thisObject = appInfo,
            result = appInfo.flags
        )
        
        // Execute the hooker
        hooker.afterHook(param)
        
        // Check the result
        val result = param.getResult<Int>()
        assertTrue((result and ApplicationInfo.FLAG_DEBUGGABLE) != 0)
    }
}
```

### Testing with LSPosed Framework

For tests that require the actual LSPosed framework:

1. Install LSPosedKit on a rooted test device
2. Enable your module in LSPosed Manager
3. Run the instrumented tests:

```bash
./gradlew :modules:debug-app:connectedAndroidTest
```

## Hot-Reload Testing

LSPosedKit's hot-reload feature enables rapid testing cycles.

### Setting Up Hot-Reload Testing

1. Start the development server:

```bash
./gradlew runDevServer
```

2. Install your module on a test device:

```bash
./gradlew :modules:debug-app:installDebug
```

3. Enable your module in LSPosed Manager

### Testing Workflow

1. Make changes to your code
2. Build the module:

```bash
./gradlew :modules:debug-app:assembleDebug
```

3. The changes are automatically hot-reloaded to the device
4. Check logs for verification:

```bash
adb logcat -s LSPK-DebugApp:V | grep "\[HR\]"
```

### Automated Hot-Reload Testing

Create scripts to automate hot-reload testing:

```bash
#!/bin/bash
# test-hot-reload.sh

# Build initial version
./gradlew :modules:debug-app:installDebug

# Make a series of changes and test each
for i in {1..5}; do
    echo "Test iteration $i"
    # Modify source file with sed
    sed -i "s/ORIGINAL_TEXT/MODIFIED_TEXT_$i/" modules/debug-app/src/main/java/com/example/debugapp/DebugApp.kt
    
    # Build and hot-reload
    ./gradlew :modules:debug-app:assembleDebug
    
    # Wait for hot-reload to complete
    sleep 2
    
    # Check logs for success
    if adb logcat -d -s LSPK-HotReload:V | grep -q "Hot reload successful"; then
        echo "Hot-reload test $i passed"
    else
        echo "Hot-reload test $i failed"
        exit 1
    fi
done
```

## Continuous Integration

LSPosedKit provides templates for setting up CI pipelines.

### GitHub Actions

Create a workflow file at `.github/workflows/android.yml`:

```yaml
name: Android CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
      with:
        submodules: 'recursive'
        
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Check code style
      run: ./gradlew ktlintCheck
      
    - name: Run unit tests
      run: ./gradlew test
      
    - name: Build debug APK
      run: ./gradlew assembleDebug
      
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: modules/*/build/outputs/apk/debug/*.apk
```

### GitLab CI

Create a `.gitlab-ci.yml` file:

```yaml
image: openjdk:17-jdk

variables:
  ANDROID_COMPILE_SDK: "35"
  ANDROID_BUILD_TOOLS: "35.0.0"
  ANDROID_SDK_TOOLS: "9477386"

before_script:
  - apt-get --quiet update --yes
  - apt-get --quiet install --yes wget unzip
  - wget --quiet --output-document=android-sdk.zip https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_SDK_TOOLS}_latest.zip
  - unzip -d android-sdk-linux android-sdk.zip
  - echo y | android-sdk-linux/cmdline-tools/bin/sdkmanager "platforms;android-${ANDROID_COMPILE_SDK}" "build-tools;${ANDROID_BUILD_TOOLS}" "platform-tools"
  - export ANDROID_HOME=$PWD/android-sdk-linux
  - export PATH=$PATH:$ANDROID_HOME/platform-tools
  - chmod +x ./gradlew

stages:
  - validate
  - test
  - build

lint:
  stage: validate
  script:
    - ./gradlew ktlintCheck

unit_tests:
  stage: test
  script:
    - ./gradlew test

build_debug:
  stage: build
  script:
    - ./gradlew assembleDebug
  artifacts:
    paths:
      - modules/*/build/outputs/apk/debug/*.apk
```

### Azure DevOps

Create an `azure-pipelines.yml` file:

```yaml
trigger:
- main

pool:
  vmImage: 'ubuntu-latest'

variables:
  GRADLE_USER_HOME: $(Pipeline.Workspace)/.gradle

steps:
- checkout: self
  submodules: true
  
- task: JavaToolInstaller@0
  inputs:
    versionSpec: '17'
    jdkArchitectureOption: 'x64'
    jdkSourceOption: 'PreInstalled'

- task: Cache@2
  inputs:
    key: 'gradle | "$(Agent.OS)" | **/build.gradle'
    restoreKeys: |
      gradle | "$(Agent.OS)"
      gradle
    path: $(GRADLE_USER_HOME)
  displayName: 'Cache Gradle packages'

- script: |
    chmod +x gradlew
    ./gradlew ktlintCheck
  displayName: 'Check code style'

- script: |
    ./gradlew test
  displayName: 'Run unit tests'

- script: |
    ./gradlew assembleDebug
  displayName: 'Build debug APK'

- task: PublishBuildArtifacts@1
  inputs:
    pathtoPublish: 'modules/*/build/outputs/apk/debug'
    artifactName: 'apk'
```

## Test Automation Best Practices

### 1. Test Coverage

Aim for comprehensive test coverage:

- Core functionality (80%+ coverage)
- Hook implementations (100% coverage)
- Settings handling (100% coverage)
- Error cases and edge conditions

Track coverage with:

```bash
./gradlew :modules:debug-app:jacocoTestReport
```

### 2. Test Categories

Organize tests by category:

- **Unit tests**: Fast, isolated, no Android dependencies
- **Integration tests**: Component interactions, may use Android framework
- **Instrumented tests**: Full system testing, requires device/emulator

### 3. Test Data Management

- Use test fixtures for consistent test data
- Avoid hardcoded values in tests
- Create helper methods for test setup

```kotlin
class TestData {
    companion object {
        fun createTestRule(): Rule {
            return Rule("BLOCK", "example.com")
        }
        
        fun createTestSettings(): Map<String, Any> {
            return mapOf(
                "enableDebug" to true,
                "logLevel" to "DEBUG"
            )
        }
    }
}
```

### 4. Test Naming

Use descriptive test names that explain:
- What is being tested
- Under what conditions
- What the expected outcome is

```kotlin
@Test
fun onPackageLoaded_whenDebugEnabled_shouldAddDebugFlagToApplicationInfo()
```

### 5. Mock vs. Real Components

- Mock external dependencies
- Use real implementations for your own code when possible
- Use specialized test doubles for complex dependencies

```kotlin
// Mock XposedInterface for testing hooks
val xposed = MockXposedInterface()

// Real SettingsProvider with test data
val settings = SettingsProvider.createForTesting(context)
settings.edit().putBoolean("enableDebug", true).apply()
```

## Testing Tools Reference

### Framework Testing Tools

LSPosedKit includes specialized testing tools:

#### MockXposedInterface

```kotlin
val xposed = MockXposedInterface()

// Register a hook
val unhooker = xposed.hook(method, TestHooker::class.java)

// Verify a method is hooked
assertTrue(xposed.isHooked(method))

// Get registered hooks
val hooks = xposed.getRegisteredHooks()

// Simulate hook execution
xposed.simulateBeforeHook(method, instance, args)
```

#### MockPackageLoadedParam

```kotlin
val param = MockPackageLoadedParam.create(
    packageName = "com.android.settings",
    classLoader = javaClass.classLoader,
    isFirstLoad = true
)
```

#### TestSettingsProvider

```kotlin
val settings = TestSettingsProvider()
settings.putBoolean("enableDebug", true)
settings.putString("logLevel", "DEBUG")

// Use in your module
val debugEnabled = settings.bool("enableDebug")
```

### Test Utilities

#### HookTestUtil

```kotlin
// Create a hook parameter for testing
val param = HookTestUtil.createHookParam(
    method = method,
    thisObject = instance,
    args = arrayOf("test"),
    result = "result"
)

// Test your hooker
myHooker.beforeHook(param)

// Check the modifications
val modifiedArgs = param.args
val modifiedResult = param.getResult<String>()
```

#### ReflectionTestUtil

```kotlin
// Access private members for testing
val privateField = ReflectionTestUtil.getField(instance, "fieldName")
val privateMethod = ReflectionTestUtil.getMethod(clazz, "methodName", paramTypes)
val result = ReflectionTestUtil.invokeMethod(instance, privateMethod, args)
```

## Test Reporting

### JUnit Reports

Generate HTML test reports:

```bash
./gradlew :modules:debug-app:testDebugUnitTest --tests "com.example.debugapp.*"
```

Reports are located at:
```
modules/debug-app/build/reports/tests/testDebugUnitTest/index.html
```

### Coverage Reports

Generate coverage reports:

```bash
./gradlew :modules:debug-app:jacocoTestReport
```

Reports are located at:
```
modules/debug-app/build/reports/jacoco/jacocoTestReport/html/index.html
```

### CI Integration

Configure your CI pipeline to publish test results:

```yaml
- name: Publish Test Results
  uses: EnricoMi/publish-unit-test-result-action@v2
  if: always()
  with:
    files: modules/*/build/test-results/**/*.xml
```

## Advanced Testing Scenarios

### Testing Module Dependencies

For modules that depend on other modules:

```kotlin
class DependentModuleTest {
    @Before
    fun setup() {
        // Register required dependency services
        FeatureManager.register(IRequiredService::class.java, MockRequiredService())
    }
    
    @Test
    fun moduleWorks_withDependencyPresent() {
        // Test that the module works with the dependency
    }
    
    @Test
    fun moduleFails_gracefully_whenDependencyMissing() {
        // Clear the dependency
        FeatureManager.unregisterAll()
        
        // Test that the module handles missing dependency gracefully
    }
}
```

### Testing Hot-Reload Logic

```kotlin
class HotReloadTest {
    private lateinit var module: TestModule
    private val hooks = mutableListOf<MethodUnhooker<*>>()
    
    @Before
    fun setup() {
        module = TestModule()
        
        // Mock hook creation
        val xposed = MockXposedInterface()
        whenever(xposed.hook(any(), any<Class<*>>())).thenAnswer { invocation ->
            val unhooker = mock(MethodUnhooker::class.java)
            hooks.add(unhooker)
            unhooker
        }
        
        // Initialize module
        module.initialize(mock(Context::class.java), xposed)
        module.onPackageLoaded(MockPackageLoadedParam.create("test"))
    }
    
    @Test
    fun onHotReload_unregistersAllHooks() {
        // Verify hooks were created
        assertTrue(hooks.isNotEmpty())
        
        // Call hot-reload
        module.onHotReload()
        
        // Verify all hooks were unhooked
        hooks.forEach { verify(it).unhook() }
    }
}
```

### Testing Error Handling

```kotlin
class ErrorHandlingTest {
    @Test
    fun module_handlesClassNotFoundGracefully() {
        // Arrange
        val xposed = MockXposedInterface()
        whenever(xposed.loadClass(any())).thenThrow(ClassNotFoundException())
        
        // Act
        val module = TestModule()
        module.initialize(mock(Context::class.java), xposed)
        
        // Assert - should not throw exception
        module.onPackageLoaded(MockPackageLoadedParam.create("test"))
        
        // Verify error was logged
        verify(xposed).logError(any(), any())
    }
}
```

## Conclusion

A comprehensive testing strategy is essential for building reliable LSPosed modules. By combining unit tests, integration tests, instrumented tests, and hot-reload testing with a robust CI pipeline, you can ensure your modules work correctly across devices and Android versions.

Remember:
1. Write tests early and often
2. Use the testing tools provided by LSPosedKit
3. Automate testing through CI pipelines
4. Aim for high test coverage of critical components
5. Test error handling and edge cases thoroughly

Following these best practices will lead to higher quality modules that are easier to maintain and extend. 