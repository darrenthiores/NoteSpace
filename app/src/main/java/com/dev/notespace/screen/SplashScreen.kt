package com.dev.notespace.screen

import android.window.SplashScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.notespace.R
import com.dev.notespace.viewModel.SplashViewModel
import kotlinx.coroutines.delay

private const val SplashWaitTime: Long = 2000

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
    navigateToLanding: () -> Unit,
    navigateToHome: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Landing Screen" },
        contentAlignment = Alignment.Center
    ) {
        LaunchedEffect(true) {
            delay(SplashWaitTime)
            if(viewModel.isSignIn) {
                navigateToHome()
            } else {
                navigateToLanding()
            }
        }

        Image(
            painterResource(id = R.drawable.notespace_logo),
            contentDescription = null,
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.Center),
            contentScale = ContentScale.Crop
        )
    }
}