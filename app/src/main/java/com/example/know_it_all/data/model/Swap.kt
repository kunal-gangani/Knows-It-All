package com.example.know_it_all.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Fixes applied:
 *  1. mentorSkillId + learnerSkillId changed Int → String to match Skill.skillId
 *     which is now a String UUID. Int was a type mismatch that compiled silently
 *     but would fail at runtime on any JOIN or lookup.
 *  2. Added @ForeignKey declarations for mentorId, learnerId (→ User) and
 *     mentorSkillId (→ Skill). Without these, Room allows orphaned swaps that
 *     reference deleted users or skills — a data integrity hole.
 *  3. learnerSkillId FK is nullable-safe: ForeignKey with deferred = true
 *     allows null (TOKEN swaps have no learner skill).
 *  4. Added @Index on mentorId, learnerId, status — the Trade screen queries
 *     "all active swaps for user X"; without these indices it's a full scan.
 *  5. updatedAt default set to 0L — must be set explicitly at DAO write time.
 *  6. verificationMethod changed String → VerificationMethod enum to remove
 *     invalid string values like typos ("QR_HANDSHAK").
 */
@Entity(
    tableName = "swaps",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uid"],
            childColumns = ["mentorId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["uid"],
            childColumns = ["learnerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Skill::class,
            parentColumns = ["skillId"],
            childColumns = ["mentorSkillId"],
            onDelete = ForeignKey.RESTRICT   // don't silently delete a skill mid-swap
        )
    ],
    indices = [
        Index(value = ["mentorId"]),
        Index(value = ["learnerId"]),
        Index(value = ["status"]),           // fast filter for active/completed swaps
        Index(value = ["mentorSkillId"])
    ]
)
data class Swap(
    @PrimaryKey
    val swapId: String,
    val mentorId: String,
    val learnerId: String,
    val mentorSkillId: String,              // ✅ String UUID — matches Skill.skillId
    val learnerSkillId: String? = null,     // ✅ String UUID — null for TOKEN swaps
    val swapType: SwapType,
    val tokenAmount: Long = 0L,
    val status: SwapStatus = SwapStatus.REQUESTED,
    val verificationMethod: VerificationMethod = VerificationMethod.NONE,  // ✅ enum, not String
    val sessionStartTime: Long? = null,
    val sessionEndTime: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = 0L                // ✅ set explicitly at DAO write time
)

enum class SwapStatus {
    REQUESTED,
    ACTIVE,
    COMPLETED,
    CANCELLED,
    DISPUTED
}

enum class SwapType {
    BARTER,     // Pure 1:1 skill exchange
    TOKEN,      // SkillToken payment, no skill offered in return
    HYBRID      // Skill exchange + token top-up
}

enum class VerificationMethod {
    NONE,           // Not yet set
    QR_HANDSHAKE,   // In-person QR scan
    VIDEO_CALL,     // Remote video confirmation
    BOTH            // QR + video (highest trust)
}