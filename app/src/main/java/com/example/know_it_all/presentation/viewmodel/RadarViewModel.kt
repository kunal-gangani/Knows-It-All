package com.example.know_it_all.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.know_it_all.data.model.dto.UserDTO
import com.example.know_it_all.data.repository.UserRepository
import com.example.know_it_all.util.LocationService
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
    private val userRepository: UserRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RadarUiState())
    val uiState: StateFlow<RadarUiState> = _uiState

    fun loadNearbyUsers(token: String, radiusKm: Double = 5.0) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val location = locationService.getCurrentLocation()
                ?: locationService.getLastLocation()

            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                // ✅ fold instead of separate onSuccess/onFailure
                userRepository.getNearbyUsers(token, latitude, longitude, radiusKm).fold(
                    onSuccess = { users ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            nearbyUsers = users,
                            currentLat = latitude,
                            currentLon = longitude,
                            radiusKm = radiusKm,
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
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Could not fetch current location"
                )
            }
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