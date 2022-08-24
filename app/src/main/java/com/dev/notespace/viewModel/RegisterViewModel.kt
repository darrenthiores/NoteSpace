package com.dev.notespace.viewModel

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.dev.core.domain.NoteSpaceUseCase
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val noteSpaceUseCase: NoteSpaceUseCase
): ViewModel() {
    fun sendVerificationCode(
        activity: Activity,
        phoneNumber: String,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) = noteSpaceUseCase.sendVerificationCode(
        activity = activity,
        number = phoneNumber,
        callback = callback
    )

    fun checkPhoneNumber(
        phoneNumber: String
    ): Boolean = runBlocking {
        noteSpaceUseCase.checkPhoneNumber(phoneNumber)
    }
}