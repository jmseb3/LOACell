
object AppConfig {
    const val AppName = "LoaCell"
    const val versionCode = 1
    const val version = "1.0.0"
    const val group = "com.wonddak.loacell"
    const val databaseName = "Database"

    object Android {
        const val packageName = "$group.android"
        const val compileSdk = 33
        const val minSdk = 30
        const val targetSdk = 33

        const val kotlinCompilerExtensionVersion = "1.4.0"
    }

    object Ios {
        const val deploymentTarget = "14.1"
    }

}