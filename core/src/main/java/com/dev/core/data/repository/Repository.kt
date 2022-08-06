package com.dev.core.data.repository

import com.dev.core.data.dataStore.DataStore
import com.dev.core.data.firebase.FirebaseDataSource
import com.dev.core.data.local.LocalDataSource
import com.dev.core.data.remote.source.RemoteDataSource

class Repository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val dataStore: DataStore,
    private val fbDataSource: FirebaseDataSource
): IRepository {
}