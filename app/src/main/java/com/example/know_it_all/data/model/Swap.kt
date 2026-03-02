package com.example.know_it_all.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SwapStatus {
    REQUESTED,
    ACTIVE,
    COMPLETED,
    CANCELLED,
    DISPUTED
}

enum class SwapType {
    BARTER,
    TOKEN,
    HYBRID
}

@Entity(tableName = "swaps")
data class Swap(
    @PrimaryKey
    val swapId: String,
    val mentorId: String,
    val learnerId: String,
    val mentorSkillId: Int,
    val learnerSkillId: Int?,
    val swapType: SwapType,
    val tokenAmount: Long = 0L,
    val status: SwapStatus = SwapStatus.REQUESTED,
    val verificationMethod: String = "", // QR_HANDSHAKE, VIDEO_CALL, etc.
    val sessionStartTime: Long? = null,
    val sessionEndTime: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
