package com.dev.notespace.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.notespace.R


@Composable
fun LandingScreen(
    navigateToLogin: ()-> Unit
){
    val Painter = painterResource(id = R.drawable.book)
    val contentDescription = "test gif"

    Box(modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Box(modifier = Modifier
            .height(425.dp)
            .width(160.dp)
        ) {
            Image(
                painter = Painter,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colors.primary,
                            fontSize = 35.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append("Share Your Note!" + "\n")
                    }

                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colors.onSurface,
                            fontSize = 14.sp
                        )
                    ) {
                        append("Help people to learn with your note" + "\n"
                                + "or learn by other person note"
                        )
                    }
                },
                textAlign = TextAlign.Center,
            )
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(top = 400.dp),
            contentAlignment = Alignment.Center
        ){
            LandingButton(navigateToLogin)
        }

    }
}

@Composable
fun LandingButton(
    navigateToLogin: ()-> Unit
) {
    Button(onClick = {
        navigateToLogin()
    },
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
        modifier = Modifier
            .height(55.dp)
            .width(210.dp)
    ) {
        Text(text = "Get Started",
            color = Color.White,
            fontSize = 18.sp
        )
    }
}