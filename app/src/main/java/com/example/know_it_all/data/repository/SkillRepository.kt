package com.example.know_it_all.data.repository

import com.example.know_it_all.data.local.dao.SkillDao
import com.example.know_it_all.data.model.Skill
import com.example.know_it_all.data.model.dto.SkillCreateRequest
import com.example.know_it_all.data.model.dto.SkillDTO
import com.example.know_it_all.data.model.dto.SkillEndorseRequest
import com.example.know_it_all.data.remote.api.SkillService
import kotlinx.coroutines.flow.Flow

class SkillRepository(
    private val skillDao: SkillDao,
    private val skillService: SkillService
) {

    fun getUserSkills(userId: String): Flow<List<Skill>> =
        skillDao.getSkillsByUser(userId)

    fun getSkillsByCategory(category: String): Flow<List<Skill>> =
        skillDao.getSkillsByCategory(category)

    fun getTopSkillsByUser(userId: String, limit: Int = 5): Flow<List<Skill>> =
        skillDao.getTopSkillsByUser(userId, limit)

    suspend fun addSkill(
        token: String,
        request: SkillCreateRequest
    ): Result<SkillDTO> {
        return try {
            val response = skillService.addSkill("Bearer $token", request)
            if (response.success && response.data != null) {
                skillDao.insertSkill(response.data.toEntity())
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to add skill"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSkill(
        token: String,
        skillId: String,
        request: SkillCreateRequest
    ): Result<SkillDTO> {
        return try {
            val response = skillService.updateSkill("Bearer $token", skillId, request)
            if (response.success && response.data != null) {
                skillDao.updateSkill(response.data.toEntity())
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to update skill"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSkill(token: String, skillId: String): Result<Unit> {
        return try {
            val response = skillService.deleteSkill("Bearer $token", skillId)
            if (response.success) {
                skillDao.deleteSkill(skillId)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Failed to delete skill"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchSkills(
        token: String,
        query: String,
        category: String? = null
    ): Result<List<SkillDTO>> {
        return try {
            val response = skillService.searchSkills("Bearer $token", query, category)
            if (response.success && response.data != null) {
                skillDao.insertSkills(response.data.map { it.toEntity() })
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Search failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun endorseSkill(
        token: String,
        skillId: String,
        endorserId: String
    ): Result<Unit> {
        return try {
            val response = skillService.endorseSkill(
                "Bearer $token",
                skillId,
                SkillEndorseRequest(skillId, endorserId)
            )
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Endorsement failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecommendedSkills(
        token: String,
        userId: String
    ): Result<List<SkillDTO>> {
        return try {
            val response = skillService.getRecommendedSkills("Bearer $token", userId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to load recommendations"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLocalSkills(userId: String): List<Skill> =
        skillDao.getSkillsByUserSync(userId)
}

private fun SkillDTO.toEntity(): Skill = Skill(
    skillId = skillId,
    userId = userId,
    skillName = skillName,
    description = description,
    category = category,
    proficiencyLevel = proficiencyLevel,
    tokenValue = tokenValue,
    verificationStatus = verificationStatus,
    endorsements = endorsements,
    createdAt = createdAt ?: System.currentTimeMillis()
)
