package com.aura.data.repository

import android.util.Log
import com.aura.data.network.AccountClient
import com.aura.data.network.LoginPassword
import com.aura.data.network.TransferRequestBody
import com.aura.ui.domain.model.AccountModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AccountRepository(private val dataService: AccountClient) {

    /**
     * Call the interface used by retrofit to get the connection access
     * @param : id (login) String
     * @param : pass (password) String
     * @return : a Flow of Result containing a Boolean (true is the connection is OK, false if it is not)
     */
    fun getLogginAccess(id: String, pass: String) = flow {
        emit(Result.Loading)
        val result = dataService.getLogin(LoginPassword(id, pass)).body()?.granted
            ?: throw Exception("Invalid data")
        Log.e("POST result", result.toString())
        emit(Result.Success(result))
    }.catch { error ->
        emit(Result.Failure(error.message))
    }

    /**
     * Call the interface used by retrofit to get the account list from an id
     * @param : id (login) String
     * @return : a Flow of Result containing a list of AccountModel
     */

    fun getAccountsFlow(id: String) = flow {
        emit(Result.Loading)
        try {
            val response = dataService.getAccountDetails(id)
            if (response.isSuccessful) {
                val accountModels = response.body()?.map { account ->
                    AccountModel(
                        id = account.id,
                        isMainAccount = account.mainAccount,
                        amount = account.amount
                    )
                } ?: listOf()
                //Log.d("AccountFlow", "Successfully fetched accounts")
                emit(Result.Success(accountModels))
            } else {
                Log.e("AccountFlow", "Failed to fetch accounts: ${response.message()}")
                emit(Result.Failure("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("AccountFlow", "Exception in fetching accounts: ${e.localizedMessage}")
            emit(Result.Failure(e.message ?: "Unknown error"))
        }
    }

    /**
     * Call the interface used by retrofit to get the connection access
     * @param : sender String
     * @param : recipient String
     * @param : amount String
     * @return : a Flow of Result containing a Boolean (true is the transfer is OK, false if it is not)
     */

    fun getTransfer(sender: String, recipient: String, amount: String) = flow {
       //Log.d("AccountRepository", "getTransfer: ")
        emit(Result.Loading)
        val result = dataService.transferMoney(TransferRequestBody( sender, recipient, amount)).body()?.result
        emit(Result.Success(result))
    }.catch { error ->
        //Log.e("WeatherRepository", error.message ?: "")
        emit(Result.Failure(error.message))
    }


}