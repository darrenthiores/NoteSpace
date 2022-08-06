package com.dev.core.di

import android.content.Context
import com.dev.core.data.dataStore.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {
    @Provides
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore = DataStore(context)
}