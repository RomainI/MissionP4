package com.aura.ui.domain.model

data class HomeUiState(
    val login: String = "",
    val password: String = "",
    val isFilled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLogOk: Boolean = false,
    val accounts: List<AccountModel> = emptyList()
)