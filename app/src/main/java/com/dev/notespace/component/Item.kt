package com.dev.notespace.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import androidx.compose.ui.text.font.FontWeight
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
            .width(180.dp)
            .height(160.dp)
            .clip(RoundedCornerShape(4.dp)),
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier
        ) {
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(100.dp)
            ){
                Image(
                    bitmap = preview ?: ImageBitmap(100, 100),
                    contentDescription = null,
                    modifier = Modifier
                        .width(180.dp)
                        .height(120.dp)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(36.dp)
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
                                .size(20.dp)
                        )
                        Text(
                            text = star.toString(),
                            modifier = Modifier.padding(top = 1.dp),
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
            }
            Text(
                text = name,
                modifier = Modifier
                    .padding(top = 8.dp, start = 4.dp),
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = subject,
                modifier = Modifier
                    .padding(start = 4.dp),
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Composable
fun UserNoteItem(
    modifier: Modifier = Modifier,
    preview: ImageBitmap?,
    star: Int,
    name: String,
    subject: String,
    firstItem: Boolean = false
) {
    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ) {
        if(firstItem) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray),
                thickness = 1.dp,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = preview ?: ImageBitmap(100, 100),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = name,
                    modifier = Modifier,
                    style = MaterialTheme.typography.subtitle1
                )
                Text(
                    text = subject,
                    modifier = Modifier,
                    style = MaterialTheme.typography.caption
                )
            }

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(30.dp)
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
    }
}

@Composable
fun SearchNoteItem(
    modifier: Modifier = Modifier,
    preview: ImageBitmap?,
    star: Int,
    name: String,
    subject: String
) {
    Card(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .width(160.dp)
            .height(240.dp),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
        ) {
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(200.dp)
            ){
                Image(
                    bitmap = preview ?: ImageBitmap(100, 100),
                    contentDescription = null,
                    modifier = Modifier
                        .width(160.dp)
                        .height(200.dp)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .padding(4.dp)
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
                    .padding(top = 8.dp, start = 4.dp),
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                text = subject,
                modifier = Modifier
                    .padding(start = 4.dp),
                style = MaterialTheme.typography.caption
            )
        }
    }
}