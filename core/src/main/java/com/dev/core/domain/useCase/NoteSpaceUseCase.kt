package com.dev.core.domain.useCase

import android.app.Activity
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import com.dev.core.data.Resource
import com.dev.core.domain.model.domain.NoteDomain
import com.dev.core.domain.model.domain.UserDomain
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.coroutines.flow.Flow

interface NoteSpaceUseCase {
    // user related
    fun getUser(): FirebaseUser?

    fun logOut()

    fun linkEmail(credential: AuthCredential): Task<AuthResult>?

    fun linkPhoneNumber(credential: PhoneAuthCredential): Task<AuthResult>?

    fun sendVerificationCode(
        activity: Activity,
        number:String,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )

    fun signInWithCredential(credential: PhoneAuthCredential): Task<AuthResult>

    fun setUser(user: UserDomain): Task<Void>

    suspend fun checkPhoneNumber(phoneNumber:String): Boolean

    fun setPhoneNumber(phoneNumber: String):Task<Void>

    suspend fun getUserData(): UserDomain

    suspend fun getUserById(user_id: String): Resource<UserDomain>

    suspend fun saveInterests(interests: List<String>)

    // note related
    suspend fun insertNote(name: String, description: String, subject: String, file: Uri): Resource<Any?>

    suspend fun getNoteById(note_id: String): Resource<NoteDomain>

    fun getPopularNote(): Flow<Resource<List<NoteDomain>>>

    fun getFirstHomeSearchedNote(searchText: String): Flow<Resource<List<NoteDomain>>>

    fun getNextHomeSearchedNote(searchText: String, lastVisible: String): Flow<Resource<List<NoteDomain>>>

    fun getFirstUserNotes(): Flow<Resource<List<NoteDomain>>>

    fun getNextUserNotes(lastVisible: String): Flow<Resource<List<NoteDomain>>>

    // storage
    suspend fun getPdfFile(user_id: String, note_id: String, height: Int, width: Int): List<ImageBitmap>

    suspend fun getPdfPreview(user_id: String, note_id: String, height: Int, width: Int): ImageBitmap?
}