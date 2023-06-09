package com.dev.core.domain.model.data.response

import com.google.firebase.firestore.PropertyName

data class NoteResponse(
    @PropertyName("note_id")
    val note_id: String = "",
    @PropertyName("name")
    val name: String = "",
    @PropertyName("description")
    val description: String = "",
    @PropertyName("subject")
    val subject: String = "",
    @PropertyName("star")
    val star: Int = 0,
    @PropertyName("user_id")
    val user_id: String = "",
    @PropertyName("preview")
    val preview: String = "",
    @PropertyName("type")
    val type: String = "",
    @PropertyName("image_urls")
    val image_urls: List<String> = emptyList(),
    @PropertyName("texts")
    val texts: List<String> = emptyList(),
    @PropertyName("keywords")
    val keywords: List<String> = emptyList(),
    @PropertyName("status")
    val status: Int = 1,
    @PropertyName("version")
    val version: Int = 0
)
