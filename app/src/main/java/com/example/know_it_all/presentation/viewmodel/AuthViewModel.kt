package com.example.know_it_all.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.know_it_all.util.NotificationHelper
import androidx.lifecycle.viewModelScope
import com.example.know_it_all.data.repository.UserRepository
import com.example.know_it_all.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val userId: String? = null,
    val userName: String? = null,           
    val error: String? = null,
    val needsOnboarding: Boolean = false
)

class AuthViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()  // ✅ immutable exposure

    init {
        if (sessionManager.isLoggedIn()) {
            _uiState.value = AuthUiState(
                isAuthenticated = true,
                userId = sessionManager.getUserId(),
                userName = sessionManager.getUserName()
                // token NOT loaded into state — stays in SessionManager only
            )
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            userRepository.register(name, email, password).fold(
                onSuccess = { authData ->
                    sessionManager.saveToken(authData.token)
                    sessionManager.saveUserInfo(authData.userId, name, email)
                    NotificationHelper.saveDeviceToken(authData.userId)
                    _uiState.value = AuthUiState(
                        isAuthenticated = true,
                        userId = authData.userId,
                        userName = name,
                        needsOnboarding = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Registration failed. Please try again."
                    )
                }
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            userRepository.login(email, password).fold(
                onSuccess = { authData ->
                    sessionManager.saveToken(authData.token)
                    sessionManager.saveUserInfo(
                        userId = authData.userId,
                        name = authData.name ?: "",     // ✅ name from response, not empty string
                        email = email
                    )
                    _uiState.value = AuthUiState(
                        isAuthenticated = true,
                        userId = authData.userId,
                        userName = authData.name
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Login failed. Check your credentials."
                    )
                }
            )
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _uiState.value = AuthUiState()      // full reset — no stale userId/userName
    }

    fun clearError() {                      // ✅ call after Snackbar shown to prevent re-show
        _uiState.value = _uiState.value.copy(error = null)
    }
}