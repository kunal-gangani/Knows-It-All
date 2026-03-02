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
    val userLocation: Pair<Double, Double>? = null,
    val radiusKm: Double = 5.0
)

class RadarViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RadarUiState())
    val uiState: StateFlow<RadarUiState> = _uiState

    fun loadNearbyUsers(token: String, latitude: Double, longitude: Double, radiusKm: Double = 5.0) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = userRepository.getNearbyUsers(token, latitude, longitude, radiusKm)
            result.onSuccess { users ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    nearbyUsers = users,
                    userLocation = Pair(latitude, longitude),
                    radiusKm = radiusKm,
                    error = null
                )
            }
            result.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }
        }
    }

    fun updateRadius(newRadius: Double) {
        _uiState.value = _uiState.value.copy(radiusKm = newRadius)
        // Reload data with new radius if we have location
        _uiState.value.userLocation?.let {
            loadNearbyUsers("", it.first, it.second, newRadius)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
