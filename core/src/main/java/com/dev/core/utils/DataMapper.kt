package com.dev.core.utils

import com.dev.core.domain.model.data.request.UserRequest
import com.dev.core.domain.model.data.response.NoteResponse
import com.dev.core.domain.model.data.response.StarredResponse
import com.dev.core.domain.model.data.response.UserResponse
import com.dev.core.domain.model.domain.NoteDomain
import com.dev.core.domain.model.domain.StarredNoteDomain
import com.dev.core.domain.model.domain.UserDomain
import com.dev.core.domain.model.presenter.Note
import com.dev.core.domain.model.presenter.StarredNote
import com.dev.core.domain.model.presenter.User

object DataMapper {
    // user
    fun mapUserResponseToDomain(input: UserResponse): UserDomain =
        UserDomain(
            name = input.name,
            mobile = input.mobile,
            education = input.education,
            major = input.major,
            interests = input.interests,
            totalStar = input.total_star
        )

    fun mapUserDomainToPresenter(input: UserDomain): User =
        User(
            name = input.name,
            mobile = input.mobile,
            education = input.education,
            major = input.major,
            interests = input.interests,
            totalStar = input.totalStar
        )

    fun mapUserPresenterToDomain(input: User): UserDomain =
        UserDomain(
            name = input.name,
            mobile = input.mobile,
            education = input.education,
            major = input.major,
            interests = input.interests,
            totalStar = input.totalStar
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
                user_id = it.user_id,
                preview = it.preview,
                version = it.version
            )
        }

    fun mapNoteResponseToDomain(input: NoteResponse): NoteDomain =
        NoteDomain(
            note_id = input.note_id,
            name = input.name,
            description = input.description,
            subject = input.subject,
            star = input.star,
            user_id = input.user_id,
            preview = input.preview,
            version = input.version
        )

    fun mapNotesDomainToPresenter(input: List<NoteDomain>): List<Note> =
        input.map {
            Note(
                note_id = it.note_id,
                name = it.name,
                description = it.description,
                subject = it.subject,
                star = it.star,
                user_id = it.user_id,
                preview = it.preview,
                version = it.version
            )
        }

    fun mapNoteDomainToPresenter(input: NoteDomain): Note =
        Note(
            note_id = input.note_id,
            name = input.name,
            description = input.description,
            subject = input.subject,
            star = input.star,
            user_id = input.user_id,
            preview = input.preview,
            version = input.version
        )

    // star a note related
    fun mapStarredNoteResponseToDomain(input: StarredResponse): StarredNoteDomain =
        StarredNoteDomain(
            user_id = input.user_id,
            note_id = input.note_id
        )

    fun mapStarredNoteResponsesToDomain(input: List<StarredResponse>): List<StarredNoteDomain> =
        input.map {
            StarredNoteDomain(
                user_id = it.user_id,
                note_id = it.note_id
            )
        }

    fun mapStarredNoteDomainToPresenter(input: StarredNoteDomain): StarredNote =
        StarredNote(
            user_id = input.user_id,
            note_id = input.note_id
        )

    fun mapStarredNotesDomainToPresenter(input: List<StarredNoteDomain>): List<StarredNote> =
        input.map {
            StarredNote(
                user_id = it.user_id,
                note_id = it.note_id
            )
        }
}