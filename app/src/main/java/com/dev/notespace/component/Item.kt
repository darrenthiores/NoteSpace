package com.dev.notespace.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    preview: ImageBitmap?,
    star: Int,
    name: String,
    subject: String
) {
    Card(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .width(120.dp)
            .height(80.dp),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(60.dp)
            ){
                Image(
                    painter = rememberAsyncImagePainter(preview),
                    contentDescription = "$name preview",
                    modifier = Modifier
                        .width(120.dp)
                        .height(60.dp),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star(s)",
                            tint = Color.Yellow,
                            modifier = Modifier
                                .size(24.dp)
                        )
                        Text(
                            text = star.toString(),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = name,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
            Text(
                text = subject,
                modifier = Modifier
                    .padding(top = 2.dp)
            )
        }
    }
}