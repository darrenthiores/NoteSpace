package com.dev.notespace.screen

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.notespace.R
import com.dev.notespace.component.CommonDialog
import com.dev.notespace.component.DataInput
import com.dev.notespace.component.EducationDropDown
import com.dev.notespace.component.PasswordInput
import com.dev.notespace.viewModel.LoginViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import timber.log.Timber

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToOtp: (String, String) -> Unit,
) {
    val (identifier, setIdentifier) = remember {
        mutableStateOf("")
    }
    val (identifierError, showIdentifierError) = remember {
        mutableStateOf(false)
    }
    val (identifierErrorDescription, setIdentifierError) = remember {
        mutableStateOf("")
    }
    val (name, setName) = remember {
        mutableStateOf("")
    }
    val (nameError, showNameError) = remember {
        mutableStateOf(false)
    }
    val (nameErrorDescription, setNameError) = remember {
        mutableStateOf("")
    }
    val (education, setEducation) = remember {
        mutableStateOf("")
    }
    val (educationError, showEducationError) = remember {
        mutableStateOf(false)
    }
    val (educationErrorDescription, setEducationError) = remember {
        mutableStateOf("")
    }
    val (major, setMajor) = remember {
        mutableStateOf("")
    }
    val (majorError, showMajorError) = remember {
        mutableStateOf(false)
    }
    val (majorErrorDescription, setMajorError) = remember {
        mutableStateOf("")
    }
    val (loading, showLoading) = remember {
        mutableStateOf(false)
    }
    val (dialog, showDialog) = remember {
        mutableStateOf(false)
    }
    val (message, setMessage) = remember {
        mutableStateOf("")
    }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {

        }
        override fun onVerificationFailed(p0: FirebaseException) {
            Timber.e(p0.message.toString())
            showLoading(false)
            setMessage(p0.message.toString())
            showDialog(true)
        }
        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            showLoading(false)
            navigateToOtp(identifier, p0)
        }
    }
    val activity = LocalContext.current as Activity

    RegisterContent(
        modifier = modifier,
        sendVerificationCode = {
            navigateToOtp("08", "123456")
//            viewModel.sendVerificationCode(
//                activity, identifier, callbacks
//            )
        },
        identifier = identifier,
        setIdentifier = setIdentifier,
        identifierError = identifierError,
        showIdentifierError = showIdentifierError,
        identifierErrorDescription = identifierErrorDescription,
        setIdentifierError = setIdentifierError,
        name = name,
        setName = setName,
        nameError = nameError,
        showNameError = showNameError,
        nameErrorDescription = nameErrorDescription,
        setNameError = setNameError,
        education = education,
        setEducation = setEducation,
        educationError = educationError,
        showEducationError = showEducationError,
        educationErrorDescription = educationErrorDescription,
        setEducationError = setEducationError,
        major = major,
        setMajor = setMajor,
        majorError = majorError,
        showMajorError = showMajorError,
        majorErrorDescription = majorErrorDescription,
        setMajorError = setMajorError,
        showDialog = showDialog,
        setMessage = setMessage,
        showLoading = showLoading
    )

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
private fun RegisterContent(
    modifier: Modifier = Modifier,
    sendVerificationCode: () -> Unit,
    identifier: String,
    setIdentifier: (String) -> Unit,
    identifierError: Boolean,
    showIdentifierError: (Boolean) -> Unit,
    identifierErrorDescription: String,
    setIdentifierError: (String) -> Unit,
    name: String,
    setName: (String) -> Unit,
    nameError: Boolean,
    showNameError: (Boolean) -> Unit,
    nameErrorDescription: String,
    setNameError: (String) -> Unit,
    education: String,
    setEducation: (String) -> Unit,
    educationError: Boolean,
    showEducationError: (Boolean) -> Unit,
    educationErrorDescription: String,
    setEducationError: (String) -> Unit,
    major: String,
    setMajor: (String) -> Unit,
    majorError: Boolean,
    showMajorError: (Boolean) -> Unit,
    majorErrorDescription: String,
    setMajorError: (String) -> Unit,
    showDialog: (Boolean) -> Unit,
    setMessage: (String) -> Unit,
    showLoading: (Boolean) -> Unit
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
            currentText = name,
            onTextChange = setName,
            error = nameError,
            errorDescription = nameErrorDescription,
            showError = showNameError
        )
        EducationDropDown(
            modifier = Modifier
                .fillMaxWidth(),
            education = education,
            onItemClicked = setEducation,
            error = educationError,
            errorDescription = educationErrorDescription,
            showError = showEducationError
        )
        DataInput(
            modifier = Modifier.padding(top = 16.dp),
            label = "Major",
            currentText = major,
            onTextChange = setMajor,
            error = majorError,
            errorDescription = majorErrorDescription,
            showError = showMajorError
        )
        DataInput(
            modifier = Modifier.padding(top = 16.dp),
            label = "Mobile No",
            currentText = identifier,
            onTextChange = setIdentifier,
            error = identifierError,
            errorDescription = identifierErrorDescription,
            showError = showIdentifierError
        )
        Button(
            onClick = {
                onButtonRegisterClick(
                    registerWithNumber = sendVerificationCode,
                    identifier = identifier,
                    setIdentifierDescription = setIdentifierError,
                    showIdentifierError = showIdentifierError,
                    name = name,
                    setNameDescription = setNameError,
                    showNameError = showNameError,
                    education = education,
                    setEducationDescription = setEducationError,
                    showEducationError = showEducationError,
                    major = major,
                    setMajorDescription = setMajorError,
                    showMajorError = showMajorError,
                    showDialog = showDialog,
                    setMessage = setMessage,
                    showLoading = showLoading
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .padding(horizontal = 32.dp)
        ) {
            Text(text = "Register with Mobile")
        }
    }
}

private fun onButtonRegisterClick(
    registerWithNumber: () -> Unit,
    identifier: String,
    setIdentifierDescription: (String) -> Unit,
    showIdentifierError: (Boolean) -> Unit,
    name: String,
    setNameDescription: (String) -> Unit,
    showNameError: (Boolean) -> Unit,
    education: String,
    setEducationDescription: (String) -> Unit,
    showEducationError: (Boolean) -> Unit,
    major: String,
    setMajorDescription: (String) -> Unit,
    showMajorError: (Boolean) -> Unit,
    showDialog: (Boolean) -> Unit,
    setMessage: (String) -> Unit,
    showLoading: (Boolean) -> Unit
) {
    when {
        name.isEmpty() -> {
            setNameDescription("Name Field Cannot Be Empty")
            showNameError(true)
        }
        name.length <= 5 -> {
            setNameDescription("Name Length Must Be Greater Than 5")
            showNameError(true)
        }
        education.isEmpty() -> {
            setEducationDescription("Please select your current education")
            showEducationError(true)
        }
        major.isEmpty() -> {
            setMajorDescription("Please let us know your current major")
            showMajorError(true)
        }
        identifier.isEmpty() -> {
            setIdentifierDescription("Identifier Field Cannot Be Empty")
            showIdentifierError(true)
        }
        identifier.startsWith("08") -> {
            setIdentifierDescription("Mobile no should start with +628")
            showIdentifierError(true)
        }
        identifier.startsWith("+628") && identifier.length <= 8 -> {
            setIdentifierDescription("Mobile No Length Must Be Greater Than 8")
            showIdentifierError(true)
        }
        identifier.startsWith("+628") && identifier.length >= 13 -> {
            setIdentifierDescription("Mobile No Length Must Be Less Than 13")
            showIdentifierError(true)
        }
        identifier.startsWith("+628") && identifier.length <= 13 && identifier.length >= 8 -> {
            showLoading(true)
            registerWithNumber()
        }
    }
}