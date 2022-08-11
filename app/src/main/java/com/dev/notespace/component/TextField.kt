package com.dev.notespace.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun DataInput(
    modifier: Modifier = Modifier,
    label: String,
    currentText: String,
    onTextChange: (String) -> Unit,
    error: Boolean,
    errorDescription: String,
    showError: (Boolean) -> Unit,
    maxLength: Int = 20,
    type: String = ""
) {
    OutlinedTextField(
        value = currentText,
        onValueChange = { text ->
            if(text.length <= maxLength) {
                if(type != "digit") onTextChange(text) else onTextChange(text.filter { it.isDigit() })
            }
            showError(false)
        },
        label = { Text(text = label) },
        isError = error,
        trailingIcon = {
            if(error) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = errorDescription,
                    tint = MaterialTheme.colors.error
                )
            }
        },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
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
fun PasswordInput(
    modifier: Modifier = Modifier,
    label: String,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    showPassword: () -> Unit,
    error: Boolean,
    errorDescription: String,
    showError: (Boolean) -> Unit,
    maxLength: Int
) {
    OutlinedTextField(
        value = password,
        onValueChange = { text ->
            if(text.length <= maxLength) onPasswordChange(text)
            showError(false)
        },
        label = { Text(text = label) },
        isError = error,
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (passwordVisible) "Hide password" else "Show password"

            Row {
                IconButton(
                    onClick = showPassword
                ){
                    Icon(
                        imageVector  = image,
                        contentDescription = description
                    )
                }

                if(error) {
                    IconButton(
                        onClick = {  }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = errorDescription,
                            tint = MaterialTheme.colors.error
                        )
                    }
                }
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
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