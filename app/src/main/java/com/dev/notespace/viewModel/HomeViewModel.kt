package com.dev.notespace.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.core.data.Resource
import com.dev.core.domain.useCase.NoteSpaceUseCase
import com.dev.core.domain.model.domain.NoteDomain
import com.dev.core.domain.model.presenter.Note
import com.dev.core.utils.DataMapper
import com.dev.notespace.holder.SearchTextFieldHolder
import com.dev.notespace.state.PagingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteSpaceUseCase: NoteSpaceUseCase
): ViewModel() {
    var searchText = SearchTextFieldHolder()

    private val _popularNotes = MutableStateFlow<Resource<List<NoteDomain>>>(Resource.Loading())
    val popularNotes: StateFlow<Resource<List<NoteDomain>>>
        get() = _popularNotes

    var userPagingState = mutableStateOf(PagingState.FirstLoad)
        private set

    private val _userNotes = mutableStateListOf<Note>()
    val userNotes: List<Note>
        get() = _userNotes

    fun userNextNote() = viewModelScope.launch {
        noteSpaceUseCase
            .getNextUserNotes(
                _userNotes[_userNotes.size-1].note_id
            ).collect {
                when(it) {
                    is Resource.Loading -> {
                        userPagingState.value = PagingState.NextLoad
                    }
                    is Resource.Error -> {
                        userPagingState.value = PagingState.NextLoadError
                    }
                    is Resource.Success -> {
                        userPagingState.value = PagingState.Success
                        val note = DataMapper.mapNotesDomainToPresenter(it.data!!)
                        _userNotes.addAll(note)
                    }
                }
            }
    }

    init {
        viewModelScope.launch {
            noteSpaceUseCase.getPopularNote().collect {
                _popularNotes.value = it
            }

            noteSpaceUseCase.getFirstUserNotes().collect {
                when(it) {
                    is Resource.Loading -> {
                        userPagingState.value = PagingState.FirstLoad
                    }
                    is Resource.Error -> {
                        userPagingState.value = PagingState.FirstLoadError
                    }
                    is Resource.Success -> {
                        userPagingState.value = PagingState.Success
                        val note = DataMapper.mapNotesDomainToPresenter(it.data!!)
                        _userNotes.addAll(note)
                    }
                }
            }
        }
    }

    var searchPagingState = mutableStateOf(PagingState.FirstLoad)
        private set

    private val _searchedNotes = mutableStateListOf<Note>()
    val searchedNotes: List<Note>
        get() = _searchedNotes

    fun searchNotes() = viewModelScope.launch {
        noteSpaceUseCase.getFirstHomeSearchedNote(searchText.enteredText).collect {
            when(it) {
                is Resource.Loading -> {
                    searchPagingState.value = PagingState.FirstLoad
                }
                is Resource.Error -> {
                    searchPagingState.value = PagingState.FirstLoadError
                    Timber.e(it.message)
                }
                is Resource.Success -> {
                    searchPagingState.value = PagingState.Success
                    val note = DataMapper.mapNotesDomainToPresenter(it.data!!)
                    _searchedNotes.addAll(note)
                }
            }
        }
    }

    fun searchNextNote() = viewModelScope.launch {
        noteSpaceUseCase
            .getNextHomeSearchedNote(
                searchText.enteredText,
                _searchedNotes[_searchedNotes.size-1].note_id
            ).collect {
                when(it) {
                    is Resource.Loading -> {
                        searchPagingState.value = PagingState.NextLoad
                    }
                    is Resource.Error -> {
                        searchPagingState.value = PagingState.NextLoadError
                    }
                    is Resource.Success -> {
                        searchPagingState.value = PagingState.Success
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
        val preview = noteSpaceUseCase.getPdfPreview(user_id, note_id, 200, 200)
        setPreview(preview)
        Timber.d("PREVIEW: $preview")
    }
}