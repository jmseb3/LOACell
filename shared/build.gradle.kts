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
            export(project(Modules.database))
            transitiveExport = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(Modules.api))
                api(project(Modules.resources))
                api(project(Modules.database))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
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