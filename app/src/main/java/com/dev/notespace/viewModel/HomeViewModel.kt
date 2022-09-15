package com.dev.notespace.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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
    private val _currentNote = mutableStateOf<Note?>(null)
    val currentNote: State<Note?>
        get() = _currentNote

    fun setNote(note: Note?) {
        _currentNote.value = note
    }

    private val _popularNotes = MutableStateFlow<Resource<List<NoteDomain>>>(Resource.Loading())
    val popularNotes: StateFlow<Resource<List<NoteDomain>>>
        get() = _popularNotes

    var userPagingState = mutableStateOf(PagingState.FirstLoad)
        private set

    private val _userNotes = mutableStateListOf<Note>()
    val userNotes: SnapshotStateList<Note>
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
    val searchedNotes: SnapshotStateList<Note>
        get() = _searchedNotes

    fun searchNotes() = viewModelScope.launch {
        _searchedNotes.clear()
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

    fun deleteNote(note: Note) = viewModelScope.launch {
        noteSpaceUseCase.deleteNote(note.note_id)
        _userNotes.remove(note)
    }
}