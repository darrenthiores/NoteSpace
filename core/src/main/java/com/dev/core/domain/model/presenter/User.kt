package com.dev.core.domain.model.presenter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val name: String,
    val mobile: String,
    val education: String,
    val major: String,
    val interests: List<String>
): Parcelable
