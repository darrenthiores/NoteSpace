package com.dev.core.data.repository

import android.app.Activity
import com.dev.core.data.dataStore.DataStore
import com.dev.core.data.firebase.FirebaseDataSource
import com.dev.core.data.local.LocalDataSource
import com.dev.core.data.remote.source.RemoteDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteSpaceNoteSpaceRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val dataStore: DataStore,
    private val fbDataSource: FirebaseDataSource
): INoteSpaceRepository {
    override fun getUser(): FirebaseUser? =
        fbDataSource.getUser()

    override fun linkEmail(credential: AuthCredential): Task<AuthResult>? =
        fbDataSource.linkEmail(credential)

    override fun linkPhoneNumber(credential: PhoneAuthCredential): Task<AuthResult>? =
        fbDataSource.linkPhoneNumber(credential)

    override fun sendVerificationCode(
        activity: Activity,
        number: String,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) = fbDataSource.sendVerificationCode(activity, number, callback)

    override fun signInWithCredential(credential: PhoneAuthCredential): Task<AuthResult> =
        fbDataSource.signInWithCredential(credential)

    override fun signInWithEmail(email: String, password: String): Task<AuthResult> =
        fbDataSource.signInWithEmail(email, password)

    override fun createWithEmail(email: String, password: String): Task<AuthResult> =
        fbDataSource.createWithEmail(email, password)
}