package com.dev.core.data.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dev.core.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = BuildConfig.DATA_STORE_NAME)

@Singleton
class DataStore @Inject constructor(
    private val context: Context
) {

}