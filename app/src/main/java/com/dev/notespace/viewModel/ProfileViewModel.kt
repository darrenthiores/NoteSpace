package com.dev.notespace.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.core.domain.model.presenter.User
import com.dev.core.domain.useCase.NoteSpaceUseCase
import com.dev.core.utils.DataMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val useCase: NoteSpaceUseCase
): ViewModel() {
    private val _user = mutableStateOf<User?>(null)
    val user: State<User?>
        get() = _user

    init {
        viewModelScope.launch {
            _user.value = DataMapper.mapUserDomainToPresenter(useCase.getUserData())
        }
    }
}