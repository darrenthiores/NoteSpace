package com.dev.notespace.state

enum class PagingState {
    FirstLoad,
    NextLoad,
    FirstLoadError,
    NextLoadError,
    Success
}