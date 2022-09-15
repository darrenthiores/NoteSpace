package com.dev.notespace.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.dev.core.data.Resource
import com.dev.core.utils.DataMapper
import com.dev.notespace.component.ActionTopBar
import com.dev.notespace.component.DataInput
import com.dev.notespace.component.PdfCarousel
import com.dev.notespace.component.SubjectDropDown
import com.dev.notespace.helper.MediaPicker
import com.dev.notespace.holder.TextFieldHolder
import com.dev.notespace.viewModel.UpdateNoteViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import timber.log.Timber
import kotlin.math.sqrt

@Composable
@ExperimentalPagerApi
fun EditNoteScreen(
    viewModel: UpdateNoteViewModel = hiltViewModel(),
    note_id: String,
    user_id: String,
    onBackClicked: () -> Unit,
    onUpdateSuccess: () -> Unit,
    showSnackBar: (String) -> Unit
) {
    val scaffoldState = rememberScaffoldState()

    val width = LocalConfiguration.current.screenWidthDp
    val height = (width * sqrt(2f)).toInt()

    var showLoading by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(true) {
        viewModel.setNote(note_id)
        viewModel.getPreviews(user_id, note_id, height, width)
    }

    LaunchedEffect(viewModel.note.value) {
        when(viewModel.note.value) {
            is Resource.Loading -> {
                Timber.d("NOTE: LOADING")
                showLoading = true
            }
            is Resource.Error -> {
                Timber.e("NOTE: ERROR: ${viewModel.note.value.message}")

                showLoading = false
            }
            is Resource.Success -> {
                Timber.d("NOTE: SUCCESS")
                val data = DataMapper.mapNoteDomainToPresenter(viewModel.note.value.data!!)

                viewModel.nameHolder.setTextFieldValue(data.name)
                viewModel.descriptionHolder.setTextFieldValue(data.description)
                viewModel.subjectHolder.setTextFieldValue(data.subject)
                viewModel.previewLink.value = data.preview
                viewModel.version.value = data.version

                showLoading = false
            }
        }
    }

    val imgLauncher = rememberLauncherForActivityResult(MediaPicker()) {
        if (it != null) {
            viewModel.previewUri.value = it
        } else {
            showSnackBar("Something Error! Please try again..")
        }
    }

    Scaffold(
        topBar = {
            ActionTopBar(
                title = "EDIT NOTE",
                onBackClicked = onBackClicked,
                actionText = "EDIT",
                onActionClicked = {
                    if(
                        viewModel.note.value is Resource.Success &&
                        onPostClick(
                            viewModel.nameHolder,
                            viewModel.subjectHolder
                        ) &&
                        (viewModel.previewUri.value != null || viewModel.previewLink.value != "")
                    ) {
                        viewModel.updateNote(note_id)
                        onUpdateSuccess()
                    } else {
                        showSnackBar("Something Error! Please try again...")
                    }
                }
            )
        },
        scaffoldState = scaffoldState
    ) {
        EditNoteContent(
            modifier = Modifier
                .padding(it),
            viewModel = viewModel,
            onRePickPreview = {
                imgLauncher.launch("")
            }
        )
    }

    if(showLoading) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
@ExperimentalPagerApi
private fun EditNoteContent(
    modifier: Modifier = Modifier,
    viewModel: UpdateNoteViewModel,
    onRePickPreview: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PdfCarousel(
            modifier = Modifier
                .padding(PaddingValues(top = 16.dp)),
            count = viewModel.previews.size,
            previews = viewModel.previews
        )

        Text(
            text = "Change Preview",
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.SemiBold
            )
        )

        Image(
            painter = rememberAsyncImagePainter(
                if(viewModel.previewUri.value == null) viewModel.previewLink.value
                else viewModel.previewUri.value
            ),
            contentDescription = null,
            modifier = Modifier
                .padding(16.dp)
                .size(100.dp)
                .background(Color.LightGray)
                .clip(RoundedCornerShape(4.dp))
                .clickable { onRePickPreview() },
            contentScale = ContentScale.Crop
        )

        DataInput(
            label = "name",
            textFieldHolder = viewModel.nameHolder,
            maxLength = 30
        )

        DataInput(
            modifier = Modifier
                .height(200.dp),
            label = "description",
            textFieldHolder = viewModel.descriptionHolder,
            maxLength = 150,
            singleLine = false
        )

        SubjectDropDown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            textFieldHolder = viewModel.subjectHolder
        )
    }
}

private fun onPostClick(
    nameHolder: TextFieldHolder,
    subjectHolder: TextFieldHolder
): Boolean {
    when {
        nameHolder.value.isEmpty() -> {
            nameHolder.setErrorDes("Name Field Cannot Be Empty")
            nameHolder.setTextFieldError(true)
            return false
        }
        nameHolder.value.length <= 5 -> {
            nameHolder.setErrorDes("Name Length Must Be Greater Than 5")
            nameHolder.setTextFieldError(true)
            return false
        }
        subjectHolder.value.isEmpty() -> {
            subjectHolder.setErrorDes("Subject Field Cannot Be Empty")
            subjectHolder.setTextFieldError(true)
            return false
        }
        else -> {
            return true
        }
    }
}