package com.aura.data.repository


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aura.data.network.LoginPassword
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map



class PreferencesManager(private val dataStore: DataStore<Preferences>) {

    /**
     * variable used to set and get login and password from and to DataStore
     */
    val loginFlow: Flow<LoginPassword> = dataStore.data
        .map { preferences -> LoginPassword(
            id = preferences[ID_KEY] ?: "",
            password = preferences[PASSWORD_KEY] ?: ""
            )
        }

    /**
     * function used to save login and password in DataStore
     * @param loginRequest
     */
    suspend fun saveLoginPassword(loginRequest: LoginPassword) {
        dataStore.edit { preferences ->
            preferences[ID_KEY] = loginRequest.id
            preferences[PASSWORD_KEY] = loginRequest.password
        }
    }

   companion object {
        private val ID_KEY = stringPreferencesKey("id")
        private val PASSWORD_KEY = stringPreferencesKey("password")
    }


}