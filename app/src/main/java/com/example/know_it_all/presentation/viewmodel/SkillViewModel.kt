package com.example.know_it_all.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.know_it_all.data.model.Skill
import com.example.know_it_all.data.model.dto.SkillCreateRequest
import com.example.know_it_all.data.repository.SkillRepository
import com.example.know_it_all.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fixes applied:
 *
 *  1. SessionManager injected — token read internally, not threaded through
 *     every public function as a parameter.
 *
 *  2. addSkill now accepts SkillCreateRequest DTO instead of a Skill Room
 *     entity. ViewModels must never accept Room entities from the UI — the
 *     entity has @Entity/@PrimaryKey annotations, and constructing one in
 *     the UI layer couples the presentation layer to the persistence layer.
 *     The DTO carries only what's needed to create a skill.
 *
 *  3. deleteSkill added — was missing from the original. SkillProfileScreen
 *     needs to remove skills; without this the delete button has nowhere to call.
 *
 *  4. updateSkill added — was missing. Editing an existing skill requires
 *     a dedicated update path through the repository.
 *
 *  5. endorseSkill added — maps to the endorse button on Radar mentor cards.
 *
 *  6. selectedSkill added to UiState — needed for an edit/detail bottom sheet.
 *
 *  7. uiState exposed via asStateFlow().
 */
data class SkillUiState(
    val isLoading: Boolean = false,
    val userSkills: List<Skill> = emptyList(),
    val searchResults: List<Skill> = emptyList(),
    val selectedSkill: Skill? = null,               // ✅ for edit/detail sheet
    val error: String? = null,
    val successMessage: String? = null              // ✅ for add/delete confirmation
)

class SkillViewModel(
    private val skillRepository: SkillRepository,
    private val sessionManager: SessionManager      // ✅ injected
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillUiState())
    val uiState: StateFlow<SkillUiState> = _uiState.asStateFlow()

    fun loadUserSkills(userId: String) {
        val token = sessionManager.getToken() ?: return  // ✅ token read internally

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Observe local cache continuously — emits immediately with cached data
            launch {
                skillRepository.getUserSkillsLocal(userId).collectLatest { skills ->
                    _uiState.value = _uiState.value.copy(userSkills = skills)
                }
            }

            // Refresh from network — writes to Room, Flow above reacts
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

    /**
     * Fixed: accepts SkillCreateRequest DTO, not a Skill Room entity.
     * The UI layer constructs the DTO from form input fields, not the entity.
     */
    fun addSkill(request: SkillCreateRequest) {     // ✅ DTO, not Room entity
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            skillRepository.addSkill(token, request).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Skill added successfully"
                    )
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

    fun updateSkill(skillId: String, request: SkillCreateRequest) { // ✅ new — was missing
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            skillRepository.updateSkill(token, skillId, request).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        selectedSkill = null,
                        successMessage = "Skill updated"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to update skill"
                    )
                }
            )
        }
    }

    fun deleteSkill(skillId: String) {              // ✅ new — was missing entirely
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            skillRepository.deleteSkill(token, skillId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        selectedSkill = null,
                        successMessage = "Skill removed"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to delete skill"
                    )
                }
            )
        }
    }

    fun endorseSkill(skillId: String, endorserId: String) { // ✅ new — was missing
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            skillRepository.endorseSkill(token, skillId, endorserId).fold(
                onSuccess = { /* local list updates via Flow observation */ },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to endorse skill"
                    )
                }
            )
        }
    }

    fun searchSkills(query: String, category: String? = null) {
        val token = sessionManager.getToken() ?: return  // ✅ token read internally
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
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

    fun selectSkill(skill: Skill?) {
        _uiState.value = _uiState.value.copy(selectedSkill = skill)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}