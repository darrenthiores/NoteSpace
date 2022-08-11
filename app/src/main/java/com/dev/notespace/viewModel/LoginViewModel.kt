package com.dev.notespace.viewModel

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.dev.core.domain.NoteSpaceUseCase
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
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
    fun signInWithEmail(email: String, password: String): Task<AuthResult> =
        noteSpaceUseCase.signInWithEmail(email, password)
}