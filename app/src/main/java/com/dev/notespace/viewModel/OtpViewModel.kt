package com.dev.notespace.viewModel

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.dev.core.domain.NoteSpaceUseCase
import com.dev.core.model.presenter.User
import com.dev.core.utils.DataMapper
import com.dev.notespace.holder.TextFieldHolder
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val noteSpaceUseCase: NoteSpaceUseCase
): ViewModel() {

    val otp = TextFieldHolder()

    fun signInWithCredential(credential: PhoneAuthCredential): Task<AuthResult> =
        noteSpaceUseCase.signInWithCredential(credential)

    fun sendVerificationCode(
        activity: Activity,
        phoneNumber: String,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) = noteSpaceUseCase.sendVerificationCode(
        activity = activity,
        number = phoneNumber,
        callback = callback
    )

    fun registerUser(user: User) =
        noteSpaceUseCase.setUser(DataMapper.mapPresenterToDomain(user))

    fun updateNumber(phoneNumber: String) =
        noteSpaceUseCase.setPhoneNumber(phoneNumber)

    fun logOut() =
        noteSpaceUseCase.logOut()
}