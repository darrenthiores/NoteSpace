package com.dev.core.domain.model.data.request

import com.google.firebase.firestore.PropertyName

data class StarNoteRequest(
    @PropertyName("user_id")
    val user_id: String,
    @PropertyName("note_id")
    val note_id: String
)