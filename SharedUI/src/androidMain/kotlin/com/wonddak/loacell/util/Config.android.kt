package com.wonddak.loacell.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.wonddak.loacell.util.LOA_CELL_PREFERENCES
import com.wonddak.loacell.util.createDataStoreWithDefaults
import dev.zacsweers.metro.Inject


actual class DataStoreProvider actual constructor() {
    private lateinit var context: Context

    @Inject
    constructor(context: Context) : this() {
        this.context = context.applicationContext
    }

    private val preferencesDataStore by lazy { createDataStoreWithDefaults() {
        context.filesDir.resolve(LOA_CELL_PREFERENCES).absolutePath
    } }

    actual fun getDataStore(): DataStore<Preferences> {
        return preferencesDataStore
    }
}
