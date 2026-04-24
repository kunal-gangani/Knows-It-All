package com.example.know_it_all.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.know_it_all.data.model.LedgerStatus
import com.example.know_it_all.data.model.TrustLedger
import kotlinx.coroutines.flow.Flow

@Dao
interface TrustLedgerDao {

    // -------------------------------------------------------------------------
    // Writes
    // -------------------------------------------------------------------------

    /**
     * OnConflictStrategy.IGNORE — ledger entries are immutable by design
     * (blockchain-inspired chain). If an entry with the same transactionId
     * already exists, we silently skip — never overwrite a ledger record.
     * REPLACE would break the hash chain integrity.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLedgerEntry(entry: TrustLedger)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLedgerEntries(entries: List<TrustLedger>)        // ✅ batch insert

    /**
     * Ledger entries are immutable — no @Update method.
     * Status changes (DISPUTED → RESOLVED) go through a dedicated query
     * so the intent is explicit, not accidental via a full object update.
     */
    @Query("UPDATE trust_ledger SET status = :status WHERE transactionId = :transactionId")
    suspend fun updateLedgerStatus(transactionId: String, status: LedgerStatus) // ✅ targeted update only

    /**
     * Deletes are restricted to disputed/resolved entries only — completed
     * ledger entries should never be deleted (tamper-evident chain).
     * This method is intentionally not exposed to the repository for
     * COMPLETED entries.
     */
    @Delete
    suspend fun deleteLedgerEntry(entry: TrustLedger)

    // -------------------------------------------------------------------------
    // Reads
    // -------------------------------------------------------------------------

    @Query("SELECT * FROM trust_ledger WHERE transactionId = :transactionId")
    suspend fun getLedgerEntryById(transactionId: String): TrustLedger?

    /**
     * Fixed: original query used userId which no longer exists on TrustLedger.
     * The entity was refactored to have explicit mentorId + learnerId columns.
     * This query returns all entries where the user participated as either party,
     * ordered newest-first for the Vault screen timeline.
     */
    @Query("""
        SELECT * FROM trust_ledger 
        WHERE mentorId = :userId OR learnerId = :userId 
        ORDER BY createdAt DESC
    """)
    fun getLedgerEntriesByUser(userId: String): Flow<List<TrustLedger>> // ✅ fixed column reference

    /**
     * All entries for a specific swap — used to validate the hash chain
     * when displaying swap details. Returns a snapshot (not observed)
     * since a swap's ledger entries don't change after completion.
     */
    @Query("SELECT * FROM trust_ledger WHERE swapId = :swapId ORDER BY createdAt ASC")
    suspend fun getLedgerEntriesBySwap(swapId: String): List<TrustLedger>

    /**
     * Latest entry — used by PDFGenerator to get the chain tip's hash
     * when creating a new entry (previousHash field).
     * Returns null if the ledger is empty (first-ever transaction).
     */
    @Query("SELECT * FROM trust_ledger ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestLedgerEntry(): TrustLedger?

    /**
     * Disputed entries — used by the Vault dispute resolution screen.
     */
    @Query("""
        SELECT * FROM trust_ledger 
        WHERE (mentorId = :userId OR learnerId = :userId) 
        AND status = 'DISPUTED'
        ORDER BY createdAt DESC
    """)
    fun getDisputedEntriesForUser(userId: String): Flow<List<TrustLedger>> // ✅ new

    /**
     * Average rating received as mentor — drives the trust score display
     * on the SkillProfile screen without a full ledger scan in the UI layer.
     */
    @Query("""
        SELECT AVG(ratingGiven) FROM trust_ledger 
        WHERE mentorId = :userId AND status = 'COMPLETED'
    """)
    suspend fun getAverageRatingForMentor(userId: String): Float?       // ✅ new

    /**
     * Total completed transactions — used for the "X swaps completed" badge
     * on the user profile card in the Radar and SkillProfile screens.
     */
    @Query("""
        SELECT COUNT(*) FROM trust_ledger 
        WHERE (mentorId = :userId OR learnerId = :userId) 
        AND status = 'COMPLETED'
    """)
    suspend fun getCompletedSwapCount(userId: String): Int              // ✅ new
}