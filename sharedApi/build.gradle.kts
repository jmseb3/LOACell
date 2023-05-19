plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization") version "1.8.10"
}

kotlin {
    android()
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
        val commonMain by getting {
            dependencies {
                implementation(Dependencies.KMM.Ktor.Core)
                implementation(Dependencies.KMM.Ktor.Resources)
                implementation(Dependencies.KMM.Ktor.Logging)
                implementation(Dependencies.KMM.Ktor.ContentNegotiation)
                implementation(Dependencies.KMM.Ktor.Serialization)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Dependencies.KMM.Ktor.Android)
            }
        }
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(Dependencies.KMM.Ktor.Ios)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
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