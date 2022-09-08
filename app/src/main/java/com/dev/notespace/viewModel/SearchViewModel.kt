package com.dev.notespace.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.core.data.Resource
import com.dev.core.domain.model.domain.NoteDomain
import com.dev.core.domain.model.presenter.Note
import com.dev.core.domain.useCase.NoteSpaceUseCase
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
class SearchViewModel @Inject constructor(
    private val useCase: NoteSpaceUseCase
): ViewModel() {
    var searchText = SearchTextFieldHolder()

    var defaultPagingState = mutableStateOf(PagingState.FirstLoad)
        private set

    private val _defaultNotes = mutableStateListOf<Note>()
    val defaultNotes: List<Note>
        get() = _defaultNotes

    fun getFirstDefaultNotes(subject: String) = viewModelScope.launch {
        useCase.getFirstNoteBySubject(subject = subject).collect {
            when(it) {
                is Resource.Loading -> {
                    defaultPagingState.value = PagingState.FirstLoad
                }
                is Resource.Error -> {
                    defaultPagingState.value = PagingState.FirstLoadError
                }
                is Resource.Success -> {
                    defaultPagingState.value = PagingState.Success
                    val note = DataMapper.mapNotesDomainToPresenter(it.data!!)
                    _defaultNotes.addAll(note)
                }
            }
        }
    }

    fun defaultNextNote(subject: String) = viewModelScope.launch {
        useCase
            .getNextNoteBySubject(
                subject = subject,
                _defaultNotes[_defaultNotes.size-1].note_id
            ).collect {
                when(it) {
                    is Resource.Loading -> {
                        defaultPagingState.value = PagingState.NextLoad
                    }
                    is Resource.Error -> {
                        defaultPagingState.value = PagingState.NextLoadError
                    }
                    is Resource.Success -> {
                        defaultPagingState.value = PagingState.Success
                        val note = DataMapper.mapNotesDomainToPresenter(it.data!!)
                        _defaultNotes.addAll(note)
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
        useCase.getFirstHomeSearchedNote(searchText.enteredText).collect {
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
        useCase
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
}