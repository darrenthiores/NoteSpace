package com.dev.core.domain

import android.app.Activity
import com.dev.core.data.repository.INoteSpaceRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteSpaceInteractor @Inject constructor(
    private val repository: INoteSpaceRepository
): NoteSpaceUseCase {
    override fun getUser(): FirebaseUser? =
        repository.getUser()

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

    override fun signInWithEmail(email: String, password: String): Task<AuthResult> =
        repository.signInWithEmail(email, password)

    override fun createWithEmail(email: String, password: String): Task<AuthResult> =
        repository.createWithEmail(email, password)
}