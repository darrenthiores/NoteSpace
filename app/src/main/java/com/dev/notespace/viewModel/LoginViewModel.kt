package com.dev.notespace.viewModel

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.dev.core.domain.useCase.NoteSpaceUseCase
import com.dev.notespace.holder.TextFieldHolder
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val noteSpaceUseCase: NoteSpaceUseCase
): ViewModel() {

    val identifierHolder = TextFieldHolder()

    fun sendVerificationCode(
        activity: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) = noteSpaceUseCase.sendVerificationCode(
        activity = activity,
        number = "+62${identifierHolder.value.drop(1)}",
        callback = callback
    )

    fun checkPhoneNumber(): Boolean = runBlocking {
        noteSpaceUseCase.checkPhoneNumber(identifierHolder.value)
    }
}