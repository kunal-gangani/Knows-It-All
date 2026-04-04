package com.example.know_it_all.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.know_it_all.data.repository.UserRepository
import com.example.know_it_all.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val token: String? = null,
    val error: String? = null,
    val userId: String? = null
)

class AuthViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        if (sessionManager.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                token = sessionManager.getToken(),
                userId = sessionManager.getUserId() // ✅ no more getUser()
            )
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            userRepository.register(name, email, password).fold(
                onSuccess = { authData ->
                    // ✅ Save both token AND userId to session
                    sessionManager.saveToken(authData.token)
                    sessionManager.saveUserInfo(authData.userId, name, email)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        token = authData.token,
                        userId = authData.userId,
                        error = null
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

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            userRepository.login(email, password).fold(
                onSuccess = { authData ->
                    // ✅ Save both token AND userId to session
                    sessionManager.saveToken(authData.token)
                    sessionManager.saveUserInfo(authData.userId, "", email)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        token = authData.token,
                        userId = authData.userId,
                        error = null
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

    fun logout() {
        sessionManager.clearSession()
        _uiState.value = AuthUiState()
    }
}