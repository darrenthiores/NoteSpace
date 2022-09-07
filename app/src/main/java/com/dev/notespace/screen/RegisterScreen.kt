package com.dev.notespace.screen

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.core.domain.model.presenter.User
import com.dev.notespace.component.*
import com.dev.notespace.holder.TextFieldHolder
import com.dev.notespace.viewModel.RegisterViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import timber.log.Timber

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
    navigateToOtp: (String, String, User) -> Unit,
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
            navigateToOtp(
                viewModel.identifierHolder.value,
                p0,
                User(
                    viewModel.nameHolder.value,
                    viewModel.identifierHolder.value,
                    viewModel.educationHolder.value,
                    viewModel.majorHolder.value,
                    emptyList()
                )
            )
        }
    }
    val activity = LocalContext.current as Activity

    RegisterContent(
        modifier = modifier,
        sendVerificationCode = {
            viewModel.sendVerificationCode(
                activity, callbacks
            )
        },
        viewModel = viewModel,
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
    viewModel: RegisterViewModel,
    sendVerificationCode: () -> Unit,
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
            textFieldHolder = viewModel.nameHolder
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
            textFieldHolder = viewModel.identifierHolder
        )
        Button(
            onClick = {
                onButtonRegisterClick(
                    registerWithNumber = sendVerificationCode,
                    identifierHolder = viewModel.identifierHolder,
                    nameHolder = viewModel.nameHolder,
                    educationHolder = viewModel.educationHolder,
                    majorHolder = viewModel.majorHolder,
                    checkPhoneNumber = viewModel::checkPhoneNumber,
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
    identifierHolder: TextFieldHolder,
    nameHolder: TextFieldHolder,
    educationHolder: TextFieldHolder,
    majorHolder: TextFieldHolder,
    checkPhoneNumber: () -> Boolean,
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
        !identifierHolder.value.startsWith("08") -> {
            identifierHolder.setErrorDes("Mobile no should start with 08")
            identifierHolder.setTextFieldError(true)
        }
        identifierHolder.value.startsWith("08") &&
        identifierHolder.value.length <= 8 -> {
            identifierHolder.setErrorDes("Mobile No Length Must Be Greater Than 8")
            identifierHolder.setTextFieldError(true)
        }
        identifierHolder.value.startsWith("08") &&
        identifierHolder.value.length >= 13 -> {
            identifierHolder.setErrorDes("Mobile No Length Must Be Less Than 13")
            identifierHolder.setTextFieldError(true)
        }
        identifierHolder.value.startsWith("08") &&
        identifierHolder.value.length <= 13 &&
        identifierHolder.value.length >= 8 -> {
            showLoading(true)
            if(checkPhoneNumber()) {
                registerWithNumber()
            } else {
                identifierHolder.setErrorDes("Mobile No is Already Registered!")
                identifierHolder.setTextFieldError(true)
                showLoading(false)
            }
        }
    }
}