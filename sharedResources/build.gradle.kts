plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
}
kotlin {
    androidTarget {
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
        commonMain.dependencies {
            api(Dependencies.KMM.MOKO.Core)
        }
        androidMain {
            dependsOn(commonMain.get())
        }
        iosMain {
            dependsOn(commonMain.get())
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