package com.dev.core.data.firebase

import android.app.Activity
import android.content.Intent
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.actionCodeSettings
import java.util.concurrent.TimeUnit

class FirebaseDataSource {

    /**Auth**/
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser

    fun getUser(): FirebaseUser? = user

    fun linkEmail(credential: AuthCredential): Task<AuthResult>? =
        user?.linkWithCredential(credential)

    fun linkPhoneNumber(credential: PhoneAuthCredential): Task<AuthResult>? =
        user?.linkWithCredential(credential)

    fun sendVerificationCode(
        activity: Activity,
        number:String,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithCredential(credential: PhoneAuthCredential): Task<AuthResult> =
        auth.signInWithCredential(credential)

    fun sendEmailLink(
        email: String
    ): Task<Void> {
        val actionCodeSettings = actionCodeSettings {
            url = "https://www.example.com/finishSignUp?cartId=1234"
            handleCodeInApp = true
            setAndroidPackageName(
                "com.example.android",
                true,
                "12")
        }
        return auth.sendSignInLinkToEmail(email, actionCodeSettings)
    }

    fun isSignInLink(emailLink: String): Boolean =
        auth.isSignInWithEmailLink(emailLink)

    fun signInWithEmail(email: String, emailLink: String): Task<AuthResult> =
        auth.signInWithEmailLink(email, emailLink)
}