package com.dev.notespace.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.core.data.Resource
import com.dev.core.domain.NoteSpaceUseCase
import com.dev.core.model.domain.NoteDomain
import com.dev.core.model.presenter.Note
import com.dev.core.utils.DataMapper
import com.dev.notespace.state.PagingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    private val _popularNotes = MutableStateFlow<Resource<List<NoteDomain>>>(Resource.Loading())
    val popularNotes: StateFlow<Resource<List<NoteDomain>>>
        get() = _popularNotes

    init {
        viewModelScope.launch {
            noteSpaceUseCase.getPopularNote().collect {
                _popularNotes.value = it
            }
        }
    }

    var pagingState = mutableStateOf(PagingState.FirstLoad)
        private set

    private val _searchedNotes = mutableStateListOf<Note>()
    val searchedNotes: List<Note>
        get() = _searchedNotes

    fun searchNotes() = viewModelScope.launch {
        noteSpaceUseCase.getFirstHomeSearchedNote(enteredText.value).collect {
            when(it) {
                is Resource.Loading -> {
                    pagingState.value = PagingState.FirstLoad
                }
                is Resource.Error -> {
                    pagingState.value = PagingState.FirstLoadError
                }
                is Resource.Success -> {
                    pagingState.value = PagingState.Success
                    val note = DataMapper.mapNotesDomainToPresenter(it.data!!)
                    _searchedNotes.addAll(note)
                }
            }
        }
    }

    fun searchNextNote() = viewModelScope.launch {
        noteSpaceUseCase
            .getNextHomeSearchedNote(
                enteredText.value,
                _searchedNotes[_searchedNotes.size-1].note_id
            ).collect {
                when(it) {
                    is Resource.Loading -> {
                        pagingState.value = PagingState.NextLoad
                    }
                    is Resource.Error -> {
                        pagingState.value = PagingState.NextLoadError
                    }
                    is Resource.Success -> {
                        pagingState.value = PagingState.Success
                        val note = DataMapper.mapNotesDomainToPresenter(it.data!!)
                        _searchedNotes.addAll(note)
                    }
                }
            }
    }

    fun getPreview(
        user_id: String,
        note_id: String,
        setPreview: (ImageBitmap?) -> Unit
    ) = viewModelScope.launch {
        val preview = noteSpaceUseCase.getPdfPreview(user_id, note_id)
        setPreview(preview)
    }
}