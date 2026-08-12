package com.wonddak.loacell.util

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

internal const val LOA_CELL_PREFERENCES = "loaCell_preferences.preferences_pb"

internal fun createDataStoreWithDefaults(
    migrations: List<DataMigration<Preferences>> = emptyList(),
    producePath: () -> String,
) = PreferenceDataStoreFactory
    .createWithPath(
        corruptionHandler = null,
        migrations = migrations,
        produceFile = {
            producePath().toPath()
        }
    )

expect class DataStoreProvider() {
    fun getDataStore(): DataStore<Preferences>
}

@Inject
class Config(provider: DataStoreProvider) {
    private val dataStore = provider.getDataStore()

    suspend fun remove(key: String) {
        dataStore.edit {
            if (it.contains(stringPreferencesKey(key))) {
                it.remove(stringPreferencesKey(key))
            }
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    val defaultUrl: Flow<String>
        get() = dataStore.data.map {
            it[stringPreferencesKey(ConfigKeys.DefaultUrl)] ?: ILOA
        }

    suspend fun updateDefaultUrl(url: String) {
        dataStore.edit {
            it[stringPreferencesKey(ConfigKeys.DefaultUrl)] = url
        }
    }

    val tokenKey : Flow<String?>
        get() = dataStore.data.map {
            it[stringPreferencesKey(ConfigKeys.TOKEN_KEY)]
        }

    suspend fun updateTokenKey(token:String) {
        dataStore.edit {
            it[stringPreferencesKey(ConfigKeys.TOKEN_KEY)] = token
        }
    }

    suspend fun getEventCache(): EventCache? = dataStore.data.first().let { preferences ->
        val events = preferences[stringPreferencesKey(ConfigKeys.EventCache)] ?: return null
        val updatedAt = preferences[stringPreferencesKey(ConfigKeys.EventCacheUpdatedAt)]?.toLongOrNull()
            ?: return null
        EventCache(events = events, updatedAt = updatedAt)
    }

    suspend fun updateEventCache(events: String, updatedAt: Long) {
        dataStore.edit {
            it[stringPreferencesKey(ConfigKeys.EventCache)] = events
            it[stringPreferencesKey(ConfigKeys.EventCacheUpdatedAt)] = updatedAt.toString()
        }
    }

    suspend fun clearEventCache() {
        dataStore.edit {
            it.remove(stringPreferencesKey(ConfigKeys.EventCache))
            it.remove(stringPreferencesKey(ConfigKeys.EventCacheUpdatedAt))
        }
    }
}

data class EventCache(
    val events: String,
    val updatedAt: Long,
)

object ConfigKeys {
    const val DefaultUrl = "default_url"
    const val TOKEN_KEY = "tokenKey"
    const val EventCache = "event_cache"
    const val EventCacheUpdatedAt = "event_cache_updated_at"
}

const val ILOA = "https://iloa.gg/character/"
const val LOAWA = "https://loawa.com/char/"
const val KLOA = "https://m.kloa.gg/characters/"

val UrlList = arrayListOf("일로아" to ILOA, "로아와" to LOAWA, "클로아" to KLOA)
