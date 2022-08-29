package com.dev.notespace.holder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TextFieldHolder {
    var value by mutableStateOf("")
        private set

    fun setTextFieldValue(newValue: String) {
        value = newValue
    }

    var error by mutableStateOf(false)
        private set

    fun setTextFieldError(isError: Boolean) {
        error = isError
    }

    var errorDescription by mutableStateOf("")
        private set

    fun setErrorDes(desc: String) {
        errorDescription = desc
    }
}