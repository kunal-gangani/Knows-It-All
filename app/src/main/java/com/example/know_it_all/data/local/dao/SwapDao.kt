package com.example.know_it_all.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.know_it_all.data.model.Swap
import kotlinx.coroutines.flow.Flow

@Dao
interface SwapDao {
    @Insert
    suspend fun insertSwap(swap: Swap)

    @Update
    suspend fun updateSwap(swap: Swap)

    @Delete
    suspend fun deleteSwap(swap: Swap)

    @Query("SELECT * FROM swaps WHERE swapId = :swapId")
    suspend fun getSwapById(swapId: String): Swap?

    @Query("SELECT * FROM swaps WHERE mentorId = :userId OR learnerId = :userId")
    fun getUserSwaps(userId: String): Flow<List<Swap>>

    @Query("SELECT * FROM swaps WHERE status = :status")
    fun getSwapsByStatus(status: String): Flow<List<Swap>>

    @Query("SELECT * FROM swaps WHERE (mentorId = :userId OR learnerId = :userId) AND status = :status")
    fun getUserSwapsByStatus(userId: String, status: String): Flow<List<Swap>>
}
