package com.example.know_it_all.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.know_it_all.data.model.TrustLedger
import com.example.know_it_all.data.repository.LedgerRepository
import com.example.know_it_all.data.repository.SkillRepository
import com.example.know_it_all.util.SkillPassportGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

data class LedgerUiState(
    val isLoading: Boolean = false,
    val ledgerEntries: List<TrustLedger> = emptyList(),
    val tokenBalance: Long = 0L,
    val trustScore: Float = 0f,
    val error: String? = null,
    val passportFile: File? = null
)

class LedgerViewModel(
    private val ledgerRepository: LedgerRepository,
    private val skillRepository: SkillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LedgerUiState())
    val uiState: StateFlow<LedgerUiState> = _uiState

    fun loadLedger(token: String, userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            launch {
                ledgerRepository.getUserLedgerLocal(userId).collectLatest { entries ->
                    _uiState.value = _uiState.value.copy(ledgerEntries = entries)
                }
            }

            ledgerRepository.getUserLedgerRemote(token, userId).fold(
                onSuccess = {
                    ledgerRepository.getTrustScore(token, userId).fold(
                        onSuccess = { scoreMap ->
                            val score = (scoreMap["trustScore"] as? Double)?.toFloat() ?: 0f
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                trustScore = score,
                                error = null
                            )
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = error.message ?: "Failed to load trust score"
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
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val skills = skillRepository.getUserSkillsLocal(userId).first()
                val skillInfos = skills.map { skill ->
                    SkillPassportGenerator.SkillInfo(
                        name = skill.skillName,
                        category = skill.category.name,
                        proficiency = skill.proficiencyLevel.name,
                        year = 2024
                    )
                }
                val file = SkillPassportGenerator.generatePDF(
                    context = context,
                    userName = userName,
                    email = userEmail,
                    foundedDate = "2024-01-01",
                    skills = skillInfos,
                    trustScore = _uiState.value.trustScore
                )
                if (file != null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, passportFile = file)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to generate PDF")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Failed to generate passport")
            }
        }
    }

    fun updateTokenBalance(balance: Long) {
        _uiState.value = _uiState.value.copy(tokenBalance = balance)
    }

    fun clearPassportFile() {
        _uiState.value = _uiState.value.copy(passportFile = null)
    }
}
