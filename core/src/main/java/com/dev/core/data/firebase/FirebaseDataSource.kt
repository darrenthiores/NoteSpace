package com.dev.core.data.firebase

import android.app.Activity
import android.content.Intent
import com.dev.core.BuildConfig
import com.dev.core.model.data.request.UserRequest
import com.dev.core.model.data.response.UserResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import java.util.concurrent.TimeUnit

class FirebaseDataSource {

    /**Auth**/
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser

    fun getUser(): FirebaseUser? = user

    fun logOut()= auth.signOut()

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

    /**Firestore**/
    private val fireStore = FirebaseFirestore.getInstance()
    private val userCollection = fireStore.collection(BuildConfig.NOTESPACE_USER_COLLECTION)
    private val mobileCollection = fireStore.collection(BuildConfig.NOTESPACE_NUMBER_COLLECTION)
    private val postCollection = fireStore.collection(BuildConfig.NOTESPACE_POST_COLLECTION)

    // user related
    fun setUser(user:UserRequest):Task<Void> = userCollection.document(auth.uid!!).set(user)

    fun checkPhoneNumber(phoneNumber:String): Task<QuerySnapshot> = mobileCollection.whereEqualTo("mobile", phoneNumber).get()

    fun setPhoneNumber(phoneNumber: String):Task<Void> = mobileCollection.document(phoneNumber).set(
        hashMapOf(
            "mobile" to phoneNumber
        ))

    fun getUserData():Task<DocumentSnapshot> = userCollection.document(auth.uid!!).get()

    /**Storage**/
    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference

}