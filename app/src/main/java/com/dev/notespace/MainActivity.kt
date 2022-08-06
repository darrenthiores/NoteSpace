package com.dev.notespace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dev.notespace.ui.theme.BaseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BaseTheme {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Hello World!",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                // if you has configured the BaseApp() or whatever you name it (better rename)
                // delete the box composable and uncomment BaseApp() or anything you name it
                /** BaseApp() **/
            }
        }
    }
}