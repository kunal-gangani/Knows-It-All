package com.example.know_it_all.data.model.dto

data class UserDTO(
    val uid: String,
    val name: String,
    val email: String,
    val profileImageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val skillTokenBalance: Long = 0L,
    val trustScore: Float = 0f,
    val profileVerified: Boolean = false
)

data class UserRegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class UserLoginRequest(
    val email: String,
    val password: String
)

data class UserLocationUpdate(
    val latitude: Double,
    val longitude: Double
)
