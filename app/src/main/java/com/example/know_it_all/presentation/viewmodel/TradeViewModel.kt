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
            val result = swapRepository.getActiveSwapsRemote(token)
            result.onSuccess { swaps ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    activeSwaps = swaps,
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

    fun loadSwapHistory(token: String, limit: Int = 10, offset: Int = 0) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = swapRepository.getSwapHistory(token, limit, offset)
            result.onSuccess { swaps ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    swapHistory = swaps,
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

    fun selectSwap(swap: SwapDTO?) {
        _uiState.value = _uiState.value.copy(selectedSwap = swap)
    }

    fun completeSwap(token: String, swapId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = swapRepository.completeSwap(token, swapId)
            result.onSuccess { updatedSwap ->
                val updatedList = _uiState.value.activeSwaps.map {
                    if (it.swapId == swapId) updatedSwap else it
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    activeSwaps = updatedList,
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

    fun rateSwap(token: String, swapId: String, rating: Float, comment: String = "") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRating = true, isLoading = true, error = null)
            val result = swapRepository.rateSwap(token, swapId, rating, comment)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRating = false,
                    error = null
                )
                loadActiveSwaps(token)
            }
            result.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRating = false,
                    error = error.message
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
