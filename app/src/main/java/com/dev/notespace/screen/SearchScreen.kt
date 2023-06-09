package com.dev.notespace.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.notespace.component.SearchList
import com.dev.notespace.component.SearchTextField
import com.dev.notespace.viewModel.SearchViewModel
import kotlin.math.roundToInt

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    subject: String,
    navigateToNoteDetail: (String, String, String) -> Unit,
    onBackClicked: () -> Unit
) {
    LaunchedEffect(true) {
        viewModel.getFirstDefaultNotes(subject)
    }

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

    val defaultListState = rememberLazyGridState()
    val searchListState = rememberLazyGridState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        val enteredTextEmpty by remember {
            derivedStateOf {
                viewModel.searchText.enteredText.isEmpty()
            }
        }

        if(enteredTextEmpty) {
            SearchContentDefault(
                modifier = Modifier,
                viewModel = viewModel,
                subject = subject,
                navigateToNoteDetail = navigateToNoteDetail,
                state = defaultListState
            )
        } else {
            SearchContentOnSearch(
                modifier = Modifier,
                viewModel = viewModel,
                navigateToNoteDetail = navigateToNoteDetail,
                state = searchListState
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
            IconButton(onClick = { onBackClicked() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.padding(16.dp)
                )
            }

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
        }
    }
}

@Composable
private fun SearchContentDefault(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    subject: String,
    navigateToNoteDetail: (String, String, String) -> Unit,
    state: LazyGridState
) {
    val queryNextItem = remember {
        derivedStateOf {
            viewModel.defaultNotes.isNotEmpty() &&
            viewModel.defaultNotes.size % 10 == 0 &&
            state.layoutInfo.visibleItemsInfo.lastOrNull()?.index == viewModel.defaultNotes.size - 1
        }
    }

    LaunchedEffect(queryNextItem.value) {
        if(queryNextItem.value) {
            viewModel.defaultNextNote(subject)
        }
    }

    SearchList(
        modifier = modifier
            .padding(horizontal = 16.dp),
        searchedNotes = viewModel.defaultNotes,
        navigateToNoteDetail = navigateToNoteDetail,
        state = state,
        searchPagingState = viewModel.defaultPagingState
    )
}

@Composable
private fun SearchContentOnSearch(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    navigateToNoteDetail: (String, String, String) -> Unit,
    state: LazyGridState
) {
    val queryNextItem = remember {
        derivedStateOf {
            viewModel.searchedNotes.isNotEmpty() &&
            viewModel.searchedNotes.size % 10 == 0 &&
            state.layoutInfo.visibleItemsInfo.lastOrNull()?.index == viewModel.searchedNotes.size - 1
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
        searchedNotes = viewModel.searchedNotes,
        navigateToNoteDetail = navigateToNoteDetail,
        state = state,
        searchPagingState = viewModel.searchPagingState
    )
}