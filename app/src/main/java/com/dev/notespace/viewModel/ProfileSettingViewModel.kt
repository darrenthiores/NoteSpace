package com.dev.notespace.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.core.domain.useCase.NoteSpaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSettingViewModel @Inject constructor(
    private val useCase: NoteSpaceUseCase
): ViewModel() {

    var showDialog by mutableStateOf(false)
        private set

    fun setShowDialogValue(value: Boolean) {
        showDialog = value
    }

    fun logOut() {
        useCase.logOut()
    }

}