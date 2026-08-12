import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.googleGmsService)
    alias(libs.plugins.metro)
}

apply(from = rootProject.file("keystore/signing.gradle"))

android {
    namespace = "com.wonddak.loacell"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.wonddak.loacell.android"
        minSdk = 28
        targetSdk = 36
        versionCode = 14
        versionName = "2.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfigs.findByName("LoaCellSigning")?.let {
                signingConfig = it
            }
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":SharedUI"))
    implementation(project(":core:common"))
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.metro.android)
    implementation(libs.metro.viewmodel)
    implementation(libs.metro.viewmodel.compose)
    implementation(libs.kakao.share)
    implementation(libs.napier)
    implementation(project.dependencies.platform(libs.hellogin.bom))
    implementation(libs.hellogin.google)
    implementation(libs.navigation.compose)

    implementation(project.dependencies.platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
}
