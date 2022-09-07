package com.dev.notespace.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.core.data.Resource
import com.dev.core.domain.model.domain.NoteDomain
import com.dev.core.domain.model.domain.UserDomain
import com.dev.core.utils.DataMapper
import com.dev.notespace.component.MidTitleTopBar
import com.dev.notespace.component.PdfCarousel
import com.dev.notespace.viewModel.NoteDetailViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.pager.ExperimentalPagerApi
import timber.log.Timber
import kotlin.math.sqrt

@Composable
@ExperimentalPagerApi
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel = hiltViewModel(),
    note_id: String,
    user_id: String,
    onBackClicked: () -> Unit
) {
    val width = LocalConfiguration.current.screenWidthDp
    val height = (width * sqrt(2f)).toInt()

    LaunchedEffect(true) {
        viewModel.setNote(note_id)
        viewModel.setUploader(user_id)
        viewModel.getPreviews(user_id, note_id, height, width)
    }

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        topBar = {
            MidTitleTopBar(
                title = "Note Detail",
                onBackClicked = onBackClicked
            )
        },
        scaffoldState = scaffoldState
    ) {
        NoteDetailContent(
            modifier = Modifier
                .padding(it),
            viewModel = viewModel
        )
    }
}

@Composable
@ExperimentalPagerApi
private fun NoteDetailContent(
    modifier: Modifier = Modifier,
    viewModel: NoteDetailViewModel
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        NoteItem(
            modifier = Modifier,
            note = viewModel.note,
            previews = viewModel.previews
        )

        UploaderItem(
            modifier = Modifier,
            uploader = viewModel.uploader
        )
    }
}

@Composable
@ExperimentalPagerApi
private fun ColumnScope.NoteItem(
    modifier: Modifier = Modifier,
    note: State<Resource<NoteDomain>>,
    previews: List<ImageBitmap?>
) {
    when(note.value) {
        is Resource.Loading -> {
            Timber.d("NOTE: LOADING")
        }
        is Resource.Error -> {
            Timber.e("NOTE: ERROR: ${note.value.message}")
        }
        is Resource.Success -> {
            val data = DataMapper.mapNoteDomainToPresenter(note.value.data!!)
            PdfCarousel(
                modifier = modifier,
                count = previews.size,
                previews = previews
            )

            Row {
                Text(text = data.name)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star(s)",
                            tint = Color.Yellow,
                            modifier = Modifier
                                .size(24.dp)
                        )
                        Text(
                            text = data.star.toString(),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Text(text = data.subject)
            Text(text = data.description)
        }
    }
}

@Composable
private fun ColumnScope.UploaderItem(
    modifier: Modifier = Modifier,
    uploader: State<Resource<UserDomain>>,
) {
    when(uploader.value) {
        is Resource.Loading -> {
            Timber.d("UPLOADER: LOADING")
        }
        is Resource.Error -> {
            Timber.e("UPLOADER: ERROR: ${uploader.value.message}")
        }
        is Resource.Success -> {
            val data = DataMapper.mapUserDomainToPresenter(uploader.value.data!!)
            Text(text = data.name)
            Row {
                Text(text = data.education)
                Text(text = data.major)
            }
            FlowRow {
                data.interests.forEach {
                    Text(text = it)
                }
            }
        }
    }
}