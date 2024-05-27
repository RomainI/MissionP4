package com.aura.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * class used to parse JSON returns from the API
 */
@JsonClass(generateAdapter = true)
class AuraServerResponse {

    @JsonClass(generateAdapter = true)
    data class AccountResponse(
        @Json(name = "id")
        val id: String,
        @Json(name = "main")
        val mainAccount: Boolean,
        @Json(name = "balance")
        val amount: Double,
    )

    @JsonClass(generateAdapter = true)
    data class TransferResponse(
        @Json(name = "result")
        val result: Boolean,
    )

    @JsonClass(generateAdapter = true)
    data class LoginResponse(
        val granted: Boolean,
    )

}