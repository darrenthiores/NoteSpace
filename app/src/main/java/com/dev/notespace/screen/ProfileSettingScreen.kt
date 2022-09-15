package com.dev.notespace.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.notespace.component.MidTitleTopBar
import com.dev.notespace.component.NegativeConfirmationDialog
import com.dev.notespace.component.ProfileSettingItem
import com.dev.notespace.viewModel.ProfileSettingViewModel

@Composable
fun ProfileScreenSettings(
    viewModel: ProfileSettingViewModel = hiltViewModel(),
    navigateToUpdateProfile: () -> Unit,
    navigateToAbout: () -> Unit,
    navigateOnLogOut: () -> Unit,
    onBackClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            MidTitleTopBar(
                title = "Settings",
                onBackClicked = onBackClicked
            )
        }
    ) {
        ProfileScreenSettingsContent(
            modifier = Modifier
                .padding(it),
            viewModel = viewModel,
            navigateToUpdateProfile = navigateToUpdateProfile,
            navigateToAbout = navigateToAbout
        )
    }

    if(viewModel.showDialog) {
        NegativeConfirmationDialog(
            message = "Are You Sure Want To Log Out?",
            onDismiss = { viewModel.setShowDialogValue(false) },
            onClicked = {
                viewModel.logOut()
                navigateOnLogOut()
            },
            confirmationText = "LOG OUT"
        )
    }
}

@Composable
private fun ProfileScreenSettingsContent(
    modifier: Modifier = Modifier,
    viewModel: ProfileSettingViewModel,
    navigateToUpdateProfile: () -> Unit,
    navigateToAbout: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        item {
            ProfileSettingItem(
                icon = Icons.Default.Circle,
                label = "Update Profile",
                color = Color.Black
            ) {
                navigateToUpdateProfile()
            }
            ProfileSettingItem(
                icon = Icons.Default.Circle,
                label = "About",
                color = Color.Black
            ) {
                navigateToAbout()
            }
            ProfileSettingItem(
                icon = Icons.Default.Circle,
                label = "Log Out",
                color = Color.Red
            ) {
                viewModel.setShowDialogValue(true)
            }
        }
    }
}