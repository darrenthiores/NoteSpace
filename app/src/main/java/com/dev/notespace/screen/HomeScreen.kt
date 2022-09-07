package com.dev.notespace.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.core.data.Resource
import com.dev.core.utils.DataMapper
import com.dev.notespace.component.HomePopularList
import com.dev.notespace.component.SearchNoteItem
import com.dev.notespace.component.SearchTextField
import com.dev.notespace.component.UserNoteItem
import com.dev.notespace.state.PagingState
import com.dev.notespace.viewModel.HomeViewModel
import com.google.accompanist.flowlayout.FlowRow
import timber.log.Timber
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToNoteDetail: (String, String) -> Unit
) {
    HomeContent(
        modifier = Modifier,
        viewModel = viewModel,
        navigateToNoteDetail = navigateToNoteDetail
    )
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    navigateToNoteDetail: (String, String) -> Unit
) {
    val toolbarHeight = 56.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
    val focusManager = LocalFocusManager.current

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

                val delta = available.y
                val newOffset = toolbarOffsetHeightPx.value + delta
                toolbarOffsetHeightPx.value = newOffset.coerceIn(-toolbarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    val userListState = rememberLazyListState()
    val searchListState = rememberLazyGridState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
                .height(toolbarHeight)
                .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) }
                .background(MaterialTheme.colors.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchTextField(
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .weight(1f),
                searchTextHolder = viewModel.searchText,
                trailingIcon = {
                    AnimatedVisibility(
                        visible = viewModel.searchText.searchText != "",
                        enter = expandIn(expandFrom = Alignment.Center),
                        exit = shrinkOut(shrinkTowards = Alignment.Center)
                    ) {
                        IconButton(
                            modifier = Modifier.clip(CircleShape),
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.searchText.setSearchTextValue("")
                                viewModel.searchText.setEnteredTextValue("")
                            })
                        {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Close",
                                tint = Color.Red
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.searchText.setEnteredTextValue(viewModel.searchText.searchText)
                        viewModel.searchNotes()
                    }
                )
            )

            IconButton(onClick = {  }) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = "Saved Note"
                )
            }
        }

        val enteredTextEmpty by remember {
            derivedStateOf {
                viewModel.searchText.enteredText.isEmpty()
            }
        }

        if(enteredTextEmpty) {
            HomeDefaultContent(
                modifier = Modifier,
                viewModel = viewModel,
                defaultState = userListState,
                navigateToNoteDetail = navigateToNoteDetail
            )
        } else {
            HomeSearchContent(
                modifier = Modifier,
                viewModel = viewModel,
                state = searchListState,
                navigateToNoteDetail = navigateToNoteDetail
            )
        }
    }
}

@Composable
private fun HomeSearchContent(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    state: LazyGridState,
    navigateToNoteDetail: (String, String) -> Unit
) {
    val searchedNotes = viewModel.searchedNotes
    val queryNextItem = remember {
        derivedStateOf {
            viewModel.searchedNotes.isNotEmpty() && state.firstVisibleItemIndex == viewModel.searchedNotes.size && viewModel.searchedNotes.size % 20 == 0
        }
    }

    LaunchedEffect(queryNextItem.value) {
        if(queryNextItem.value) {
            viewModel.searchNextNote()
        }
    }

    LazyVerticalGrid(
        modifier = modifier
            .padding(top = 120.dp)
            .padding(horizontal = 16.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        state = state
    ) {
        when(
            viewModel.searchPagingState.value
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
                if(viewModel.userNotes.isEmpty()) {
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
                        var preview by remember {
                            mutableStateOf<ImageBitmap?>(null)
                        }

                        LaunchedEffect(true) {
                            viewModel.getPreview(
                                note.user_id,
                                note.note_id
                            ) { imgBitmap -> preview = imgBitmap }
                        }

                        SearchNoteItem(
                            modifier = Modifier
                                .clickable { navigateToNoteDetail(note.note_id, note.user_id) },
                            preview = preview,
                            star = note.star,
                            name = note.name,
                            subject = note.subject
                        )
                    }
                }
            }
        }

        if(
            viewModel.userPagingState.value == PagingState.NextLoad
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
            viewModel.userPagingState.value == PagingState.NextLoadError
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

@Composable
private fun HomeDefaultContent(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    defaultState: LazyListState,
    navigateToNoteDetail: (String, String) -> Unit
) {
    val notes = viewModel.popularNotes.collectAsState()
    val queryNextItem = remember {
        derivedStateOf {
            viewModel.userNotes.isNotEmpty() && defaultState.firstVisibleItemIndex == viewModel.userNotes.size && viewModel.userNotes.size % 20 == 0
        }
    }

    LaunchedEffect(queryNextItem.value) {
        if(queryNextItem.value) {
            viewModel.userNextNote()
        }
    }

    LazyColumn(
        modifier = modifier
            .padding(top = 120.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        state = defaultState
    ) {
        item {
            FlowRow {
                subject().forEach {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colors.primary)
                        )
                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            text = it
                        )
                    }
                }
            }

            Text(
                text = "Popular Notes",
                modifier = Modifier
                    .padding(top = 32.dp),
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            when(notes.value) {
                is Resource.Loading -> {
                    Timber.d("POPULAR NOTES LOADING")
                }
                is Resource.Error -> {
                    Timber.e("POPULAR NOTES ERROR: ${notes.value.message}")
                }
                is Resource.Success -> {
                    Timber.d("POPULAR NOTES SUCCESS")
                    val data = DataMapper.mapNotesDomainToPresenter(notes.value.data!!)
                    HomePopularList(
                        modifier = Modifier,
                        notes = data,
                        getPreview = viewModel::getPreview,
                        navigateToNoteDetail = navigateToNoteDetail
                    )
                }
            }
        }

        item {
            Text(
                text = "My Notes",
                modifier = Modifier
                    .padding(top = 32.dp),
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        when(
            viewModel.userPagingState.value
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
                if(viewModel.userNotes.isEmpty()) {
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
                    itemsIndexed(
                        items = viewModel.userNotes,
                        key = { _, note -> note.note_id }
                    ) { index, note ->
                        var preview by remember {
                            mutableStateOf<ImageBitmap?>(null)
                        }

                        LaunchedEffect(true) {
                            viewModel.getPreview(
                                note.user_id,
                                note.note_id
                            ) { imgBitmap -> preview = imgBitmap }
                        }

                        UserNoteItem(
                            modifier = Modifier
                                .clickable { navigateToNoteDetail(note.note_id, note.user_id) },
                            preview = preview,
                            star = note.star,
                            name = note.name,
                            subject = note.subject,
                            firstItem = index == 0
                        )
                    }
                }
            }
        }

        if(
            viewModel.userPagingState.value == PagingState.NextLoad
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
            viewModel.userPagingState.value == PagingState.NextLoadError
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

private fun subject(): List<String> =
    listOf(
        "Math",
        "Biology",
        "Physics",
        "Chems",
        "English",
        "Bahasa",
        "Geo",
        "History",
        "Other"
    )