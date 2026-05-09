package com.example.know_it_all.data.repository

import com.example.know_it_all.data.model.SwapStatus
import com.example.know_it_all.data.model.SwapType
import com.example.know_it_all.data.model.VerificationMethod
import com.example.know_it_all.data.model.dto.SwapDTO
import com.example.know_it_all.data.model.dto.SwapRequestBody
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseSwapRepository {

    private val db = FirebaseFirestore.getInstance()
    private val swapsCollection = db.collection("swaps")
    private val usersCollection = db.collection("users")

    // ── Real-time observation ─────────────────────────────────────────────────

    fun observeActiveSwaps(userId: String): Flow<List<SwapDTO>> = callbackFlow {
        val listener = swapsCollection
            .whereIn("status", listOf("REQUESTED", "ACTIVE"))
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val swaps = snapshot?.documents
                    ?.mapNotNull { it.toSwapDTO() }
                    ?.filter { it.mentorId == userId || it.learnerId == userId }
                    ?.sortedByDescending { it.createdAt }
                    ?: emptyList()
                trySend(swaps)
            }
        awaitClose { listener.remove() }
    }

    fun observePendingCount(userId: String): Flow<Int> = callbackFlow {
        val listener = swapsCollection
            .whereEqualTo("learnerId", userId)
            .whereEqualTo("status", "REQUESTED")
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.size() ?: 0)
            }
        awaitClose { listener.remove() }
    }

    // ── One-shot reads ────────────────────────────────────────────────────────

    suspend fun getActiveSwaps(userId: String): Result<List<SwapDTO>> {
        return try {
            val snapshot = swapsCollection
                .whereIn("status", listOf("REQUESTED", "ACTIVE"))
                .get().await()
            val swaps = snapshot.documents
                .mapNotNull { it.toSwapDTO() }
                .filter { it.mentorId == userId || it.learnerId == userId }
                .sortedByDescending { it.createdAt }
            Result.success(swaps)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSwapHistory(
        userId: String,
        limit: Int = 10
    ): Result<List<SwapDTO>> {
        return try {
            val snapshot = swapsCollection
                .whereEqualTo("status", "COMPLETED")
                .limit(limit.toLong())
                .get().await()
            val swaps = snapshot.documents
                .mapNotNull { it.toSwapDTO() }
                .filter { it.mentorId == userId || it.learnerId == userId }
                .sortedByDescending { it.createdAt }
            Result.success(swaps)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSwapById(swapId: String): Result<SwapDTO> {
        return try {
            val snapshot = swapsCollection.document(swapId).get().await()
            val swap = snapshot.toSwapDTO() ?: throw Exception("Swap not found")
            Result.success(swap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Mutations ─────────────────────────────────────────────────────────────

    suspend fun requestSwap(request: SwapRequestBody): Result<SwapDTO> {
        return try {
            // Fetch mentor name for display
            val mentorDoc  = usersCollection.document(request.mentorId).get().await()
            val learnerDoc = usersCollection.document(request.learnerId).get().await()
            val mentorName  = mentorDoc.getString("name") ?: ""
            val learnerName = learnerDoc.getString("name") ?: ""

            val swapId = UUID.randomUUID().toString()
            val now    = System.currentTimeMillis()

            val swapMap = mapOf(
                "swapId"             to swapId,
                "mentorId"           to request.mentorId,
                "learnerId"          to request.learnerId,
                "mentorName"         to mentorName,
                "learnerName"        to learnerName,
                "mentorSkillId"      to request.mentorSkillId,
                "learnerSkillId"     to request.learnerSkillId,
                "swapType"           to (request.swapType?.name ?: "TOKEN"),
                "tokenAmount"        to (request.tokenAmount ?: 0L),
                "status"             to "REQUESTED",
                "verificationMethod" to "NONE",
                "sessionStartTime"   to null,
                "sessionEndTime"     to null,
                "createdAt"          to now,
                "updatedAt"          to now,
                "skillName"          to ""   // filled when skill is fetched
            )

            swapsCollection.document(swapId).set(swapMap).await()

            val dto = SwapDTO(
                swapId             = swapId,
                mentorId           = request.mentorId,
                learnerId          = request.learnerId,
                mentorName         = mentorName,
                learnerName        = learnerName,
                skillName          = "",
                mentorSkillId      = request.mentorSkillId,
                learnerSkillId     = request.learnerSkillId,
                swapType           = request.swapType ?: SwapType.TOKEN,
                tokenAmount        = request.tokenAmount ?: 0L,
                status             = SwapStatus.REQUESTED,
                verificationMethod = VerificationMethod.NONE,
                createdAt          = now
            )
            Result.success(dto)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun acceptSwap(swapId: String): Result<SwapDTO> {
        return try {
            swapsCollection.document(swapId).update(
                mapOf(
                    "status"    to "ACTIVE",
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            getSwapById(swapId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeSwap(swapId: String): Result<SwapDTO> {
        return try {
            val swapDoc = swapsCollection.document(swapId).get().await()
            val swap    = swapDoc.toSwapDTO() ?: throw Exception("Swap not found")

            swapsCollection.document(swapId).update(
                mapOf(
                    "status"    to "COMPLETED",
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()

            // Transfer tokens if TOKEN or HYBRID swap
            if (swap.tokenAmount > 0) {
                db.runTransaction { transaction ->
                    val mentorRef  = usersCollection.document(swap.mentorId)
                    val learnerRef = usersCollection.document(swap.learnerId)
                    val mentorSnap  = transaction.get(mentorRef)
                    val learnerSnap = transaction.get(learnerRef)

                    val mentorBal  = mentorSnap.getLong("skillTokenBalance") ?: 0L
                    val learnerBal = learnerSnap.getLong("skillTokenBalance") ?: 0L

                    if (learnerBal >= swap.tokenAmount) {
                        transaction.update(learnerRef, "skillTokenBalance",
                            learnerBal - swap.tokenAmount)
                        transaction.update(mentorRef, "skillTokenBalance",
                            mentorBal + swap.tokenAmount)
                    }
                }.await()
            }

            getSwapById(swapId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelSwap(swapId: String): Result<Unit> {
        return try {
            swapsCollection.document(swapId).update(
                mapOf(
                    "status"    to "CANCELLED",
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rateSwap(
        swapId: String,
        rating: Int,
        comment: String = ""
    ): Result<Unit> {
        return try {
            swapsCollection.document(swapId).update(
                mapOf(
                    "rating"    to rating,
                    "comment"   to comment,
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// ── Extension ─────────────────────────────────────────────────────────────────

private fun com.google.firebase.firestore.DocumentSnapshot.toSwapDTO(): SwapDTO? {
    return try {
        SwapDTO(
            swapId             = getString("swapId") ?: id,
            mentorId           = getString("mentorId") ?: "",
            learnerId          = getString("learnerId") ?: "",
            mentorName         = getString("mentorName") ?: "",
            learnerName        = getString("learnerName") ?: "",
            skillName          = getString("skillName") ?: "",
            mentorSkillId      = getString("mentorSkillId") ?: "",
            learnerSkillId     = getString("learnerSkillId"),
            swapType           = runCatching {
                SwapType.valueOf(getString("swapType") ?: "TOKEN")
            }.getOrDefault(SwapType.TOKEN),
            tokenAmount        = getLong("tokenAmount") ?: 0L,
            status             = runCatching {
                SwapStatus.valueOf(getString("status") ?: "REQUESTED")
            }.getOrDefault(SwapStatus.REQUESTED),
            verificationMethod = runCatching {
                VerificationMethod.valueOf(getString("verificationMethod") ?: "NONE")
            }.getOrDefault(VerificationMethod.NONE),
            sessionStartTime   = getLong("sessionStartTime"),
            sessionEndTime     = getLong("sessionEndTime"),
            createdAt          = getLong("createdAt")
        )
    } catch (e: Exception) { null }
}