package com.example.know_it_all.data.model

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val error: String? = null
)

data class AuthResponse(
    val token: String,
    val user: Map<String, Any>
)
