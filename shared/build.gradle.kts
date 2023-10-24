plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
}

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = AppConfig.Ios.deploymentTarget
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            linkerOpts.add("-lsqlite3")
            export(project(Modules.api))
            export(project(Modules.resources))
            export("dev.icerock.moko:resources:0.23.0")
            export(project(Modules.database))
            transitiveExport = true
        }
        pod("FirebaseCore", Versions.Dependencies.iOS.Firebase.core)
        pod("FirebaseFirestore", Versions.Dependencies.iOS.Firebase.firestore)
        pod("FirebaseAuth", Versions.Dependencies.iOS.Firebase.auth)
        pod("GoogleSignIn", Versions.Dependencies.iOS.Firebase.googleAuth)
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(Modules.api))
                api(project(Modules.resources))
                api(project(Modules.database))
                implementation(Dependencies.KMM.Kotlinx.Coroutines)
                implementation(Dependencies.KMM.Kotlinx.DateTime)
                implementation("com.russhwolf:multiplatform-settings:1.0.0")
                implementation("com.russhwolf:multiplatform-settings-coroutines:1.0.0")

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(platform(Dependencies.Android.Firebase.Bom))
                implementation(Dependencies.Android.Firebase.Firestore)
                implementation(Dependencies.Android.Firebase.Auth)
                implementation(Dependencies.Android.Firebase.AuthGoogle)
                implementation("com.russhwolf:multiplatform-settings-datastore:1.0.0")
                implementation("androidx.datastore:datastore-preferences:1.0.0")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    namespace = AppConfig.loaCellgroup
    compileSdk = AppConfig.Android.compileSdk
    defaultConfig {
        minSdk = AppConfig.Android.minSdk
    }
}