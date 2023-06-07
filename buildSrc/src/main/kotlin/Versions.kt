object Versions {
    object Project {
        const val Kotlin = "1.8.21"
        const val Gradle = "7.4.1"
    }


    object Dependencies {
        //All multiplatform libraries
        object KMM {
            const val SQLDelightVersion = "2.0.0-rc01"
            const val KotlinSerializationVersion = Versions.Project.Kotlin
            const val ktorVersion = "2.2.1"
            const val mokoVersion = "0.22.0"
        }

        //All Android libraries
        object Android {

            object Compose {
                const val bomVersion = "2023.01.00"
                const val activity = "1.7.1"
            }

            object Firebase {
                const val bomVersion = "31.5.0"
                const val googleAuth = "20.5.0"
                const val googleService = "4.3.15"
                const val crashlyticsGradle = "2.9.5"

            }

        }

        //All iOS libraries
        object iOS {

        }
    }

}