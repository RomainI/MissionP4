package com.aura.ui.domain.model

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.network.LoginPassword
import com.aura.data.repository.AccountRepository
import com.aura.data.repository.PreferencesManager
import com.aura.data.repository.Result
import com.aura.di.NetworkUtil
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val dataRepository: AccountRepository, private val preferencesManager: PreferencesManager, application : Application) :
    AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AuraUiState())
    val uiState: Flow<AuraUiState> = _uiState.asStateFlow()

    fun setLoginPassword(login: String, password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                login = login,
                password = password,
                isFilled = password.isNotEmpty() && login.isNotEmpty(),
            )

        }
    }

    fun getConnection(login: String, password: String) {
        if (NetworkUtil.isNetworkAvailable(getApplication())) {

            //Log.d("ACTIVITY", "getConnection: $login $password",)
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null,
                    isLogOk = null
                )
            }
            dataRepository.getLogginAccess(login, password).onEach { logUpdate ->
                when (logUpdate) {
                    is Result.Failure -> _uiState.update { currentState ->
                        currentState.copy(
                            errorMessage = logUpdate.message,
                            isLogOk = false
                        )
                    }

                    Result.Loading -> _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = true,
                            errorMessage = null,
                        )
                    }

                    is Result.Success -> _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = true,
                            isLogOk = logUpdate.value,
                            errorMessage = null,
                        )
                    }
                }
            }.launchIn(viewModelScope)

        } else {
            // Afficher un message ou gérer l'absence de connexion
            Snackbar.make(getApplication(), "No internet connection", Toast.LENGTH_SHORT).show()

        }
    }

    fun saveData(login :String, password: String){
        viewModelScope.launch (Dispatchers.IO){
            //Log.d("savepref", ": $login")
            val loginRequest = LoginPassword(login, password)
            preferencesManager.saveLoginPassword(loginRequest)
        }
    }



}




