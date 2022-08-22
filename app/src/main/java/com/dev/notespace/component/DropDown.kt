package com.dev.notespace.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EducationDropDown(
    modifier: Modifier = Modifier,
    education: String,
    onItemClicked: (String) -> Unit,
    error: Boolean,
    errorDescription: String,
    showError: (Boolean) -> Unit,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    EducationDropDown(
        modifier = modifier,
        expanded = isExpanded,
        education = education,
        onDropDownClicked = {
            isExpanded = true
            showError(false)
        },
        onDismiss = { isExpanded = false },
        onItemClicked = onItemClicked
    )

    if(error) {
        Text(
            text = errorDescription,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            textAlign = TextAlign.Start
        )
    }
}

@Composable
private fun EducationDropDown(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    education: String,
    onDropDownClicked: () -> Unit,
    onDismiss: () -> Unit,
    onItemClicked: (String) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        OutlinedButton(onClick = onDropDownClicked) {
            Text(text = education.ifEmpty { "Pendidikan" })
            Icon(
                imageVector = if(expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = "Choose Education",
                modifier = Modifier.padding(all = 8.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            modifier = Modifier
                .width(156.dp)
                .padding(all = 4.dp)
        ) {
            educationList().forEach {
                DropdownMenuItem(onClick = {
                    onDismiss()
                    onItemClicked(it)
                }) {
                    Text(
                        text = it,
                        color = if (it == education) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                    )
                }
                Divider()
            }
        }
    }
}

private fun educationList(): List<String> =
    listOf(
        "SMA",
        "Kuliah"
    )