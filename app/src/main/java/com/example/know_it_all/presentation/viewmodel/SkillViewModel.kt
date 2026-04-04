package com.example.know_it_all.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.know_it_all.data.model.Skill
import com.example.know_it_all.data.repository.SkillRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SkillUiState(
    val isLoading: Boolean = false,
    val userSkills: List<Skill> = emptyList(),
    val searchResults: List<Skill> = emptyList(),
    val error: String? = null
)

class SkillViewModel(private val skillRepository: SkillRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillUiState())
    val uiState: StateFlow<SkillUiState> = _uiState

    fun loadUserSkills(token: String, userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Listen to local cache continuously
            launch {
                skillRepository.getUserSkillsLocal(userId).collectLatest { skills ->
                    _uiState.value = _uiState.value.copy(userSkills = skills)
                }
            }

            // ✅ FIXED: fold instead of isSuccess
            skillRepository.getUserSkillsRemote(token, userId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load skills"
                    )
                }
            )
        }
    }

    fun addSkill(token: String, skill: Skill) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // ✅ FIXED: fold instead of isSuccess
            skillRepository.addSkill(token, skill).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to add skill"
                    )
                }
            )
        }
    }

    fun searchSkills(token: String, query: String, category: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // ✅ FIXED: fold instead of separate onSuccess/onFailure
            skillRepository.searchSkills(token, query, category).fold(
                onSuccess = { skills ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        searchResults = skills,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Search failed"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}