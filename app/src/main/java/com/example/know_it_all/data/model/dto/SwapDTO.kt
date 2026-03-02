package com.example.know_it_all.data.model.dto

import com.example.know_it_all.data.model.SwapStatus
import com.example.know_it_all.data.model.SwapType

data class SwapDTO(
    val swapId: String,
    val mentorId: String,
    val learnerId: String,
    val mentorSkillId: Int,
    val learnerSkillId: Int?,
    val swapType: SwapType,
    val tokenAmount: Long = 0L,
    val status: SwapStatus = SwapStatus.REQUESTED
)

data class SwapRequestBody(
    val mentorId: String,
    val learnerId: String,
    val mentorSkillId: Int,
    val learnerSkillId: Int?,
    val swapType: SwapType,
    val tokenAmount: Long = 0L
)

data class SwapRatingRequest(
    val swapId: String,
    val rating: Float,
    val comment: String = ""
)
