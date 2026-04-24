package com.example.know_it_all.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.know_it_all.data.model.Skill
import com.example.know_it_all.data.model.SkillCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {

    // -------------------------------------------------------------------------
    // Writes
    // -------------------------------------------------------------------------

    /**
     * OnConflictStrategy.REPLACE allows upsert behaviour — inserting a skill
     * that already exists (same skillId) replaces the cached version.
     * Without this, re-inserting after a network refresh throws a constraint
     * violation and the cache never updates.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: Skill)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkills(skills: List<Skill>)          // ✅ batch insert for profile load

    @Update
    suspend fun updateSkill(skill: Skill)

    @Delete
    suspend fun deleteSkill(skill: Skill)

    @Query("DELETE FROM skills WHERE skillId = :skillId")
    suspend fun deleteSkillById(skillId: String)           // ✅ delete by ID, not by object reference

    // -------------------------------------------------------------------------
    // Reads — suspend for one-shot queries, Flow for observed lists
    // -------------------------------------------------------------------------

    /**
     * Fixed: skillId is now String (was Int — type mismatch with the entity).
     */
    @Query("SELECT * FROM skills WHERE skillId = :skillId")
    suspend fun getSkillById(skillId: String): Skill?      // ✅ String, was Int

    /**
     * Observed flow — emits a new list whenever any skill for this user changes.
     * Used by SkillProfileScreen to reactively update the skill list without
     * manual refresh calls.
     */
    @Query("SELECT * FROM skills WHERE userId = :userId ORDER BY createdAt DESC")
    fun getSkillsByUser(userId: String): Flow<List<Skill>>

    /**
     * One-shot search — returns current snapshot, not observed.
     * The Radar screen calls this on demand; it doesn't need to observe changes.
     */
    @Query("SELECT * FROM skills WHERE skillName LIKE '%' || :query || '%' ORDER BY endorsements DESC")
    suspend fun searchSkills(query: String): List<Skill>   // ✅ param renamed query (clearer intent)

    /**
     * Fixed: category param is now SkillCategory enum, not a raw String.
     * Room's TypeConverter (RoomTypeConverters.kt) handles the conversion —
     * passing a raw String would bypass type safety entirely.
     */
    @Query("SELECT * FROM skills WHERE category = :category ORDER BY endorsements DESC")
    fun getSkillsByCategory(category: SkillCategory): Flow<List<Skill>> // ✅ enum + Flow (was suspend List)

    /**
     * Used by the token economy — fetch skills sorted by token value
     * for the "top skills in your area" Radar feature.
     */
    @Query("SELECT * FROM skills WHERE userId = :userId ORDER BY tokenValue DESC")
    suspend fun getTopSkillsByUser(userId: String): List<Skill>         // ✅ new query

    /**
     * Verification status filter — used by the Radar to show only
     * community-verified mentors when the filter is applied.
     */
    @Query("SELECT * FROM skills WHERE verificationStatus = 1 ORDER BY endorsements DESC")
    fun getVerifiedSkills(): Flow<List<Skill>>                          // ✅ new query
}