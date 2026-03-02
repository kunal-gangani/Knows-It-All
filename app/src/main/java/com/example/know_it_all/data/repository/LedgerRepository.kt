package com.example.know_it_all.data.repository

import com.example.know_it_all.data.local.db.KnowItAllDatabase
import com.example.know_it_all.data.model.TrustLedger
import com.example.know_it_all.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class LedgerRepository(private val database: KnowItAllDatabase) {
    private val ledgerService = RetrofitClient.createLedgerService()
    private val ledgerDao = database.trustLedgerDao()

    fun getUserLedgerLocal(userId: String): Flow<List<TrustLedger>> {
        return ledgerDao.getLedgerEntriesByUser(userId)
    }

    suspend fun getUserLedgerRemote(
        token: String,
        userId: String,
        limit: Int = 50,
        offset: Int = 0
    ): Result<List<TrustLedger>> {
        return try {
            val response = ledgerService.getUserLedger("Bearer $token", userId, limit, offset)
            if (response.success && response.data != null) {
                response.data.forEach { ledgerDao.insertLedgerEntry(it) }
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to fetch ledger"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyTransaction(
        token: String,
        transactionId: String
    ): Result<Map<String, Any>> {
        return try {
            val response = ledgerService.verifyTransaction("Bearer $token", transactionId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Verification failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrustScore(token: String, userId: String): Result<Map<String, Any>> {
        return try {
            val response = ledgerService.getTrustScore("Bearer $token", userId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to fetch trust score"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveLedgerEntry(entry: TrustLedger) {
        ledgerDao.insertLedgerEntry(entry)
    }
}
