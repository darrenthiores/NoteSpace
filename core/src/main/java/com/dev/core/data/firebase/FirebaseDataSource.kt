package com.dev.core.data.firebase

import android.app.Activity
import android.net.Uri
import com.dev.core.BuildConfig
import com.dev.core.data.remote.source.ApiResponse
import com.dev.core.domain.model.data.request.NoteRequest
import com.dev.core.domain.model.data.request.UserRequest
import com.dev.core.domain.model.data.response.NoteResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
    private val noteCollection = fireStore.collection(BuildConfig.NOTESPACE_NOTE_COLLECTION)

    // user related
    fun setUser(user:UserRequest):Task<Void> = userCollection.document(auth.uid!!).set(user)

    fun checkPhoneNumber(phoneNumber:String): Task<QuerySnapshot> = mobileCollection.whereEqualTo("mobile", phoneNumber).get()

    fun setPhoneNumber(phoneNumber: String):Task<Void> = mobileCollection.document(phoneNumber).set(
        hashMapOf(
            "mobile" to phoneNumber
        ))

    fun getUserData():Task<DocumentSnapshot> = userCollection.document(auth.uid!!).get()

    fun getUserById(user_id: String): Task<DocumentSnapshot> = userCollection.document(user_id).get()

    // note related
    fun insertNote(note: NoteRequest): Task<Void> {
        val newNote = note.copy(user_id = auth.uid!!)
        return noteCollection.document(note.note_id).set(newNote)
    }

    fun getNoteById(note_id: String): Task<DocumentSnapshot> = noteCollection.document(note_id).get()

    fun getPopularNote(interest: List<String>): Flow<ApiResponse<List<NoteResponse>>> = callbackFlow {
        val noteCollectionQuery = if(interest.isEmpty()) noteCollection else noteCollection.whereIn("subject", interest)
        val listenerRegistration = noteCollectionQuery
            .orderBy("star", Query.Direction.DESCENDING)
            .limit(8)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(ApiResponse.Error(error.message.toString()))
                    cancel(message = "Error fetching Notes", cause = error)
                    return@addSnapshotListener
                }

                val notes = value?.documents?.mapNotNull {
                    it.toObject(NoteResponse::class.java)
                }

                if(notes.isNullOrEmpty()) {
                    trySend(ApiResponse.Error("Error Fetching Notes: null or empty"))
                } else {
                    trySend(ApiResponse.Success(notes))
                }
            }
        awaitClose {
            listenerRegistration.remove()
        }
    }

    // home search
    fun getFirstHomeSearchedNote(searchText: String): Flow<ApiResponse<List<NoteResponse>>> = callbackFlow {
        val listenerRegistration = noteCollection
            .whereIn("name", listOf(searchText))
            .limit(20)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(ApiResponse.Error(error.message.toString()))
                    cancel(message = "Error fetching Notes", cause = error)
                    return@addSnapshotListener
                }

                val notes = value?.documents?.mapNotNull {
                    it.toObject(NoteResponse::class.java)
                }

                if(notes.isNullOrEmpty()) {
                    trySend(ApiResponse.Error("Error Fetching Notes: null or empty"))
                } else {
                    trySend(ApiResponse.Success(notes))
                }
            }
        awaitClose {
            listenerRegistration.remove()
        }
    }

    fun getNextHomeSearchedNote(searchText: String, lastVisible: String): Flow<ApiResponse<List<NoteResponse>>> = callbackFlow {
        val listenerRegistration = noteCollection
            .whereIn("name", listOf(searchText))
            .whereIn("subject", listOf(searchText))
            .startAfter(lastVisible)
            .limit(20)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(ApiResponse.Error(error.message.toString()))
                    cancel(message = "Error fetching Notes", cause = error)
                    return@addSnapshotListener
                }

                val notes = value?.documents?.mapNotNull {
                    it.toObject(NoteResponse::class.java)
                }

                if(notes.isNullOrEmpty()) {
                    trySend(ApiResponse.Error("Error Fetching Notes: null or empty"))
                } else {
                    trySend(ApiResponse.Success(notes))
                }
            }
        awaitClose {
            listenerRegistration.remove()
        }
    }

    fun getFirstUserNotes(): Flow<ApiResponse<List<NoteResponse>>> = callbackFlow {
        val listenerRegistration = noteCollection
            .whereEqualTo("user_id", auth.uid)
            .limit(20)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(ApiResponse.Error(error.message.toString()))
                    cancel(message = "Error fetching Notes", cause = error)
                    return@addSnapshotListener
                }

                val notes = value?.documents?.mapNotNull {
                    it.toObject(NoteResponse::class.java)
                }

                if(notes.isNullOrEmpty()) {
                    trySend(ApiResponse.Error("Error Fetching Notes: null or empty"))
                } else {
                    trySend(ApiResponse.Success(notes))
                }
            }
        awaitClose {
            listenerRegistration.remove()
        }
    }

    fun getNextUserNotes(lastVisible: String): Flow<ApiResponse<List<NoteResponse>>> = callbackFlow {
        val listenerRegistration = noteCollection
            .whereEqualTo("user_id", auth.uid)
            .startAfter(lastVisible)
            .limit(20)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(ApiResponse.Error(error.message.toString()))
                    cancel(message = "Error fetching Notes", cause = error)
                    return@addSnapshotListener
                }

                val notes = value?.documents?.mapNotNull {
                    it.toObject(NoteResponse::class.java)
                }

                if(notes.isNullOrEmpty()) {
                    trySend(ApiResponse.Error("Error Fetching Notes: null or empty"))
                } else {
                    trySend(ApiResponse.Success(notes))
                }
            }
        awaitClose {
            listenerRegistration.remove()
        }
    }

    /**Storage**/
    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference

    fun insertPdfFile(note_id: String, file: Uri): UploadTask =
        storageReference
            .child("notespace_note/${auth.uid}/${note_id}.pdf")
            .putFile(file)

    fun getPdfFile(user_id: String, note_id: String): StorageReference =
        storageReference
            .child("notespace_note/${user_id}/${note_id}.pdf")
}