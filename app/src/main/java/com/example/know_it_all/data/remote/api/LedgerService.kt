package com.example.know_it_all.data.remote.api

import com.example.know_it_all.data.model.ApiResponse
import com.example.know_it_all.data.model.TrustLedger
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface LedgerService {
    @GET("ledger/user/{userId}")
    suspend fun getUserLedger(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): ApiResponse<List<TrustLedger>>

    @GET("ledger/verify/{transactionId}")
    suspend fun verifyTransaction(
        @Header("Authorization") token: String,
        @Path("transactionId") transactionId: String
    ): ApiResponse<Map<String, Any>>

    @GET("ledger/trust-score/{userId}")
    suspend fun getTrustScore(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): ApiResponse<Map<String, Any>>
}
