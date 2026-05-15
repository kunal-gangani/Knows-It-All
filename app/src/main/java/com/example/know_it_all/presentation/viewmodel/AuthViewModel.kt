package com.example.know_it_all.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.know_it_all.data.repository.FirebaseUserRepository
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
    val error: String? = null
)

class AuthViewModel(
    private val userRepository: FirebaseUserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        if (sessionManager.isLoggedIn()) {
            _uiState.value = AuthUiState(
                isAuthenticated = true,
                userId = sessionManager.getUserId(),
                userName = sessionManager.getUserName()
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            userRepository.login(email, password).fold(
                onSuccess = { user ->
                    sessionManager.saveUserInfo(user.uid, user.name, user.email)
                    _uiState.value = AuthUiState(
                        isAuthenticated = true,
                        userId = user.uid,
                        userName = user.name
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Login failed"
                    )
                }
            )
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            userRepository.register(name, email, password).fold(
                onSuccess = { user ->
                    sessionManager.saveUserInfo(user.uid, user.name, user.email)
                    _uiState.value = AuthUiState(
                        isAuthenticated = true,
                        userId = user.uid,
                        userName = user.name
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Registration failed"
                    )
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            sessionManager.clearSession()
            _uiState.value = AuthUiState()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}