package com.dev.core.model.data.request

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
    @PropertyName("file")
    val file: String,
    @PropertyName("user_id")
    val user_id: String
)
