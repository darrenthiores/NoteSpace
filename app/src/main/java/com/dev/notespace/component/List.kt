package com.dev.notespace.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dev.core.domain.model.presenter.Note
import com.dev.notespace.state.PagingState

@Composable
fun HomePopularList(
    modifier: Modifier = Modifier,
    notes: List<Note>,
    navigateToNoteDetail: (String, String) -> Unit
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
    ) {
        itemsIndexed(
            items = notes,
            key = { _, note -> note.note_id }
        ) { index, note ->
            NoteItem(
                modifier = Modifier
                    .clickable { navigateToNoteDetail(note.note_id, note.user_id) },
                preview = note.preview,
                star = note.star,
                name = note.name,
                subject = note.subject,
                lastItem = index == notes.size
            )
        }
    }
}

@Composable
fun HomePersonalList(
    modifier: Modifier = Modifier,
    notes: List<Note>
) {
    LazyColumn(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        items(
            items = notes,
            key = { note -> note.note_id }
        ) { note ->
            UserNoteItem(
                modifier = Modifier
                    .clickable {  },
                preview = note.preview,
                star = note.star,
                name = note.name,
                subject = note.subject
            )
        }
    }
}

@Composable
fun SearchList(
    modifier: Modifier = Modifier,
    searchedNotes: List<Note>,
    navigateToNoteDetail: (String, String) -> Unit,
    state: LazyGridState,
    searchPagingState: MutableState<PagingState>
) {
    LazyVerticalGrid(
        modifier = modifier,
        contentPadding = PaddingValues(top = 80.dp, bottom = 16.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        state = state
    ) {
        when(
            searchPagingState.value
        ) {
            PagingState.FirstLoad -> {

            }
            PagingState.FirstLoadError -> {
                item {
                    Text(
                        text = "Error Loading First Page!",
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                if(searchedNotes.isEmpty()) {
                    item {
                        Text(
                            text = "Have you make a note today?",
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(
                        items = searchedNotes,
                        key = { note -> note.note_id }
                    ) { note ->
                        SearchNoteItem(
                            modifier = Modifier
                                .clickable { navigateToNoteDetail(note.note_id, note.user_id) },
                            preview = note.preview,
                            star = note.star,
                            name = note.name,
                            subject = note.subject
                        )
                    }
                }
            }
        }

        if(
            searchPagingState.value == PagingState.NextLoad
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }

        if(
            searchPagingState.value == PagingState.NextLoadError
        ) {
            item {
                Text(
                    text = "Error Loading Next Page!",
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}