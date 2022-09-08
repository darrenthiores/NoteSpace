package com.dev.notespace.viewModel

import androidx.lifecycle.ViewModel
import com.dev.core.domain.useCase.NoteSpaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val useCase: NoteSpaceUseCase
): ViewModel() {
    val isSignIn = useCase.getUser()!=null
}