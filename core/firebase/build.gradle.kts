import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlinCocoapods)
}

kotlin {
    android {
        namespace = "com.wonddak.loacell.core.firebase"
        compileSdk = 36
        minSdk = 26
        androidResources.enable = true
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
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

    val iosFirebase = "11.3"
    cocoapods {
        version = "1.0"
        ios.deploymentTarget = "16.0"
        framework {
            baseName = "CoreFirebase"
        }
        pod("FirebaseCore") {
            version = iosFirebase
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        // As of Firebase 10.17 Firestore has moved all ObjC headers to FirebaseFirestoreInternal and the kotlin cocoapods plugin does not handle this well
        // Adding it manually seems to resolve the issue
        pod("FirebaseFirestoreInternal") {
            version = iosFirebase
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseFirestore") {
            version = iosFirebase
            extraOpts += listOf("-compiler-option", "-fmodules")
            useInteropBindingFrom("FirebaseFirestoreInternal")
        }
        pod("FirebaseAuth") {
            version = iosFirebase
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseStorage") {
            version = iosFirebase
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("GoogleSignIn") {
            version = "8.0"
            linkOnly = true
        }
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.compose.ui.test)
        }

        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.storage)
            implementation(libs.firebase.auth)
        }

        iosMain.dependencies {

        }
    }
}