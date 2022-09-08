package com.dev.core.domain.model.domain

data class NoteDomain(
    val note_id: String,
    val name: String,
    val description: String,
    val subject: String,
    val star: Int,
    val user_id: String,
    val preview: String
)
