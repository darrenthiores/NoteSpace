package com.dev.notespace.screen

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import com.dev.notespace.R
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
import com.dev.notespace.component.CommonDialog
import com.dev.notespace.component.DataInput
import com.dev.notespace.component.DigitDataInput
import com.dev.notespace.component.PasswordInput
import com.dev.notespace.viewModel.LoginViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import timber.log.Timber

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToOtp: (String, String) -> Unit,
    navigateToRegister: () -> Unit
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

    LoginContent(
        modifier = modifier,
        sendVerificationCode = {
            viewModel.sendVerificationCode(
                activity, identifier, callbacks
            )
        },
        navigateToRegister = navigateToRegister,
        identifier = identifier,
        setIdentifier = setIdentifier,
        identifierError = identifierError,
        showIdentifierError = showIdentifierError,
        identifierErrorDescription = identifierErrorDescription,
        setIdentifierError = setIdentifierError,
        checkPhoneNumber = { viewModel.checkPhoneNumber(identifier) },
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
private fun LoginContent(
    modifier: Modifier = Modifier,
    sendVerificationCode: () -> Unit,
    navigateToRegister: () -> Unit,
    identifier: String,
    setIdentifier: (String) -> Unit,
    identifierError: Boolean,
    showIdentifierError: (Boolean) -> Unit,
    identifierErrorDescription: String,
    setIdentifierError: (String) -> Unit,
    checkPhoneNumber: () -> Boolean,
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
        Box(
            modifier = Modifier
                .padding(top = 48.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.auth_screens_background),
                contentDescription = null,
                modifier = Modifier.height(36.dp)
            )
        }
        Text(
            text = "NOTE SPACE",
            modifier = Modifier.padding(top = 8.dp)
        )
        DigitDataInput(
            modifier = Modifier.padding(top = 128.dp),
            label = "Mobile No",
            currentText = identifier,
            onTextChange = setIdentifier,
            error = identifierError,
            errorDescription = identifierErrorDescription,
            showError = showIdentifierError,
            maxLength = 13
        )
        Button(
            onClick = {
                onButtonLoginClick(
                    loginWithNumber = sendVerificationCode,
                    identifier = identifier,
                    setIdentifierDescription = setIdentifierError,
                    showIdentifierError = showIdentifierError,
                    checkPhoneNumber = checkPhoneNumber,
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
            Text(text = "Login")
        }
        Text(
            text = "Register your Account",
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    navigateToRegister()
                }
        )
    }
}

private fun onButtonLoginClick(
    loginWithNumber: () -> Unit,
    identifier: String,
    setIdentifierDescription: (String) -> Unit,
    showIdentifierError: (Boolean) -> Unit,
    checkPhoneNumber: () -> Boolean,
    showDialog: (Boolean) -> Unit,
    setMessage: (String) -> Unit,
    showLoading: (Boolean) -> Unit
) {
    when {
        identifier.isEmpty() -> {
            setIdentifierDescription("This Field Cannot Be Empty")
            showIdentifierError(true)
        }
        !identifier.startsWith("08") -> {
            setIdentifierDescription("Mobile no should start with 08")
            showIdentifierError(true)
        }
        identifier.startsWith("08") && identifier.length <= 8 -> {
            setIdentifierDescription("Mobile No Length Must Be Greater Than 8")
            showIdentifierError(true)
        }
        identifier.startsWith("08") && identifier.length >= 13 -> {
            setIdentifierDescription("Mobile No Length Must Be Less Than 13")
            showIdentifierError(true)
        }
        identifier.startsWith("08") && identifier.length <= 13 && identifier.length >= 8 -> {
            showLoading(true)
            if(checkPhoneNumber()) {
                setIdentifierDescription("Mobile No is Not Registered Yet!")
                showIdentifierError(true)
                showLoading(false)
            } else {
                loginWithNumber()
            }
        }
    }
}