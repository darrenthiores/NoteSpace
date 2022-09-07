package com.dev.core.domain.model.data.request

import com.google.firebase.firestore.PropertyName

data class NoteRequest(
    @PropertyName("note_id")
    val note_id: String,
    @PropertyName("name")
    val name: String,
    @PropertyName("description")
    val description: String,
    @PropertyName("subject")
    val subject: String,
    @PropertyName("user_id")
    val user_id: String,
    @PropertyName("star")
    val star: Int = 0
)