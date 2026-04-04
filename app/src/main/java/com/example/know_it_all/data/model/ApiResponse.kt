package com.example.know_it_all.data.model

import com.example.know_it_all.data.model.dto.UserDTO
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val error: String? = null
)

data class AuthResponse(
    val token: String,
    val userId: String,
    val user: UserDTO? = null
)

data class AuthData(
    val token: String,
    val userId: String
)
