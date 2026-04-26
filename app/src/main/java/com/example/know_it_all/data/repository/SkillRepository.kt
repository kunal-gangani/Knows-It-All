package com.example.know_it_all.data.repository

import com.example.know_it_all.data.local.dao.SkillDao
import com.example.know_it_all.data.model.Skill
import com.example.know_it_all.data.model.SkillCategory
import com.example.know_it_all.data.model.dto.SkillCreateRequest
import com.example.know_it_all.data.model.dto.SkillDTO
import com.example.know_it_all.data.remote.api.SkillService
import kotlinx.coroutines.flow.Flow

/**
 * Fixes applied:
 *
 *  1. SkillService and SkillDao are now INJECTED — no RetrofitClient or
 *     database god-object references inside the repository body.
 *
 *  2. addSkill now accepts a SkillCreateRequest DTO instead of a Skill
 *     Room entity. Sending Room entities across the network boundary is a
 *     layering violation — the entity has Room-specific annotations
 *     (@Entity, @PrimaryKey) and may contain local cache fields that the
 *     API doesn't understand. The request DTO carries only what the API needs.
 *
 *  3. deleteSkill parameter fixed from Int → String to match the corrected
 *     Skill.skillId type (String UUID). Int was a silent type mismatch.
 *     The local cache is also cleaned up after a successful remote delete.
 *
 *  4. getUserSkillsRemote maps SkillDTO → Skill before inserting into Room.
 *     Previously the raw API response type was inserted directly, which breaks
 *     if the API and entity schemas ever diverge.
 *
 *  5. searchSkills now has a local fallback — if the network is unavailable
 *     it queries the Room cache instead of propagating a network error.
 *
 *  6. updateSkill sends a SkillCreateRequest (not the full entity) and
 *     writes the confirmed server response back to the cache.
 *
 *  7. getSkillsByCategory uses the SkillCategory enum, not a raw String.
 *
 *  8. endorseSkill added — was missing from the original but is needed by
 *     the Radar screen's "endorse" button on mentor cards.
 */
class SkillRepository(
    private val skillDao: SkillDao,              // ✅ injected DAO
    private val skillService: SkillService       // ✅ injected service
) {

    // -------------------------------------------------------------------------
    // Local reads (offline-first)
    // -------------------------------------------------------------------------

    fun getUserSkillsLocal(userId: String): Flow<List<Skill>> =
        skillDao.getSkillsByUser(userId)

    fun getSkillsByCategoryLocal(category: SkillCategory): Flow<List<Skill>> =
        skillDao.getSkillsByCategory(category)   // ✅ enum, not String

    fun getVerifiedSkillsLocal(): Flow<List<Skill>> =
        skillDao.getVerifiedSkills()

    suspend fun getSkillById(skillId: String): Skill? =
        skillDao.getSkillById(skillId)           // ✅ String, not Int

    // -------------------------------------------------------------------------
    // Remote fetch + cache write-through
    // -------------------------------------------------------------------------

    suspend fun getUserSkillsRemote(token: String, userId: String): Result<List<Skill>> {
        return try {
            val response = skillService.getUserSkills("Bearer $token", userId)
            if (response.success && response.data != null) {
                val skills = response.data.map { it.toEntity() }   // ✅ DTO → entity
                skillDao.insertSkills(skills)                       // ✅ batch insert
                Result.success(skills)
            } else {
                Result.failure(Exception(response.error ?: "Failed to fetch skills"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Searches remotely first; falls back to local cache on network failure.
     * The Radar screen must remain functional in poor connectivity — the cache
     * provides a degraded-but-usable experience.
     */
    suspend fun searchSkills(
        token: String,
        query: String,
        category: String? = null
    ): Result<List<Skill>> {
        return try {
            val response = skillService.searchSkills("Bearer $token", query, category)
            if (response.success && response.data != null) {
                val skills = response.data.map { it.toEntity() }
                Result.success(skills)
            } else {
                Result.failure(Exception(response.error ?: "Search failed"))
            }
        } catch (e: Exception) {
            // ✅ Network unavailable — fall back to local cache search
            val localResults = skillDao.searchSkills(query)
            Result.success(localResults)
        }
    }

    // -------------------------------------------------------------------------
    // Remote mutations
    // -------------------------------------------------------------------------

    /**
     * Accepts SkillCreateRequest DTO — never a Room entity.
     * The entity has @Entity/@PrimaryKey annotations and Room-specific
     * fields that don't belong in an API payload.
     */
    suspend fun addSkill(
        token: String,
        request: SkillCreateRequest                                 // ✅ DTO, not entity
    ): Result<Skill> {
        return try {
            val response = skillService.addSkill("Bearer $token", request)
            if (response.success && response.data != null) {
                val skill = response.data.toEntity()
                skillDao.insertSkill(skill)                         // ✅ cache confirmed response
                Result.success(skill)
            } else {
                Result.failure(Exception(response.error ?: "Failed to add skill"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSkill(
        token: String,
        skillId: String,                                            // ✅ String, not Int
        request: SkillCreateRequest                                 // ✅ DTO, not entity
    ): Result<Skill> {
        return try {
            val response = skillService.updateSkill("Bearer $token", skillId, request)
            if (response.success && response.data != null) {
                val skill = response.data.toEntity()
                skillDao.updateSkill(skill)                         // ✅ write confirmed update to cache
                Result.success(skill)
            } else {
                Result.failure(Exception(response.error ?: "Failed to update skill"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSkill(
        token: String,
        skillId: String                                             // ✅ String UUID, was Int
    ): Result<Unit> {
        return try {
            val response = skillService.deleteSkill("Bearer $token", skillId)
            if (response.success) {
                skillDao.deleteSkillById(skillId)                   // ✅ clean up local cache
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Failed to delete skill"))
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
                com.example.know_it_all.data.model.dto.SkillEndorseRequest(
                    skillId = skillId,
                    endorserId = endorserId
                )   // ✅ wrapped in DTO
            )
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Failed to endorse skill"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// -----------------------------------------------------------------------------
// DTO → Entity mapper
// -----------------------------------------------------------------------------

private fun SkillDTO.toEntity(): Skill = Skill(
    skillId = skillId,
    userId = userId,
    skillName = skillName,
    description = description,
    category = runCatching { SkillCategory.valueOf(category) }
        .getOrDefault(SkillCategory.HYBRID),       // safe fallback for unknown API values
    proficiencyLevel = runCatching {
        com.example.know_it_all.data.model.ProficiencyLevel.valueOf(proficiencyLevel)
    }.getOrDefault(com.example.know_it_all.data.model.ProficiencyLevel.BEGINNER),
    tokenValue = tokenValue,
    verificationStatus = verificationStatus,
    endorsements = endorsements,
    createdAt = createdAt ?: System.currentTimeMillis()
)