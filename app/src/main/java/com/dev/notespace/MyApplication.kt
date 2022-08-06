package com.dev.notespace

import android.app.Application
import androidx.paging.ExperimentalPagingApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.BuildConfig
import timber.log.Timber

open class MyApplication : Application() {

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}