package com.example.know_it_all.data.repository

import com.example.know_it_all.data.local.db.KnowItAllDatabase
import com.example.know_it_all.data.model.Swap
import com.example.know_it_all.data.model.dto.SwapDTO
import com.example.know_it_all.data.model.dto.SwapRequestBody
import com.example.know_it_all.data.model.dto.SwapRatingRequest
import com.example.know_it_all.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class SwapRepository(private val database: KnowItAllDatabase) {
    private val swapService = RetrofitClient.createSwapService()
    private val swapDao = database.swapDao()

    suspend fun requestSwap(
        token: String,
        swapRequest: SwapRequestBody
    ): Result<SwapDTO> {
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

    suspend fun rateSwap(
        token: String,
        swapId: String,
        rating: Float,
        comment: String = ""
    ): Result<Map<String, Any>> {
        return try {
            val response = swapService.rateSwap(
                "Bearer $token",
                swapId,
                SwapRatingRequest(swapId, rating, comment)
            )
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to rate swap"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getActiveSwapsLocal(userId: String): Flow<List<Swap>> {
        return swapDao.getUserSwapsByStatus(userId, "ACTIVE")
    }

    suspend fun getActiveSwapsRemote(token: String): Result<List<SwapDTO>> {
        return try {
            val response = swapService.getActiveSwaps("Bearer $token")
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to fetch active swaps"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSwapHistory(
        token: String,
        limit: Int = 10,
        offset: Int = 0
    ): Result<List<SwapDTO>> {
        return try {
            val response = swapService.getSwapHistory("Bearer $token", limit, offset)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to fetch swap history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
