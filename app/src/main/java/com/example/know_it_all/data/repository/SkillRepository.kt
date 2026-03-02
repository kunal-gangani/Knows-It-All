package com.example.know_it_all.data.repository

import com.example.know_it_all.data.local.db.KnowItAllDatabase
import com.example.know_it_all.data.model.Skill
import com.example.know_it_all.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class SkillRepository(private val database: KnowItAllDatabase) {
    private val skillService = RetrofitClient.createSkillService()
    private val skillDao = database.skillDao()

    suspend fun addSkill(token: String, skill: Skill): Result<Skill> {
        return try {
            val response = skillService.addSkill("Bearer $token", skill)
            if (response.success && response.data != null) {
                skillDao.insertSkill(response.data)
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to add skill"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserSkillsLocal(userId: String): Flow<List<Skill>> {
        return skillDao.getSkillsByUser(userId)
    }

    suspend fun getUserSkillsRemote(token: String, userId: String): Result<List<Skill>> {
        return try {
            val response = skillService.getUserSkills("Bearer $token", userId)
            if (response.success && response.data != null) {
                response.data.forEach { skillDao.insertSkill(it) }
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to fetch skills"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchSkills(
        token: String,
        query: String,
        category: String? = null
    ): Result<List<Skill>> {
        return try {
            val response = skillService.searchSkills("Bearer $token", query, category)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Search failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSkill(token: String, skill: Skill): Result<Skill> {
        return try {
            val response = skillService.updateSkill("Bearer $token", skill.skillId, skill)
            if (response.success && response.data != null) {
                skillDao.updateSkill(response.data)
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to update skill"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSkill(token: String, skillId: Int): Result<Unit> {
        return try {
            val response = skillService.deleteSkill("Bearer $token", skillId)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Failed to delete skill"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
