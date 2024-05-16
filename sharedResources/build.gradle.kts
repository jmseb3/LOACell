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

    targets.configureEach {
        compilations.configureEach {
            compilerOptions.configure {
                freeCompilerArgs.addAll( "-Xexpect-actual-classes")
            }
        }
    }

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