plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("app.cash.sqldelight") version Versions.Dependencies.KMM.SQLDelightVersion
}

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "share db"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = AppConfig.Ios.deploymentTarget
        framework {
            baseName = "sharedDatabase"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Dependencies.KMM.Kotlinx.Coroutines)
                implementation(Dependencies.KMM.SQLDelight.Adapter)
                implementation(Dependencies.KMM.SQLDelight.Coroutine)
                implementation(Dependencies.KMM.Kotlinx.DateTime)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Dependencies.KMM.SQLDelight.Android)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(Dependencies.KMM.SQLDelight.Ios)
            }
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    namespace = AppConfig.Shared.database
    compileSdk = AppConfig.Android.compileSdk
    defaultConfig {
        minSdk = AppConfig.Android.minSdk
    }
}

sqldelight {
    databases {
        create(AppConfig.databaseName) {
            packageName.set(AppConfig.loaCellgroup)
        }
    }
}