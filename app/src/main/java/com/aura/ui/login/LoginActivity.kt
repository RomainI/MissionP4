package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.data.network.LoginRequest
import com.aura.data.repository.PreferencesManager
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.domain.model.LoginViewModel
import com.aura.ui.home.HomeActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * The login activity for the app.
 */

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    /**
     * The binding for the login layout.
     */
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var flowJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val login = binding.login
        val loading = binding.loading
        val passEditText: EditText = binding.password
        val logEditText: EditText = binding.identifier

        //login button is disabled by default
        login.isEnabled = false
        var loginText = logEditText.text.toString()
        var passwordText = passEditText.text.toString()

        /**passEditText.doAfterTextChanged {
        val loginText = logEditText.text.toString()
        val passwordText = passEditText.text.toString()
        viewModel.setLoginPassword(loginText, passwordText)
        Log.d("afterpasschanged", "onCreate: $loginText$passwordText")

        }

        logEditText.doAfterTextChanged {
        val loginText = logEditText.text.toString()
        val passwordText = passEditText.text.toString()
        viewModel.setLoginPassword(loginText, passwordText)
        Log.d("afterpasschanged", "onCreate: $loginText$passwordText")
        }*/


        passEditText.doAfterTextChanged {
            loginText = logEditText.text.toString()
            passwordText = passEditText.text.toString()
            lifecycleScope.launch {
                viewModel.setLoginPassword(
                    loginText,
                    passwordText
                )
                viewModel.uiState.collect {
                    login.isEnabled = it.isFilled
                }

            }
        }

        logEditText.doAfterTextChanged {
            loginText = logEditText.text.toString()
            passwordText = passEditText.text.toString()
            lifecycleScope.launch {
                viewModel.setLoginPassword(
                    loginText,
                    passwordText
                )
                viewModel.uiState.collect {
                    login.isEnabled = it.isFilled
                }
            }
        }
        /**
        lifecycleScope.launch {
        viewModel.uiState.collect { uiState ->
        login.isEnabled = uiState.isFilled
        if (uiState.isLoading) {
        loading.visibility = View.VISIBLE
        } else {
        loading.visibility = View.INVISIBLE
        }
        }
        }
         */

        /**login.setOnClickListener {
            val currentLogin = logEditText.text.toString()
            val currentPassword = passEditText.text.toString()
            lifecycleScope.launch {
                loading.visibility = View.VISIBLE
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    login.isEnabled = false
                    flowJob = launch {
                        viewModel.uiState.collect {
                            viewModel.getConnection(
                                currentLogin,
                                currentPassword
                            )
                            if (it.isLogOk==true) {
                                viewModel.saveData(
                                    currentLogin,
                                    currentPassword
                                )
                                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else if (it.errorMessage != null) {
                                Snackbar.make(
                                    binding.root,
                                    it.errorMessage.toString(),
                                    Snackbar.LENGTH_LONG
                                )
                                    .show()
                                flowJob?.cancel()
                            } else{
                                Snackbar.make(
                                    binding.root,
                                    "Login or password incorrect",
                                    Snackbar.LENGTH_LONG
                                ).show()
                                loading.visibility = View.INVISIBLE
                                flowJob?.cancel()

                            }
                        }

                        /** if (it.isLogOk && it.errorMessage.isNullOrBlank()) {
                        //saving user datas if the login & password are correct
                        viewModel.saveData(it.login, it.password)
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                        } else if (!it.isLogOk) {
                        Snackbar.make(
                        binding.root,
                        "Login or password incorrect",
                        Snackbar.LENGTH_LONG
                        ).show()
                        loading.visibility = View.INVISIBLE
                        flowJob?.cancel()
                        } else if (!it.errorMessage.isNullOrBlank()) Snackbar.make(
                        binding.root,
                        it.errorMessage.toString(),
                        Snackbar.LENGTH_LONG
                        )
                        .show() else Snackbar.make(
                        binding.root,
                        "Unexpected Error",
                        Snackbar.LENGTH_LONG
                        ).show()
                        }*/

                    }
                }
            }
            //loading.visibility = View.INVISIBLE
            login.isEnabled = true
        }*/


        login.setOnClickListener {
            val currentLogin = logEditText.text.toString()
            val currentPassword = passEditText.text.toString()

            flowJob = lifecycleScope.launch {
                loading.visibility = View.VISIBLE
                viewModel.setLoginPassword(currentLogin, currentPassword)
                viewModel.getConnection(
                    currentLogin,
                    currentPassword
                )
                viewModel.uiState.collect { state ->
                    if (state.isLogOk==true) {
                        // Connexion réussie
                        stopFlow()
                        viewModel.saveData(currentLogin, currentPassword)
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    } else if (state.errorMessage != null) {
                        // Afficher l'erreur spécifique

                        stopFlow()
                        Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_LONG).show()
                    } else if (state.isLogOk==false){
                        stopFlow()
                        Snackbar.make(binding.root, "Login or password incorrect", Snackbar.LENGTH_LONG).show()
                    }
                    loading.visibility = View.INVISIBLE
                    login.isEnabled = true
                }
            }
        }

    }

    private fun stopFlow(){
        flowJob?.cancel()
    }

}





