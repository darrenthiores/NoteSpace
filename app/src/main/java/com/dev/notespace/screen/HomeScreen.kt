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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.core.data.Resource
import com.dev.core.utils.DataMapper
import com.dev.notespace.component.*
import com.dev.notespace.helper.Subject
import com.dev.notespace.state.PagingState
import com.dev.notespace.viewModel.HomeViewModel
import com.google.accompanist.flowlayout.FlowRow
import timber.log.Timber
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToNoteDetail: (String, String) -> Unit,
    navigateToSearch: (String) -> Unit,
    navigateToStarred: () -> Unit
) {
    HomeContent(
        modifier = Modifier,
        viewModel = viewModel,
        navigateToNoteDetail = navigateToNoteDetail,
        navigateToSearch = navigateToSearch,
        navigateToStarred = navigateToStarred
    )
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    navigateToNoteDetail: (String, String) -> Unit,
    navigateToSearch: (String) -> Unit,
    navigateToStarred: () -> Unit
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
                navigateToNoteDetail = navigateToNoteDetail,
                navigateToSearch = navigateToSearch
            )
        } else {
            HomeSearchContent(
                modifier = Modifier,
                viewModel = viewModel,
                state = searchListState,
                navigateToNoteDetail = navigateToNoteDetail
            )
        }

        Row(
            modifier = Modifier
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
                                contentDescription = "Close"
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

            IconButton(onClick = navigateToStarred) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = "Saved Note"
                )
            }
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
            viewModel.searchedNotes.isNotEmpty() && state.firstVisibleItemIndex+1 == viewModel.searchedNotes.size && viewModel.searchedNotes.size % 10 == 0
        }
    }

    LaunchedEffect(queryNextItem.value) {
        if(queryNextItem.value) {
            viewModel.searchNextNote()
        }
    }

    SearchList(
        modifier = modifier
            .padding(horizontal = 16.dp),
        searchedNotes = searchedNotes,
        navigateToNoteDetail = navigateToNoteDetail,
        state = state,
        searchPagingState = viewModel.searchPagingState
    )
}

@Composable
private fun HomeDefaultContent(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    defaultState: LazyListState,
    navigateToNoteDetail: (String, String) -> Unit,
    navigateToSearch: (String) -> Unit
) {
    val notes = viewModel.popularNotes.collectAsState()
    val queryNextItem = remember {
        derivedStateOf {
            viewModel.userNotes.isNotEmpty() && defaultState.firstVisibleItemIndex+1 == viewModel.userNotes.size && viewModel.userNotes.size % 5 == 0
        }
    }

    LaunchedEffect(queryNextItem.value) {
        if(queryNextItem.value) {
            viewModel.userNextNote()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(top = 80.dp),
        state = defaultState
    ) {
        item {
            FlowRow(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                Subject.values().forEach {
                    Column(
                        modifier = Modifier
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .clickable {
                                    navigateToSearch(it.name)
                                },
                            painter = painterResource(id = it.icon),
                            contentDescription = it.name
                        )
                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            text = it.name
                        )
                    }
                }
            }

            Text(
                text = "Popular Notes",
                modifier = Modifier
                    .padding(top = 32.dp)
                    .padding(horizontal = 16.dp),
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
                        navigateToNoteDetail = navigateToNoteDetail
                    )
                }
            }
        }

        item {
            Text(
                text = "My Notes",
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 16.dp),
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
                            .padding(horizontal = 16.dp)
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
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    itemsIndexed(
                        items = viewModel.userNotes,
                        key = { _, note -> note.note_id }
                    ) { index, note ->
                        UserNoteItem(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clickable { navigateToNoteDetail(note.note_id, note.user_id) },
                            preview = note.preview,
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
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}