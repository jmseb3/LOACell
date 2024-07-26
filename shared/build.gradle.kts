plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    id("dev.icerock.mobile.multiplatform-resources")
    id("app.cash.sqldelight") version libs.versions.sqldelight.get()
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

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.addAll("-Xexpect-actual-classes")
                }
            }
        }
    }

    cocoapods {
        summary = "shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
            linkerOpts.add("-lsqlite3")
            export("dev.icerock.moko:resources:${libs.moko.resources.get().version}")
        }
        pod("FirebaseCore") {
            version = "10.27.0"
        }
        // As of Firebase 10.17 Firestore has moved all ObjC headers to FirebaseFirestoreInternal and the kotlin cocoapods plugin does not handle this well
        // Adding it manually seems to resolve the issue
        pod("FirebaseFirestoreInternal") {
            version = "10.27.0"
        }
        pod("FirebaseFirestore") {
            version = "10.27.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
            useInteropBindingFrom("FirebaseFirestoreInternal")
        }
        pod("FirebaseAuth") {
            version = "10.27.0"
        }
        pod("FirebaseStorage") {
            version = "10.27.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("GoogleSignIn") {
            version = "7.1"
        }
    }
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
        commonMain.dependencies {
            api(libs.moko.resources)

            api(libs.bundles.koin.shared)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutine)
            implementation(libs.androidx.lifecycle.viewmodel)
            api(libs.napier)
            implementation(libs.kotlinx.serialization)
            implementation(libs.androidx.datastore.preferences.core)

            implementation(libs.bundles.ktor)

            implementation(libs.sqldelight.adapters)
            implementation(libs.sqldelight.coroutines)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            api(libs.koin.android)
            implementation(libs.ktor.android)
            implementation(libs.sqldelight.android)

            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.storage)
            implementation(libs.firebase.auth)
            implementation(libs.gms.auth)
            implementation(libs.androidx.datastore.preferences)


            implementation("androidx.credentials:credentials:1.3.0-alpha04")
            // optional - needed for credentials support from play services, for devices running
            // Android 13 and below.
            implementation("androidx.credentials:credentials-play-services-auth:1.3.0-alpha04")
            implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
        }
        iosMain.dependencies {
            implementation(libs.ktor.ios)
            implementation(libs.sqldelight.ios)
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
multiplatformResources {
    resourcesPackage.set("com.wonddak.loacell")
    resourcesClassName.set("SharedRes")
    iosBaseLocalizationRegion.set("ko")
    iosMinimalDeploymentTarget.set("16.0")
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.wonddak.loacell")
        }
    }
}