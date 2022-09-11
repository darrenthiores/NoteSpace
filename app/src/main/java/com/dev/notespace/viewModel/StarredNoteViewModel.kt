package com.dev.notespace.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.core.data.Resource
import com.dev.core.domain.model.presenter.Note
import com.dev.core.domain.model.presenter.StarredNote
import com.dev.core.domain.useCase.NoteSpaceUseCase
import com.dev.core.utils.DataMapper
import com.dev.notespace.state.PagingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StarredNoteViewModel @Inject constructor(
    private val useCase: NoteSpaceUseCase
): ViewModel() {
    var pagingState = mutableStateOf(PagingState.FirstLoad)
        private set

    private val _starredNotes = mutableStateListOf<StarredNote>()
    val starredNotes: SnapshotStateList<StarredNote>
        get() = _starredNotes

    fun userNextStarredNote() = viewModelScope.launch {
        useCase
            .getNextStarredId(
                _starredNotes[_starredNotes.size-1].note_id
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
                        val note = DataMapper.mapStarredNotesDomainToPresenter(it.data!!)
                        _starredNotes.addAll(note)
                    }
                }
            }
    }

    init {
        viewModelScope.launch {
            useCase.getFirstStarredId().collect {
                when(it) {
                    is Resource.Loading -> {
                        pagingState.value = PagingState.FirstLoad
                    }
                    is Resource.Error -> {
                        pagingState.value = PagingState.FirstLoadError
                    }
                    is Resource.Success -> {
                        pagingState.value = PagingState.Success
                        val note = DataMapper.mapStarredNotesDomainToPresenter(it.data!!)
                        _starredNotes.addAll(note)
                    }
                }
            }
        }
    }

    fun getNoteById(note_id: String, setNote: (Note?) -> Unit) = viewModelScope.launch {
        when(val data = useCase.getNoteById(note_id)) {
            is Resource.Loading -> {
                setNote(null)
            }
            is Resource.Error -> {
                setNote(null)
            }
            is Resource.Success -> {
                setNote(DataMapper.mapNoteDomainToPresenter(data.data!!))
            }
        }
    }
}