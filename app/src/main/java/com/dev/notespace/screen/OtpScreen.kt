package com.dev.notespace.screen

import android.app.Activity
import android.os.CountDownTimer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.core.domain.model.presenter.User
import com.dev.notespace.component.CommonDialog
import com.dev.notespace.component.MidTitleTopBar
import com.dev.notespace.component.OtpTextFields
import com.dev.notespace.viewModel.OtpViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import timber.log.Timber

@ExperimentalComposeUiApi
@Composable
fun MobileOtpScreen(
    modifier: Modifier = Modifier,
    viewModel: OtpViewModel = hiltViewModel(),
    number: String,
    verification_id: String,
    user: User?,
    showSnackBar: (String) -> Unit,
    navigateToHome: () -> Unit,
    onBackClicked: () -> Unit
) {
    var verificationId by remember {
        mutableStateOf(verification_id)
    }
    var timerCount by rememberSaveable {
        mutableStateOf<Long>(30000)
    }
    var cdTimer by remember {
        mutableStateOf<Long>(0)
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

    val scaffoldState = rememberScaffoldState()

    val timer : CountDownTimer = object : CountDownTimer(timerCount, 1000){

        override fun onTick(millisUntilFinished: Long) {
            val second = millisUntilFinished/1000
            cdTimer = second
        }

        override fun onFinish() {
            timerCount *= 3
        }

    }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {

        }
        override fun onVerificationFailed(p0: FirebaseException) {
            Timber.e(p0.message.toString())
            showLoading(false)
            setMessage(p0.message.toString())
            showDialog(true)
            timer.start()
        }
        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            showLoading(false)
            verificationId = p0
            showSnackBar("Code Sent!")
            timer.start()
        }
    }
    val activity = LocalContext.current as Activity

    LaunchedEffect(true) {
        timer.start()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MidTitleTopBar(
                title = "Verify Otp",
                onBackClicked = onBackClicked
            )
        }
    ) {
        MobileOtpContent(
            modifier = modifier
                .padding(it),
            viewModel = viewModel,
            timer = cdTimer,
            onResent = {
                showLoading(true)
                viewModel.sendVerificationCode(activity, "+62${number.drop(1)}", callbacks)
            },
            verifyOtp = {
                if(viewModel.otp.value.length < 6) {
                    viewModel.otp.setErrorDes("Please Fill the OTP Code Correctly!")
                    viewModel.otp.setTextFieldError(true)
                } else {
                    val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, viewModel.otp.value)
                    viewModel.signInWithCredential(credential)
                        .addOnCompleteListener {
                            if(it.isSuccessful) {
                                if(user==null) {
                                    navigateToHome()
                                } else {
                                    viewModel.registerUser(user)
                                        .addOnCompleteListener { register ->
                                            if(register.isSuccessful) {
                                                viewModel.updateNumber(number)
                                                    .addOnCompleteListener { update ->
                                                        if(update.isSuccessful) {
                                                            navigateToHome()
                                                        }
                                                    }
                                                    .addOnFailureListener {
                                                        setMessage("Failed Registering User! Please Try Again")
                                                        showDialog(true)
                                                        viewModel.logOut()
                                                    }
                                            }
                                        }
                                        .addOnFailureListener {
                                            setMessage("Failed Registering User! Please Try Again")
                                            showDialog(true)
                                            viewModel.logOut()
                                        }
                                }
                            }
                        }
                        .addOnFailureListener {
                            viewModel.otp.setErrorDes("OTP Code Incorrect, please check it!")
                            viewModel.otp.setTextFieldError(true)
                        }
                }
            }
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

@ExperimentalComposeUiApi
@Composable
private fun MobileOtpContent(
    modifier: Modifier = Modifier,
    viewModel: OtpViewModel,
    timer: Long,
    onResent: () -> Unit,
    verifyOtp: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OtpTextFields(
            modifier = Modifier
                .padding(top = 128.dp)
                .fillMaxWidth(),
            whenFull = { viewModel.otp.setTextFieldValue(it) },
            error = viewModel.otp.error,
            errorDescription = viewModel.otp.errorDescription,
            showError = viewModel.otp::setTextFieldError
        )

        if(timer > 0) {
            Text(
                text = "$timer second..",
                modifier = Modifier
                    .padding(8.dp)
            )
        } else {
            Text(
                text = "RESENT OTP",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        onResent()
                    }
            )
        }

        Button(
            onClick = verifyOtp,
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Verify")
        }
    }
}