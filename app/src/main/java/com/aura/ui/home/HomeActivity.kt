package com.aura.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aura.R
import com.aura.databinding.ActivityHomeBinding
import com.aura.ui.domain.model.AccountModel
import com.aura.ui.domain.model.HomeViewModel
import com.aura.ui.domain.model.LoginViewModel
import com.aura.ui.login.LoginActivity
import com.aura.ui.transfer.TransferActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * The home activity for the app.
 */

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val viewModel: HomeViewModel by viewModels()

    /**
     * The binding for the home layout.
     */
    private lateinit var binding: ActivityHomeBinding

    /**
     * A callback for the result of starting the TransferActivity.
     */
    private val startTransferActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            //TODO
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val loadingHome = binding.loadingHome

        val balance = binding.balance
        val transfer = binding.transfer
        viewModel.getAccounts()
        //Log.d("onCreate: HomeActivity", "onCreate: HomeActivity")
        lifecycleScope.launch {
            loadingHome.visibility = View.VISIBLE
            viewModel.uiState.collect { uiState ->
                // Directly update the TextView with the main account's balance


                uiState.accounts.find { it.isMainAccount }?.let { mainAccount ->
                    balance.text = mainAccount.amount.toString()
                    //Log.d("HomeActivity", "Main account balance: ${mainAccount.amount}")
                }
                if(!uiState.isLoading) loadingHome.visibility = View.INVISIBLE
            }
        }
        ////Log.d("GET ACCOUNTS", viewModel.getAccounts()?.get(0)?.amount.toString())
        /**viewModel.getAccounts()?.onEach {

        when(it.isMainAccount){
        true -> balance.text = it.amount.toString()
        false -> TODO()
        }
        }*/


        transfer.setOnClickListener {
            startTransferActivityForResult.launch(
                Intent(
                    this@HomeActivity,
                    TransferActivity::class.java
                )
            )
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.disconnect -> {
                viewModel.resetLoginState()
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

  /**  override fun onDestroy() {
        super.onDestroy()
        viewModel.deletePreferences()
    }*/

   /** override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        viewModel.deletePreferences()
        return super.getOnBackInvokedDispatcher()
    }*/


}
