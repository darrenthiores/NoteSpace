package com.dev.notespace.component

import android.annotation.SuppressLint
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlin.math.roundToInt

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

const val ANIMATION_DURATION = 500
const val MIN_DRAG_AMOUNT = 6

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun SwipeAbleUserItem(
    modifier: Modifier = Modifier,
    childModifier: Modifier = Modifier,
    preview: String,
    star: Int,
    name: String,
    subject: String,
    onDelete: () -> Unit,
    onSetting: () -> Unit,
    firstItem: Boolean = false
) {
    var isRevealed by remember {
        mutableStateOf(false)
    }
    val transitionState = remember {
        MutableTransitionState(isRevealed).apply {
            targetState = !isRevealed
        }
    }
    val transition = updateTransition(transitionState, "cardTransition")
    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (isRevealed) -168f else 0f }
        )

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

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .width(50.dp)
                    .align(Alignment.CenterEnd)
                    .background(Color.LightGray)
                    .clickable {
                        onDelete()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    tint = Color.Gray,
                    contentDescription = "delete action",
                    modifier = Modifier
                        .size(24.dp),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            when {
                                dragAmount >= MIN_DRAG_AMOUNT -> {
                                    isRevealed = false
                                }
                                dragAmount < -MIN_DRAG_AMOUNT -> {
                                    isRevealed = true
                                }
                            }
                        }
                    }
                    .background(MaterialTheme.colors.background),
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

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable {
                            onSetting()
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Settings",
                        modifier = Modifier
                            .padding(4.dp)
                            .size(24.dp)
                    )
                }
            }
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

@Composable
fun ManageItem(
    modifier: Modifier = Modifier,
    title: String,
    textColor: Color = Color.Black,
    firstItem: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!firstItem) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray),
                thickness = 1.dp,
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle1.copy(
                fontWeight = FontWeight.Normal,
                color = textColor
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
fun ProfileSettingItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = Color.Black),
                onClick = onClick
            ), contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = icon, contentDescription = "Icon", tint = color)
                    Text(modifier = Modifier.padding(24.dp), text = label, color = color)
                }

                Icon(
                    modifier = Modifier.fillMaxHeight(),
                    imageVector = Icons.Default.ArrowRight,
                    contentDescription = "Arrow Right",
                    tint = Color.DarkGray
                )
            }

            Divider(thickness = 1.dp, modifier = Modifier.fillMaxWidth())
        }
    }
}