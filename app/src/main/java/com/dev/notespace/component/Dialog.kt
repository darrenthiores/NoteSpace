package com.dev.notespace.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dev.notespace.helper.MediaType

@Composable
fun CommonDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text(text = message) },
        buttons = {
            Column {
                Divider(
                    Modifier.padding(horizontal = 12.dp),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                )
                TextButton(
                    onClick = onDismiss,
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CLOSE")
                }
            }
        }
    )
}

@Composable
fun NegativeConfirmationDialog(
    message: String,
    onDismiss: () -> Unit,
    onClicked: () -> Unit,
    confirmationText: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text(text = message) },
        buttons = {
            Column {
                Divider(
                    Modifier.padding(horizontal = 12.dp),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDismiss,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text(
                            "CLOSE"
                        )
                    }
                    TextButton(
                        onClick = onClicked,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text(
                            text = confirmationText,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun PostPickerDialog(
    message: String,
    onDismiss: () -> Unit,
    onClicked: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = message)
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                   MediaType.values().forEach {
                       ManageButton(
                           modifier = Modifier
                               .padding(horizontal = 8.dp)
                               .clickable {
                                   onClicked(it.name)
                               },
                           icon = it.icon,
                           title = it.description
                       )
                   }
                }
            }
        },
        buttons = {
            Column {
                Divider(
                    Modifier.padding(horizontal = 12.dp),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                )
                TextButton(
                    onClick = onDismiss,
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CLOSE")
                }
            }
        }
    )
}