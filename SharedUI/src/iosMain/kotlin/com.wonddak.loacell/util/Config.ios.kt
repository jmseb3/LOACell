package com.wonddak.loacell.util

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.wonddak.loacell.util.LOA_CELL_PREFERENCES
import com.wonddak.loacell.util.createDataStoreWithDefaults
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager
import platform.Foundation.NSLibraryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDomainMask
import dev.zacsweers.metro.Inject

@Inject
actual class DataStoreProvider actual constructor() {
    private val dataStore = createDataStoreWithDefaults(
        listOf(
            NSUserDefaultsMigration(NSUserDefaults.standardUserDefaults)
        )
    ) {
        providePath()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun providePath(): String {
        val filePath: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSLibraryDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(filePath).path + "/$LOA_CELL_PREFERENCES"
    }

    actual fun getDataStore(): DataStore<Preferences> {
        return dataStore
    }
}

internal const val MIGRATION_COMPLETE = "migrationComplete"

class NSUserDefaultsMigration(
    private val sharedPref: NSUserDefaults
) : DataMigration<Preferences> {
    override suspend fun shouldMigrate(currentData: Preferences): Boolean {
        var result = true
        if (sharedPref.boolForKey(MIGRATION_COMPLETE)) {
            result = false
        }
        return result
    }

    override suspend fun migrate(currentData: Preferences): Preferences {
        val currentKeys = currentData.asMap().keys.map { it.name }
        val filteredSharedPreferences =
            sharedPref.dictionaryRepresentation().filter { (key, _) -> key !in currentKeys }

        val mutablePreferences = currentData.toMutablePreferences()
        for ((key, value) in filteredSharedPreferences) {
            if (key is String) {
                when (value) {
                    is Boolean -> mutablePreferences[
                        booleanPreferencesKey(key)
                    ] = value

                    is Float -> mutablePreferences[
                        floatPreferencesKey(key)
                    ] = value

                    is Int -> mutablePreferences[
                        intPreferencesKey(key)
                    ] = value

                    is Long -> mutablePreferences[
                        longPreferencesKey(key)
                    ] = value

                    is String -> mutablePreferences[
                        stringPreferencesKey(key)
                    ] = value

                    is Set<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        mutablePreferences[
                            stringSetPreferencesKey(key)
                        ] = value as Set<String>
                    }
                }
            }
        }

        return mutablePreferences.toPreferences()
    }

    override suspend fun cleanUp() {
        sharedPref.removePersistentDomainForName(
            domainName = NSBundle.mainBundle().bundleIdentifier()!!
        )
        sharedPref.setBool(value = true, forKey = MIGRATION_COMPLETE)
        sharedPref.synchronize()
    }

}
