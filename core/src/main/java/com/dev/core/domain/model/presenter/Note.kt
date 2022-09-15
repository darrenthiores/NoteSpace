package com.dev.core.domain.model.presenter

data class Note(
    val note_id: String,
    val name: String,
    val description: String,
    val subject: String,
    val star: Int,
    val user_id: String,
    val preview: String,
    val version: Int
)
