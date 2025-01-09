plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.prima_pagina"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.prima_pagina"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0") //pentru diagrama

    implementation("androidx.biometric:biometric:1.1.0") //pentru amprenta

    //pentru cast de la double la float pentru total cheltuieli
    implementation ("org.apache.commons:commons-lang3:3.12.0")

    implementation ("androidx.core:core:1.13.1")

    implementation ("commons-codec:commons-codec:1.15") // pt a decoda parola

    implementation ("com.google.android.material:material:1.x.x") //pentru afisare la utilizator parola introdusa

    // pentru a trimite notificari
    implementation ("androidx.core:core-ktx:VERSION_CODE")
    implementation ("androidx.appcompat:appcompat:VERSION_CODE")

}


