import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android {
        namespace = "com.wonddak.loacell"
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
        summary = "shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "ComposeApp"
            isStatic = true
            linkerOpts.add("-lsqlite3")
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
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material3)
            api(compose.components.resources)
            api(compose.materialIconsExtended)
            api(compose.components.uiToolingPreview)

            implementation(libs.bundles.koin.shared)

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutine)
            implementation(libs.androidx.lifecycle.viewmodel)
            api(libs.napier)
            api(libs.kotlinx.serialization)
            implementation(libs.androidx.datastore.preferences.core)

            implementation(libs.bundles.ktor)
            implementation(libs.bundles.coil)

            api("org.jetbrains.androidx.navigation:navigation-compose:2.9.1")
            implementation(project.dependencies.platform(libs.hellogin.bom))
            implementation(libs.hellogin.google.ui)
            implementation(libs.hellogin.apple.ui)

            implementation("io.github.jmseb3:capturable:1.0.0")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            api(libs.ktor.android)
            implementation(libs.kakao.share)

            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.bundles.firebase)
            implementation(libs.androidx.datastore.preferences)


            implementation("androidx.credentials:credentials:1.5.0")
            implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
            implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
        }

        iosMain.dependencies {
            implementation(libs.ktor.ios)
        }
    }
}