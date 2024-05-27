package com.aura.data.network

import com.aura.data.response.AuraServerResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface AccountClient {
    @GET("/accounts/{id}")
    suspend fun getAccountDetails(
        @Path("id") id: String
    ): Response<List<AuraServerResponse.AccountResponse>>


    @POST("/login")
    suspend fun getLogin(
        @Body body: LoginPassword,
    ): Response<AuraServerResponse.LoginResponse>

    @POST("/transfer")
    suspend fun transferMoney(
        @Body requestBody: TransferRequestBody
    ): Response<AuraServerResponse.TransferResponse>
}

/**
 * class used to call the connection API
 */
data class LoginPassword(
    val id: String,
    val password: String
)

/**
 * class used to call the transfer API
 */
data class TransferRequestBody(
    val sender: String,
    val recipient: String,
    val amount: String
)