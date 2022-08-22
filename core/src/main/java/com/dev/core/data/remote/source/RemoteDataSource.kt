package com.dev.core.data.remote.source

import com.dev.core.data.remote.service.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
}