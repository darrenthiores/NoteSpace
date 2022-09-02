package com.dev.core.model.presenter

data class Note(
    val note_id: String,
    val name: String,
    val description: String,
    val subject: String,
    val file: String,
    val star: Int,
    val user_id: String
)
