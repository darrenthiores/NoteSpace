package com.dev.notespace.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


@Composable
fun InterestCheckBoxItem(
    modifier: Modifier = Modifier,
    subject: String,
    checked: Boolean,
    onChecked: (String, Boolean) -> Unit
) {
    CheckBoxItem(
        modifier = modifier,
        subject = subject,
        checked = checked,
        onChecked = { newValue ->
            onChecked(subject, newValue)
        }
    )
}

@Composable
private fun CheckBoxItem(
    modifier: Modifier = Modifier,
    subject: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = subject,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            style = MaterialTheme.typography.body1
        )
        Checkbox(
            modifier = Modifier,
            checked = checked,
            onCheckedChange = onChecked
        )
    }
}