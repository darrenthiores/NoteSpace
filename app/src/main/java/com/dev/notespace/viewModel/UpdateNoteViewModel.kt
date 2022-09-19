package com.dev.notespace.viewModel

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.core.data.Resource
import com.dev.core.domain.model.domain.NoteDomain
import com.dev.core.domain.model.domain.UserDomain
import com.dev.core.domain.useCase.NoteSpaceUseCase
import com.dev.notespace.holder.TextFieldHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateNoteViewModel @Inject constructor(
    private val useCase: NoteSpaceUseCase
): ViewModel() {
    val nameHolder = TextFieldHolder()
    val descriptionHolder = TextFieldHolder()
    val subjectHolder = TextFieldHolder()

    val previewUri = mutableStateOf<Uri?>(null)
    val previewLink = mutableStateOf("")
    val version = mutableStateOf(0)
    val imageUrls = mutableStateListOf<String>()

    private val _note = mutableStateOf<Resource<NoteDomain>>(Resource.Loading())
    val note: State<Resource<NoteDomain>>
        get() = _note

    fun setNote(note_id: String) = viewModelScope.launch {
        _note.value = useCase.getNoteById(note_id)
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

    fun updateNote(note_id: String) = viewModelScope.launch {
        useCase.updateNote(
            note_id,
            previewUri.value,
            previewLink.value,
            nameHolder.value,
            descriptionHolder.value,
            subjectHolder.value,
            version.value
        )
    }
}