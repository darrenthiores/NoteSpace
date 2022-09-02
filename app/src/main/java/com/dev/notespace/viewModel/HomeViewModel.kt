package com.dev.notespace.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import com.dev.core.domain.NoteSpaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteSpaceUseCase: NoteSpaceUseCase
): ViewModel() {
    var searchText = mutableStateOf("")
        private set
    var enteredText = mutableStateOf("")
        private set

    fun setSearchText(newText: String) {
        searchText.value = newText
    }

    fun setEnteredText(newText: String) {
        enteredText.value = newText
    }
}