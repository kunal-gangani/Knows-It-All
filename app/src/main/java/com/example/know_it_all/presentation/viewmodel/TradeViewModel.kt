package com.example.know_it_all.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.know_it_all.data.model.dto.SwapDTO
import com.example.know_it_all.data.model.dto.SwapRequestBody
import com.example.know_it_all.data.repository.FirebaseSwapRepository
import com.example.know_it_all.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TradeUiState(
    val activeSwaps: List<SwapDTO> = emptyList(),
    val swapHistory: List<SwapDTO> = emptyList(),
    val pendingRequestCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class TradeViewModel(
    private val swapRepository: FirebaseSwapRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(TradeUiState())
    val uiState: StateFlow<TradeUiState> = _uiState.asStateFlow()

    private val userId get() = sessionManager.getUserId() ?: ""

    init {
        observePendingCount()
        loadActiveSwaps()
    }

    // ── Real-time pending count ────────────────────────────────────────────────

    private fun observePendingCount() {
        viewModelScope.launch {
            swapRepository.observePendingCount(userId).collect { count ->
                _uiState.value = _uiState.value.copy(pendingRequestCount = count)
            }
        }
    }

    // ── Load active swaps ─────────────────────────────────────────────────────

    fun loadActiveSwaps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Observe real-time active swaps
            swapRepository.observeActiveSwaps(userId).collect { swaps ->
                _uiState.value = _uiState.value.copy(
                    activeSwaps = swaps,
                    isLoading = false
                )
            }
        }
    }

    // ── Load swap history ─────────────────────────────────────────────────────

    fun loadSwapHistory() {
        viewModelScope.launch {
            swapRepository.getSwapHistory(userId).fold(
                onSuccess = { history ->
                    _uiState.value = _uiState.value.copy(swapHistory = history)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Failed to load history"
                    )
                }
            )
        }
    }

    // ── Request swap ──────────────────────────────────────────────────────────

    fun requestSwap(request: SwapRequestBody) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            swapRepository.requestSwap(request).fold(
                onSuccess = { loadActiveSwaps() },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to request swap"
                    )
                }
            )
        }
    }

    // ── Complete swap ─────────────────────────────────────────────────────────

    fun completeSwap(swapId: String) {
        viewModelScope.launch {
            swapRepository.completeSwap(swapId).fold(
                onSuccess = { loadActiveSwaps() },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Failed to complete swap"
                    )
                }
            )
        }
    }

    // ── Cancel swap ───────────────────────────────────────────────────────────

    fun cancelSwap(swapId: String) {
        viewModelScope.launch {
            swapRepository.cancelSwap(swapId).fold(
                onSuccess = { loadActiveSwaps() },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Failed to cancel swap"
                    )
                }
            )
        }
    }

    // ── Rate swap ─────────────────────────────────────────────────────────────

    fun rateSwap(swapId: String, rating: Int, comment: String = "") {
        viewModelScope.launch {
            swapRepository.rateSwap(swapId, rating, comment).fold(
                onSuccess = { loadSwapHistory() },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Failed to rate swap"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}