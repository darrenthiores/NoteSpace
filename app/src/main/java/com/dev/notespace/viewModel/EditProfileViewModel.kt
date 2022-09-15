package com.dev.notespace.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.core.domain.model.presenter.User
import com.dev.core.domain.useCase.NoteSpaceUseCase
import com.dev.core.utils.DataMapper
import com.dev.notespace.holder.ListFieldHolder
import com.dev.notespace.holder.TextFieldHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val useCase: NoteSpaceUseCase
): ViewModel() {
    private val _user = mutableStateOf<User?>(null)
    val user: State<User?>
        get() = _user

    val identifierHolder = TextFieldHolder()
    val nameHolder = TextFieldHolder()
    val educationHolder = TextFieldHolder()
    val majorHolder = TextFieldHolder()
    val interestsHolder = ListFieldHolder()

    init {
        viewModelScope.launch {
            val data = DataMapper.mapUserDomainToPresenter(useCase.getUserData())
            _user.value = data
            identifierHolder.setTextFieldValue(data.mobile)
            nameHolder.setTextFieldValue(data.name)
            educationHolder.setTextFieldValue(data.education)
            majorHolder.setTextFieldValue(data.major)
            data.interests.forEach {
                interestsHolder.updateTextFieldValue(it)
            }
        }
    }

    fun updateProfile() = viewModelScope.launch {
        useCase.updateUser(
            nameHolder.value,
            interestsHolder.value.subList(0, interestsHolder.value.size).toList(),
            educationHolder.value,
            majorHolder.value
        )
    }
}