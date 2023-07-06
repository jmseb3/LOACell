
object AppConfig {
    const val AppName = "LoaCell"
    const val versionCode = 6
    const val version = "1.0.3"
    const val group = "com.wonddak"
    const val loaCellgroup = "$group.loacell"
    const val databaseName = "Database"

    object Shared {
        const val api = "$group.sharedapi"
        const val database = "$group.database"
        const val resoureces = "$group.sharedresources"
    }
    object Android {
        const val packageName = "$loaCellgroup.android"
        const val compileSdk = 33
        const val minSdk = 26
        const val targetSdk = 33

        const val kotlinCompilerExtensionVersion = "1.4.7"
    }

    object Ios {
        const val deploymentTarget = "14.1"
    }

}