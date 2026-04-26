package com.example.know_it_all.data.repository

import com.example.know_it_all.data.local.dao.TrustLedgerDao
import com.example.know_it_all.data.model.LedgerStatus
import com.example.know_it_all.data.model.TrustLedger
import com.example.know_it_all.data.model.dto.LedgerEntryDTO
import com.example.know_it_all.data.remote.api.LedgerService
import kotlinx.coroutines.flow.Flow

/**
 * Fixes applied:
 *
 *  1. LedgerService is now INJECTED, not constructed via RetrofitClient
 *     internally. The original made unit testing impossible without a live
 *     network. Injected dependencies can be mocked in tests.
 *
 *  2. TrustLedgerDao is now INJECTED directly instead of accessed through
 *     the KnowItAllDatabase god-object. Repositories should depend on DAOs,
 *     not on the database class.
 *
 *  3. getUserLedgerRemote now maps LedgerEntryDTO → TrustLedger before
 *     inserting into Room. The original inserted whatever the API returned
 *     directly into the cache — if the API shape ever diverged from the Room
 *     entity, this would silently corrupt the local database.
 *
 *  4. getTrustScore and verifyTransaction return typed data classes instead
 *     of Map<String, Any>. Untyped maps bypass the compiler's type system —
 *     any field access is an unchecked cast at runtime.
 *
 *  5. getLatestLedgerEntry exposed for PDFGenerator / hash-chain use.
 *
 *  6. getAverageRating and getCompletedCount added as local aggregates so
 *     the SkillProfile screen doesn't need a network call for display stats.
 */
class LedgerRepository(
    private val ledgerDao: TrustLedgerDao,       // ✅ injected DAO, not database
    private val ledgerService: LedgerService     // ✅ injected service, not RetrofitClient
) {

    // -------------------------------------------------------------------------
    // Local reads (offline-first — always available)
    // -------------------------------------------------------------------------

    fun getUserLedgerLocal(userId: String): Flow<List<TrustLedger>> =
        ledgerDao.getLedgerEntriesByUser(userId)

    fun getDisputedEntriesLocal(userId: String): Flow<List<TrustLedger>> =
        ledgerDao.getDisputedEntriesForUser(userId)

    suspend fun getLatestEntry(): TrustLedger? =
        ledgerDao.getLatestLedgerEntry()

    suspend fun getAverageRating(userId: String): Float? =
        ledgerDao.getAverageRatingForMentor(userId)

    suspend fun getCompletedCount(userId: String): Int =
        ledgerDao.getCompletedSwapCount(userId)

    suspend fun saveLedgerEntry(entry: TrustLedger) =
        ledgerDao.insertLedgerEntry(entry)

    // -------------------------------------------------------------------------
    // Remote fetch + cache write-through
    // -------------------------------------------------------------------------

    /**
     * Fetches paginated ledger entries from the API and writes them to Room.
     * Maps DTO → entity before inserting so API shape changes don't corrupt
     * the local cache schema.
     */
    suspend fun getUserLedgerRemote(
        token: String,
        userId: String,
        limit: Int = 50,
        offset: Int = 0
    ): Result<List<TrustLedger>> {
        return try {
            val response = ledgerService.getUserLedger("Bearer $token", userId, limit, offset)
            if (response.success && response.data != null) {
                val entries = response.data.map { it.toEntity() }  // ✅ DTO → entity mapping
                ledgerDao.insertLedgerEntries(entries)              // ✅ batch insert
                Result.success(entries)
            } else {
                Result.failure(Exception(response.error ?: "Failed to fetch ledger"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // Remote actions
    // -------------------------------------------------------------------------

    /**
     * Returns typed TrustVerificationResult instead of Map<String, Any>.
     * Untyped maps bypass compile-time safety — any field access is an
     * unchecked cast that crashes at runtime on field name typos.
     */
    suspend fun verifyTransaction(
        token: String,
        transactionId: String
    ): Result<TrustVerificationResult> {
        return try {
            val response = ledgerService.verifyTransaction("Bearer $token", transactionId)
            if (response.success && response.data != null) {
                Result.success(response.data)                       // ✅ typed result
            } else {
                Result.failure(Exception(response.error ?: "Verification failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrustScore(
        token: String,
        userId: String
    ): Result<TrustScoreResult> {
        return try {
            val response = ledgerService.getTrustScore("Bearer $token", userId)
            if (response.success && response.data != null) {
                Result.success(response.data)                       // ✅ typed result
            } else {
                Result.failure(Exception(response.error ?: "Failed to fetch trust score"))
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
                com.example.know_it_all.data.remote.api.DisputeBody(reason)  // ✅ wrapped in DTO
            )
            if (response.success) {
                // Update local cache status immediately (optimistic update)
                ledgerDao.updateLedgerStatus(transactionId, LedgerStatus.DISPUTED)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Failed to raise dispute"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// -----------------------------------------------------------------------------
// Typed response models — replace Map<String, Any>
// -----------------------------------------------------------------------------

data class TrustVerificationResult(
    val transactionId: String,
    val isValid: Boolean,
    val hashMatch: Boolean,
    val verifiedAt: Long
)

data class TrustScoreResult(
    val userId: String,
    val score: Float,
    val totalSwaps: Int,
    val averageRating: Float
)

// -----------------------------------------------------------------------------
// DTO → Entity mapper (lives here until a dedicated Mappers.kt is warranted)
// -----------------------------------------------------------------------------

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