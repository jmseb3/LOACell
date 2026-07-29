plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidMultiplatformLibrary).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.kotlinSerialization).apply(false)
    alias(libs.plugins.metro).apply(false)

    alias(libs.plugins.firebaseCrashlytics).apply(false)
    alias(libs.plugins.googleGmsService).apply(false)
}
