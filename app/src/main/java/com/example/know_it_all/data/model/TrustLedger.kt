package com.example.know_it_all.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trust_ledger")
data class TrustLedger(
    @PrimaryKey
    val transactionId: String,
    val swapId: String,
    val userId: String,
    val ratingGiven: Float = 0f,
    val previousHash: String = "",
    val currentHash: String = "",
    val transactionData: String = "", // JSON serialized transaction details
    val ratingComment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
