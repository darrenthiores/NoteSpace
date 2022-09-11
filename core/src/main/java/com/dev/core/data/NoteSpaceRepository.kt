package com.dev.core.data

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.dev.core.data.dataStore.DataStore
import com.dev.core.data.firebase.FirebaseDataSource
import com.dev.core.data.firebase.ApiResponse
import com.dev.core.domain.repository.INoteSpaceRepository
import com.dev.core.domain.model.data.request.NoteRequest
import com.dev.core.domain.model.data.request.StarNoteRequest
import com.dev.core.domain.model.data.response.NoteResponse
import com.dev.core.domain.model.data.response.UserResponse
import com.dev.core.domain.model.domain.NoteDomain
import com.dev.core.domain.model.domain.StarredNoteDomain
import com.dev.core.domain.model.domain.UserDomain
import com.dev.core.utils.Converters
import com.dev.core.utils.DataMapper
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteSpaceRepository @Inject constructor(
    private val dataStore: DataStore,
    private val fbDataSource: FirebaseDataSource
): INoteSpaceRepository {
    override fun getUser(): FirebaseUser? =
        fbDataSource.getUser()

    override fun logOut() = fbDataSource.logOut()

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

    override fun setUser(user: UserDomain): Task<Void> =
        fbDataSource.setUser(DataMapper.mapUserDomainToRequest(user))

    override suspend fun checkPhoneNumber(phoneNumber: String): Boolean {
        val result = fbDataSource.checkPhoneNumber(phoneNumber).await()
        return result.isEmpty
    }

    override fun setPhoneNumber(phoneNumber: String): Task<Void> =
        fbDataSource.setPhoneNumber(phoneNumber)

    override suspend fun getUserData(): UserDomain {
        val data = fbDataSource.getUserData().await().toObject(UserResponse::class.java)
        return DataMapper.mapUserResponseToDomain(data!!)
    }

    override suspend fun getUserById(user_id: String): Resource<UserDomain> =
        try {
            val data = fbDataSource.getUserById(user_id).await().toObject(UserResponse::class.java)
            Resource.Success(DataMapper.mapUserResponseToDomain(data!!))
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }

    override suspend fun saveInterests(interests: List<String>) =
        dataStore.saveInterests(Converters().listOfStringToString(interests))

    override suspend fun insertNote(
        name: String,
        description: String,
        subject: String,
        file: Uri,
        preview: Uri
    ): Resource<Any?> {
        val noteId = "${UUID.randomUUID()}-notespace"
        val uploadFile = fbDataSource
            .insertPdfFile(noteId, file)
            .await()

        val uploadPreview = fbDataSource
            .insertPreview(noteId, preview)
            .await()

        if(
            uploadFile.task.isSuccessful &&
            uploadPreview.task.isSuccessful
        ) {
            val url = fbDataSource.downloadPreview(noteId).await()
            return try {
                val noteRequest = NoteRequest(noteId, name, description, subject, "", 0, url.toString())
                fbDataSource
                    .insertNote(
                        noteRequest
                    )
                    .await()

                Resource.Success(null)
            } catch (e: Exception) {
                Resource.Error(e.message.toString())
            }
        } else {
            return if(!uploadFile.task.isSuccessful) {
                Resource.Error(uploadFile.error?.message.toString())
            } else {
                Resource.Error(uploadPreview.error?.message.toString())
            }
        }
    }

    override suspend fun getNoteById(note_id: String): Resource<NoteDomain> =
        try {
            val data = fbDataSource.getNoteById(note_id).await().toObject(NoteResponse::class.java)
            Resource.Success(DataMapper.mapNoteResponseToDomain(data!!))
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }

    override fun getPopularNote(): Flow<Resource<List<NoteDomain>>> = flow {
        emit(Resource.Loading())
        when(
            val apiResponse = fbDataSource.getPopularNote(
                Converters().stringToListOfString(dataStore.interests.first())
            ).first()
        ) {
            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.errorMessage))
            }
            is ApiResponse.Empty -> {
                emit(Resource.Loading())
                delay(3000)
                emit(Resource.Success(emptyList()))
            }
            is ApiResponse.Success -> {
                emit(Resource.Success(DataMapper.mapNotesResponseToDomain(apiResponse.data)))
            }
        }
    }

    override fun getFirstHomeSearchedNote(searchText: String): Flow<Resource<List<NoteDomain>>> = flow {
        emit(Resource.Loading())
        when(
            val apiResponse = fbDataSource.getFirstHomeSearchedNote(searchText = searchText).first()
        ) {
            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.errorMessage))
            }
            is ApiResponse.Empty -> {
                emit(Resource.Loading())
                delay(3000)
                emit(Resource.Success(emptyList()))
            }
            is ApiResponse.Success -> {
                emit(Resource.Success(DataMapper.mapNotesResponseToDomain(apiResponse.data)))
            }
        }
    }

    override fun getNextHomeSearchedNote(
        searchText: String,
        lastVisible: String
    ): Flow<Resource<List<NoteDomain>>> = flow {
        emit(Resource.Loading())
        when(
            val apiResponse = fbDataSource.getNextHomeSearchedNote(searchText, lastVisible).first()
        ) {
            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.errorMessage))
            }
            is ApiResponse.Empty -> {
                emit(Resource.Loading())
                delay(3000)
                emit(Resource.Success(emptyList()))
            }
            is ApiResponse.Success -> {
                emit(Resource.Success(DataMapper.mapNotesResponseToDomain(apiResponse.data)))
            }
        }
    }

    override fun getFirstUserNotes(): Flow<Resource<List<NoteDomain>>> = flow {
        emit(Resource.Loading())
        when(
            val apiResponse = fbDataSource.getFirstUserNotes().first()
        ) {
            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.errorMessage))
            }
            is ApiResponse.Empty -> {
                emit(Resource.Loading())
                delay(3000)
                emit(Resource.Success(emptyList()))
            }
            is ApiResponse.Success -> {
                emit(Resource.Success(DataMapper.mapNotesResponseToDomain(apiResponse.data)))
            }
        }
    }

    override fun getNextUserNotes(lastVisible: String): Flow<Resource<List<NoteDomain>>> = flow {
        emit(Resource.Loading())
        when(
            val apiResponse = fbDataSource.getNextUserNotes(lastVisible).first()
        ) {
            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.errorMessage))
            }
            is ApiResponse.Empty -> {
                emit(Resource.Loading())
                delay(3000)
                emit(Resource.Success(emptyList()))
            }
            is ApiResponse.Success -> {
                emit(Resource.Success(DataMapper.mapNotesResponseToDomain(apiResponse.data)))
            }
        }
    }

    override fun getFirstNoteBySubject(subject: String): Flow<Resource<List<NoteDomain>>> = flow {
        emit(Resource.Loading())
        when(
            val apiResponse = fbDataSource.getFirstNoteBySubject(subject).first()
        ) {
            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.errorMessage))
            }
            is ApiResponse.Empty -> {
                emit(Resource.Loading())
                delay(3000)
                emit(Resource.Success(emptyList()))
            }
            is ApiResponse.Success -> {
                emit(Resource.Success(DataMapper.mapNotesResponseToDomain(apiResponse.data)))
            }
        }
    }

    override fun getNextNoteBySubject(
        subject: String,
        lastVisible: String
    ): Flow<Resource<List<NoteDomain>>> = flow {
        emit(Resource.Loading())
        when(
            val apiResponse = fbDataSource.getNextNoteBySubject(subject, lastVisible).first()
        ) {
            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.errorMessage))
            }
            is ApiResponse.Empty -> {
                emit(Resource.Loading())
                delay(3000)
                emit(Resource.Success(emptyList()))
            }
            is ApiResponse.Success -> {
                emit(Resource.Success(DataMapper.mapNotesResponseToDomain(apiResponse.data)))
            }
        }
    }

    override suspend fun insertNoteToStarred(note_id: String) =
        fbDataSource.insertNoteToStared(StarNoteRequest(note_id = note_id, user_id = ""))

    override suspend fun unStarNote(note_id: String) =
        fbDataSource.unStarNote(note_id)

    override suspend fun getFirstStarredId(): Flow<Resource<List<StarredNoteDomain>>> = flow {
        emit(Resource.Loading())
        when(
            val apiResponse = fbDataSource.getFirstUserStarredNotesId().first()
        ) {
            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.errorMessage))
            }
            is ApiResponse.Empty -> {
                emit(Resource.Loading())
                delay(3000)
                emit(Resource.Success(emptyList()))
            }
            is ApiResponse.Success -> {
                emit(Resource.Success(DataMapper.mapStarredNoteResponsesToDomain(apiResponse.data)))
            }
        }
    }

    override suspend fun getNextStarredId(lastVisible: String): Flow<Resource<List<StarredNoteDomain>>> = flow {
        emit(Resource.Loading())
        when(
            val apiResponse = fbDataSource.getNextUserStarredNotesId(lastVisible).first()
        ) {
            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.errorMessage))
            }
            is ApiResponse.Empty -> {
                emit(Resource.Loading())
                delay(3000)
                emit(Resource.Success(emptyList()))
            }
            is ApiResponse.Success -> {
                emit(Resource.Success(DataMapper.mapStarredNoteResponsesToDomain(apiResponse.data)))
            }
        }
    }

    override suspend fun checkIsNoteStarred(note_id: String): Boolean =
        fbDataSource.checkIsNoteStarred(note_id)

    override suspend fun updateNoteStarCount(note_id: String, newCount: Int) =
        fbDataSource.updateNoteCount(note_id, newCount)

    override suspend fun getPdfFile(
        user_id: String,
        note_id: String,
        height: Int,
        width: Int
    ): List<ImageBitmap> = withContext(Dispatchers.IO) {
        val imageList = mutableListOf<ImageBitmap>()
        val localFile: File? = kotlin.runCatching {
            File.createTempFile("notespace", "pdf")
        }.getOrNull()

        if(localFile!=null) {
            val snapshot = fbDataSource
                .getPdfFile(user_id, note_id)
                .getFile(localFile)
                .await()

            if(snapshot.task.isSuccessful) {
                imageList.clear()
                val input = kotlin.runCatching { ParcelFileDescriptor.open(localFile, ParcelFileDescriptor.MODE_READ_ONLY) }.getOrNull()
                val renderer = if(input!=null) kotlin.runCatching { PdfRenderer(input) }.getOrNull() else null

                for (i in 0 until (renderer?.pageCount ?: 0)) {
                    val page = renderer?.openPage(i)
                    val bitmap =
                        Bitmap.createBitmap(
                            width,
                            height,
                            Bitmap.Config.ARGB_8888
                        )
                    page?.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    imageList.add(bitmap.asImageBitmap())
                    page?.close()
                }
                renderer?.close()

                imageList
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    override suspend fun getPdfPreview(
        user_id: String,
        note_id: String,
        height: Int,
        width: Int
    ): ImageBitmap? = withContext(Dispatchers.IO) {
        val localFile: File? = kotlin.runCatching {
            File.createTempFile("notespace", "pdf")
        }.getOrNull()

        if(localFile!=null) {
            Timber.d("HERE")
            val snapshot = fbDataSource
                .getPdfFile(user_id, note_id)
                .getFile(localFile)
                .await()
            if(snapshot.task.isSuccessful) {
                val input = kotlin.runCatching { ParcelFileDescriptor.open(localFile, ParcelFileDescriptor.MODE_READ_ONLY) }.getOrNull()
                val renderer = if(input!=null) kotlin.runCatching { PdfRenderer(input) }.getOrNull() else null
                val page = renderer?.openPage(0)
                val bitmap =
                    Bitmap.createBitmap(
                        width,
                        height,
                        Bitmap.Config.ARGB_8888
                    )
                page?.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page?.close()
                renderer?.close()

                bitmap.asImageBitmap()
            } else {
                null
            }
        } else {
            Timber.d("LOCAL FILE: NULL")
            null
        }
    }


}