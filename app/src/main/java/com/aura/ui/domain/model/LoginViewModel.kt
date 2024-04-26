package com.aura.ui.domain.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: Flow<HomeUiState> = _uiState.asStateFlow()

    private val _isButtonEnabled = MutableStateFlow(false)


    fun setLoginPassword(login: String, password: String) {
        _uiState.update {currentState ->
            currentState.copy(
                login = login,
                password = password,
                isFilled = password.isNotEmpty() && login.isNotEmpty()

            )
        }
    }


}




data class HomeUiState(
    val login: String = "",
    val password: String ="",
    val isFilled: Boolean = false,
    val errorMessage: String? = null
)