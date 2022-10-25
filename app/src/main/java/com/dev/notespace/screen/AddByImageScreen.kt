package com.dev.notespace.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.dev.core.data.Resource
import com.dev.notespace.component.*
import com.dev.notespace.helper.MediaPicker
import com.dev.notespace.holder.TextFieldHolder
import com.dev.notespace.viewModel.AddByImageViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import timber.log.Timber

@Composable
@ExperimentalPagerApi
fun AddByImageScreen(
    viewModel: AddByImageViewModel = hiltViewModel(),
    _imgUri: List<Uri>,
    onBackClicked: () -> Unit,
    onPostSuccess: () -> Unit,
    showSnackBar: (String) -> Unit
) {
    val scaffoldState = rememberScaffoldState()

    val imgUri = remember {
        mutableStateOf(_imgUri)
    }

    val previewUri = remember {
        mutableStateOf<Uri?>(null)
    }

    val context = LocalContext.current

    LaunchedEffect(imgUri.value) {
        viewModel.getTextByImages(imgUri.value, context)
    }

    var isPosting by remember {
        mutableStateOf(false)
    }

    var showLoading by remember {
        mutableStateOf(false)
    }

    val imgLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        if (it.isNotEmpty()) {
            imgUri.value = it
        } else {
            showSnackBar("Something Error! Please try again..")
        }
    }

    val previewLauncher = rememberLauncherForActivityResult(MediaPicker()) {
        if (it != null) {
            previewUri.value = it
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
                        imgUri.value.isNotEmpty()
                    ) {
                        if(previewUri.value!=null) {
                            viewModel.insertNote(imgUri.value, previewUri.value!!)
                            isPosting = true
                            showLoading = true
                        } else {
                            showSnackBar("Please add a preview!")
                        }
                    } else {
                        showSnackBar("Something Error! Please try again...")
                    }
                }
            )
        },
        scaffoldState = scaffoldState
    ) {
        AddByImageContent(
            modifier = Modifier
                .padding(it),
            viewModel = viewModel,
            onRePickClicked = {
                imgLauncher.launch("image/*")
            },
            imgUri = imgUri,
            previewUri = previewUri,
            onRePickPreview = {
                previewLauncher.launch("")
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
private fun AddByImageContent(
    modifier: Modifier = Modifier,
    viewModel: AddByImageViewModel,
    onRePickClicked: () -> Unit,
    previewUri: State<Uri?>,
    imgUri: State<List<Uri>>,
    onRePickPreview: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ImageUriCarousel(
            modifier = Modifier
                .padding(PaddingValues(top = 16.dp)),
            count = imgUri.value.size,
            previews = imgUri.value
        )

        Text(
            text = "Change Image",
            modifier = Modifier
                .padding(16.dp)
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
        
//        viewModel.textByImages.forEach {
//            Text(text = it, modifier = Modifier.padding(top = 8.dp))
//        }

        Text(
            text = "Add Preview",
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.SemiBold
            )
        )

        if(previewUri.value == null) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(100.dp)
                    .background(Color.LightGray)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onRePickPreview() }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    tint = Color.DarkGray
                )
            }
        } else {
            Image(
                painter = rememberAsyncImagePainter(previewUri.value),
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .size(100.dp)
                    .background(Color.LightGray)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onRePickPreview() },
                contentScale = ContentScale.Crop
            )
        }

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