plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
}
kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "share Resources"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = AppConfig.Ios.deploymentTarget
        framework {
            baseName = "sharedResources"
            isStatic = true
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Dependencies.KMM.MOKO.Core)
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
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
    namespace = AppConfig.Shared.resoureces
    compileSdk = AppConfig.Android.compileSdk
    defaultConfig {
        minSdk = AppConfig.Android.minSdk
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.wonddak.loacell" // required
    multiplatformResourcesClassName = "SharedRes" // optional, default MR
    iosBaseLocalizationRegion = "ko" // optional, default "en"
}