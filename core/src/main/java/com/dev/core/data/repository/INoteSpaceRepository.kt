package com.dev.core.data.repository

import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

interface INoteSpaceRepository {
    fun getUser(): FirebaseUser?

    fun linkEmail(credential: AuthCredential): Task<AuthResult>?

    fun linkPhoneNumber(credential: PhoneAuthCredential): Task<AuthResult>?

    fun sendVerificationCode(
        activity: Activity,
        number:String,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )

    fun signInWithCredential(credential: PhoneAuthCredential): Task<AuthResult>

    fun sendEmailLink(email: String): Task<Void>

    fun isSignInLink(emailLink: String): Boolean

    fun signInWithEmail(email: String, emailLink: String): Task<AuthResult>
}