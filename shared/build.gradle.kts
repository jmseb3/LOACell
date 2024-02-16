plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
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
//            transitiveExport = true
        }
        pod("FirebaseCore", Versions.Dependencies.iOS.Firebase.core)
        pod("FirebaseFirestore", Versions.Dependencies.iOS.Firebase.firestore)
        pod("FirebaseAuth", Versions.Dependencies.iOS.Firebase.auth)
        pod("GoogleSignIn", Versions.Dependencies.iOS.Firebase.googleAuth)
    }
    sourceSets {
        commonMain.dependencies {
            api(project(Modules.api))
            api(project(Modules.resources))
            api(project(Modules.database))
            implementation(Dependencies.KMM.Kotlinx.Coroutines)
            implementation(Dependencies.KMM.Kotlinx.DateTime)
            implementation("com.russhwolf:multiplatform-settings:1.0.0")
            implementation("com.russhwolf:multiplatform-settings-coroutines:1.0.0")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
                implementation(project.dependencies.platform(Dependencies.Android.Firebase.Bom))
                implementation(Dependencies.Android.Firebase.Firestore)
                implementation(Dependencies.Android.Firebase.Auth)
                implementation(Dependencies.Android.Firebase.AuthGoogle)
                implementation("com.russhwolf:multiplatform-settings-datastore:1.0.0")
                implementation("androidx.datastore:datastore-preferences:1.0.0")

                implementation("androidx.credentials:credentials:1.3.0-alpha01")

                // optional - needed for credentials support from play services, for devices running
                // Android 13 and below.
                implementation("androidx.credentials:credentials-play-services-auth:1.3.0-alpha01")

                implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
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