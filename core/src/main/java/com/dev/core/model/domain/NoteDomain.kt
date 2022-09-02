package com.dev.core.model.domain

import com.google.firebase.firestore.PropertyName

data class NoteDomain(
    val note_id: String,
    val name: String,
    val description: String,
    val subject: String,
    val file: String,
    val star: Int,
    val user_id: String
)
