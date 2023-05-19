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

rootProject.name = "Loacell"
include(":androidApp")
include(":shared")
include(":sharedApi")
include(":sharedResources")
include(":sharedDatabase")
