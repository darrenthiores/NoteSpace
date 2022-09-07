package com.dev.core.utils

import com.dev.core.domain.model.data.request.UserRequest
import com.dev.core.domain.model.data.response.NoteResponse
import com.dev.core.domain.model.data.response.UserResponse
import com.dev.core.domain.model.domain.NoteDomain
import com.dev.core.domain.model.domain.UserDomain
import com.dev.core.domain.model.presenter.Note
import com.dev.core.domain.model.presenter.User

object DataMapper {
    // user
    fun mapUserResponseToDomain(input: UserResponse): UserDomain =
        UserDomain(
            name = input.name,
            mobile = input.mobile,
            education = input.education,
            major = input.major,
            interests = input.interests
        )

    fun mapUserDomainToPresenter(input: UserDomain): User =
        User(
            name = input.name,
            mobile = input.mobile,
            education = input.education,
            major = input.major,
            interests = input.interests
        )

    fun mapUserPresenterToDomain(input: User): UserDomain =
        UserDomain(
            name = input.name,
            mobile = input.mobile,
            education = input.education,
            major = input.major,
            interests = input.interests
        )

    fun mapUserDomainToRequest(input: UserDomain): UserRequest =
        UserRequest(
            name = input.name,
            mobile = input.mobile,
            education = input.education,
            major = input.major,
            interests = input.interests
        )

    // note
    fun mapNotesResponseToDomain(input: List<NoteResponse>): List<NoteDomain> =
        input.map {
            NoteDomain(
                note_id = it.note_id,
                name = it.name,
                description = it.description,
                subject = it.subject,
                star = it.star,
                user_id = it.user_id
            )
        }

    fun mapNoteResponseToDomain(input: NoteResponse): NoteDomain =
        NoteDomain(
            note_id = input.note_id,
            name = input.name,
            description = input.description,
            subject = input.subject,
            star = input.star,
            user_id = input.user_id
        )

    fun mapNotesDomainToPresenter(input: List<NoteDomain>): List<Note> =
        input.map {
            Note(
                note_id = it.note_id,
                name = it.name,
                description = it.description,
                subject = it.subject,
                star = it.star,
                user_id = it.user_id
            )
        }

    fun mapNoteDomainToPresenter(input: NoteDomain): Note =
        Note(
            note_id = input.note_id,
            name = input.name,
            description = input.description,
            subject = input.subject,
            star = input.star,
            user_id = input.user_id
        )
}