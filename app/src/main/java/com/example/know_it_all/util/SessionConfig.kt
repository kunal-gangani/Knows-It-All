package com.example.know_it_all.data.model

/**
 * Attached to a swap when it becomes ACTIVE.
 * Tracks how many sessions were agreed, how many completed,
 * and verification method per swap type.
 */
data class SessionConfig(
    val totalSessions: Int = 1,
    val durationMinutes: Int = 60,
    val completedSessions: Int = 0,
    val verificationMethod: SessionVerification = SessionVerification.NONE
) {
    val isFullyComplete: Boolean get() = completedSessions >= totalSessions
    val remainingSessions: Int get() = totalSessions - completedSessions

    /**
     * Calculate token release percentage based on average rating.
     *
     * 5★ = 100%
     * 4★ = 75%
     * 3★ = 50%
     * <3★ = 25%
     */
    fun tokenReleasePercent(rating: Float): Double = when {
        rating >= 4.5f -> 1.0   // 5 stars
        rating >= 3.5f -> 0.75  // 4 stars
        rating >= 2.5f -> 0.5   // 3 stars
        else           -> 0.25  // below 3
    }

    fun tokensToRelease(totalTokens: Long, rating: Float): Long =
        (totalTokens * tokenReleasePercent(rating)).toLong()

    fun tokensToReturn(totalTokens: Long, rating: Float): Long =
        totalTokens - tokensToRelease(totalTokens, rating)
}

enum class SessionVerification {
    NONE,
    QR_SCAN,     // Physical — QR handshake
    VIDEO_CALL,  // Online — Jitsi session
    MANUAL       // Either user marks done (fallback)
}