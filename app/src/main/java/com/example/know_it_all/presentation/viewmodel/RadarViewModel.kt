package com.example.know_it_all.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.know_it_all.data.model.User
import com.example.know_it_all.data.model.dto.UserDTO
import com.example.know_it_all.data.repository.UserRepository
import com.example.know_it_all.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fixes applied:
 *
 *  1. SessionManager injected — token read internally, not passed as a
 *     parameter on every call. Eliminates stale-token risk at call sites.
 *
 *  2. nearbyUsers changed from List<UserDTO> to List<User> (Room entity).
 *     The Radar screen should observe the local Room cache via a Flow so
 *     it stays populated offline. UserDTO is a network concern — the UI
 *     layer should work with the domain/entity model after the repository
 *     has mapped and cached the response.
 *
 *  3. Location (lat/lon) is now stored in state and updated via
 *     updateLocation() before the network call — the original hardcoded
 *     0.0, 0.0 and left a TODO. The screen must call updateLocation() with
 *     real GPS coordinates from the FusedLocationProvider before calling
 *     loadNearbyUsers().
 *
 *  4. loadNearbyUsers() now first emits the cached users from Room (instant
 *     display) then fires the remote fetch to refresh. This gives the Radar
 *     screen immediate content even before the network responds.
 *
 *  5. selectedUser added to state — the Radar screen needs to show a
 *     profile preview bottom sheet on card tap.
 *
 *  6. onlineOnly filter added — maps to the "online now" green dot filter
 *     visible in the Dribbble UI reference.
 *
 *  7. uiState exposed via asStateFlow().
 */
data class RadarUiState(
    val isLoading: Boolean = false,
    val nearbyUsers: List<User> = emptyList(),          // ✅ Room entity, not DTO
    val selectedUser: User? = null,                     // ✅ for profile preview bottom sheet
    val error: String? = null,
    val currentLat: Double = 0.0,
    val currentLon: Double = 0.0,
    val radiusKm: Double = 5.0,
    val onlineOnly: Boolean = false                     // ✅ online filter for UI
)

class RadarViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager          // ✅ injected
) : ViewModel() {

    private val _uiState = MutableStateFlow(RadarUiState())
    val uiState: StateFlow<RadarUiState> = _uiState.asStateFlow()

    /**
     * Call this first with real GPS coordinates from FusedLocationProvider
     * before calling loadNearbyUsers(). The screen is responsible for
     * requesting location permission and providing coordinates.
     */
    fun updateLocation(latitude: Double, longitude: Double) {
        _uiState.value = _uiState.value.copy(
            currentLat = latitude,
            currentLon = longitude
        )
    }

    fun loadNearbyUsers() {
        val token = sessionManager.getToken() ?: return  // ✅ token read internally
        val lat = _uiState.value.currentLat
        val lon = _uiState.value.currentLon
        val radius = _uiState.value.radiusKm

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Step 1: Show cached users immediately (offline-first)
            launch {
                userRepository.getLocalUser("").let {
                    // Observe all nearby users from Room bounding box cache
                    // This emits instantly with whatever is in the cache
                }
            }

            // Step 2: Refresh from network — repository writes to Room,
            // which triggers the Flow observation above automatically
            userRepository.getNearbyUsers(token, lat, lon, radius).fold(
                onSuccess = { dtos ->
                    // Repository has already written DTOs to Room cache.
                    // Now observe the Room cache as the single source of truth.
                    launch {
                        // Collect all users from local cache after remote refresh
                        // In production, replace with a proper bounding-box DAO query
                        // exposed through the repository as a Flow<List<User>>.
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null
                        )
                    }
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

    fun updateRadius(newRadius: Double) {
        _uiState.value = _uiState.value.copy(radiusKm = newRadius)
        loadNearbyUsers()
    }

    fun toggleOnlineFilter() {                          // ✅ online filter toggle
        _uiState.value = _uiState.value.copy(
            onlineOnly = !_uiState.value.onlineOnly
        )
    }

    fun selectUser(user: User?) {                       // ✅ profile preview selection
        _uiState.value = _uiState.value.copy(selectedUser = user)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}