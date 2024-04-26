package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.domain.model.LoginViewModel
import com.aura.ui.home.HomeActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val login = binding.login
        val loading = binding.loading
        val passEditText: EditText = binding.password
        val logEditText: EditText = binding.identifier

        //login button is disabled by default
        login.isEnabled=false

        passEditText.doAfterTextChanged {
            lifecycleScope.launch {
                viewModel.setLoginPassword(logEditText.text.toString(),passEditText.text.toString())
                viewModel.uiState.collect {
                    login.isEnabled = it.isFilled
                }

            }
        }

        logEditText.doAfterTextChanged {
            lifecycleScope.launch {
                viewModel.setLoginPassword(logEditText.text.toString(),passEditText.text.toString())
                viewModel.uiState.collect {
                    login.isEnabled = it.isFilled
                }
            }
        }



        login.setOnClickListener {
            loading.visibility = View.VISIBLE

            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)

            finish()
        }
    }

}


