package com.example.know_it_all.data.remote.api

import com.example.know_it_all.data.model.ApiResponse
import com.example.know_it_all.data.model.dto.LedgerEntryDTO
import com.example.know_it_all.data.repository.TrustScoreResult
import com.example.know_it_all.data.repository.TrustVerificationResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Fixes applied:
 *
 *  1. getUserLedger returns ApiResponse<List<LedgerEntryDTO>> instead of
 *     ApiResponse<List<TrustLedger>>. API interfaces must never return Room
 *     entities — TrustLedger carries @Entity/@PrimaryKey annotations that
 *     Gson doesn't understand and that leak the persistence layer into the
 *     network layer. The repository maps DTO → entity before touching Room.
 *
 *  2. verifyTransaction returns ApiResponse<TrustVerificationResult> instead
 *     of ApiResponse<Map<String, Any>>. Typed response classes give compile-time
 *     safety — Map<String, Any> requires unchecked casts at every field access
 *     and crashes at runtime on field name typos.
 *
 *  3. getTrustScore returns ApiResponse<TrustScoreResult> for the same reason.
 *
 *  4. disputeTransaction added — LedgerRepository.disputeTransaction() calls
 *     this endpoint. It was missing entirely from the original interface,
 *     meaning the dispute flow would have crashed with a NoSuchMethodError.
 */
interface LedgerService {

    @GET("ledger/user/{userId}")
    suspend fun getUserLedger(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): ApiResponse<List<LedgerEntryDTO>>               // ✅ DTO, not Room entity

    @GET("ledger/verify/{transactionId}")
    suspend fun verifyTransaction(
        @Header("Authorization") token: String,
        @Path("transactionId") transactionId: String
    ): ApiResponse<TrustVerificationResult>            // ✅ typed result, not Map<String, Any>

    @GET("ledger/trust-score/{userId}")
    suspend fun getTrustScore(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): ApiResponse<TrustScoreResult>                   // ✅ typed result, not Map<String, Any>

    @POST("ledger/dispute/{transactionId}")            // ✅ new — was missing, called by repository
    suspend fun disputeTransaction(
        @Header("Authorization") token: String,
        @Path("transactionId") transactionId: String,
        @Body reason: DisputeBody
    ): ApiResponse<Unit>
}

/** Request body for dispute endpoint. */
data class DisputeBody(val reason: String)