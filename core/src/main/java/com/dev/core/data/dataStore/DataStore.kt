package com.dev.core.data.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dev.core.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = BuildConfig.DATA_STORE_NAME)

@Singleton
class DataStore @Inject constructor(
    private val context: Context
) {
    private val _interests = stringPreferencesKey(BuildConfig.DATA_STORE_NAME)
    val interests: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[_interests] ?: "[]"
        }

    suspend fun saveInterests(interest: String) {
        context.dataStore.edit { settings ->
            settings[_interests] = interest
        }
    }
}