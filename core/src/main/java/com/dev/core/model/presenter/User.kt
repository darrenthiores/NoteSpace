package com.dev.core.model.presenter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val name: String,
    val mobile: String,
    val education: String,
    val major: String
): Parcelable
