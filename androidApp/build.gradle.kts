import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

apply("../keystore/signing.gradle")

android {
    namespace = "com.wonddak.loacell.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.wonddak.loacell.android"
        minSdk = 26
        targetSdk = 33
        versionCode = 10
        versionName = "1.1.3"
        setProperty("archivesBaseName", "${applicationId}-v${versionName}(${versionCode})")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    api(project(Modules.shared))

    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.bundles.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation("com.github.IamCheng5:ComposeWheelPicker:1.1")
    implementation(libs.wheel.picker)
    implementation(libs.kakao.share)
    implementation(libs.browser)
    implementation(libs.capturable)
}