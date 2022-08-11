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
    navigateToHome: () -> Unit,
    navigateToOtp: (String, String) -> Unit,
    navigateToRegister: () -> Unit
) {
    val (identifier, setIdentifier) = remember {
        mutableStateOf("")
    }
    val (password, setPassword) = remember {
        mutableStateOf("")
    }
    val (identifierError, showIdentifierError) = remember {
        mutableStateOf(false)
    }
    val (identifierErrorDescription, setIdentifierError) = remember {
        mutableStateOf("")
    }
    val (passwordError, showPasswordError) = remember {
        mutableStateOf(false)
    }
    val (passwordErrorDescription, setPasswordError) = remember {
        mutableStateOf("")
    }
    var passwordVisible by remember {
        mutableStateOf(false)
    }
    var passwordFieldShowed by remember {
        mutableStateOf(false)
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
        loginWithEmail = {
            viewModel.signInWithEmail(identifier, password)
        },
        navigateToHome = navigateToHome,
        navigateToRegister = navigateToRegister,
        passwordFieldShowed = passwordFieldShowed,
        showPasswordField = { passwordFieldShowed = true },
        identifier = identifier,
        setIdentifier = setIdentifier,
        identifierError = identifierError,
        showIdentifierError = showIdentifierError,
        identifierErrorDescription = identifierErrorDescription,
        setIdentifierError = setIdentifierError,
        password = password,
        setPassword = setPassword,
        passwordVisible = passwordVisible,
        showPassword = { passwordVisible = !passwordVisible },
        passwordError = passwordError,
        showPasswordError = showPasswordError,
        passwordErrorDescription = passwordErrorDescription,
        setPasswordError = setPasswordError,
        showDialog = showDialog,
        setMessage = setMessage,
        showLoading = showLoading
    )

    LaunchedEffect(identifier) {
        passwordFieldShowed = identifier.endsWith("@gmail.com")
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
private fun LoginContent(
    modifier: Modifier = Modifier,
    sendVerificationCode: () -> Unit,
    loginWithEmail: () -> Task<AuthResult>,
    navigateToHome: () -> Unit,
    navigateToRegister: () -> Unit,
    passwordFieldShowed: Boolean,
    showPasswordField: () -> Unit,
    identifier: String,
    setIdentifier: (String) -> Unit,
    identifierError: Boolean,
    showIdentifierError: (Boolean) -> Unit,
    identifierErrorDescription: String,
    setIdentifierError: (String) -> Unit,
    password: String,
    setPassword: (String) -> Unit,
    passwordVisible: Boolean,
    showPassword: () -> Unit,
    passwordError: Boolean,
    showPasswordError: (Boolean) -> Unit,
    passwordErrorDescription: String,
    setPasswordError: (String) -> Unit,
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
        DataInput(
            modifier = Modifier.padding(top = 128.dp),
            label = "Mobile No/Email",
            currentText = identifier,
            onTextChange = setIdentifier,
            error = identifierError,
            errorDescription = identifierErrorDescription,
            showError = showIdentifierError
        )
        AnimatedVisibility(passwordFieldShowed) {
            PasswordInput(
                label = "Password",
                password = password,
                onPasswordChange = setPassword,
                passwordVisible = passwordVisible,
                showPassword = showPassword,
                error = passwordError,
                errorDescription = passwordErrorDescription,
                showError = showPasswordError,
                maxLength = 20
            )
        }
        Text(
            text = "Forgot Password?",
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 16.dp)
                .clickable {

                }
        )
        Button(
            onClick = {
                onButtonLoginClick(
                    navigateToHome = navigateToHome,
                    loginWithNumber = sendVerificationCode,
                    loginWithEmail = loginWithEmail,
                    identifier = identifier,
                    setIdentifierDescription = setIdentifierError,
                    showIdentifierError = showIdentifierError,
                    password = password,
                    setPasswordDescription = setPasswordError,
                    showPasswordError = showPasswordError,
                    passwordFieldShowed = passwordFieldShowed,
                    showPasswordField = showPasswordField,
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
    navigateToHome: () -> Unit ,
    loginWithNumber: () -> Unit,
    loginWithEmail: () -> Task<AuthResult>,
    identifier: String,
    setIdentifierDescription: (String) -> Unit,
    showIdentifierError: (Boolean) -> Unit,
    password: String,
    setPasswordDescription: (String) -> Unit,
    showPasswordError: (Boolean) -> Unit,
    passwordFieldShowed: Boolean,
    showPasswordField: () -> Unit,
    showDialog: (Boolean) -> Unit,
    setMessage: (String) -> Unit,
    showLoading: (Boolean) -> Unit
) {
    when {
        identifier.isEmpty() -> {
            setIdentifierDescription("This Field Cannot Be Empty")
            showIdentifierError(true)
        }
        identifier.startsWith("08") -> {
            setIdentifierDescription("Mobile no should start with +628")
            showIdentifierError(true)
        }
        !identifier.startsWith("+628") && !identifier.endsWith("@gmail.com") -> {
            setIdentifierDescription("Identifier is a mobile no or email")
            showIdentifierError(true)
        }
        identifier.startsWith("+628") && identifier.length <= 8 -> {
            setIdentifierDescription("Mobile No Length Must Be Greater Than 8")
            showIdentifierError(true)
        }
        identifier.startsWith("+628") && identifier.length >= 13 -> {
            setIdentifierDescription("Mobile No Length Must Be Less Than 8")
            showIdentifierError(true)
        }
        identifier.startsWith("+628") && identifier.length <= 13 && identifier.length >= 8 -> {
            showLoading(true)
            loginWithNumber()
        }
        identifier.endsWith("@gmail.com") && passwordFieldShowed -> {
            when {
                password.isEmpty() -> {
                    setPasswordDescription("Password Cannot Be Empty")
                    showPasswordError(true)
                }
                password.length < 8 -> {
                    setPasswordDescription("Password Length Must Be Greater Than 8")
                    showPasswordError(true)
                }
                else -> {
                    showLoading(true)
                    loginWithEmail()
                        .addOnCompleteListener {
                            showLoading(false)
                            navigateToHome()
                        }
                        .addOnFailureListener {
                            showLoading(false)
                            setMessage(it.message.toString())
                            showDialog(true)
                            Timber.e(it.message.toString())
                        }
                }
            }
        }
        identifier.endsWith("@gmail.com") && !passwordFieldShowed -> {
            showPasswordField()
        }
    }
}