package com.dev.notespace.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ActionTopBar(
    modifier: Modifier = Modifier,
    title: String,
    onBackClicked: () -> Unit,
    actionText: String,
    onActionClicked: () -> Unit
) {
    TopAppBar(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Box {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.padding(16.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                textAlign = TextAlign.Center
            )
            TextButton(
                onClick = onActionClicked,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.primary),
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
                    .align(Alignment.BottomCenter),
                thickness = 1.dp,
            )
        }
    }
}

@Composable
fun MidTitleTopBar(
    modifier: Modifier = Modifier,
    title: String,
    endContent: @Composable (() -> Unit)? = null,
    onBackClicked: () -> Unit
) {
    TopAppBar(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Box {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClicked
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                endContent?.let { it() }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
    }
}