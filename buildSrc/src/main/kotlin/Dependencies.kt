object Dependencies {
    //All multiplatform libraries
    object KMM {
        object Ktor {
            private const val Base = "io.ktor:ktor-client-"
            const val Core = "${Base}core:${Versions.Dependencies.KMM.ktorVersion}"
            const val Resources = "${Base}resources:${Versions.Dependencies.KMM.ktorVersion}"
            const val Logging = "${Base}logging:${Versions.Dependencies.KMM.ktorVersion}"
            const val ContentNegotiation =
                "${Base}content-negotiation:${Versions.Dependencies.KMM.ktorVersion}"
            const val Serialization =
                "io.ktor:ktor-serialization-kotlinx-json:${Versions.Dependencies.KMM.ktorVersion}"

            const val Android = "${Base}android:${Versions.Dependencies.KMM.ktorVersion}"
            const val Ios = "${Base}darwin:${Versions.Dependencies.KMM.ktorVersion}"
        }

        object MOKO {
            private const val Base = "dev.icerock.moko:"
            const val Generator =
                "${Base}resources-generator:${Versions.Dependencies.KMM.mokoVersion}"
            const val Core = "${Base}resources:${Versions.Dependencies.KMM.mokoVersion}"
        }

        object SQLDelight {
            private const val Base = "app.cash.sqldelight:"
            const val Adapter =
                "${Base}primitive-adapters:${Versions.Dependencies.KMM.SQLDelightVersion}"
            const val Coroutine =
                "${Base}coroutines-extensions:${Versions.Dependencies.KMM.SQLDelightVersion}"
            const val Android =
                "${Base}android-driver:${Versions.Dependencies.KMM.SQLDelightVersion}"
            const val Ios = "${Base}native-driver:${Versions.Dependencies.KMM.SQLDelightVersion}"
        }

    }

    //All Android libraries
    object Android {

        object Compose {
            const val Bom =
                "androidx.compose:compose-bom:${Versions.Dependencies.Android.Compose.bomVersion}"
            const val UIPreview = "androidx.compose.ui:ui-tooling-preview"
            const val UITooling = "androidx.compose.ui:ui-tooling"
            const val Material3 = "androidx.compose.material3:material3:1.2.0-alpha01"
            const val Activity =
                "androidx.activity:activity-compose:${Versions.Dependencies.Android.Compose.activity}"

            const val BottomDialog =
                "com.holix.android:bottomsheetdialog-compose:${Versions.Dependencies.Android.Compose.bottomDialog}"
        }

        object Firebase {
            const val Bom =
                "com.google.firebase:firebase-bom:${Versions.Dependencies.Android.Firebase.bomVersion}"
            const val Analytics = "com.google.firebase:firebase-analytics-ktx"
            const val Crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
            const val Firestore = "com.google.firebase:firebase-firestore-ktx"
            const val Auth = "com.google.firebase:firebase-auth-ktx"
            const val AuthGoogle =
                "com.google.android.gms:play-services-auth:${Versions.Dependencies.Android.Firebase.googleAuth}"
            const val GoogleService =
                "com.google.gms:google-services:${Versions.Dependencies.Android.Firebase.googleService}"
            const val CrashlyticsPlugin =
                "com.google.firebase:firebase-crashlytics-gradle:${Versions.Dependencies.Android.Firebase.crashlyticsGradle}"

        }

    }

    //All iOS libraries
    object iOS {

    }
}
