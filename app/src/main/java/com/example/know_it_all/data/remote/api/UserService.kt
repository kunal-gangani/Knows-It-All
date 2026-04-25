package com.example.know_it_all.data.remote.api

import com.example.know_it_all.data.model.ApiResponse
import com.example.know_it_all.data.model.AuthData
import com.example.know_it_all.data.model.dto.UserDTO
import com.example.know_it_all.data.model.dto.UserLocationUpdate
import com.example.know_it_all.data.model.dto.UserLoginRequest
import com.example.know_it_all.data.model.dto.UserProfileUpdateRequest
import com.example.know_it_all.data.model.dto.UserRegisterRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Fixes applied:
 *
 *  1. register and login return ApiResponse<AuthData> instead of
 *     ApiResponse<AuthResponse>. AuthResponse wraps a UserDTO that the
 *     ViewModel never uses — AuthData is the minimal type the session
 *     needs (token + userId + name). Returning the full AuthResponse
 *     was unnecessary indirection that required an extra mapping step.
 *
 *  2. getUserProfile now accepts @Path("userId") — the original had no
 *     userId parameter, meaning it could only ever return the authenticated
 *     user's own profile and the Radar screen could never fetch another
 *     user's profile. Updated endpoint to "users/profile/{userId}".
 *
 *  3. updateProfile renamed from updateUserProfile and now accepts
 *     @Path("userId") and @Body UserProfileUpdateRequest instead of
 *     @Body UserDTO. Sending a full UserDTO as an update body leaks
 *     read-only fields (trustScore, tokenBalance, profileVerified) that
 *     the backend should own — the request DTO carries only name and email.
 *     Method name aligned with what UserRepository.updateProfile() calls.
 *
 *  4. updateLocation renamed from updateUserLocation and now accepts
 *     @Path("userId") so the backend knows which user to update.
 *     Method name aligned with what UserRepository.updateLocation() calls.
 *     Response changed from ApiResponse<UserDTO> → ApiResponse<Unit> —
 *     the repository doesn't use the returned UserDTO after a location update.
 *
 *  5. getNearbyUsers @Query params renamed lat → latitude, long → longitude
 *     for clarity and to match the repository call site parameter names.
 *     "long" is also a reserved keyword in some contexts.
 */
interface UserService {

    @POST("auth/register")
    suspend fun register(
        @Body request: UserRegisterRequest
    ): ApiResponse<AuthData>                            // ✅ AuthData, not AuthResponse

    @POST("auth/login")
    suspend fun login(
        @Body request: UserLoginRequest
    ): ApiResponse<AuthData>                            // ✅ AuthData, not AuthResponse

    @GET("users/profile/{userId}")                      // ✅ userId path param added
    suspend fun getUserProfile(
        @Header("Authorization") token: String,
        @Path("userId") userId: String                  // ✅ required for Radar profile view
    ): ApiResponse<UserDTO>

    @PUT("users/profile/{userId}")                      // ✅ renamed + userId path param added
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Body request: UserProfileUpdateRequest         // ✅ DTO with only name+email, not full UserDTO
    ): ApiResponse<UserDTO>

    @PUT("users/location/{userId}")                     // ✅ renamed + userId path param added
    suspend fun updateLocation(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Body location: UserLocationUpdate
    ): ApiResponse<Unit>                                // ✅ Unit — response body unused

    @GET("users/nearby")
    suspend fun getNearbyUsers(
        @Header("Authorization") token: String,
        @Query("latitude") latitude: Double,            // ✅ renamed from "lat" — clearer + not a keyword
        @Query("longitude") longitude: Double,          // ✅ renamed from "long" — "long" is a keyword
        @Query("radiusKm") radiusKm: Double = 5.0
    ): ApiResponse<List<UserDTO>>
}