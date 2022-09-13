package com.dev.notespace.screen

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import com.dev.notespace.R
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.notespace.component.CommonDialog
import com.dev.notespace.component.DataInput
import com.dev.notespace.component.DigitDataInput
import com.dev.notespace.component.PasswordInput
import com.dev.notespace.holder.TextFieldHolder
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
            navigateToOtp(viewModel.identifierHolder.value, p0)
        }
    }
    val activity = LocalContext.current as Activity

    LoginContent(
        modifier = modifier,
        sendVerificationCode = {
            viewModel.sendVerificationCode(
                activity, callbacks
            )
        },
        navigateToRegister = navigateToRegister,
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
private fun LoginContent(
    modifier: Modifier = Modifier,
    sendVerificationCode: () -> Unit,
    navigateToRegister: () -> Unit,
    viewModel: LoginViewModel,
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
        Image(
            painter = painterResource(id = R.drawable.notespace_logo),
            contentDescription = null,
            modifier = Modifier
                .size(250.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = "NOTE SPACE",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold
            )
        )
        DigitDataInput(
            modifier = Modifier.padding(top = 64.dp),
            label = "Mobile No",
            textFieldHolder = viewModel.identifierHolder,
            maxLength = 13
        )
        Button(
            onClick = {
                onButtonLoginClick(
                    loginWithNumber = sendVerificationCode,
                    identifierHolder = viewModel.identifierHolder,
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
    identifierHolder: TextFieldHolder,
    checkPhoneNumber: () -> Boolean,
    showDialog: (Boolean) -> Unit,
    setMessage: (String) -> Unit,
    showLoading: (Boolean) -> Unit
) {
    when {
        identifierHolder.value.isEmpty() -> {
            identifierHolder.setErrorDes("This Field Cannot Be Empty")
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
                identifierHolder.setErrorDes("Mobile No is Not Registered Yet!")
                identifierHolder.setTextFieldError(true)
                showLoading(false)
            } else {
                loginWithNumber()
            }
        }
    }
}