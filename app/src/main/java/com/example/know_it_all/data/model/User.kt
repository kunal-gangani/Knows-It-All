package com.example.know_it_all.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val uid: String,
    val name: String,
    val email: String,
    val profileImageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val skillTokenBalance: Long = 0L,
    val trustScore: Float = 0f,
    val profileVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
