@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android {
        namespace = "com.wonddak.loacell.shared"
        compileSdk = 37
        minSdk = 26
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
            baseName = "ComposeApp"
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }
    }

    swiftPMDependencies {
        iosMinimumDeploymentTarget.set("16.0")
        discoverClangModulesImplicitly = false

        swiftPackage(
            url = url("https://github.com/firebase/firebase-ios-sdk.git"),
            version = exact("11.3.0"),
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
            version = exact("8.0.0"),
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
            implementation("org.jetbrains.compose.runtime:runtime:1.11.1")
            implementation("org.jetbrains.compose.foundation:foundation:1.11.1")
            implementation("org.jetbrains.compose.material3:material3:1.9.0")
            implementation("org.jetbrains.compose.components:components-resources:1.11.1")
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
            implementation("org.jetbrains.compose.components:components-ui-tooling-preview:1.11.1")

            implementation(libs.bundles.koin.shared)

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutine)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.napier)
            implementation(libs.kotlinx.serialization)
            implementation(libs.androidx.datastore.preferences.core)

            implementation(libs.bundles.ktor)
            implementation(libs.bundles.coil)

            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.2")
            implementation(project.dependencies.platform(libs.hellogin.bom))
            implementation(libs.hellogin.google.ui)
            implementation(libs.hellogin.apple.ui)

            implementation("io.github.jmseb3:capturable:1.0.0")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.compose.ui:ui-test:1.11.1")
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)

            implementation(libs.koin.android)
            implementation(libs.ktor.android)

            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.bundles.firebase)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.kakao.share)


            implementation("androidx.credentials:credentials:1.3.0")
            implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
            implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
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
