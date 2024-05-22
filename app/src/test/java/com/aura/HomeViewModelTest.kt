package com.aura

import android.app.Application
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aura.data.network.LoginPassword
import com.aura.data.repository.AccountRepository
import com.aura.data.repository.PreferencesManager
import com.aura.data.repository.Result
import com.aura.data.response.AuraServerResponse
import com.aura.di.NetworkUtil
import com.aura.ui.domain.model.AccountModel
import com.aura.ui.domain.model.HomeViewModel
import com.aura.ui.domain.model.LoginViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    //Bouchon des trois variables pour initialiser LoginViewModel
    @MockK
    private lateinit var accountRepository: AccountRepository

    @MockK
    private lateinit var preferencesManager: PreferencesManager

    @MockK
    private lateinit var application: Application

    private lateinit var homeViewModel: HomeViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        //initialisation de MockK
        MockKAnnotations.init(this, relaxed = true)
        Dispatchers.setMain(testDispatcher)
        homeViewModel = HomeViewModel(accountRepository, preferencesManager, application)
    }


    //clean mocks after each test
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }
    @Test
    fun `test getAccounts with successful result`() = runTest {
        // Given
        val mockLoginFlow = flow { emit(LoginPassword("test_login_id","")) }
        val mockAccountsFlow = flow { emit(Result.Success(listOf(AccountModel("Account1",1.0,true), AccountModel("Account2",2.0,false)))) }

        every { preferencesManager.loginFlow } returns mockLoginFlow
        coEvery { accountRepository.getAccountsFlow("test_login_id") } returns mockAccountsFlow

        // When
        homeViewModel.getAccounts()

        // Then
        advanceUntilIdle()

        val uiState = homeViewModel.uiState.first()
        Assert.assertFalse(uiState.isLoading)
        Assert.assertNull(uiState.errorMessage)
        Assert.assertEquals(2, uiState.accounts.size)
    }

    @Test
    fun `test getAccounts with failure result`() = runTest {
        // Given
        val mockLoginFlow = flow { emit(LoginPassword("test_login_id","")) }
        val mockFailureFlow = flow { emit(Result.Failure("Error occurred")) }

        every { preferencesManager.loginFlow } returns mockLoginFlow
        coEvery { accountRepository.getAccountsFlow("test_login_id") } returns mockFailureFlow

        // When
        homeViewModel.getAccounts()

        // Then
        advanceUntilIdle()

        val uiState = homeViewModel.uiState.first()
        Assert.assertFalse(uiState.isLoading)
        Assert.assertEquals("Error occurred", uiState.errorMessage)
        Assert.assertTrue(uiState.accounts.isEmpty())
    }





}