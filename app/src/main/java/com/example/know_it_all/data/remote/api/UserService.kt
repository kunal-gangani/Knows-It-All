package com.example.know_it_all.data.remote.api

import com.example.know_it_all.data.model.ApiResponse
import com.example.know_it_all.data.model.AuthResponse
import com.example.know_it_all.data.model.dto.UserDTO
import com.example.know_it_all.data.model.dto.UserLoginRequest
import com.example.know_it_all.data.model.dto.UserRegisterRequest
import com.example.know_it_all.data.model.dto.UserLocationUpdate
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Header

interface UserService {
    @POST("auth/register")
    suspend fun register(@Body request: UserRegisterRequest): ApiResponse<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: UserLoginRequest): ApiResponse<AuthResponse>

    @GET("users/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): ApiResponse<UserDTO>

    @PUT("users/profile")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body user: UserDTO
    ): ApiResponse<UserDTO>

    @PUT("users/location")
    suspend fun updateUserLocation(
        @Header("Authorization") token: String,
        @Body location: UserLocationUpdate
    ): ApiResponse<UserDTO>

    @GET("users/nearby")
    suspend fun getNearbyUsers(
        @Header("Authorization") token: String,
        @Query("lat") latitude: Double,
        @Query("long") longitude: Double,
        @Query("radiusKm") radiusKm: Double = 5.0
    ): ApiResponse<List<UserDTO>>
}
