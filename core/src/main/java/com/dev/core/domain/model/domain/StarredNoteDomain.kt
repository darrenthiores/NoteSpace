package com.dev.core.domain.model.domain

import com.google.firebase.firestore.PropertyName

data class StarredNoteDomain(
    val user_id: String,
    val note_id: String
)
