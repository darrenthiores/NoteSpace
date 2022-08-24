package com.dev.core.data.repository

import android.app.Activity
import com.dev.core.model.domain.UserDomain
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import java.util.concurrent.TimeUnit

interface INoteSpaceRepository {
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
}