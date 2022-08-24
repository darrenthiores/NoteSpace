package com.dev.core.model.domain

import com.google.firebase.firestore.PropertyName

data class UserDomain(
    val name: String,
    val mobile: String,
    val education: String,
    val major: String
)