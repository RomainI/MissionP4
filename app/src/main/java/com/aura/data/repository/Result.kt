package com.aura.data.repository

sealed class Result <out T>  {

    object Loading : Result<Nothing>()

    data class Failure(
        val message: String? = null,

        ) : Result<Nothing>()

    data class Success <out R>(val value: R): Result<R>()

}