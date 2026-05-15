package com.example.know_it_all.data.repository

import com.example.know_it_all.data.local.dao.SwapDao
import com.example.know_it_all.data.model.Swap
import com.example.know_it_all.data.model.SwapStatus
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

    fun getUserSwaps(userId: String): Flow<List<Swap>> =
        swapDao.getUserSwaps(userId)

    fun getSwapsByStatus(status: SwapStatus): Flow<List<Swap>> =
        swapDao.getSwapsByStatus(status)

    suspend fun requestSwap(
        token: String,
        request: SwapRequestBody
    ): Result<SwapDTO> {
        return try {
            val response = swapService.requestSwap("Bearer $token", request)
            if (response.success && response.data != null) {
                swapDao.insertSwap(response.data.toEntity())
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Swap request failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSwapDetails(token: String, swapId: String): Result<SwapDTO> {
        return try {
            val response = swapService.getSwapDetails("Bearer $token", swapId)
            if (response.success && response.data != null) {
                swapDao.insertSwap(response.data.toEntity())
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to load swap details"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun acceptSwap(token: String, swapId: String): Result<SwapDTO> {
        return try {
            val response = swapService.acceptSwap("Bearer $token", swapId)
            if (response.success && response.data != null) {
                swapDao.updateSwap(response.data.toEntity())
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
                swapDao.updateSwap(response.data.toEntity())
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
                swapDao.deleteSwap(swapId)
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

    suspend fun getActiveSwaps(token: String): Result<List<SwapDTO>> {
        return if (USE_MOCK) {
            val dtos = MockDataSource.getActiveSwaps()
            swapDao.insertSwaps(dtos.map { it.toEntity() })
            Result.success(dtos)
        } else {
            try {
                val response = swapService.getActiveSwaps("Bearer $token")
                if (response.success && response.data != null) {
                    swapDao.insertSwaps(response.data.map { it.toEntity() })
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.error ?: "Failed to load active swaps"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getSwapHistory(
        token: String,
        limit: Int = 10,
        offset: Int = 0
    ): Result<List<SwapDTO>> {
        return if (USE_MOCK) {
            val dtos = MockDataSource.getSwapHistory()
            swapDao.insertSwaps(dtos.map { it.toEntity() })
            Result.success(dtos)
        } else {
            try {
                val response = swapService.getSwapHistory("Bearer $token", limit, offset)
                if (response.success && response.data != null) {
                    swapDao.insertSwaps(response.data.map { it.toEntity() })
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.error ?: "Failed to load history"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

private fun SwapDTO.toEntity(): Swap = Swap(
    swapId = swapId,
    mentorId = mentorId,
    learnerId = learnerId,
    mentorName = mentorName,
    learnerName = learnerName,
    skillName = skillName,
    mentorSkillId = mentorSkillId,
    learnerSkillId = learnerSkillId ?: "",
    swapType = swapType,
    tokenAmount = tokenAmount,
    status = status,
    verificationMethod = verificationMethod,
    sessionStartTime = sessionStartTime,
    sessionEndTime = sessionEndTime,
    createdAt = createdAt ?: System.currentTimeMillis()
)
