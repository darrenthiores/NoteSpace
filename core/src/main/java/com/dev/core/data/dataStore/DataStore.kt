package com.dev.core.data.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dev.core.BuildConfig

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = BuildConfig.DATA_STORE_NAME)

class DataStore(
    private val context: Context
) {

}