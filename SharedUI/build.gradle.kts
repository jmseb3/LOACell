@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.metro)
}

kotlin {
    android {
        namespace = "com.wonddak.loacell.shared"
        compileSdk = 37
        minSdk = 28
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.add("-Xjdk-release=${JavaVersion.VERSION_17}")
        }
    }


    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SharedUI"
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }
    }

    swiftPMDependencies {
        iosMinimumDeploymentTarget.set("16.0")
        discoverClangModulesImplicitly = false

        swiftPackage(
            url = url("https://github.com/firebase/firebase-ios-sdk.git"),
            version = exact(libs.versions.firebase.ios.sdk.get()),
            products = listOf(
                product("FirebaseAuth"),
                product("FirebaseCore"),
                product("FirebaseFirestore"),
                product("FirebaseStorage"),
            ),
            importedClangModules = listOf(
                "FirebaseAuth",
                "FirebaseCore",
                "FirebaseFirestoreInternal",
                "FirebaseStorage",
            ),
        )
        swiftPackage(
            url = url("https://github.com/google/GoogleSignIn-iOS.git"),
            version = exact(libs.versions.google.signin.ios.get()),
            products = listOf(product("GoogleSignIn")),
            importedClangModules = listOf("GoogleSignIn"),
        )
    }

    
    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.addAll("-Xexpect-actual-classes")
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:model"))
            implementation(project(":core:di"))
            implementation(project(":core:navigation"))
            implementation(project(":core:ui"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.resources)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.ui.tooling.preview)

            implementation(libs.metro.viewmodel)
            implementation(libs.metro.viewmodel.compose)

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutine)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.napier)
            implementation(libs.kotlinx.serialization)
            implementation(libs.androidx.datastore.preferences.core)

            implementation(libs.bundles.ktor)
            implementation(libs.bundles.coil)

            implementation(libs.navigation.compose)
            implementation(project.dependencies.platform(libs.hellogin.bom))
            implementation(libs.hellogin.google)
            implementation(libs.hellogin.apple)

            implementation(libs.capturable)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.compose.ui.test)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)

            implementation(libs.metro.android)
            implementation(libs.ktor.android)

            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.bundles.firebase)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.kakao.share)


            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.googleid)
        }

        iosMain {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")

            dependencies {
                implementation(libs.ktor.ios)
            }
        }

        iosArm64Main {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }

        iosSimulatorArm64Main {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
    }
}
