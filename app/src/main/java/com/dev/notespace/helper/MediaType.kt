package com.dev.notespace.helper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.ui.graphics.vector.ImageVector

enum class MediaType(
    val icon: ImageVector,
    val description: String
) {
    Pdf(
        Icons.Default.PictureAsPdf,
        "Pdf"
    ),
    Image(
        Icons.Default.Image,
        "Image"
    )
}