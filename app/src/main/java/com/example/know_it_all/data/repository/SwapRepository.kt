package com.example.know_it_all.data.repository

import com.example.know_it_all.data.local.dao.SwapDao
import com.example.know_it_all.data.model.Swap
import com.example.know_it_all.data.model.SwapStatus
import com.example.know_it_all.data.model.VerificationMethod
import com.example.know_it_all.data.model.dto.SwapDTO
import com.example.know_it_all.data.model.dto.SwapRatingRequest
import com.example.know_it_all.data.model.dto.SwapRequestBody
import com.example.know_it_all.data.remote.MockDataSource
import com.example.know_it_all.data.remote.api.SwapService
import kotlinx.coroutines.flow.Flow

class SwapRepository(
    private val swapDao: SwapDao,
    private val swapService: SwapService
) {

    private val USE_MOCK = true

    // ── Local reads ───────────────────────────────────────────────────────────

    fun getActiveSwapsLocal(userId: String): Flow<List<Swap>> =
        swapDao.getActiveSwapsForUser(userId)

    fun getSwapsByStatusLocal(userId: String, status: SwapStatus): Flow<List<Swap>> =
        swapDao.getUserSwapsByStatus(userId, status)

    fun getPendingRequestCount(userId: String): Flow<Int> =
        swapDao.getPendingRequestCount(userId)

    suspend fun getCompletedSwapsLocal(userId: String): List<Swap> =
        swapDao.getCompletedSwapsForUser(userId)

    // ── Remote fetch ──────────────────────────────────────────────────────────

    /**
     * Fixed: mock branch now returns Result.success(dtos) properly.
     * Cache write removed from mock mode — mock swap data references
     * userIds that don't exist in local Room DB, which would trigger
     * the same FK constraint crash we just fixed in Swap.kt.
     */
    suspend fun getActiveSwapsRemote(token: String): Result<List<SwapDTO>> {
        return if (USE_MOCK) {
            Result.success(MockDataSource.getActiveSwaps())         // ✅ fixed
        } else {
            try {
                val response = swapService.getActiveSwaps("Bearer $token")
                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.error ?: "Failed to fetch swaps"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Fixed: mock branch now returns Result.success(dtos) properly.
     */
    suspend fun getSwapHistory(
        token: String,
        limit: Int = 10,
        offset: Int = 0
    ): Result<List<SwapDTO>> {
        return if (USE_MOCK) {
            Result.success(MockDataSource.getSwapHistory())         // ✅ fixed
        } else {
            try {
                val response = swapService.getSwapHistory("Bearer $token", limit, offset)
                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.error ?: "Failed to fetch history"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getSwapDetails(token: String, swapId: String): Result<SwapDTO> {
        return try {
            val response = swapService.getSwapDetails("Bearer $token", swapId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to fetch swap details"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Remote mutations ──────────────────────────────────────────────────────

    suspend fun requestSwap(token: String, swapRequest: SwapRequestBody): Result<SwapDTO> {
        return try {
            val response = swapService.requestSwap("Bearer $token", swapRequest)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to request swap"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun acceptSwap(token: String, swapId: String): Result<SwapDTO> {
        return try {
            val response = swapService.acceptSwap("Bearer $token", swapId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to accept swap"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeSwap(token: String, swapId: String): Result<SwapDTO> {
        return try {
            val response = swapService.completeSwap("Bearer $token", swapId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to complete swap"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelSwap(token: String, swapId: String): Result<Unit> {
        return try {
            val response = swapService.cancelSwap("Bearer $token", swapId)
            if (response.success) {
                swapDao.deleteSwapById(swapId)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Failed to cancel swap"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rateSwap(
        token: String,
        swapId: String,
        rating: Int,
        comment: String = ""
    ): Result<Unit> {
        return try {
            val response = swapService.rateSwap(
                "Bearer $token",
                swapId,
                SwapRatingRequest(swapId, rating, comment)
            )
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Failed to rate swap"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// ── DTO → Entity mapper ───────────────────────────────────────────────────────

private fun SwapDTO.toEntity(): Swap = Swap(
    swapId = swapId,
    mentorId = mentorId,
    learnerId = learnerId,
    mentorSkillId = mentorSkillId,
    learnerSkillId = learnerSkillId,
    swapType = swapType,
    tokenAmount = tokenAmount,
    status = status,
    verificationMethod = verificationMethod,
    sessionStartTime = sessionStartTime,
    sessionEndTime = sessionEndTime,
    createdAt = createdAt ?: System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)