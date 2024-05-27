package com.aura.ui.domain.model

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.repository.AccountRepository
import com.aura.data.repository.PreferencesManager
import com.aura.data.repository.Result
import com.aura.di.NetworkUtil
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TransferViewModel @Inject constructor(
    private val dataRepository: AccountRepository,
    private val preferencesManager: PreferencesManager,
    application: Application
) :
    AndroidViewModel(application) {

    private val _loginState = MutableStateFlow<String?>(null)
    val loginState: StateFlow<String?> = _loginState.asStateFlow()
    private val _uiState = MutableStateFlow(AuraUiState())
    val uiState: Flow<AuraUiState> = _uiState.asStateFlow()
    private var jobFlow: Job? = null


    /**
     * Function used to activate, or not, the transfer button when recipient and amount are both filled
     * @param recipient String
     * @param amount String
     *
     */
    fun checkTransferButton(recipient: String, amount: String) {
        _uiState.update { currentState ->
            currentState.copy(
                isFilled = recipient.isNotEmpty() && amount.isNotEmpty() && amount != "",
            )

        }
    }

    /**
     * Used to proceed via the repository, and the API. Updates uiState depending of the answer
     * @param recipient String
     * @param amount String
     *
     */
    fun proceedTransfer(recipient: String, amount: String) {
        stopFlow()
        if (NetworkUtil.isNetworkAvailable(getApplication())) {
            jobFlow = viewModelScope.launch {
                preferencesManager.loginFlow.collect { loginRequest ->
                    // Ensure _loginState is not null and loginRequest is valid before assignment
                    _loginState.value = loginRequest.id
                    _uiState.value = _uiState.value.copy(isLoading = true)
                    val login = loginState.value
                    // Ensure there is a login before proceeding
                    if (login != null) {
                        dataRepository.getTransfer(login, recipient, amount).collect { result ->
                            when (result) {
                                is Result.Loading -> _uiState.value =
                                    _uiState.value.copy(isLoading = true)

                                is Result.Success -> _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    resultTransfer = result.value,
                                    errorMessage = null
                                )

                                is Result.Failure -> _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    resultTransfer = false,
                                    errorMessage = result.message
                                )
                            }
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Unexpected Error",
                            resultTransfer = false
                        )
                    }

                }

            }
        } else {
            /**
             * Display a Snackbar when there is an internet issue
             */
            Snackbar.make(getApplication(), "No internet connection", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Used to cancel jobFlow
     *
     */
    fun stopFlow() {
        jobFlow?.cancel()
    }


}