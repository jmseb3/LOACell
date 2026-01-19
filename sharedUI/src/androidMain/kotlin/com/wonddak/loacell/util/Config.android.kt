package com.wonddak.loacell.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.wonddak.loacell.util.LOA_CELL_PREFERENCES
import com.wonddak.loacell.util.createDataStoreWithDefaults
import org.koin.java.KoinJavaComponent


actual class DataStoreProvider actual constructor() {
    private val context: Context = KoinJavaComponent.getKoin().get()

    private val dataStore = createDataStoreWithDefaults() {
        context.filesDir.resolve(LOA_CELL_PREFERENCES).absolutePath
    }

    actual fun getDataStore(): DataStore<Preferences> {
        return dataStore
    }
}