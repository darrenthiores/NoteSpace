package com.dev.notespace.screen

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.dev.core.data.Resource
import com.dev.core.domain.model.domain.NoteDomain
import com.dev.core.domain.model.domain.UserDomain
import com.dev.core.utils.DataMapper
import com.dev.notespace.component.MidTitleTopBar
import com.dev.notespace.component.PdfCarousel
import com.dev.notespace.navigation.NoteSpaceScreen
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

    val isNoteStarred = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(true) {
        viewModel.setNote(note_id)
        viewModel.setUploader(user_id)
        viewModel.getPreviews(user_id, note_id, height, width)
        isNoteStarred.value = viewModel.checkIsNoteStarred(note_id)
    }

    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            MidTitleTopBar(
                title = "Note Detail",
                onBackClicked = onBackClicked,
                endContent = {
                    IconButton(
                        onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "https://www.notespace.com/${NoteSpaceScreen.NoteDetail.name}/$note_id/$user_id"
                                )
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)

                            context.startActivity(shareIntent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Post"
                        )
                    }
                    IconButton(onClick = {
                        if(viewModel.currentStar != null) {
                            if(isNoteStarred.value) {
                                viewModel.unStarNote(note_id)
                                viewModel.updateNoteCount(user_id, note_id, -1L)
                            } else {
                                viewModel.starNote(note_id)
                                viewModel.updateNoteCount(user_id, note_id, 1L)
                            }

                            isNoteStarred.value = !isNoteStarred.value
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = if(isNoteStarred.value) Color.Yellow else Color.LightGray
                        )
                    }
                }
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
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        NoteItem(
            modifier = Modifier,
            note = viewModel.note,
            previews = viewModel.previews,
            starCount = viewModel.currentStar,
            updateCurrentStar = viewModel::setStar
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
    previews: List<ImageBitmap?>,
    starCount: Long?,
    updateCurrentStar: (Long) -> Unit
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

            LaunchedEffect(true) {
                updateCurrentStar(data.star.toLong())
            }

            PdfCarousel(
                modifier = modifier
                    .padding(PaddingValues(top = 16.dp)),
                count = previews.size,
                previews = previews
            )

            Divider(
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.LightGray)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier,
                        text = data.name,
                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp),
                        text = data.subject,
                        style = MaterialTheme.typography.subtitle1
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star(s)",
                        tint = Color.Yellow,
                        modifier = Modifier
                            .size(24.dp)
                    )
                    Text(
                        text = (starCount ?: 0).toString(),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }

            if(data.description.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    text = data.description,
                    style = MaterialTheme.typography.caption
                )
            }

            Text(
                text = "Preview",
                modifier = Modifier
                    .padding(top = 16.dp),
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Image(
                painter = rememberAsyncImagePainter(data.preview),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(100.dp)
                    .background(Color.LightGray)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
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

            Text(
                text = "Upload By",
                modifier = Modifier
                    .padding(top = 32.dp),
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Text(
                modifier = Modifier
                    .padding(top = 8.dp),
                text = data.name,
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Text(
                modifier = Modifier
                    .padding(top = 4.dp),
                text = "${data.education} - ${data.major}",
                style = MaterialTheme.typography.subtitle2
            )

            FlowRow(
                modifier = Modifier
                    .padding(bottom = 8.dp)
            ) {
                data.interests.forEach {
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp, end = 4.dp),
                        text = "#$it",
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
    }
}