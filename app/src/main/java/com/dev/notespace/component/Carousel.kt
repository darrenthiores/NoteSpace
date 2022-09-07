package com.dev.notespace.component

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    HorizontalPager(
        count = count,
        state = state,
        contentPadding = PaddingValues(horizontal = 32.dp),
        modifier = modifier
            .fillMaxWidth()
    ) { page ->
        val preview = previews[page]

        val width = LocalConfiguration.current.screenWidthDp
        val height = (width * sqrt(2f)).toInt()

        Image(
            bitmap = preview ?: ImageBitmap(width = width, height = height),
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f / sqrt(2f))
                .fillMaxWidth()
                .background(Color.LightGray),
            contentScale = ContentScale.Fit
        )
    }
}