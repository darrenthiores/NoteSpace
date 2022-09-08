package com.dev.notespace.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class NoteSpaceNavigation(
    val icon: ImageVector
) {
    Home(
        icon = Icons.Default.Home
    ),
    Post(
        icon = Icons.Default.Add
    ),
    Profile(
        icon = Icons.Default.AccountCircle
    );

    companion object{
        fun fromRoute(route: String?): NoteSpaceNavigation =
            when (route?.substringBefore("/")) {
                Home.name -> Home
                Profile.name -> Profile
                null -> Home
                else -> Home// throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}