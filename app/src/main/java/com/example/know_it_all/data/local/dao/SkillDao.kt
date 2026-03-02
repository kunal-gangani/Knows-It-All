package com.example.know_it_all.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.know_it_all.data.model.Skill
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {
    @Insert
    suspend fun insertSkill(skill: Skill)

    @Update
    suspend fun updateSkill(skill: Skill)

    @Delete
    suspend fun deleteSkill(skill: Skill)

    @Query("SELECT * FROM skills WHERE skillId = :skillId")
    suspend fun getSkillById(skillId: Int): Skill?

    @Query("SELECT * FROM skills WHERE userId = :userId")
    fun getSkillsByUser(userId: String): Flow<List<Skill>>

    @Query("SELECT * FROM skills WHERE skillName LIKE '%' || :skillName || '%'")
    suspend fun searchSkills(skillName: String): List<Skill>

    @Query("SELECT * FROM skills WHERE category = :category")
    suspend fun getSkillsByCategory(category: String): List<Skill>
}
