pluginManagement {
    repositories {
        maven(System.getenv("NEXUS_PUBLIC")) {
            credentials {
                username = System.getenv("NEXUS_USR")
                password = System.getenv("NEXUS_PSW")
            }
        }
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven(System.getenv("NEXUS_PUBLIC")) {
            credentials {
                username = System.getenv("NEXUS_USR")
                password = System.getenv("NEXUS_PSW")
            }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "Loacell"
include(":androidApp")
include(":shared")