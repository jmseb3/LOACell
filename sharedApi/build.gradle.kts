plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization") version Versions.Project.Kotlin
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "shared api"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = AppConfig.Ios.deploymentTarget
        framework {
            baseName = "sharedApi"
        }
    }


    sourceSets {
        commonMain.dependencies {
            implementation(Dependencies.KMM.Ktor.Core)
            implementation(Dependencies.KMM.Ktor.Resources)
            implementation(Dependencies.KMM.Ktor.Logging)
            implementation(Dependencies.KMM.Ktor.ContentNegotiation)
            implementation(Dependencies.KMM.Ktor.Serialization)
            implementation(Dependencies.KMM.Kotlinx.DateTime)
            implementation(Dependencies.KMM.Kotlinx.Serialization)
        }

        androidMain.dependencies {
            implementation(Dependencies.KMM.Ktor.Android)
        }

        iosMain.dependencies {
            implementation(Dependencies.KMM.Ktor.Ios)
        }

    }
}

android {
    namespace = AppConfig.Shared.api
    compileSdk = AppConfig.Android.compileSdk
    defaultConfig {
        minSdk = AppConfig.Android.minSdk
    }
}