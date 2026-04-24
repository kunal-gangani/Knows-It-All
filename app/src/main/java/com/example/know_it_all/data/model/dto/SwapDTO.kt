package com.example.know_it_all.data.model.dto

import com.example.know_it_all.data.model.SwapStatus
import com.example.know_it_all.data.model.SwapType
import com.example.know_it_all.data.model.VerificationMethod

/**
 * Fixes applied vs original:
 *  1. mentorSkillId + learnerSkillId changed Int → String UUID to match
 *     the corrected Skill.skillId type. Int was a silent type mismatch
 *     that compiled but failed at runtime on any JOIN or lookup.
 *  2. learnerSkillId is now nullable (String?) — TOKEN swaps have no
 *     learner skill offered in return.
 *  3. verificationMethod changed String → VerificationMethod enum to
 *     eliminate invalid freeform strings like typos ("QR_HANDSHAK").
 *  4. Added learnerName — symmetric with mentorName for Trade screen display.
 *  5. Added sessionStartTime + sessionEndTime — needed by TradeScreen to
 *     show session duration and progress state.
 *  6. SwapRatingRequest.rating changed Float → Int (1–5 discrete stars).
 *  7. Added SwapStatusUpdateRequest — was missing from original DTO set.
 */

data class SwapDTO(
    val swapId: String,
    val mentorId: String,
    val learnerId: String,
    val mentorName: String = "",
    val learnerName: String = "",           // ✅ was missing, symmetric with mentorName
    val skillName: String = "",
    val mentorSkillId: String,              // ✅ String UUID, was Int
    val learnerSkillId: String? = null,     // ✅ String UUID, nullable for TOKEN swaps
    val swapType: SwapType,
    val tokenAmount: Long = 0L,
    val status: SwapStatus = SwapStatus.REQUESTED,
    val verificationMethod: VerificationMethod = VerificationMethod.NONE, // ✅ enum, was String
    val sessionStartTime: Long? = null,     // ✅ was missing
    val sessionEndTime: Long? = null,       // ✅ was missing
    val createdAt: Long? = null
)

data class SwapRequestBody(
    val mentorId: String,
    val learnerId: String,
    val mentorSkillId: String,              // ✅ String UUID, was Int
    val learnerSkillId: String? = null,     // ✅ nullable for TOKEN swaps
    val swapType: SwapType,
    val tokenAmount: Long = 0L
)

data class SwapRatingRequest(
    val swapId: String,
    val rating: Int,                        // ✅ Int, was Float — 1 to 5 only
    val comment: String = ""
)

data class SwapStatusUpdateRequest(         // ✅ was missing from original
    val swapId: String,
    val status: SwapStatus
)