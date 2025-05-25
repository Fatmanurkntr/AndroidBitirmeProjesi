plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.androidxNavigationSafeargsKotlin)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.kotlinParcelize) // libs.versions.toml'daki kotlinParcelize alias'ına göre
}

android {
    namespace = "com.example.yemeksiparisuygulamasi"
    compileSdk = 35 // jlink hatasını çözmek için 34'te bırakalım

    defaultConfig {
        applicationId = "com.example.yemeksiparisuygulamasi"
        minSdk = 24
        targetSdk = 35 // compileSdk ile aynı
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8 // VEYA VERSION_11
        targetCompatibility = JavaVersion.VERSION_1_8 // VEYA VERSION_11
    }
    kotlinOptions {
        jvmTarget = "1.8" // VEYA "11" (compileOptions ile uyumlu olmalı)
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX Core & UI
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Networking
    implementation(libs.retrofit.main)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.gson.main)

    // Image Loading
    implementation(libs.glide.main)
    kapt(libs.glide.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}