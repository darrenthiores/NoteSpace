package com.dev.notespace.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dev.notespace.navigation.NoteSpaceNavigation

@Composable
fun BottomBar(
    allScreens: List<NoteSpaceNavigation>,
    onTabSelected: (NoteSpaceNavigation) -> Unit,
    currentScreen: NoteSpaceNavigation,
    onAddPostClicked: () -> Unit
) {
    BottomNavigation(
        modifier = Modifier,
        backgroundColor = Color.White,
        contentColor = Color.LightGray
    ) {
        allScreens.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.name
                    )
                },
                selected = currentScreen == screen,
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = Color.LightGray,
                onClick = {
                    if(screen == NoteSpaceNavigation.Post) {
                        onAddPostClicked()
                    } else {
                        onTabSelected(screen)
                    }
                }
            )
        }
    }
}