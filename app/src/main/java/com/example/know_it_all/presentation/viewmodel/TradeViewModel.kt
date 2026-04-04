package com.example.know_it_all.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.know_it_all.data.model.dto.SwapDTO
import com.example.know_it_all.data.repository.SwapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TradeUiState(
    val isLoading: Boolean = false,
    val activeSwaps: List<SwapDTO> = emptyList(),
    val selectedSwap: SwapDTO? = null,
    val swapHistory: List<SwapDTO> = emptyList(),
    val error: String? = null,
    val isRating: Boolean = false
)

class TradeViewModel(private val swapRepository: SwapRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TradeUiState())
    val uiState: StateFlow<TradeUiState> = _uiState

    fun loadActiveSwaps(token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
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

    fun loadSwapHistory(token: String, limit: Int = 10, offset: Int = 0) {
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

    fun selectSwap(swap: SwapDTO?) {
        _uiState.value = _uiState.value.copy(selectedSwap = swap)
    }

    fun completeSwap(token: String, swapId: String) {
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

    fun rateSwap(token: String, swapId: String, rating: Float, comment: String = "") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRating = true, isLoading = true, error = null)
            swapRepository.rateSwap(token, swapId, rating, comment).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRating = false,
                        error = null
                    )
                    loadActiveSwaps(token) // refresh list after rating
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRating = false,
                        error = error.message ?: "Failed to submit rating"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}