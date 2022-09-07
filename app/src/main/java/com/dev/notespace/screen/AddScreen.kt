package com.dev.notespace.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.core.data.Resource
import com.dev.notespace.component.ActionTopBar
import com.dev.notespace.component.DataInput
import com.dev.notespace.component.PdfCarousel
import com.dev.notespace.component.SubjectDropDown
import com.dev.notespace.holder.TextFieldHolder
import com.dev.notespace.viewModel.AddViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import timber.log.Timber
import kotlin.math.sqrt

@Composable
@ExperimentalPagerApi
fun AddScreen(
    viewModel: AddViewModel = hiltViewModel(),
    _mediaUri: Uri?,
    onBackClicked: () -> Unit,
    onPostSuccess: () -> Unit,
    showSnackBar: (String) -> Unit
) {
    val scaffoldState = rememberScaffoldState()

    val mediaUri = remember {
        mutableStateOf(_mediaUri)
    }

    val width = LocalConfiguration.current.screenWidthDp
    val height = (width * sqrt(2f)).toInt()
    val context = LocalContext.current

    LaunchedEffect(mediaUri.value) {
        viewModel.getPreviews(mediaUri.value, width, height, context)
    }

    var isPosting by remember {
        mutableStateOf(false)
    }

    var showLoading by remember {
        mutableStateOf(false)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            mediaUri.value = it
        } else {
            showSnackBar("Something Error! Please try again..")
        }
    }

    Scaffold(
        topBar = {
            ActionTopBar(
                title = "ADD NOTE",
                onBackClicked = onBackClicked,
                actionText = "POST",
                onActionClicked = {
                    if(
                        onPostClick(
                            viewModel.nameHolder,
                            viewModel.subjectHolder
                        ) &&
                        mediaUri.value!=null
                    ) {
                        viewModel.insertNote(mediaUri.value!!)
                        isPosting = true
                        showLoading = true
                    }
                }
            )
        },
        scaffoldState = scaffoldState
    ) {
        AddContent(
            modifier = Modifier
                .padding(it),
            viewModel = viewModel,
            onRePickClicked = {
                launcher.launch("application/pdf")
            }
        )
    }

    if(isPosting) {
        val postResult = viewModel.insertResult

        LaunchedEffect(postResult.value) {
            when(postResult.value) {
                is Resource.Loading -> {
                    Timber.d("POST: LOADING")
                    showLoading = true
                }
                is Resource.Error -> {
                    Timber.e("POST: ERROR: ${postResult.value.message}")
                    isPosting = false
                    showLoading = false
                    showSnackBar("Error: ${postResult.value.message}")
                }
                is Resource.Success -> {
                    Timber.d("POST: SUCCESS")
                    isPosting = false
                    showLoading = false
                    onPostSuccess()
                }
            }
        }
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
fun AddContent(
    modifier: Modifier = Modifier,
    viewModel: AddViewModel,
    onRePickClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PdfCarousel(
            modifier = Modifier,
            count = viewModel.previews.size,
            previews = viewModel.previews
        )

        Text(
            text = "Change Pdf",
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
                .clickable {
                    onRePickClicked()
                }
                .padding(vertical = 2.dp),
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Center
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
            maxLength = 150
        )

        SubjectDropDown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
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
            subjectHolder.setErrorDes("Name Field Cannot Be Empty")
            subjectHolder.setTextFieldError(true)
            return false
        }
        else -> {
            return true
        }
    }
}