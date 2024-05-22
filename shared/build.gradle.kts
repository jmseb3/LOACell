plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
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
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            linkerOpts.add("-lsqlite3")
            export(project(":sharedApi"))
            export(project(":sharedResources"))
            export(project(":sharedDatabase"))
            export("dev.icerock.moko:resources:0.23.0")
        }
        pod("FirebaseCore") {
            version = "10.16"
        }
        pod("FirebaseFirestore") {
            version = "10.16"
        }
        pod("FirebaseAuth") {
            version = "10.16"
        }
        pod("GoogleSignIn") {
            version = "7.0"
        }
    }
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
        commonMain.dependencies {
            api(project(":sharedApi"))
            api(project(":sharedResources"))
            api(project(":sharedDatabase"))
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutine)
            implementation("com.russhwolf:multiplatform-settings:1.0.0")
            implementation("com.russhwolf:multiplatform-settings-coroutines:1.0.0")
            implementation(libs.androidx.lifecycle.viewmodel)

        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.auth)
            implementation(libs.gms.auth)

            implementation("com.russhwolf:multiplatform-settings-datastore:1.0.0")
            implementation("androidx.datastore:datastore-preferences:1.0.0")

            implementation("androidx.credentials:credentials:1.3.0-alpha04")
            // optional - needed for credentials support from play services, for devices running
            // Android 13 and below.
            implementation("androidx.credentials:credentials-play-services-auth:1.3.0-alpha04")
            implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
        }
    }
}

android {
    namespace = "com.wonddak.loacell"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
}