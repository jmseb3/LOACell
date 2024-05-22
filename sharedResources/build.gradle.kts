plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
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
        ios.deploymentTarget = "16.0"
        framework {
            baseName = "sharedResources"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.moko.resources)
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
    namespace = "com.wonddak.loacell.sharedresources"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.wonddak.loacell" // required
    multiplatformResourcesClassName = "SharedRes" // optional, default MR
    iosBaseLocalizationRegion = "ko" // optional, default "en"
}