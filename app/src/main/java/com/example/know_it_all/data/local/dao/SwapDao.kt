package com.example.know_it_all.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.know_it_all.data.model.Swap
import com.example.know_it_all.data.model.SwapStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface SwapDao {

    // -------------------------------------------------------------------------
    // Writes
    // -------------------------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)      // ✅ upsert — cache stays fresh on refresh
    suspend fun insertSwap(swap: Swap)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSwaps(swaps: List<Swap>)             // ✅ batch insert for trade history load

    @Update
    suspend fun updateSwap(swap: Swap)

    @Delete
    suspend fun deleteSwap(swap: Swap)

    @Query("DELETE FROM swaps WHERE swapId = :swapId")
    suspend fun deleteSwapById(swapId: String)             // ✅ delete by ID

    // -------------------------------------------------------------------------
    // Reads
    // -------------------------------------------------------------------------

    @Query("SELECT * FROM swaps WHERE swapId = :swapId")
    suspend fun getSwapById(swapId: String): Swap?

    /**
     * Observed flow of all swaps involving a user (as mentor OR learner).
     * Trade screen observes this to reactively update the swap list.
     */
    @Query("""
        SELECT * FROM swaps 
        WHERE mentorId = :userId OR learnerId = :userId 
        ORDER BY updatedAt DESC
    """)
    fun getUserSwaps(userId: String): Flow<List<Swap>>

    /**
     * Fixed: status param is now SwapStatus enum, not a raw String.
     * Passing a raw String like "ACTIVE" would work until someone
     * renames the enum entry — then it silently returns nothing.
     * The TypeConverter handles enum ↔ String automatically.
     */
    @Query("SELECT * FROM swaps WHERE status = :status ORDER BY updatedAt DESC")
    fun getSwapsByStatus(status: SwapStatus): Flow<List<Swap>>         // ✅ enum, was String

    /**
     * Fixed: status param is now SwapStatus enum.
     * Primary query for the Trade screen — "my active swaps".
     */
    @Query("""
        SELECT * FROM swaps 
        WHERE (mentorId = :userId OR learnerId = :userId) 
        AND status = :status 
        ORDER BY updatedAt DESC
    """)
    fun getUserSwapsByStatus(userId: String, status: SwapStatus): Flow<List<Swap>> // ✅ enum, was String

    /**
     * Active swaps only — convenience query used by the Trade screen badge count
     * and by the bottom nav unread indicator.
     */
    @Query("""
        SELECT * FROM swaps 
        WHERE (mentorId = :userId OR learnerId = :userId) 
        AND status IN ('REQUESTED', 'ACTIVE')
        ORDER BY createdAt DESC
    """)
    fun getActiveSwapsForUser(userId: String): Flow<List<Swap>>        // ✅ new — active + requested

    /**
     * Completed swap history — used by the Vault screen ledger view.
     * Returns a snapshot (not observed) since history doesn't change.
     */
    @Query("""
        SELECT * FROM swaps 
        WHERE (mentorId = :userId OR learnerId = :userId) 
        AND status = 'COMPLETED'
        ORDER BY updatedAt DESC
    """)
    suspend fun getCompletedSwapsForUser(userId: String): List<Swap>   // ✅ new — history view

    /**
     * Count of pending swap requests — drives the notification badge
     * on the Trade screen bottom nav item.
     */
    @Query("""
        SELECT COUNT(*) FROM swaps 
        WHERE learnerId = :userId AND status = 'REQUESTED'
    """)
    fun getPendingRequestCount(userId: String): Flow<Int>              // ✅ new — badge count
}