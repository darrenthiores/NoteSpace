package com.dev.core.domain.model.data.response

import com.google.firebase.firestore.PropertyName


data class StarredResponse(
    @PropertyName("user_id")
    val user_id: String = "",
    @PropertyName("note_id")
    val note_id: String = ""
)