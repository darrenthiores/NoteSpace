package com.dev.notespace.component

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlin.math.sqrt

@Composable
@ExperimentalPagerApi
fun PdfCarousel(
    modifier: Modifier = Modifier,
    count: Int,
    state: PagerState = rememberPagerState(),
    previews: List<ImageBitmap?>
) {
    val width = LocalConfiguration.current.screenWidthDp
    val height = (width * sqrt(2f)).toInt()

    HorizontalPager(
        count = count,
        state = state,
        modifier = modifier
            .fillMaxWidth()
    ) { page ->
        val preview = previews[page]

        Image(
            bitmap = preview ?: ImageBitmap(width = width, height = height),
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f / sqrt(2f))
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
    }

    if(count==0) {
        Image(
            bitmap = ImageBitmap(width = width, height = height),
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f / sqrt(2f))
                .fillMaxWidth()
                .height(height.dp)
                .background(Color.LightGray),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
@ExperimentalPagerApi
fun ImageUriCarousel(
    modifier: Modifier = Modifier,
    count: Int,
    state: PagerState = rememberPagerState(),
    previews: List<Uri>
) {
    val width = LocalConfiguration.current.screenWidthDp
    val height = (width * sqrt(2f)).toInt()

    HorizontalPager(
        count = count,
        state = state,
        modifier = modifier
            .fillMaxWidth()
    ) { page ->
        val preview = previews[page]

        Image(
            painter = rememberAsyncImagePainter(preview),
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f / sqrt(2f))
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
    }

    if(count==0) {
        Image(
            bitmap = ImageBitmap(width = width, height = height),
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f / sqrt(2f))
                .fillMaxWidth()
                .height(height.dp)
                .background(Color.LightGray),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
@ExperimentalPagerApi
fun ImageUrlCarousel(
    modifier: Modifier = Modifier,
    count: Int,
    state: PagerState = rememberPagerState(),
    previews: List<String>
) {
    val width = LocalConfiguration.current.screenWidthDp
    val height = (width * sqrt(2f)).toInt()

    HorizontalPager(
        count = count,
        state = state,
        modifier = modifier
            .fillMaxWidth()
    ) { page ->
        val preview = previews[page]

        Image(
            painter = rememberAsyncImagePainter(preview),
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f / sqrt(2f))
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
    }

    if(count==0) {
        Image(
            bitmap = ImageBitmap(width = width, height = height),
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f / sqrt(2f))
                .fillMaxWidth()
                .height(height.dp)
                .background(Color.LightGray),
            contentScale = ContentScale.Fit
        )
    }
}