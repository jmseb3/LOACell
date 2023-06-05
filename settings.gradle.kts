pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "LoaCell"
include(":androidApp")
include(":shared")
include(":sharedApi")
include(":sharedResources")
include(":sharedDatabase")
