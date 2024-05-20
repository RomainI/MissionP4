package com.aura.ui.transfer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.databinding.ActivityTransferBinding
import com.aura.ui.domain.model.HomeViewModel
import com.aura.ui.domain.model.TransferViewModel
import com.aura.ui.home.HomeActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * The transfer activity for the app.
 */

@AndroidEntryPoint
class TransferActivity : AppCompatActivity() {


    private val viewModel: TransferViewModel by viewModels()

    /**
     * The binding for the transfer layout.
     */
    private lateinit var binding: ActivityTransferBinding
    private lateinit var recipient: EditText
    private lateinit var amount: EditText
    private lateinit var transfer: Button
    private lateinit var loading: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //INIT

        setupUiComponents()


        /**
         * check if recipient edit text is filled
         */


        /**
         * click on transfer button
         */
        transfer.setOnClickListener {
            lifecycleScope.launch {
                loading.visibility = View.VISIBLE
                viewModel.proceedTransfer(recipient.text.toString(), amount.text.toString())
                observeTransferState()
                /**transfer.isEnabled = false
                Log.d("TransferActivity", "transfer.isenable=false ")
                viewModel.uiState.collectLatest {
                    Log.d("TransferActivity", "uiStateCollect ")

                    if (it.resultTransfer == true) {
                        Log.d("TransferActivity", "it.resultTransfer==true ")
                        loading.visibility = View.INVISIBLE
                        showSnackbar("Success !")
                        navigateToHome()
                    } else
                        if (it.resultTransfer == false) {
                            Log.d("TransferActivity", "it.resultTransfer==false ")
                            loading.visibility = View.INVISIBLE
                            showSnackbar("Insufficient balance")
                        } else
                            if (it.isLoading) {
                                Log.d("TransferActivity", "it.isLoading")
                                loading.visibility = View.VISIBLE
                            } else {
                                Log.d("TransferActivity", "else")
                                loading.visibility = View.INVISIBLE
                                showSnackbar("Unexpected error")

                            }


                }*/

            }


        }

    }

    private fun setupUiComponents() {
        recipient = binding.recipient
        amount = binding.amount
        transfer = binding.transfer
        loading = binding.loading

        setupTransferButton()
    }

    private fun setupTransferButton() {
        transfer.isEnabled = false
        recipient.doAfterTextChanged {
            lifecycleScope.launch {
                //Ask ViewModel if both edit text are filled to activate transfer button
                viewModel.checkTransferButton(recipient.text.toString(), amount.text.toString())
                Log.d(
                    "Transfer activity oncreate",
                    "transfer onCreate: amount = " + amount.text.toString()
                )
                viewModel.uiState.collect {
                    transfer.isEnabled = it.isFilled
                }

            }
        }


        /**
         * check if amount edit text is filled
         */
        amount.doAfterTextChanged {
            lifecycleScope.launch {
                //Ask ViewModel if both edit text are filled to activate transfer button
                viewModel.checkTransferButton(recipient.text.toString(), amount.text.toString())
                viewModel.uiState.collect {
                    transfer.isEnabled = it.isFilled
                }
            }
        }
    }



    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun observeTransferState() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                binding.loading.visibility = if (state.isLoading) View.VISIBLE else View.INVISIBLE
                binding.transfer.isEnabled = state.isFilled && !state.isLoading

                if (state.resultTransfer == true) {
                    showSnackbar("Success!")
                    delay(500L)
                    navigateToHome()
                } else if (state.resultTransfer == false) {
                    showSnackbar("Insufficient balance")
                    delay(500L)
                    navigateToHome()
                } else if (state.errorMessage != null) {
                    showSnackbar(state.errorMessage)
                }
            }
        }
    }


}
