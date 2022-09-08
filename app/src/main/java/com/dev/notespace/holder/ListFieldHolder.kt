package com.dev.notespace.holder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ListFieldHolder {
    var value = mutableStateListOf<String>()
        private set

    fun updateTextFieldValue(newValue: String) {
        if(value.contains(newValue)) value.remove(newValue) else value.add(newValue)
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