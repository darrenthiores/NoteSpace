package com.dev.core.model.data.response

import com.google.firebase.firestore.PropertyName

data class UserResponse(
    @PropertyName("name")
    val name: String,
    @PropertyName("mobile")
    val mobile: String,
    @PropertyName("education")
    val education: String,
    @PropertyName("major")
    val major: String
)