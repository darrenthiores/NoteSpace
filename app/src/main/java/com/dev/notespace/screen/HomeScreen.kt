package com.dev.notespace.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.R
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.core.data.Resource
import com.dev.core.utils.DataMapper
import com.dev.notespace.component.HomePopularList
import com.dev.notespace.component.SearchTextField
import com.dev.notespace.viewModel.HomeViewModel
import com.google.accompanist.flowlayout.FlowRow
import timber.log.Timber
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    HomeContent(
        modifier = Modifier,
        viewModel = viewModel
    )
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
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
                searchText = viewModel.searchText.value,
                onSearchTextChange = viewModel::setSearchText,
                trailingIcon = {
                    AnimatedVisibility(
                        visible = viewModel.searchText.value != "",
                        enter = expandIn(expandFrom = Alignment.Center),
                        exit = shrinkOut(shrinkTowards = Alignment.Center)
                    ) {
                        IconButton(
                            modifier = Modifier.clip(CircleShape),
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.setEnteredText("")
                                viewModel.setSearchText("")
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
                        viewModel.setEnteredText(viewModel.searchText.value)
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
    }

    if(viewModel.enteredText.value.isEmpty()) {
        HomeDefaultContent(
            modifier = Modifier,
            viewModel = viewModel
        )
    } else {
        HomeSearchContent(
            modifier = Modifier
        )
    }
}

@Composable
private fun HomeSearchContent(
    modifier: Modifier = Modifier
) {

}

@Composable
private fun HomeDefaultContent(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val notes = viewModel.popularNotes.collectAsState()

    Column(
        modifier = modifier
            .padding(top = 64.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
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
                        modifier = Modifier.padding(top = 2.dp),
                        text = it
                    )
                }
            }

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
                    modifier = Modifier.padding(top = 2.dp),
                    text = "Other"
                )
            }
        }

        Text(text = "Popular Notes")

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
                    getPreview = viewModel::getPreview
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
        "History"
    )