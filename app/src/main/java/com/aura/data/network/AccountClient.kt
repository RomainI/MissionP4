package com.aura.data.network

import com.aura.data.response.AuraServerResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
interface AccountClient {
    @GET("/accounts/{id}")
    suspend fun getAccountDetails(
        @Path("id") id: String
    ): Response<List<AuraServerResponse.AccountResponse>>


    @POST("/login")
    suspend fun getLogin(
        @Body body: LoginRequest,
       /** @Query (value = "id") id: String,
        @Query (value = "password") password : String*/
    ): Response<AuraServerResponse.LoginResponse>

    @POST("/transfer")
    suspend fun transferMoney(
        @Body requestBody: TransferRequestBody
    ): Response<AuraServerResponse.TransferResponse>
}

data class LoginRequest(
    val id: String,
    val password: String
)

data class TransferRequestBody(
    val sender: String,
    val recipient: String,
    val amount: String
)