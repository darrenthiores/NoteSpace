package com.dev.core.domain.model.domain

data class UserDomain(
    val name: String,
    val mobile: String,
    val education: String,
    val major: String,
    val interests: List<String>
)