package com.aura.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aura.data.network.LoginPassword
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map



class PreferencesManager(private val dataStore: DataStore<Preferences>) {

    //private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aura_user_prefs")
    //private val dataStore = context.dataStore
    val loginFlow: Flow<LoginPassword> = dataStore.data
        .map { preferences -> LoginPassword(
            id = preferences[ID_KEY] ?: "",
            password = preferences[PASSWORD_KEY] ?: ""
            )
        }

    suspend fun saveLoginPassword(loginRequest: LoginPassword) {
        //Log.d("saveLoginPassword", "saveLoginPassword: "+loginRequest.id)
        dataStore.edit { preferences ->
            preferences[ID_KEY] = loginRequest.id
            preferences[PASSWORD_KEY] = loginRequest.password
        }
    }

   companion object {
        private val ID_KEY = stringPreferencesKey("id")
        private val PASSWORD_KEY = stringPreferencesKey("password")
    }

    suspend fun deleteLogin(){
        dataStore.edit { preferences ->
            preferences.remove(ID_KEY)
            preferences.remove(PASSWORD_KEY)
        }
    }
}