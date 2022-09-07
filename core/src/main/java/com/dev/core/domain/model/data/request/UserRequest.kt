package com.dev.core.domain.model.data.request

import com.google.firebase.firestore.PropertyName

data class UserRequest(
    @PropertyName("name")
    val name: String,
    @PropertyName("mobile")
    val mobile: String,
    @PropertyName("education")
    val education: String,
    @PropertyName("major")
    val major: String,
    @PropertyName("interests")
    val interests: List<String>
)