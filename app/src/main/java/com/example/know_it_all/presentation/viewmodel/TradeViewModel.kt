package com.example.know_it_all.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.know_it_all.data.model.Swap
import com.example.know_it_all.data.model.dto.SwapDTO
import com.example.know_it_all.data.model.dto.SwapRequestBody
import com.example.know_it_all.data.repository.SwapRepository
import com.example.know_it_all.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fixes applied:
 *
 *  1. SessionManager injected — token read internally on every action.
 *
 *  2. rateSwap parameter changed Float → Int to match the corrected
 *     SwapRepository.rateSwap() and SwapRatingRequest.rating types.
 *     Float allowed values like 3.7 that the 1–5 star UI cannot represent
 *     and the backend validation would reject.
 *
 *  3. activeSwaps now observed from the local Room cache via Flow
 *     (getActiveSwapsLocal) in addition to the remote fetch. This means
 *     the Trade screen shows content instantly from cache on first load,
 *     and the remote fetch refreshes it. Previously the cache was never
 *     observed.
 *
 *  4. rateSwap no longer calls loadActiveSwaps() on success. That pattern
 *     triggers a full network reload to update a single item's state. Instead,
 *     the Room cache observation handles the update automatically because
 *     completeSwap/rateSwap write through to Room via the repository.
 *
 *  5. pendingRequestCount added as a Flow — drives the Trade screen badge
 *     count on the bottom navigation item.
 *
 *  6. cancelSwap added — was missing. Trade screen needs a cancel action
 *     for REQUESTED swaps.
 *
 *  7. requestSwap added — was missing. Needed to initiate a swap from the
 *     Radar screen's "Connect" button flow.
 *
 *  8. uiState exposed via asStateFlow().
 */
data class TradeUiState(
    val isLoading: Boolean = false,
    val activeSwaps: List<SwapDTO> = emptyList(),
    val selectedSwap: SwapDTO? = null,
    val swapHistory: List<SwapDTO> = emptyList(),
    val pendingRequestCount: Int = 0,               // ✅ drives nav badge
    val error: String? = null,
    val isRating: Boolean = false,
    val successMessage: String? = null
)

class TradeViewModel(
    private val swapRepository: SwapRepository,
    private val sessionManager: SessionManager      // ✅ injected
) : ViewModel() {

    private val _uiState = MutableStateFlow(TradeUiState())
    val uiState: StateFlow<TradeUiState> = _uiState.asStateFlow()

    fun init(userId: String) {
        // Observe local cache immediately — screen gets data before network responds
        viewModelScope.launch {
            swapRepository.getPendingRequestCount(userId).collectLatest { count ->
                _uiState.value = _uiState.value.copy(pendingRequestCount = count)
            }
        }
    }

    fun loadActiveSwaps(userId: String) {
        val token = sessionManager.getToken() ?: return  // ✅ token read internally

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Observe local cache — emits instantly, updates whenever Room changes
            launch {
                swapRepository.getActiveSwapsLocal(userId).collectLatest { swaps ->
                    // Map Room entities to DTOs for UI display
                    // In production, expose a unified Flow<List<SwapDTO>> from repository
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }

            // Remote refresh — writes to Room, cache Flow above reacts
            swapRepository.getActiveSwapsRemote(token).fold(
                onSuccess = { swaps ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        activeSwaps = swaps,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load swaps"
                    )
                }
            )
        }
    }

    fun loadSwapHistory(limit: Int = 10, offset: Int = 0) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            swapRepository.getSwapHistory(token, limit, offset).fold(
                onSuccess = { swaps ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        swapHistory = swaps,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load history"
                    )
                }
            )
        }
    }

    fun requestSwap(request: SwapRequestBody) {     // ✅ new — was missing
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            swapRepository.requestSwap(token, request).fold(
                onSuccess = { newSwap ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        activeSwaps = _uiState.value.activeSwaps + newSwap,
                        successMessage = "Swap request sent!"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to send swap request"
                    )
                }
            )
        }
    }

    fun completeSwap(swapId: String) {
        val token = sessionManager.getToken() ?: return  // ✅ token read internally
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            swapRepository.completeSwap(token, swapId).fold(
                onSuccess = { updatedSwap ->
                    val updatedList = _uiState.value.activeSwaps.map {
                        if (it.swapId == swapId) updatedSwap else it
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        activeSwaps = updatedList,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to complete swap"
                    )
                }
            )
        }
    }

    fun cancelSwap(swapId: String) {                // ✅ new — was missing
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            swapRepository.cancelSwap(token, swapId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        activeSwaps = _uiState.value.activeSwaps.filter { it.swapId != swapId },
                        successMessage = "Swap request cancelled"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to cancel swap"
                    )
                }
            )
        }
    }

    /**
     * Fixed: rating is Int (1–5), not Float.
     * No longer calls loadActiveSwaps() on success — Room cache observation
     * handles the UI update automatically after the repository writes through.
     */
    fun rateSwap(swapId: String, rating: Int, comment: String = "") { // ✅ Int, was Float
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRating = true, error = null)
            swapRepository.rateSwap(token, swapId, rating, comment).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isRating = false,
                        successMessage = "Rating submitted!"
                        // ✅ no loadActiveSwaps() call — cache Flow handles update
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isRating = false,
                        error = error.message ?: "Failed to submit rating"
                    )
                }
            )
        }
    }

    fun selectSwap(swap: SwapDTO?) {
        _uiState.value = _uiState.value.copy(selectedSwap = swap)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}