package com.dev.notespace.viewModel

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.core.data.Resource
import com.dev.core.domain.useCase.NoteSpaceUseCase
import com.dev.core.domain.model.domain.NoteDomain
import com.dev.core.domain.model.domain.UserDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val useCase: NoteSpaceUseCase
): ViewModel() {
    private val _note = mutableStateOf<Resource<NoteDomain>>(Resource.Loading())
    val note: State<Resource<NoteDomain>>
        get() = _note

    fun setNote(note_id: String) = viewModelScope.launch {
        _note.value = useCase.getNoteById(note_id)
    }

    private val _uploader = mutableStateOf<Resource<UserDomain>>(Resource.Loading())
    val uploader: State<Resource<UserDomain>>
        get() = _uploader

    fun setUploader(user_id: String) = viewModelScope.launch {
        _uploader.value = useCase.getUserById(user_id)
    }

    private val _previews = mutableStateListOf<ImageBitmap?>()
    val previews: SnapshotStateList<ImageBitmap?>
        get() = _previews

    fun getPreviews(
        user_id: String,
        note_id: String,
        height: Int,
        width: Int
    ) = viewModelScope.launch {
        _previews.addAll(useCase.getPdfFile(user_id, note_id, height = height, width = width))
    }

    fun checkIsNoteStarred(note_id: String): Boolean = runBlocking { useCase.checkIsNoteStarred(note_id) }

    fun starNote(note_id: String) = viewModelScope.launch {
        useCase.insertNoteToStarred(note_id)
    }

    fun unStarNote(note_id: String) = viewModelScope.launch {
        useCase.unStarNote(note_id)
    }

    var currentStar by mutableStateOf<Int?>(null)
    private set

    fun setStar(star: Int) {
        currentStar = star
    }

    fun updateNoteCount(note_id: String, newCount: Int) = viewModelScope.launch {
        useCase.updateNoteStarCount(note_id, (currentStar!! + newCount))
        currentStar = currentStar!! + newCount
    }
}