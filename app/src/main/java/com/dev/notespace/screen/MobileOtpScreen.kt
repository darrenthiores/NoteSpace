package com.dev.notespace.screen

import android.app.Activity
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.notespace.component.CommonDialog
import com.dev.notespace.component.OtpTextFields
import com.dev.notespace.viewModel.MobileOtpViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import timber.log.Timber

@ExperimentalComposeUiApi
@Composable
fun MobileOtpScreen(
    modifier: Modifier = Modifier,
    viewModel: MobileOtpViewModel = hiltViewModel(),
    number: String,
    verification_id: String,
    showSnackBar: (String) -> Unit,
    navigateToHome: () -> Unit
) {
    var verificationId by remember {
        mutableStateOf(verification_id)
    }
    var otp by remember {
        mutableStateOf("")
    }
    var timerCount by rememberSaveable {
        mutableStateOf<Long>(10000)
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
    val (otpError, showOtpError) = remember {
        mutableStateOf(false)
    }
    val (otpErrorDescription, setOtpError) = remember {
        mutableStateOf("")
    }

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

    MobileOtpContent(
        modifier = modifier,
        setOtp = { otp = it },
        timer = cdTimer,
        onResent = {
            showLoading(true)
            viewModel.sendVerificationCode(activity, number, callbacks)
        },
        otpError = otpError,
        showOtpError = showOtpError,
        otpErrorDescription = otpErrorDescription,
        verifyOtp = {
            if(otp.length < 6) {
                setOtpError("Please Fill the OTP Code Correctly!")
                showOtpError(true)
            } else {
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, otp)
                viewModel.signInWithCredential(credential)
                    .addOnCompleteListener {
                        navigateToHome()
                    }
                    .addOnFailureListener {
                        setOtpError("OTP Code Incorrect, please check it!")
                        showOtpError(true)
                    }
            }
        }
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

@ExperimentalComposeUiApi
@Composable
private fun MobileOtpContent(
    modifier: Modifier = Modifier,
    setOtp: (String) -> Unit,
    timer: Long,
    onResent: () -> Unit,
    otpError: Boolean,
    showOtpError: (Boolean) -> Unit,
    otpErrorDescription: String,
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
            whenFull = { setOtp(it) },
            error = otpError,
            errorDescription = otpErrorDescription,
            showError = showOtpError
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