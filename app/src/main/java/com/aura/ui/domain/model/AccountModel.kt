package com.aura.ui.domain.model

import java.util.Calendar

data class AccountModel(
    val id:String,
    val amount: Double,
    val isMainAccount: Boolean
)