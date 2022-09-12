package com.dev.notespace.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.core.domain.model.presenter.Note
import com.dev.notespace.component.MidTitleTopBar
import com.dev.notespace.component.UserNoteItem
import com.dev.notespace.state.PagingState
import com.dev.notespace.viewModel.StarredNoteViewModel

@Composable
fun StarredNoteScreen(
    viewModel: StarredNoteViewModel = hiltViewModel(),
    navigateToNoteDetail: (String, String) -> Unit,
    onBackClicked: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            MidTitleTopBar(
                title = "Starred Note",
                onBackClicked = onBackClicked
            )
        },
        scaffoldState = scaffoldState
    ) {
        StarredNoteContent(
            modifier = Modifier
                .padding(it),
            viewModel = viewModel,
            state = listState,
            navigateToNoteDetail = navigateToNoteDetail
        )
    }
}

@Composable
private fun StarredNoteContent(
    modifier: Modifier = Modifier,
    viewModel: StarredNoteViewModel,
    state: LazyListState,
    navigateToNoteDetail: (String, String) -> Unit
) {
    val queryNextItem = remember {
        derivedStateOf {
            viewModel.starredNotes.isNotEmpty() &&
            state.firstVisibleItemIndex+1 == viewModel.starredNotes.size &&
            viewModel.starredNotes.size % 10 == 0
        }
    }

    LaunchedEffect(queryNextItem.value) {
        if(queryNextItem.value) {
            viewModel.userNextStarredNote()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = state
    ) {
        when(
            viewModel.pagingState.value
        ) {
            PagingState.FirstLoad -> {

            }
            PagingState.FirstLoadError -> {
                item {
                    Text(
                        text = "Error Loading First Page!",
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                if(viewModel.starredNotes.isEmpty()) {
                    item {
                        Text(
                            text = "Have you ever star a note today?",
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    itemsIndexed(
                        items = viewModel.starredNotes,
                        key = { _, note -> note.note_id }
                    ) { index, note ->
                        var noteById by remember {
                            mutableStateOf<Note?>(null)
                        }

                        LaunchedEffect(true) {
                            viewModel.getNoteById(note.note_id) {
                                noteById = it
                            }
                        }

                        if(noteById!=null) {
                            UserNoteItem(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .clickable { navigateToNoteDetail(note.note_id, note.user_id) },
                                preview = noteById!!.preview,
                                star = noteById!!.star,
                                name = noteById!!.name,
                                subject = noteById!!.subject,
                                firstItem = index == 0
                            )
                        }
                    }
                }
            }
        }

        if(
            viewModel.pagingState.value == PagingState.NextLoad
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
            viewModel.pagingState.value == PagingState.NextLoadError
        ) {
            item {
                Text(
                    text = "Error Loading Next Page!",
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}