package com.dev.core.data.firebase

import android.app.Activity
import android.net.Uri
import com.dev.core.BuildConfig
import com.dev.core.domain.model.data.request.NoteRequest
import com.dev.core.domain.model.data.request.StarNoteRequest
import com.dev.core.domain.model.data.request.UserRequest
import com.dev.core.domain.model.data.response.NoteResponse
import com.dev.core.domain.model.data.response.StarredResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*
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
    private val noteStarredCollection = fireStore.collection(BuildConfig.NOTESPACE_STAR_COLLECTION)

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

    fun getNoteById(note_id: String): Task<DocumentSnapshot> = noteCollection
        .document(note_id)
        .get()

    fun getPopularNote(interest: List<String>): Flow<ApiResponse<List<NoteResponse>>> = callbackFlow {
        val noteCollectionQuery = if(interest.isEmpty()) noteCollection else noteCollection.whereIn("subject", interest)
        val listenerRegistration = noteCollectionQuery
            .whereEqualTo("status", 1)
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
            .whereEqualTo("status", 1)
            .whereArrayContains("keywords", searchText)
            .orderBy("note_id", Query.Direction.DESCENDING)
            .limit(10)
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
            .whereEqualTo("status", 1)
            .whereArrayContains("keywords", searchText)
            .orderBy("note_id", Query.Direction.DESCENDING)
            .startAfter(lastVisible)
            .limit(10)
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
            .whereEqualTo("status", 1)
            .whereEqualTo("user_id", auth.uid)
            .orderBy("note_id", Query.Direction.DESCENDING)
            .limit(5)
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
            .whereEqualTo("status", 1)
            .whereEqualTo("user_id", auth.uid)
            .orderBy("note_id", Query.Direction.DESCENDING)
            .startAfter(lastVisible)
            .limit(5)
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

    fun getFirstNoteBySubject(subject: String): Flow<ApiResponse<List<NoteResponse>>> = callbackFlow {
        val listenerRegistration = noteCollection
            .whereEqualTo("status", 1)
            .whereIn("subject", listOf(subject))
            .orderBy("note_id", Query.Direction.DESCENDING)
            .limit(10)
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

    fun getNextNoteBySubject(subject: String, lastVisible: String): Flow<ApiResponse<List<NoteResponse>>> = callbackFlow {
        val listenerRegistration = noteCollection
            .whereEqualTo("status", 1)
            .whereIn("subject", listOf(subject))
            .orderBy("note_id", Query.Direction.DESCENDING)
            .startAfter(lastVisible)
            .limit(10)
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

    // note starred
    suspend fun insertNoteToStared(starNoteRequest: StarNoteRequest) {
        val starredNote = starNoteRequest.copy(user_id = auth.uid!!)
        noteStarredCollection
            .document("${starredNote.note_id}-${starredNote.user_id}")
            .set(starredNote)
            .await()
    }

    suspend fun unStarNote(note_id: String) {
        noteStarredCollection
            .document("$note_id-${auth.uid}")
            .delete()
            .await()
    }

    suspend fun getFirstUserStarredNotesId(): Flow<ApiResponse<List<StarredResponse>>> = callbackFlow {
        val listenerRegistration = noteStarredCollection
            .whereEqualTo("user_id", (auth.uid ?: ""))
            .orderBy("note_id", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(ApiResponse.Error(error.message.toString()))
                    cancel(message = "Error fetching Notes", cause = error)
                    return@addSnapshotListener
                }

                val notes = value?.documents?.mapNotNull {
                    it.toObject(StarredResponse::class.java)
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

    suspend fun getNextUserStarredNotesId(lastVisible: String): Flow<ApiResponse<List<StarredResponse>>> = callbackFlow {
        val listenerRegistration = noteStarredCollection
            .whereEqualTo("user_id", (auth.uid ?: ""))
            .orderBy("note_id", Query.Direction.DESCENDING)
            .startAfter(lastVisible)
            .limit(10)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(ApiResponse.Error(error.message.toString()))
                    cancel(message = "Error fetching Notes", cause = error)
                    return@addSnapshotListener
                }

                val notes = value?.documents?.mapNotNull {
                    it.toObject(StarredResponse::class.java)
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

    suspend fun checkIsNoteStarred(note_id: String): Boolean =
        !noteStarredCollection
            .whereEqualTo("note_id", note_id)
            .whereEqualTo("user_id", auth.uid)
            .limit(1)
            .get()
            .await()
            .isEmpty

    suspend fun updateNoteCount(note_id: String, addition: Long) {
        noteCollection
            .document(note_id)
            .update(
                mapOf(
                    "star" to FieldValue.increment(addition)
                )
            )
            .await()
    }

    suspend fun updateUserCount(user_id: String, addition: Long) {
        userCollection
            .document(user_id)
            .update(
                mapOf(
                    "total_star" to FieldValue.increment(addition)
                )
            )
            .await()
    }

    // note manipulation
    suspend fun deleteNote(note_id: String) {
        noteCollection.document(note_id)
            .update(
                mapOf(
                    "status" to 0
                )
            )
            .await()
    }

    suspend fun updateNote(
        note_id: String,
        preview: String,
        name: String,
        description: String,
        subject: String,
        keywords: List<String>,
        version: Int
    ) {
        noteCollection.document(note_id)
            .update(
                mapOf(
                    "preview" to preview,
                    "name" to name,
                    "description" to description,
                    "subject" to subject,
                    "keywords" to keywords,
                    "version" to version
                )
            )
            .await()
    }

    // user manipulation
    suspend fun updateUser(
        name: String,
        interests: List<String>,
        education: String,
        major: String
    ) {
        if(user?.uid != null) {
            userCollection
                .document(user.uid)
                .update(
                    mapOf(
                        "name" to name,
                        "interests" to interests,
                        "education" to education,
                        "major" to major
                    )
                )
                .await()
        }
    }

    /**Storage**/
    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference

    fun insertPreview(note_id: String, preview: Uri): UploadTask =
        storageReference
            .child("notespace_preview/${auth.uid}/${note_id}")
            .putFile(preview)

    fun downloadPreview(note_id: String): Task<Uri> =
        storageReference
            .child("notespace_preview/${auth.uid}/${note_id}")
            .downloadUrl

    fun insertPdfFile(note_id: String, file: Uri): UploadTask =
        storageReference
            .child("notespace_note/${auth.uid}/${note_id}.pdf")
            .putFile(file)

    fun getPdfFile(user_id: String, note_id: String): StorageReference =
        storageReference
            .child("notespace_note/${user_id}/${note_id}.pdf")
}