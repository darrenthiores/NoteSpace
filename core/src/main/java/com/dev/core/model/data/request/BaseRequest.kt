package com.dev.core.model.data.request

import kotlinx.serialization.Serializable

@Serializable
data class BaseRequest(
    val id: Int
)