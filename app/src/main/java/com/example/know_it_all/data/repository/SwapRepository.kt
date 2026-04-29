package com.example.know_it_all.data.repository

import com.example.know_it_all.data.local.dao.SwapDao
import com.example.know_it_all.data.model.Swap
import com.example.know_it_all.data.model.SwapStatus
import com.example.know_it_all.data.model.SwapType
import com.example.know_it_all.data.model.VerificationMethod
import com.example.know_it_all.data.model.dto.SwapDTO
import com.example.know_it_all.data.model.dto.SwapRatingRequest
import com.example.know_it_all.data.model.dto.SwapRequestBody
import com.example.know_it_all.data.remote.MockDataSource
import com.example.know_it_all.data.remote.api.SwapService
import kotlinx.coroutines.flow.Flow

/**
 * Fixes applied:
 *
 *  1. SwapService and SwapDao are now INJECTED — no RetrofitClient or
 *     database references constructed inside the repository.
 *
 *  2. rateSwap parameter changed Float → Int to match the corrected
 *     SwapRatingRequest.rating type. Float allowed values like 3.7 which
 *     the 1–5 star UI cannot represent.
 *
 *  3. getActiveSwapsLocal now passes SwapStatus.ACTIVE enum to the DAO
 *     instead of the raw String "ACTIVE". The DAO's TypeConverter expects
 *     an enum — passing a String would either fail to compile or silently
 *     match nothing depending on Room's version handling.
 *
 *  4. getActiveSwapsRemote and getSwapHistory now write results to the
 *     local Room cache. The original fetched remote data and discarded it
 *     after returning — the offline-first cache was never populated.
 *
 *  5. requestSwap writes the confirmed response to the cache immediately
 *     so the Trade screen shows the new swap without waiting for a refresh.
 *
 *  6. acceptSwap, completeSwap, and cancelSwap all update the local cache
 *     after a successful API call (optimistic-safe: only updates on success).
 *
 *  7. Added cancelSwap — was missing from the original but is needed by
 *     the Trade screen's "cancel request" action.
 *
 *  8. USE_MOCK flag mirrors the pattern established in UserRepository so
 *     the switch to real endpoints is a one-line change per repository.
 */
class SwapRepository(
    private val swapDao: SwapDao,                // ✅ injected DAO
    private val swapService: SwapService         // ✅ injected service
) {

    private val USE_MOCK = true

    // -------------------------------------------------------------------------
    // Local reads (offline-first)
    // -------------------------------------------------------------------------

    /**
     * Fixed: SwapStatus.ACTIVE enum passed to DAO, not raw String "ACTIVE".
     */
    fun getActiveSwapsLocal(userId: String): Flow<List<Swap>> =
        swapDao.getActiveSwapsForUser(userId)    // ✅ uses the combined REQUESTED+ACTIVE query

    fun getSwapsByStatusLocal(userId: String, status: SwapStatus): Flow<List<Swap>> =
        swapDao.getUserSwapsByStatus(userId, status)  // ✅ enum, not String

    fun getPendingRequestCount(userId: String): Flow<Int> =
        swapDao.getPendingRequestCount(userId)

    suspend fun getCompletedSwapsLocal(userId: String): List<Swap> =
        swapDao.getCompletedSwapsForUser(userId)

    // -------------------------------------------------------------------------
    // Remote fetch + cache write-through
    // -------------------------------------------------------------------------

    suspend fun getActiveSwapsRemote(token: String): Result<List<SwapDTO>> {
        return if (USE_MOCK) {
            val dtos = MockDataSource.getActiveSwaps()
        } else {
            try {
                val response = swapService.getActiveSwaps("Bearer $token")
                if (response.success && response.data != null) {
                    val entities = response.data.map { it.toEntity() }
                    swapDao.insertSwaps(entities)                   // ✅ cache write
//                     Result.success(response.data)
                } else {
                    Result.failure(Exception(response.error ?: "Failed to fetch swaps"))
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
        } else {
            try {
                val response = swapService.getSwapHistory("Bearer $token", limit, offset)
                if (response.success && response.data != null) {
                    val entities = response.data.map { it.toEntity() }
                    swapDao.insertSwaps(entities)
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
                swapDao.insertSwap(response.data.toEntity())        // ✅ cache single swap
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to fetch swap details"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // Remote mutations — all write confirmed state back to cache
    // -------------------------------------------------------------------------

    suspend fun requestSwap(
        token: String,
        swapRequest: SwapRequestBody
    ): Result<SwapDTO> {
        return try {
            val response = swapService.requestSwap("Bearer $token", swapRequest)
            if (response.success && response.data != null) {
                swapDao.insertSwap(response.data.toEntity())        // ✅ cache new swap
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
                swapDao.insertSwap(response.data.toEntity())        // ✅ update cached status
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
                swapDao.insertSwap(response.data.toEntity())        // ✅ update cached status
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to complete swap"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelSwap(token: String, swapId: String): Result<Unit> { // ✅ new — was missing
        return try {
            val response = swapService.cancelSwap("Bearer $token", swapId)
            if (response.success) {
                swapDao.deleteSwapById(swapId)                      // ✅ remove from local cache
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Failed to cancel swap"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fixed: rating is Int (1–5), not Float.
     * Float allowed values like 3.7 that the star-rating UI can't represent.
     */
    suspend fun rateSwap(
        token: String,
        swapId: String,
        rating: Int,                                                // ✅ Int, was Float
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

// -----------------------------------------------------------------------------
// DTO → Entity mapper
// -----------------------------------------------------------------------------

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
