package com.example.know_it_all.data.repository

import com.example.know_it_all.data.local.dao.TrustLedgerDao
import com.example.know_it_all.data.model.TrustLedger
import com.example.know_it_all.data.model.dto.LedgerDisputeRequest
import com.example.know_it_all.data.model.dto.LedgerEntryDTO
import com.example.know_it_all.data.remote.api.LedgerService
import kotlinx.coroutines.flow.Flow

class LedgerRepository(
    private val ledgerDao: TrustLedgerDao,
    private val ledgerService: LedgerService
) {

    fun getUserLedger(userId: String): Flow<List<TrustLedger>> =
        ledgerDao.getLedgerEntriesByUser(userId)

    suspend fun getUserLedgerRemote(
        token: String,
        userId: String,
        limit: Int = 50,
        offset: Int = 0
    ): Result<List<LedgerEntryDTO>> {
        return try {
            val response = ledgerService.getUserLedger("Bearer $token", userId, limit, offset)
            if (response.success && response.data != null) {
                ledgerDao.insertLedgers(response.data.map { it.toEntity() })
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to load ledger"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyTransaction(token: String, transactionId: String): Result<Unit> {
        return try {
            val response = ledgerService.verifyTransaction("Bearer $token", transactionId)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Verification failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrustScore(token: String, userId: String): Result<Unit> {
        return try {
            val response = ledgerService.getTrustScore("Bearer $token", userId)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Failed to load trust score"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun disputeTransaction(
        token: String,
        transactionId: String,
        reason: String
    ): Result<Unit> {
        return try {
            val response = ledgerService.disputeTransaction(
                "Bearer $token",
                transactionId,
                DisputeBody(reason)
            )
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Dispute failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDisputedEntries(): List<TrustLedger> =
        ledgerDao.getDisputedEntries()

    suspend fun getLatestEntry(userId: String): TrustLedger? =
        ledgerDao.getLatestEntry(userId)

    suspend fun getCompletedCount(userId: String): Int =
        ledgerDao.getCompletedCount(userId)
}

private fun LedgerEntryDTO.toEntity(): TrustLedger = TrustLedger(
    transactionId = transactionId,
    swapId = swapId,
    mentorId = mentorId,
    learnerId = learnerId,
    skillName = skillName,
    previousHash = previousHash,
    currentHash = currentHash,
    ratingGiven = ratingGiven,
    ratingComment = ratingComment,
    status = status,
    createdAt = createdAt ?: System.currentTimeMillis()
)

data class DisputeBody(val reason: String)
