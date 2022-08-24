package com.dev.core.data.repository

import android.app.Activity
import com.dev.core.data.dataStore.DataStore
import com.dev.core.data.firebase.FirebaseDataSource
import com.dev.core.data.local.LocalDataSource
import com.dev.core.data.remote.source.RemoteDataSource
import com.dev.core.model.data.response.UserResponse
import com.dev.core.model.domain.UserDomain
import com.dev.core.utils.DataMapper
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
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
        fbDataSource.setUser(DataMapper.mapDomainToRequest(user))

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

}