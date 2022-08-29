package com.dev.notespace.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.dev.notespace.holder.TextFieldHolder

@Composable
fun DataInput(
    modifier: Modifier = Modifier,
    label: String,
    currentText: String,
    onTextChange: (String) -> Unit,
    error: Boolean,
    errorDescription: String,
    showError: (Boolean) -> Unit,
    maxLength: Int = 20
) {
    OutlinedTextField(
        value = currentText,
        onValueChange = { text ->
            if(text.length <= maxLength) {
                onTextChange(text)
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
            .padding(vertical = 4.dp)
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
fun DataInput(
    modifier: Modifier = Modifier,
    label: String,
    textFieldHolder: TextFieldHolder,
    maxLength: Int = 20
) {
    OutlinedTextField(
        value = textFieldHolder.value,
        onValueChange = { text ->
            if(text.length <= maxLength) {
                textFieldHolder.setTextFieldValue(text)
            }
            textFieldHolder.setTextFieldError(false)
        },
        label = { Text(text = label) },
        isError = textFieldHolder.error,
        trailingIcon = {
            if(textFieldHolder.error) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = textFieldHolder.errorDescription,
                    tint = MaterialTheme.colors.error
                )
            }
        },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 4.dp)
    )
    if(textFieldHolder.error) {
        Text(
            text = textFieldHolder.errorDescription,
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
fun DigitDataInput(
    modifier: Modifier = Modifier,
    label: String,
    currentText: String,
    onTextChange: (String) -> Unit,
    error: Boolean,
    errorDescription: String,
    showError: (Boolean) -> Unit,
    maxLength: Int = 20
) {
    OutlinedTextField(
        value = currentText,
        onValueChange = { text ->
            if(text.length <= maxLength) {
                onTextChange(text)
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
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 4.dp)
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
fun DigitDataInput(
    modifier: Modifier = Modifier,
    label: String,
    textFieldHolder: TextFieldHolder,
    maxLength: Int = 20
) {
    OutlinedTextField(
        value = textFieldHolder.value,
        onValueChange = { text ->
            if(text.length <= maxLength) {
                textFieldHolder.setTextFieldValue(text)
            }
            textFieldHolder.setTextFieldError(false)
        },
        label = { Text(text = label) },
        isError = textFieldHolder.error,
        trailingIcon = {
            if(textFieldHolder.error) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = textFieldHolder.errorDescription,
                    tint = MaterialTheme.colors.error
                )
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 4.dp)
    )
    if(textFieldHolder.error) {
        Text(
            text = textFieldHolder.errorDescription,
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
fun NonErrorDataInput(
    modifier: Modifier = Modifier,
    label: String,
    currentText: String,
    onTextChange: (String) -> Unit,
    maxLength: Int = 20,
    type: String = ""
) {
    OutlinedTextField(
        value = currentText,
        onValueChange = { text ->
            if(text.length <= maxLength) {
                if(type != "digit") onTextChange(text) else onTextChange(text.filter { it.isDigit() })
            }
        },
        label = { Text(text = label) },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 4.dp)
    )
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

@ExperimentalComposeUiApi
@Composable
fun OtpTextFields(
    modifier: Modifier,
    otpCodeLength: Int = 6,
    whenFull: (String) -> Unit,
    error: Boolean,
    errorDescription: String,
    showError: (Boolean) -> Unit
) {
    val enteredNumbers = remember {
        mutableStateListOf(
            *((0 until otpCodeLength).map { "" }.toTypedArray())
        )
    }
    val focusRequesters: List<FocusRequester> = remember {
        (0 until otpCodeLength).map { FocusRequester() }
    }
    Row(
        modifier = modifier
    ) {
        (0 until otpCodeLength).forEach { index ->
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .size(160.dp, 80.dp)
                    .padding(horizontal = 4.dp)
                    .onKeyEvent { event ->
                        val cellValue = enteredNumbers[index]
                        if (event.type == KeyEventType.KeyUp) {
                            if (event.key == Key.Backspace && cellValue == "") {
                                if (index > 0) {
                                    focusRequesters
                                        .getOrNull(index - 1)
                                        ?.requestFocus()
                                    enteredNumbers[index - 1] = ""
                                }
                            } else if (cellValue != "") {
                                focusRequesters
                                    .getOrNull(index + 1)
                                    ?.requestFocus()
                            }
                        }
                        false
                    }
                    .padding(vertical = 2.dp)
                    .focusRequester(focusRequesters[index]),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.LightGray,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedIndicatorColor = MaterialTheme.colors.primary,
                    cursorColor = Color.Gray,
                    textColor = Color.Gray
                ),
                singleLine = true,
                value = enteredNumbers[index],
                onValueChange = { value: String ->
                    if (value.isDigitsOnly()) {
                        if (value.length > 1) {
                            enteredNumbers[index] = value.last().toString()
                            showError(false)
                            whenFull(enteredNumbers.joinToString(separator = ""))
                            return@TextField
                        }
                        if (focusRequesters[index].freeFocus()) {
                            enteredNumbers[index] = value
                            if (enteredNumbers[index].isBlank() && index > 0 && index < otpCodeLength) {
                                focusRequesters[index - 1].requestFocus()
                            } else if (index < otpCodeLength - 1) {
                                focusRequesters[index + 1].requestFocus()
                            }
                        }
                    }
                    showError(false)
                    whenFull(enteredNumbers.joinToString(separator = ""))
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                textStyle = MaterialTheme.typography.caption
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
    if(error) {
        Text(
            text = errorDescription,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Start
        )
    }
}