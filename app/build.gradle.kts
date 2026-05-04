plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.know_it_all"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.know_it_all"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ✅ Dynamic BASE_URL Logic
        // Pulls from local.properties (e.g., api.url=http://192.168.1.5:8080/api/v1/)
        // Defaults to 10.0.2.2 for Emulator if not found.
        val apiUri = project.findProperty("api.url") ?: "http://10.0.2.2:8080/api/v1/"
        buildConfigField("String", "BASE_URL", "\"$apiUri\"")
    }

    // ✅ Enable BuildConfig generation
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.zxing.core)
    
    // Navigation & Maps
    implementation(libs.navigation.compose)
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    
    // ViewModel & Lifecycle
    implementation(libs.viewmodel.compose)
    implementation(libs.runtime.compose)
    
    // Retrofit & Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    
    // ✅ Security & Crypto (Added for PreferenceManager)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    
    // Room Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    
    // Location Services
    implementation(libs.play.services.location)
    
    // TensorFlow Lite
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.gpu)
    
    // Image Loading
    implementation(libs.coil.compose)
    
    // Accompanist
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.systemuicontroller)
    
    // iText PDF
    implementation(libs.itext.kernel)
    implementation(libs.itext.io)
    implementation(libs.itext.layout)
    implementation(libs.itext.forms)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}