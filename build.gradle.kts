buildscript {
    dependencies {
        classpath(Plugins.Google)
        classpath(Plugins.Crashlytics)
        classpath(Plugins.MokoResourceGenerator)
    }
}

plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version(Versions.Project.Gradle).apply(false)
    id("com.android.library").version(Versions.Project.Gradle).apply(false)
    kotlin("android").version(Versions.Project.Kotlin).apply(false)
    kotlin("multiplatform").version(Versions.Project.Kotlin).apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
