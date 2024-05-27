package com.aura.ui.domain.model

import android.app.Application
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

    private val _loginState = MutableStateFlow<String?>(null)
    private val loginState: StateFlow<String?> = _loginState.asStateFlow()

    /**
     * provides flow of account model list. It uses login catched from DataStore
     */
    fun getAccounts() {
        viewModelScope.launch {
            preferencesManager.loginFlow.collectLatest { login ->
                if (login != null && login.id.isNotEmpty()) {
                    dataRepository.getAccountsFlow(login.id).collect { resultUpdate ->
                        handleResultUpdate(resultUpdate)
                    }
                } else {
                }
            }
        }
    }

    /**
     * Updates the uiState based on a Result<List<AccountModel>>
     */
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



    fun resetLoginState() {
        _loginState.value = null
    }


}

