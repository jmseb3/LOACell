@file:OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)

package com.wonddak.loacell

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "loaCell")

actual class Config(
    private val datastore : DataStore<Preferences>
) {
    constructor(context: Context) : this(context.dataStore)

    actual val settings : FlowSettings = DataStoreSettings(datastore)
}
