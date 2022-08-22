package com.dev.core.data.local

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val baseDb: BaseDb
) {
}