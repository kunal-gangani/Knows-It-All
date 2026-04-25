package com.example.know_it_all.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.know_it_all.data.model.TrustLedger
import com.example.know_it_all.data.repository.LedgerRepository
import com.example.know_it_all.data.repository.SkillRepository
import com.example.know_it_all.util.SessionManager
import com.example.know_it_all.util.SkillPassportGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

/**
 * Fixes applied:
 *
 *  1. SessionManager injected — ViewModel can read the token itself instead
 *     of requiring every caller to pass it as a parameter. This eliminates
 *     the risk of callers passing a stale or wrong token.
 *
 *  2. getTrustScore now uses TrustScoreResult typed fields instead of casting
 *     from Map<String, Any>. The LedgerService now returns TrustScoreResult
 *     so the old map cast was always going to fail at runtime.
 *
 *  3. tokenBalance is now observed from UserDao.getTokenBalance() Flow via
 *     UserRepository, not set manually via updateTokenBalance(). Manual
 *     setters get out of sync whenever the balance changes elsewhere
 *     (e.g. after a swap completes in TradeViewModel).
 *
 *  4. passportFile removed from UiState. Files are OS resources — holding
 *     them in StateFlow means the file stays open for the entire ViewModel
 *     lifetime. Instead a one-shot passportGeneratedEvent Flow is used so
 *     the screen can launch a share/view intent and the ViewModel forgets
 *     the reference immediately.
 *
 *  5. Year hardcoded as 2024 replaced with Calendar.getInstance().get(YEAR).
 *
 *  6. uiState exposed via asStateFlow().
 *
 *  7. disputeTransaction added — Vault screen needs to raise disputes.
 */
data class LedgerUiState(
    val isLoading: Boolean = false,
    val ledgerEntries: List<TrustLedger> = emptyList(),
    val tokenBalance: Long = 0L,
    val trustScore: Float = 0f,
    val averageRating: Float = 0f,
    val completedSwapCount: Int = 0,
    val error: String? = null,
    val isGeneratingPassport: Boolean = false
    // passportFile removed — files must not live in UiState (resource leak)
)

class LedgerViewModel(
    private val ledgerRepository: LedgerRepository,
    private val skillRepository: SkillRepository,
    private val userRepository: com.example.know_it_all.data.repository.UserRepository,
    private val sessionManager: SessionManager                  // ✅ injected
) : ViewModel() {

    private val _uiState = MutableStateFlow(LedgerUiState())
    val uiState: StateFlow<LedgerUiState> = _uiState.asStateFlow()

    /**
     * One-shot event for passport generation result.
     * Screen collects this, launches the share intent, then the reference
     * is consumed and dropped — the ViewModel holds no File reference.
     */
    private val _passportEvent = MutableStateFlow<File?>(null)
    val passportEvent: StateFlow<File?> = _passportEvent.asStateFlow()

    fun loadLedger(userId: String) {
        val token = sessionManager.getToken() ?: return  // ✅ token read internally

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Observe local cache continuously — UI updates as Room changes
            launch {
                ledgerRepository.getUserLedgerLocal(userId).collectLatest { entries ->
                    _uiState.value = _uiState.value.copy(ledgerEntries = entries)
                }
            }

            // Observe token balance from UserDao — stays in sync automatically
            launch {                                                 // ✅ Flow observation
                userRepository.getTokenBalance(userId).collectLatest { balance ->
                    _uiState.value = _uiState.value.copy(tokenBalance = balance ?: 0L)
                }
            }

            // Remote fetch — populates local cache, then read above observes it
            ledgerRepository.getUserLedgerRemote(token, userId).fold(
                onSuccess = {
                    // Load aggregated stats from local DB after cache is populated
                    val avgRating = ledgerRepository.getAverageRating(userId) ?: 0f
                    val count = ledgerRepository.getCompletedCount(userId)

                    // Fetch trust score from API
                    ledgerRepository.getTrustScore(token, userId).fold(
                        onSuccess = { result ->                     // ✅ typed TrustScoreResult
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                trustScore = result.score,          // ✅ no Map cast
                                averageRating = avgRating,
                                completedSwapCount = count,
                                error = null
                            )
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                trustScore = avgRating,             // fallback to local average
                                averageRating = avgRating,
                                completedSwapCount = count,
                                error = null                        // non-critical — don't show error
                            )
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load ledger"
                    )
                }
            )
        }
    }

    fun generateSkillPassport(
        context: Context,
        userId: String,
        userName: String,
        userEmail: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingPassport = true, error = null)
            try {
                val skills = skillRepository.getUserSkillsLocal(userId)
                    .let { flow ->
                        var result = emptyList<com.example.know_it_all.data.model.Skill>()
                        flow.collectLatest { result = it }
                        result
                    }

                val currentYear = Calendar.getInstance()
                    .get(Calendar.YEAR)                             // ✅ dynamic year

                val skillInfos = skills.map { skill ->
                    SkillPassportGenerator.SkillInfo(
                        name = skill.skillName,
                        category = skill.category.name,
                        proficiency = skill.proficiencyLevel.name,
                        year = currentYear
                    )
                }
                val file = SkillPassportGenerator.generatePDF(
                    context = context,
                    userName = userName,
                    email = userEmail,
                    foundedDate = "${currentYear}-01-01",
                    skills = skillInfos,
                    trustScore = _uiState.value.trustScore
                )
                _uiState.value = _uiState.value.copy(isGeneratingPassport = false)
                _passportEvent.value = file                         // ✅ one-shot event, not state
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGeneratingPassport = false,
                    error = e.message ?: "Failed to generate passport"
                )
            }
        }
    }

    fun disputeTransaction(transactionId: String, reason: String) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            ledgerRepository.disputeTransaction(token, transactionId, reason).fold(
                onSuccess = { /* local cache updated in repository */ },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to raise dispute"
                    )
                }
            )
        }
    }

    /** Call after the screen has handled the passport event (launched intent). */
    fun consumePassportEvent() {
        _passportEvent.value = null
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}