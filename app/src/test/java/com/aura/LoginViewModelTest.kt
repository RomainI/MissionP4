// File path: src/test/java/com/aura/ui/domain/model/LoginViewModelTest.kt

package com.aura

import android.app.Application
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aura.data.network.LoginPassword
import com.aura.data.repository.AccountRepository
import com.aura.data.repository.PreferencesManager
import com.aura.data.repository.Result
import com.aura.di.NetworkUtil
import com.aura.ui.domain.model.LoginViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    //Bouchon des trois variables pour initialiser LoginViewModel
    @MockK
    private lateinit var accountRepository: AccountRepository
    @MockK
    private lateinit var preferencesManager: PreferencesManager
    @MockK
    private lateinit var application: Application

    private lateinit var loginViewModel: LoginViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        //initialisation de MockK
        MockKAnnotations.init(this, relaxed = true)
        Dispatchers.setMain(testDispatcher)
        loginViewModel = LoginViewModel(accountRepository, preferencesManager, application)
    }


    //clean mocks after each test
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `test setLoginPassword updates uiState correctly`() = runBlocking {
        // Given
        val login = "testLogin"
        val password = "testPassword"

        // When
        loginViewModel.setLoginPassword(login, password)

        // Then
        val uiState = loginViewModel.uiState.first()
        assertThat(uiState.login).isEqualTo(login)
        assertThat(uiState.password).isEqualTo(password)
        assertThat(uiState.isFilled).isTrue()
    }

    @Test
    fun `test getConnection updates uiState on success`() = runBlocking {
        // Given
        val login = "testLogin"
        val password = "testPassword"
        val errorMessage = "Login failed"
        mockkObject(NetworkUtil)
        every { NetworkUtil.isNetworkAvailable(application) } returns true
        coEvery { accountRepository.getLogginAccess(any(), any()) } returns flowOf(Result.Success(true))

        // When
        loginViewModel.getConnection(login, password)

        // Then
        val uiState = loginViewModel.uiState.first { it.isLoading }
        assertThat(uiState.isLoading).isTrue()
        val uiStateFailure = loginViewModel.uiState.first { it.isLogOk == true }
        assertThat(uiStateFailure.errorMessage).isEqualTo(errorMessage)
        assertThat(uiStateFailure.isLogOk).isFalse()
    }

    @Test
    fun `test getConnection updates uiState on failure`() = runBlocking {
        // Given
        val login = "testLogin"
        val password = "testPassword"
        val errorMessage = "Login failed"
        mockkObject(NetworkUtil)
        every { NetworkUtil.isNetworkAvailable(application) } returns true
        coEvery { accountRepository.getLogginAccess(any(), any()) } returns flowOf(Result.Failure(errorMessage))

        // When
        loginViewModel.getConnection(login, password)

        // Then
        val uiState = loginViewModel.uiState.first { it.isLoading }
        assertThat(uiState.isLoading).isTrue()
        val uiStateSuccess = loginViewModel.uiState.first { it.isLoading && it.isLogOk != null }
        assertThat(uiStateSuccess.isLogOk).isTrue()
    }

    @Test
    fun `test saveData calls preferencesManager`() = runBlocking {
        // Given
        val login = "testLogin"
        val password = "testPassword"

        // When
        loginViewModel.saveData(login, password)

        // Then
        coVerify {
            preferencesManager.saveLoginPassword(LoginPassword(login, password))
        }
    }
}