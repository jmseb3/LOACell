import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

apply("../keystore/signing.gradle")

android {
    namespace = AppConfig.Android.packageName
    compileSdk = AppConfig.Android.compileSdk
    defaultConfig {
        applicationId = AppConfig.Android.packageName
        minSdk = AppConfig.Android.minSdk
        targetSdk = AppConfig.Android.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.version
        setProperty("archivesBaseName", "${applicationId}-v${versionName}(${versionCode})")
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = AppConfig.Android.kotlinCompilerExtensionVersion
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
            signingConfig = signingConfigs.getByName("LoaCellSigning")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isDebuggable = true
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(Modules.shared))

    val composeBom = platform(Dependencies.Android.Compose.Bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(Dependencies.Android.Compose.UIPreview)
    debugImplementation(Dependencies.Android.Compose.UITooling)
    implementation(Dependencies.Android.Compose.Material3)
    implementation(Dependencies.Android.Compose.Activity)
    implementation(Dependencies.Android.Compose.WheelPicker)

    implementation(platform(Dependencies.Android.Firebase.Bom))
    implementation(Dependencies.Android.Firebase.Analytics)
    implementation(Dependencies.Android.Firebase.Crashlytics)
    implementation(Dependencies.Android.Firebase.Auth)
    implementation(Dependencies.Android.Firebase.AuthGoogle)

    implementation("com.kakao.sdk:v2-share:2.14.0") // 메시지(카카오톡 공유)

}