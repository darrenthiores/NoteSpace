package com.dev.notespace.viewModel

import androidx.lifecycle.ViewModel
import com.dev.core.domain.NoteSpaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val noteSpaceUseCase: NoteSpaceUseCase
): ViewModel() {
}