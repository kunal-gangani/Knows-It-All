package com.example.know_it_all.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SkillCategory {
    DIGITAL,
    PHYSICAL,
    HYBRID
}

enum class ProficiencyLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    EXPERT
}

@Entity(tableName = "skills")
data class Skill(
    @PrimaryKey(autoGenerate = true)
    val skillId: Int = 0,
    val userId: String,
    val skillName: String,
    val description: String = "",
    val category: SkillCategory,
    val proficiencyLevel: ProficiencyLevel,
    val verificationStatus: Boolean = false,
    val endorsements: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
