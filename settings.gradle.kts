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
    }
}

rootProject.name = "LoaCell"
include(":androidApp")
include(":shared")
include(":sharedApi")
include(":sharedResources")
include(":sharedDatabase")
