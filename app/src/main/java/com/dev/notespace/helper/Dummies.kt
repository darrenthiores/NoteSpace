package com.dev.notespace.helper

import com.dev.core.domain.model.presenter.Note

// if you need to create dummy data
object Dummies {
    fun dummyNote(): List<Note> = (1..8).map { index ->
        Note(
            note_id = index.toString(),
            name = "note-$index",
            description = "",
            subject = "subject-$index",
            star = 0,
            user_id = "user-$index",
            preview = "$index"
        )
    }
}