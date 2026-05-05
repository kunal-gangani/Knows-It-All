package com.example.know_it_all.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.know_it_all.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * LoginViewModel
 *
 * Manages the login screen state and orchestrates authentication.
 *
 * State:
 *   - email: User input for email field
 *   - password: User input for password field
 *   - isLoading: True while login request is in-flight
 *   - errorMessage: Set when login fails (network error, invalid credentials)
 *   - loginSuccess: Raised once after successful login to trigger navigation
 *
 * Actions:
 *   - login(): Validates inputs and calls AuthRepository.login()
 *   - updateEmail(): Updates email field
 *   - updatePassword(): Updates password field
 *   - clearError(): Dismisses error message
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false
)

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Validates inputs and attempts login.
     *
     * Validation:
     *   - Email must not be empty
     *   - Password must be at least 6 characters
     *
     * On validation failure: Set errorMessage in state
     * On login success: Set loginSuccess = true to trigger navigation
     * On login failure: Set errorMessage with backend error or network error
     */
    fun login() {
        val currentState = _uiState.value

        // Validation
        when {
            currentState.email.isBlank() -> {
                _uiState.value = currentState.copy(errorMessage = "Email cannot be empty")
                return
            }
            currentState.password.length < 6 -> {
                _uiState.value = currentState.copy(errorMessage = "Password must be at least 6 characters")
                return
            }
        }

        // Start login
        _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = authRepository.login(currentState.email, currentState.password)
            result.onSuccess { authData ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loginSuccess = true
                )
            }
            result.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Login failed"
                )
            }
        }
    }

    /**
     * Reset login success flag after navigation has occurred.
     * Prevents duplicate navigation on recomposition.
     */
    fun resetLoginSuccess() {
        _uiState.value = _uiState.value.copy(loginSuccess = false)
    }
}
