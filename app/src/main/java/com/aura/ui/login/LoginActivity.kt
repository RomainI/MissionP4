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
import com.aura.data.network.LoginPassword
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

        //Updating uiState from ViewModel to activate (or desactivate) login button
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
        //Updating uiState from ViewModel to activate (or desactivate) login button
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

        //sending to viewmodel login and password and checking the flow answer
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
                    if (state.isLogOk == true) {
                        // Connection succeed
                        viewModel.saveData(currentLogin, currentPassword)
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    } else if (state.errorMessage != null) {
                        // Error catched
                        Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_LONG).show()
                    } else if (state.isLogOk == false) {
                        // Connection failed (id or password incorrect)
                        Snackbar.make(
                            binding.root,
                            "Login or password incorrect",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    stopFlow()
                    loading.visibility = View.INVISIBLE
                    login.isEnabled = true
                }
            }
        }

    }

    private fun stopFlow() {
        flowJob?.cancel()
    }

}





