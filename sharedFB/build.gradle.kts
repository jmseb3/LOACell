plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        pod("FirebaseFirestore","~> 10.10.0")
        pod("FirebaseFirestoreSwift","~> 10.10.0")
        noPodspec()
        framework {
            baseName = "sharedFB"
            isStatic = true
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {

            }
        }
        val androidMain by getting {
            dependencies {
                implementation(platform(Dependencies.Android.Firebase.Bom))
                implementation(Dependencies.Android.Firebase.Firestore)
            }
        }
    }
}

android {
    namespace = "com.wonddak.sharedfb"
    compileSdk = AppConfig.Android.compileSdk
    defaultConfig {
        minSdk = AppConfig.Android.minSdk
    }
}