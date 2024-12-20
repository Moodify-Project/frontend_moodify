plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
//    id("com.google.gms.google-services")
    id("com.google.gms.google-services") version "4.4.2"
}

android {
    namespace = "com.example.frontend_moodify"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.frontend_moodify"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)


    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.glide)
    implementation(libs.okhttp)
    implementation (libs.androidx.paging.runtime)
    implementation (libs.jetbrains.kotlinx.coroutines.android)
    implementation (libs.logging.interceptor)
    implementation (libs.androidx.datastore.preferences)
    implementation (libs.jwtdecode)

    implementation (libs.material)
    implementation (libs.androidx.core.animation)
    implementation (libs.androidx.work.runtime.ktx)
    implementation (platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation (libs.firebase.messaging)



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.volley)
    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.appcompat.v131)
    

}