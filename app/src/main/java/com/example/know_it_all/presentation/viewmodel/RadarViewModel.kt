package com.example.know_it_all.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.know_it_all.data.model.dto.UserDTO
import com.example.know_it_all.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RadarUiState(
    val isLoading: Boolean = false,
    val nearbyUsers: List<UserDTO> = emptyList(),
    val error: String? = null,
    val currentLat: Double? = null,  
    val currentLon: Double? = null,
    val radiusKm: Double = 5.0
)

class RadarViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RadarUiState())
    val uiState: StateFlow<RadarUiState> = _uiState

    fun loadNearbyUsers(token: String, radiusKm: Double = 5.0) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // TODO: Implement location fetching when LocationService is available
            // For now, using mock nearby users from repository
            userRepository.getNearbyUsers(token, 0.0, 0.0, radiusKm).fold(
                onSuccess = { users ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        nearbyUsers = users,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load nearby users"
                    )
                }
            )
        }
    }

    fun updateRadius(token: String, newRadius: Double) {
        _uiState.value = _uiState.value.copy(radiusKm = newRadius)
        loadNearbyUsers(token, newRadius)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}