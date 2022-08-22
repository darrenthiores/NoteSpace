package com.dev.notespace

import android.app.Application
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@HiltAndroidApp
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