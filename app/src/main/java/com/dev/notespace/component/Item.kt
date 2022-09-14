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
    childModifier: Modifier = Modifier,
    preview: String,
    star: Int,
    name: String,
    subject: String,
    lastItem: Boolean = false
) {
    Card(
        modifier = modifier
            .padding(end = if (lastItem) 0.dp else 8.dp)
            .width(180.dp)
            .height(160.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier
                .width(180.dp)
                .height(160.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(preview),
                contentDescription = null,
                modifier = childModifier
                    .width(180.dp)
                    .height(100.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier
                    .padding(top = 8.dp, start = 4.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = name,
                        modifier = childModifier,
                        style = MaterialTheme.typography.subtitle1.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = subject,
                        modifier = childModifier,
                        style = MaterialTheme.typography.caption
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star(s)",
                        tint = Color.Yellow,
                        modifier = childModifier
                            .size(20.dp)
                    )
                    Text(
                        text = star.toString(),
                        modifier = childModifier.padding(start = 2.dp, end = 4.dp),
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
    }
}

@Composable
fun UserNoteItem(
    modifier: Modifier = Modifier,
    childModifier: Modifier = Modifier,
    preview: String,
    star: Int,
    name: String,
    subject: String,
    firstItem: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        if(!firstItem) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray),
                thickness = 1.dp,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(preview),
                contentDescription = null,
                modifier = childModifier
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
                    modifier = childModifier,
                    style = MaterialTheme.typography.subtitle1
                )
                Text(
                    text = subject,
                    modifier = childModifier,
                    style = MaterialTheme.typography.caption
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star(s)",
                        tint = Color.Yellow,
                        modifier = childModifier
                            .size(20.dp)
                    )
                    Text(
                        text = star.toString(),
                        modifier = childModifier.padding(start = 2.dp),
                        style = MaterialTheme.typography.caption
                    )
                }
            }

//            Box(
//                modifier = Modifier
//                    .padding(4.dp)
//                    .size(36.dp)
//                    .clip(CircleShape)
//                    .background(Color.LightGray)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .align(Alignment.Center),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Star,
//                        contentDescription = "Star(s)",
//                        tint = Color.Yellow,
//                        modifier = Modifier
//                            .size(20.dp)
//                    )
//                    Text(
//                        text = star.toString(),
//                        modifier = Modifier.padding(top = 1.dp),
//                        style = MaterialTheme.typography.caption
//                    )
//                }
//            }
        }
    }
}

@Composable
fun SearchNoteItem(
    modifier: Modifier = Modifier,
    preview: String,
    star: Int,
    name: String,
    subject: String
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier
        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(180.dp)
//            ){
//                Image(
//                    painter = rememberAsyncImagePainter(preview),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(180.dp)
//                        .background(Color.LightGray),
//                    contentScale = ContentScale.Crop
//                )
//                Box(
//                    modifier = Modifier
//                        .padding(4.dp)
//                        .size(36.dp)
//                        .align(Alignment.TopEnd)
//                        .clip(CircleShape)
//                        .background(Color.LightGray)
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .align(Alignment.Center),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Star,
//                            contentDescription = "Star(s)",
//                            tint = Color.Yellow,
//                            modifier = Modifier
//                                .size(20.dp)
//                        )
//                        Text(
//                            text = star.toString(),
//                            modifier = Modifier.padding(top = 1.dp),
//                            style = MaterialTheme.typography.caption
//                        )
//                    }
//                }
//            }
            Image(
                painter = rememberAsyncImagePainter(preview),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier
                    .padding(top = 8.dp, start = 4.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = name,
                        modifier = Modifier,
                        style = MaterialTheme.typography.subtitle1.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = subject,
                        modifier = Modifier,
                        style = MaterialTheme.typography.caption
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
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
                        modifier = Modifier.padding(start = 2.dp, end = 4.dp),
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
    }
}