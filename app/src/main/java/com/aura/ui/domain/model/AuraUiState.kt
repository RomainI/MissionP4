package com.aura.ui.domain.model

data class AuraUiState(
    val login: String = "",
    val password: String = "",
    val isFilled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLogOk: Boolean? = null,
    val accounts: List<AccountModel> = emptyList(),
    val resultTransfer : Boolean? = null
)