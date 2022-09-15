package com.dev.notespace.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.SettingBottomSheet(
    onShareClicked: () -> Unit,
    onCopyLinkClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(top = 8.dp)
            .align(Alignment.CenterHorizontally)
            .clip(RoundedCornerShape(32.dp))
    ) {
        Divider(
            modifier = Modifier
                .height(6.dp)
                .width(56.dp)
                .background(MaterialTheme.colors.surface)
        )
    }

    Row(
        modifier = Modifier
            .padding(top = 32.dp)
            .padding(horizontal = 24.dp)
            .align(Alignment.CenterHorizontally)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        ManageButton(
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable {
                    onShareClicked()
                },
            icon = Icons.Default.IosShare,
            title = "Share"
        )
        ManageButton(
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable {
                    onCopyLinkClicked()
                },
            icon = Icons.Default.Link,
            title = "Link"
        )
    }

    Card(
        modifier = Modifier
            .padding(top = 16.dp)
            .padding(horizontal = 24.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp
    ) {
        LazyColumn {
            item {
                ManageItem(
                    modifier = Modifier
                        .clickable {

                        },
                    title = "Archive",
                    firstItem = true
                )
                ManageItem(
                    modifier = Modifier
                        .clickable {
                            onEditClicked()
                        },
                    title = "Edit"
                )
                ManageItem(
                    modifier = Modifier
                        .clickable {
                            onDeleteClicked()
                        },
                    title = "Delete"
                )
            }
        }
    }
}