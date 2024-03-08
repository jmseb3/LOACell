object Versions {
    object Project {
        const val Kotlin = "1.9.23"
        const val Gradle = "8.1.4"
    }


    object Dependencies {
        //All multiplatform libraries
        object KMM {
            const val SQLDelightVersion = "2.0.1"
            const val KotlinSerializationVersion = Versions.Project.Kotlin
            const val ktorVersion = "2.3.5"
            const val mokoVersion = "0.23.0"
        }

        //All Android libraries
        object Android {

            object Compose {
                const val bomVersion = "2024.02.00"
                const val activity = "1.8.0"
            }

            object Firebase {
                const val bomVersion = "32.3.1"
                const val googleAuth = "20.5.0"
                const val googleService = "4.3.15"
                const val crashlyticsGradle = "2.9.5"
            }

        }

        //All iOS libraries
        object iOS {
            object Firebase {
                const val core = "10.16"
                const val firestore = "10.16"
                const val auth = "10.16"
                const val googleAuth = "7.0"
            }
        }
    }

}