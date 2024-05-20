package com.aura.ui.domain.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.repository.AccountRepository
import com.aura.data.repository.PreferencesManager
import com.aura.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataRepository: AccountRepository,
    private val preferencesManager: PreferencesManager,
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AuraUiState())
    val uiState: Flow<AuraUiState> = _uiState.asStateFlow()

    /** init {
    Log.d("HomeViewModel", "INIT() ")
    getLogin()
    }*/


    private val _loginState = MutableStateFlow<String?>(null)
    private val loginState: StateFlow<String?> = _loginState.asStateFlow()

    /**private fun getLogin() {
    viewModelScope.launch {
    preferencesManager.loginFlow.collectLatest { loginRequest ->
    _loginState.value = loginRequest.id
    Log.d("LoginViewModel", "Login retrieved: ${loginRequest.id}")
    }
    }
    }*/

    private fun getLogin() {
        viewModelScope.launch {
            preferencesManager.loginFlow.collectLatest { loginRequest ->
                // Ensure _loginState is not null and loginRequest is valid before assignment
                if (_loginState != null) {
                    _loginState.value = loginRequest.id
                    Log.d("HomeViewModel", "getLogin login: ${loginRequest.id}")
                }
            }
        }
    }


    /**
    fun getAccounts() {
    getLogin()
    viewModelScope.launch {

    Log.d("getAccountsLogin", ": $login")

    if (login.isNotEmpty())
    dataRepository.getAccountsFlow(login).collect { resultUpdate ->
    when (resultUpdate) {
    is Result.Failure -> _uiState.update { currentState ->
    currentState.copy(
    errorMessage = resultUpdate.message
    )
    }

    Result.Loading -> _uiState.update { currentState ->
    currentState.copy(
    isLoading = true,
    errorMessage = null,
    )
    }

    is Result.Success ->
    _uiState.update { currentState ->
    currentState.copy(
    isLoading = false,
    errorMessage = null,
    accounts = resultUpdate.value
    )
    }


    }
    }

    }


    }*/

    fun getAccounts() {
        getLogin()
        viewModelScope.launch {
            loginState.collect { login ->
                if (login != null && login.isNotEmpty()) {
                    Log.d("HomeViewModel", "Fetching accounts for login: $login")
                    dataRepository.getAccountsFlow(login).collect { resultUpdate ->
                        handleResultUpdate(resultUpdate)
                    }
                } else {
                    Log.d("HomeViewModel", "getAccounts login is null")
                }
            }
        }
    }


    private fun handleResultUpdate(resultUpdate: Result<List<AccountModel>>) {
        when (resultUpdate) {
            is Result.Failure -> _uiState.update { currentState ->
                currentState.copy(errorMessage = resultUpdate.message)
            }

            Result.Loading -> _uiState.update { currentState ->
                currentState.copy(isLoading = true, errorMessage = null)
            }

            is Result.Success -> _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    errorMessage = null,
                    accounts = resultUpdate.value
                )
            }
        }
    }

    fun deletePreferences() {
        viewModelScope.launch {
            preferencesManager.deleteLogin()
        }
    }

    fun resetLoginState() {
        _loginState.value = null
    }


}

