package com.dev.notespace.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.notespace.viewModel.EditProfileViewModel
import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.core.domain.model.presenter.User
import com.dev.notespace.component.*
import com.dev.notespace.holder.ListFieldHolder
import com.dev.notespace.holder.TextFieldHolder
import com.dev.notespace.viewModel.RegisterViewModel

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    navigateEditSuccess: () -> Unit,
    onBackClicked: () -> Unit,
    showSnackBar: (String) -> Unit
) {
    val (loading, showLoading) = remember {
        mutableStateOf(false)
    }
    val (dialog, showDialog) = remember {
        mutableStateOf(false)
    }
    val (message, setMessage) = remember {
        mutableStateOf("")
    }
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        topBar = {
            MidTitleTopBar(
                title = "EDIT PROFILE",
                onBackClicked = onBackClicked
            )
        },
        scaffoldState = scaffoldState
    ) {
        EditProfileContent(
            modifier = Modifier
                .padding(it),
            viewModel = viewModel,
            navigateEditSuccess = navigateEditSuccess,
            showDialog = showDialog,
            setMessage = setMessage,
            showLoading = showLoading,
            showSnackBar = showSnackBar
        )
    }

    if(loading) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    if(dialog) {
        CommonDialog(
            message = message,
            onDismiss = { showDialog(false) }
        )
    }

}

@Composable
private fun EditProfileContent(
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel,
    navigateEditSuccess: () -> Unit,
    showDialog: (Boolean) -> Unit,
    setMessage: (String) -> Unit,
    showLoading: (Boolean) -> Unit,
    showSnackBar: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DataInput(
            modifier = Modifier.padding(top = 16.dp),
            label = "Name",
            textFieldHolder = viewModel.nameHolder
        )
        InterestDropDown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            listFieldHolder = viewModel.interestsHolder,
            showSnackBar = showSnackBar
        )
        EducationDropDown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            textFieldHolder = viewModel.educationHolder
        )
        DataInput(
            modifier = Modifier.padding(top = 16.dp),
            label = "Major",
            textFieldHolder = viewModel.majorHolder
        )
        DigitDataInput(
            modifier = Modifier.padding(top = 16.dp),
            label = "Mobile No",
            textFieldHolder = viewModel.identifierHolder,
            enabled = false
        )
        Button(
            onClick = {
                if(viewModel.user.value!=null) {
                    onButtonEditClicked(
                        editProfile = {
                            viewModel.updateProfile()
                            navigateEditSuccess()
                        },
                        identifierHolder = viewModel.identifierHolder,
                        nameHolder = viewModel.nameHolder,
                        educationHolder = viewModel.educationHolder,
                        majorHolder = viewModel.majorHolder,
                        interestHolder = viewModel.interestsHolder,
                        showDialog = showDialog,
                        setMessage = setMessage,
                        showLoading = showLoading
                    )
                } else {
                    showSnackBar("Something Error! Please try again..")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .padding(horizontal = 32.dp)
        ) {
            Text(text = "Update Profile")
        }
    }
}

private fun onButtonEditClicked(
    editProfile: () -> Unit,
    identifierHolder: TextFieldHolder,
    nameHolder: TextFieldHolder,
    educationHolder: TextFieldHolder,
    majorHolder: TextFieldHolder,
    interestHolder: ListFieldHolder,
    showDialog: (Boolean) -> Unit,
    setMessage: (String) -> Unit,
    showLoading: (Boolean) -> Unit
) {
    when {
        nameHolder.value.isEmpty() -> {
            nameHolder.setErrorDes("Name Field Cannot Be Empty")
            nameHolder.setTextFieldError(true)
        }
        nameHolder.value.length <= 5 -> {
            nameHolder.setErrorDes("Name Length Must Be Greater Than 5")
            nameHolder.setTextFieldError(true)
        }
        interestHolder.value.isEmpty() -> {
            interestHolder.setErrorDes("You Must Choose 3-5 Interests")
            interestHolder.setTextFieldError(true)
        }
        interestHolder.value.size < 3 -> {
            interestHolder.setErrorDes("You Must Choose At Least 3 Interests")
            interestHolder.setTextFieldError(true)
        }
        interestHolder.value.size > 5 -> {
            interestHolder.setErrorDes("You Can Only Choose Up To 5 Interests")
            interestHolder.setTextFieldError(true)
        }
        educationHolder.value.isEmpty() -> {
            educationHolder.setErrorDes("Please select your current education")
            educationHolder.setTextFieldError(true)
        }
        majorHolder.value.isEmpty() -> {
            majorHolder.setErrorDes("Please let us know your current major")
            majorHolder.setTextFieldError(true)
        }
        identifierHolder.value.isEmpty() -> {
            identifierHolder.setErrorDes("Identifier Field Cannot Be Empty")
            identifierHolder.setTextFieldError(true)
        }
        else -> {
            editProfile()
        }
    }
}