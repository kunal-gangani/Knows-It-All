package com.example.know_it_all.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.know_it_all.data.model.TrustLedger
import kotlinx.coroutines.flow.Flow

@Dao
interface TrustLedgerDao {
    @Insert
    suspend fun insertLedgerEntry(entry: TrustLedger)

    @Delete
    suspend fun deleteLedgerEntry(entry: TrustLedger)

    @Query("SELECT * FROM trust_ledger WHERE transactionId = :transactionId")
    suspend fun getLedgerEntryById(transactionId: String): TrustLedger?

    @Query("SELECT * FROM trust_ledger WHERE userId = :userId ORDER BY createdAt DESC")
    fun getLedgerEntriesByUser(userId: String): Flow<List<TrustLedger>>

    @Query("SELECT * FROM trust_ledger WHERE swapId = :swapId")
    suspend fun getLedgerEntriesBySwap(swapId: String): List<TrustLedger>

    @Query("SELECT * FROM trust_ledger ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestLedgerEntry(): TrustLedger?
}
