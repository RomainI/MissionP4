package com.aura.data.repository

import android.util.Log
import com.aura.data.repository.Result
import com.aura.data.network.AccountClient
import com.aura.data.network.LoginRequest
import com.aura.data.network.TransferRequestBody
import com.aura.ui.domain.model.AccountModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class AccountRepository(private val dataService: AccountClient) {


    //private val _uiState = MutableStateFlow(HomeUiState())
    //val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    //fun updateState(update: (HomeUiState) -> HomeUiState) {
    //    _uiState.update(update)
    //}
    fun getLogginAccess(id: String, pass: String) = flow {
        emit(Result.Loading)
        val result = dataService.getLogin(LoginRequest(id, pass)).body()?.granted
            ?: throw Exception("Invalid data")
        Log.e("POST result", result.toString())
        emit(Result.Success(result))
    }.catch { error ->
        //Log.e("WeatherRepository", error.message ?: "")
        emit(Result.Failure(error.message))
    }

    /**   fun getAccounts(id: String) = flow {
    emit(Result.Loading)
    val isMainAccount =
    dataService.getAccountDetails(id).body()?.mainAccount ?: throw Exception("Invalid data")
    val amount =
    dataService.getAccountDetails(id).body()?.amount ?: throw Exception("Invalid data")
    val idAccount =
    dataService.getAccountDetails(id).body()?.id ?: throw Exception("Invalid Data")
    val result = AccountModel(idAccount, amount, isMainAccount)
    emit(Result.Success(result))
    }.catch { error ->
    emit(Result.Failure(error.message))
    }
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
                Log.d("AccountFlow", "Successfully fetched accounts")
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
   /** fun getAccountsFlow(id: String) = flow {
        emit(Result.Loading)
        // Récupérer la liste d'Account depuis l'API
        val accounts = dataService.getAccountDetails(id)

        // Convertir la liste d'Account en liste d'AccountModel
        val accountModels = accounts.body()?.map { account ->

                AccountModel(
                    id = account.id,
                    isMainAccount = account.mainAccount,
                    amount = account.amount
                )
                //Log.d("accountmodels2", "getAccountsFlow: "+account.amount.toString())

        }?.toMutableList() ?: mutableListOf()
        emit(Result.Success(accountModels))
    }.catch { error ->
        emit(Result.Failure(error.message))
    }*/

    fun getTransfer(sender: String, recipient: String, amount: String) = flow {
       Log.d("AccountRepository", "getTransfer: ")
        emit(Result.Loading)
        val result = dataService.transferMoney(TransferRequestBody( sender, recipient, amount)).body()?.result
        emit(Result.Success(result))
    }.catch { error ->
        //Log.e("WeatherRepository", error.message ?: "")
        emit(Result.Failure(error.message))
    }


}