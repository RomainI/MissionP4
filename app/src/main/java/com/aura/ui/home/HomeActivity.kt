package com.aura.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aura.R
import com.aura.databinding.ActivityHomeBinding
import com.aura.ui.domain.model.HomeViewModel
import com.aura.ui.login.LoginActivity
import com.aura.ui.transfer.TransferActivity
import dagger.hilt.android.AndroidEntryPoint
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
    private val startTransferActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val loadingHome = binding.loadingHome

        val balance = binding.balance
        val transfer = binding.transfer
        viewModel.getAccounts()
        //Checking all accounts from ViewModel
        lifecycleScope.launch {
            loadingHome.visibility = View.VISIBLE
            viewModel.uiState.collect { uiState ->
                // updates the TextView with the main account balance by checking if this account is the main account
                uiState.accounts.find { it.isMainAccount }?.let { mainAccount ->
                    balance.text = mainAccount.amount.toString()
                }
                if (!uiState.isLoading) loadingHome.visibility = View.INVISIBLE
            }
        }

        //Proceed transfer via View Model and launch HomeActivity
        transfer.setOnClickListener {
            startTransferActivityForResult.launch(
                Intent(
                    this@HomeActivity,
                    TransferActivity::class.java
                )
            )
        }


    }

    /**
     * activate the option menu bar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }
    /**
     * implements the disconnecting button
     */
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



}
