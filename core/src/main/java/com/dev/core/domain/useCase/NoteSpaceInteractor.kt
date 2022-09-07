package com.dev.core.domain.useCase

import android.app.Activity
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import com.dev.core.data.Resource
import com.dev.core.domain.repository.INoteSpaceRepository
import com.dev.core.domain.model.domain.NoteDomain
import com.dev.core.domain.model.domain.UserDomain
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteSpaceInteractor @Inject constructor(
    private val repository: INoteSpaceRepository
): NoteSpaceUseCase {
    override fun getUser(): FirebaseUser? =
        repository.getUser()

    override fun logOut() =
        repository.logOut()

    override fun linkEmail(credential: AuthCredential): Task<AuthResult>? =
        repository.linkEmail(credential)

    override fun linkPhoneNumber(credential: PhoneAuthCredential): Task<AuthResult>? =
        repository.linkPhoneNumber(credential)

    override fun sendVerificationCode(
        activity: Activity,
        number: String,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) = repository.sendVerificationCode(activity, number, callback)

    override fun signInWithCredential(credential: PhoneAuthCredential): Task<AuthResult> =
        repository.signInWithCredential(credential)

    override fun setUser(user: UserDomain): Task<Void> =
        repository.setUser(user)

    override suspend fun checkPhoneNumber(phoneNumber: String): Boolean =
        repository.checkPhoneNumber(phoneNumber)

    override fun setPhoneNumber(phoneNumber: String): Task<Void> =
        repository.setPhoneNumber(phoneNumber)

    override suspend fun getUserData(): UserDomain =
        repository.getUserData()

    override suspend fun getUserById(user_id: String): Resource<UserDomain> =
        repository.getUserById(user_id)

    override suspend fun saveInterests(interests: List<String>) =
        repository.saveInterests(interests)

    override suspend fun insertNote(
        name: String,
        description: String,
        subject: String,
        file: Uri
    ): Resource<Any?> =
        repository.insertNote(name, description, subject, file)

    override suspend fun getNoteById(note_id: String): Resource<NoteDomain> =
        repository.getNoteById(note_id)

    override fun getPopularNote(): Flow<Resource<List<NoteDomain>>> =
        repository.getPopularNote()

    override fun getFirstHomeSearchedNote(searchText: String): Flow<Resource<List<NoteDomain>>> =
        repository.getFirstHomeSearchedNote(searchText)

    override fun getNextHomeSearchedNote(
        searchText: String,
        lastVisible: String
    ): Flow<Resource<List<NoteDomain>>> =
        repository.getNextHomeSearchedNote(searchText, lastVisible)

    override fun getFirstUserNotes(): Flow<Resource<List<NoteDomain>>> =
        repository.getFirstUserNotes()

    override fun getNextUserNotes(lastVisible: String): Flow<Resource<List<NoteDomain>>> =
        repository.getNextUserNotes(lastVisible)

    override suspend fun getPdfFile(
        user_id: String, note_id: String, height: Int, width: Int
    ): List<ImageBitmap> =
        repository.getPdfFile(user_id, note_id, height, width)

    override suspend fun getPdfPreview(
        user_id: String, note_id: String, height: Int, width: Int
    ): ImageBitmap? =
        repository.getPdfPreview(user_id, note_id, height, width)
}