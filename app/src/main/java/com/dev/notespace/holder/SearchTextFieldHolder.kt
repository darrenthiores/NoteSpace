package com.dev.notespace.holder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SearchTextFieldHolder {
    var searchText by mutableStateOf("")
        private set

    fun setSearchTextValue(newValue: String) {
        searchText = newValue
    }

    var enteredText by mutableStateOf("")
        private set

    fun setEnteredTextValue(newValue: String) {
        enteredText = newValue
    }
}