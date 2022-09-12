package com.dev.core.domain.model.data.response

import com.google.firebase.firestore.PropertyName

data class UserResponse(
    @PropertyName("name")
    val name: String = "",
    @PropertyName("mobile")
    val mobile: String = "",
    @PropertyName("education")
    val education: String = "",
    @PropertyName("major")
    val major: String = "",
    @PropertyName("interests")
    val interests: List<String> = emptyList(),
    @PropertyName("total_star")
    val total_star: Int = 0
)