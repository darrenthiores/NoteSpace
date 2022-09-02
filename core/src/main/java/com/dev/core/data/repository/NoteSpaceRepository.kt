package com.dev.core.data.repository

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import com.dev.core.data.Resource
import com.dev.core.data.dataStore.DataStore
import com.dev.core.data.firebase.FirebaseDataSource
import com.dev.core.data.local.LocalDataSource
import com.dev.core.data.remote.source.ApiResponse
import com.dev.core.data.remote.source.RemoteDataSource
import com.dev.core.model.data.response.UserResponse
import com.dev.core.model.domain.NoteDomain
import com.dev.core.model.domain.UserDomain
import com.dev.core.utils.DataMapper
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteSpaceRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
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

    override fun getPopularNote(): Flow<Resource<List<NoteDomain>>> = flow {
        emit(Resource.Loading())
        when(
            val apiResponse = fbDataSource.getPopularNote().first()
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

    override fun insertPdfFile(user_id: String, note_id: String, file: Uri): UploadTask =
        fbDataSource.insertPdfFile(user_id, note_id, file)

    override suspend fun getPdfFile(
        user_id: String,
        note_id: String
    ): List<ImageBitmap> = withContext(Dispatchers.IO) {
        val imageList = mutableListOf<ImageBitmap>()
        val localFile: File? = kotlin.runCatching {
            File.createTempFile("notespace", "pdf")
        }.getOrNull()

        if(localFile!=null) {
            fbDataSource
                .getPdfFile(user_id, note_id)
                .getFile(localFile)
                .addOnSuccessListener {
                    imageList.clear()
                    if (it.task.isSuccessful) {
                        val input = ParcelFileDescriptor.open(localFile, ParcelFileDescriptor.MODE_READ_ONLY)
                        val renderer = PdfRenderer(input)

                        for (i in 0 until renderer.pageCount) {

                            val page = renderer.openPage(i)
                            val bitmap =
                                Bitmap.createBitmap(
                                    100,
                                    100,
                                    Bitmap.Config.ARGB_8888
                                )
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            imageList.add(bitmap.asImageBitmap())
                            page.close()

                        }
                        renderer.close()
                    }
                }
                .addOnFailureListener {
                    Timber.d(it.message.toString())
                }

            imageList
        } else {
            emptyList()
        }
    }

    override suspend fun getPdfPreview(user_id: String, note_id: String): ImageBitmap? = withContext(Dispatchers.IO) {
        var imgBitmap: ImageBitmap? = null
        val localFile: File? = kotlin.runCatching {
            File.createTempFile("notespace", "pdf")
        }.getOrNull()

        if(localFile!=null) {
            fbDataSource
                .getPdfFile(user_id, note_id)
                .getFile(localFile)
                .addOnSuccessListener {
                    if (it.task.isSuccessful) {
                        val input = ParcelFileDescriptor.open(localFile, ParcelFileDescriptor.MODE_READ_ONLY)
                        val renderer = PdfRenderer(input)
                        val page = renderer.openPage(0)
                        val bitmap =
                            Bitmap.createBitmap(
                                100,
                                100,
                                Bitmap.Config.ARGB_8888
                            )
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        imgBitmap = bitmap.asImageBitmap()
                        page.close()
                        renderer.close()
                    }
                }
                .addOnFailureListener {
                    Timber.d(it.message.toString())
                }
            imgBitmap
        } else {
            null
        }
    }


}