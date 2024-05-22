// File path: src/test/java/com/aura/ui/domain/model/LoginViewModelTest.kt

package com.aura

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aura.data.network.LoginPassword
import com.aura.data.repository.AccountRepository
import com.aura.data.repository.PreferencesManager
import com.aura.data.repository.Result
import com.aura.di.NetworkUtil
import com.aura.ui.domain.model.TransferViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class TransferViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var accountRepository: AccountRepository

    @MockK
    private lateinit var preferencesManager: PreferencesManager

    @MockK
    private lateinit var application: Application

    private lateinit var transferViewModel: TransferViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        Dispatchers.setMain(testDispatcher)
        transferViewModel = TransferViewModel(accountRepository, preferencesManager, application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun testProceedTransferUpdatesUiStateOnFailure() = runBlocking {
        // Given
        val receiver = "testReceiver"
        val amount = "0"
        val errorMessage = "Transfer failed"

        mockkObject(NetworkUtil)
        every { NetworkUtil.isNetworkAvailable(application) } returns true
        coEvery { preferencesManager.loginFlow } returns flowOf(LoginPassword("", ""))
        coEvery { accountRepository.getTransfer(any(), any(), any()) } returns flowOf(Result.Failure(errorMessage))

        // When
        transferViewModel.proceedTransfer(receiver, amount)

        // Then
        val uiState = transferViewModel.uiState.first()
        assertThat(uiState.isLoading).isFalse()
        val uiStateFailure = transferViewModel.uiState.first { it.resultTransfer == false }
        assertThat(uiStateFailure.resultTransfer).isFalse()
    }

    @Test
    fun testProceedTransferUpdatesUiStateOnSuccess() = runBlocking {
        // Given
        val receiver = "testReceiver"
        val amount = "0"

        mockkObject(NetworkUtil)
        every { NetworkUtil.isNetworkAvailable(application) } returns true
        coEvery { preferencesManager.loginFlow } returns flowOf(LoginPassword("", ""))
        coEvery { accountRepository.getTransfer(any(), any(), any()) } returns flowOf(Result.Success(true))

        // When
        transferViewModel.proceedTransfer(receiver, amount)

        // Then
        val uiState = transferViewModel.uiState.first()
        assertThat(uiState.isLoading).isFalse()
        val uiStateSuccess = transferViewModel.uiState.first { it.resultTransfer != null }
        assertThat(uiStateSuccess.resultTransfer).isTrue()
    }
}