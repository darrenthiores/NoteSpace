package com.dev.core.data.repository

import android.app.Activity
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
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

}