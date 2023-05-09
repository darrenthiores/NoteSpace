package com.dev.notespace.screen

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.core.data.Resource
import com.dev.core.domain.model.presenter.Note
import com.dev.core.utils.DataMapper
import com.dev.notespace.component.*
import com.dev.notespace.helper.Dummies
import com.dev.notespace.helper.Subject
import com.dev.notespace.navigation.NoteSpaceScreen
import com.dev.notespace.state.PagingState
import com.dev.notespace.viewModel.HomeViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.placeholder
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.roundToInt

@Composable
@ExperimentalMaterialApi
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToNoteDetail: (String, String, String) -> Unit,
    navigateToSearch: (String) -> Unit,
    navigateToStarred: () -> Unit,
    navigateToUpdateNote: (String, String, String) -> Unit,
    showSnackBar: (String) -> Unit
) {
    val userListState = rememberLazyListState()
    val searchListState = rememberLazyGridState()

    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember {
        mutableStateOf(false)
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            val context = LocalContext.current
            val clipboardManager: ClipboardManager = LocalClipboardManager.current
            Column(
                modifier = Modifier.height(450.dp)
            ) {
                SettingBottomSheet(
                    onShareClicked = {
                        val currentNote = viewModel.currentNote.value
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "https://www.notespace.com/${NoteSpaceScreen.NoteDetail.name}/${currentNote?.note_id}/${currentNote?.user_id}/${currentNote?.type}"
                            )
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)

                        context.startActivity(shareIntent)
                    },
                    onCopyLinkClicked = {
                        val currentNote = viewModel.currentNote.value
                        coroutineScope.launch {
                            scaffoldState.bottomSheetState.collapse()
                        }

                        clipboardManager.setText(AnnotatedString(("https://www.notespace.com/${NoteSpaceScreen.NoteDetail.name}/${currentNote?.note_id}/${currentNote?.user_id}/${currentNote?.type}")))
                        showSnackBar("Link Copied!")
                    },
                    onEditClicked = {
                        if(viewModel.currentNote.value!=null) {
                            navigateToUpdateNote(viewModel.currentNote.value?.note_id!!, viewModel.currentNote.value?.user_id!!, viewModel.currentNote.value?.type!!)

                            coroutineScope.launch {
                                scaffoldState.bottomSheetState.collapse()
                            }
                        }
                    },
                    onDeleteClicked = {
                        coroutineScope.launch {
                            scaffoldState.bottomSheetState.collapse()
                        }
                        showDialog = true
                    }
                )
            }
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = MaterialTheme.colors.surface,
        sheetPeekHeight = 0.dp
    ) { paddingValues ->
        HomeContent(
            modifier = Modifier
                .padding(paddingValues),
            viewModel = viewModel,
            navigateToNoteDetail = navigateToNoteDetail,
            navigateToSearch = navigateToSearch,
            navigateToStarred = navigateToStarred,
            onDeleteNote = {
                viewModel.setNote(it)
                showDialog = true
            },
            onSetting = {
                viewModel.setNote(it)
                coroutineScope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
            },
            userListState = userListState,
            searchListState = searchListState
        )
    }

    if(showDialog) {
        NegativeConfirmationDialog(
            message = "Are you sure want to delete this note?" +
                    "deleted Note cannot be restored!",
            onDismiss = {
                showDialog = false
                viewModel.setNote(null)
            },
            onClicked = {
                if(viewModel.currentNote.value != null) {
                    viewModel.deleteNote(viewModel.currentNote.value!!)

                    showDialog = false
                }
            },
            confirmationText = "Delete"
        )
    }
}

@Composable
@ExperimentalMaterialApi
private fun HomeContent(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    navigateToNoteDetail: (String, String, String) -> Unit,
    navigateToSearch: (String) -> Unit,
    navigateToStarred: () -> Unit,
    onDeleteNote: (Note) -> Unit,
    onSetting: (Note) -> Unit,
    userListState: LazyListState,
    searchListState: LazyGridState
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
            HomeDefaultContent(
                modifier = modifier,
                viewModel = viewModel,
                defaultState = userListState,
                navigateToNoteDetail = navigateToNoteDetail,
                navigateToSearch = navigateToSearch,
                onDeleteNote = onDeleteNote,
                onSetting = onSetting
            )
        } else {
            HomeSearchContent(
                modifier = modifier,
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
    navigateToNoteDetail: (String, String, String) -> Unit
) {
    val searchedNotes = viewModel.searchedNotes
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
    navigateToNoteDetail: (String, String, String) -> Unit,
    navigateToSearch: (String) -> Unit,
    onDeleteNote: (Note) -> Unit,
    onSetting: (Note) -> Unit
) {
    val queryNextItem = remember {
        derivedStateOf {
            viewModel.userNotes.isNotEmpty() &&
            viewModel.userNotes.size % 5 == 0 &&
            viewModel.userPagingState.value != PagingState.NextLoadError &&
            defaultState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == viewModel.userNotes.size - 1
        }
    }

    LaunchedEffect(queryNextItem.value) {
        if(queryNextItem.value) {
            Timber.d("QUERY")
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
        }

        item {
            val notes = viewModel.popularNotes.collectAsState()
            when(notes.value) {
                is Resource.Loading -> {
                    Timber.d("POPULAR NOTES LOADING")
                    HomePopularList(
                        modifier = Modifier,
                        childModifier = Modifier.placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.fade()
                        ),
                        notes = Dummies.dummyNote(),
                        navigateToNoteDetail = navigateToNoteDetail
                    )
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
                itemsIndexed(
                    items = Dummies.dummyNote(),
                    key = { _, note -> note.note_id }
                ) { index, note ->
                    UserNoteItem(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        preview = note.preview,
                        star = note.star,
                        name = note.name,
                        subject = note.subject,
                        firstItem = index == 0,
                        childModifier = Modifier.placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                }
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
                        SwipeAbleUserItem(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clickable { navigateToNoteDetail(note.note_id, note.user_id, note.type) },
                            preview = note.preview,
                            star = note.star,
                            name = note.name,
                            subject = note.subject,
                            onDelete = { onDeleteNote(note) },
                            onSetting = { onSetting(note) },
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