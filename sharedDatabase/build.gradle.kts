plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("app.cash.sqldelight") version Versions.Dependencies.KMM.SQLDelightVersion
}

kotlin {
    androidTarget() {
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
        summary = "share db"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = AppConfig.Ios.deploymentTarget
        framework {
            baseName = "sharedDatabase"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(Dependencies.KMM.Kotlinx.Coroutines)
            implementation(Dependencies.KMM.SQLDelight.Adapter)
            implementation(Dependencies.KMM.SQLDelight.Coroutine)
            implementation(Dependencies.KMM.Kotlinx.DateTime)
        }

        androidMain.dependencies {
            implementation(Dependencies.KMM.SQLDelight.Android)
        }

        iosMain.dependencies {
            implementation(Dependencies.KMM.SQLDelight.Ios)
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