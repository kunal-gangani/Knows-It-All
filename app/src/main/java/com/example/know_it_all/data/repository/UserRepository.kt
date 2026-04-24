package com.example.know_it_all.data.repository

import com.example.know_it_all.data.local.dao.UserDao
import com.example.know_it_all.data.model.AuthData
import com.example.know_it_all.data.model.User
import com.example.know_it_all.data.model.dto.UserDTO
import com.example.know_it_all.data.model.dto.UserLoginRequest
import com.example.know_it_all.data.model.dto.UserProfileUpdateRequest
import com.example.know_it_all.data.model.dto.UserRegisterRequest
import com.example.know_it_all.data.remote.MockDataSource
import com.example.know_it_all.data.remote.api.UserService
import kotlinx.coroutines.flow.Flow

/**
 * Fixes applied:
 *
 *  1. UserService and UserDao are now INJECTED — no RetrofitClient or
 *     KnowItAllDatabase constructed inside the repository.
 *
 *  2. getUserProfile now accepts a userId parameter so the calling ViewModel
 *     can request a specific user's profile. The original had no userId param,
 *     meaning it could only ever fetch the authenticated user's own profile
 *     with no way to view another user's profile from the Radar screen.
 *
 *  3. getUserProfile now writes the API response to the local Room cache
 *     (offline-first). The original fetched and returned the DTO without ever
 *     writing to Room — the cache was wired up but permanently empty.
 *
 *  4. getNearbyUsers writes results to Room via batch insert so the Radar
 *     screen has a cached set of nearby users for offline use.
 *
 *  5. updateProfile added — was missing entirely from the original, but is
 *     required by the SkillProfileScreen edit flow.
 *
 *  6. updateLocation added — required by the Radar screen to keep the user's
 *     GPS position fresh on the backend so other users can discover them.
 *
 *  7. logout added — clears the local user cache on sign-out so the next
 *     user who logs in on the same device doesn't see stale data.
 *
 *  8. USE_MOCK flag makes the mock/real switch a one-line change.
 */
class UserRepository(
    private val userDao: UserDao,                // ✅ injected DAO
    private val userService: UserService         // ✅ injected service
) {

    private val USE_MOCK = true

    // -------------------------------------------------------------------------
    // Local reads (offline-first)
    // -------------------------------------------------------------------------

    fun getLocalUser(uid: String): Flow<User?> =
        userDao.getUserByIdFlow(uid)

    fun getTokenBalance(uid: String): Flow<Long?> =
        userDao.getTokenBalance(uid)

    suspend fun saveUserLocally(user: User) =
        userDao.insertUser(user)

    // -------------------------------------------------------------------------
    // Authentication
    // -------------------------------------------------------------------------

    suspend fun login(email: String, password: String): Result<AuthData> {
        return if (USE_MOCK) {
            MockDataSource.login(email, password)
        } else {
            try {
                val response = userService.login(UserLoginRequest(email, password))
                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.error ?: "Login failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<AuthData> {
        return if (USE_MOCK) {
            MockDataSource.register(name, email, password)
        } else {
            try {
                val response = userService.register(UserRegisterRequest(name, email, password))
                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.error ?: "Registration failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // -------------------------------------------------------------------------
    // Profile
    // -------------------------------------------------------------------------

    /**
     * Fixed: userId parameter added so any user's profile can be fetched,
     * not just the authenticated user's own profile.
     * Fixed: writes API response to Room cache for offline-first access.
     */
    suspend fun getUserProfile(token: String, userId: String): Result<UserDTO> {
        return try {
            val response = userService.getUserProfile("Bearer $token", userId) // ✅ userId param
            if (response.success && response.data != null) {
                userDao.insertUser(response.data.toEntity())        // ✅ write-through to cache
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to fetch profile"))
            }
        } catch (e: Exception) {
            // Network unavailable — caller should fall back to getLocalUser()
            Result.failure(e)
        }
    }

    suspend fun updateProfile(                                      // ✅ new — was missing
        token: String,
        userId: String,
        request: UserProfileUpdateRequest
    ): Result<UserDTO> {
        return try {
            val response = userService.updateProfile("Bearer $token", userId, request)
            if (response.success && response.data != null) {
                userDao.insertUser(response.data.toEntity())        // ✅ update cache
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to update profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // Radar — nearby users
    // -------------------------------------------------------------------------

    suspend fun getNearbyUsers(
        token: String,
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 5.0
    ): Result<List<UserDTO>> {
        return if (USE_MOCK) {
            val dtos = MockDataSource.getNearbyUsers()
            userDao.insertUsers(dtos.map { it.toEntity() })         // ✅ cache nearby users
            Result.success(dtos)
        } else {
            try {
                val response = userService.getNearbyUsers(
                    token = "Bearer $token",
                    latitude = latitude,
                    longitude = longitude,
                    radiusKm = radiusKm
                )
                if (response.success && response.data != null) {
                    userDao.insertUsers(response.data.map { it.toEntity() }) // ✅ cache
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.error ?: "Failed to load nearby users"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateLocation(                                     // ✅ new — required by Radar
        token: String,
        userId: String,
        latitude: Double,
        longitude: Double
    ): Result<Unit> {
        return try {
            val response = userService.updateLocation(
                "Bearer $token",
                userId,
                com.example.know_it_all.data.model.dto.UserLocationUpdate(latitude, longitude)
            )
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Failed to update location"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // Session cleanup
    // -------------------------------------------------------------------------

    suspend fun logout() {                                          // ✅ new — wipes cache on sign-out
        userDao.clearAllUsers()
    }
}

// -----------------------------------------------------------------------------
// DTO → Entity mapper
// -----------------------------------------------------------------------------

private fun UserDTO.toEntity(): User = User(
    uid = uid,
    name = name,
    email = email,
    profileImageUrl = profileImageUrl ?: "",
    latitude = latitude ?: 0.0,
    longitude = longitude ?: 0.0,
    skillTokenBalance = skillTokenBalance ?: 0L,
    trustScore = trustScore ?: 0f,
    profileVerified = profileVerified ?: false,
    isOnline = isOnline ?: false,
    createdAt = createdAt ?: System.currentTimeMillis(),
    updatedAt = updatedAt ?: System.currentTimeMillis()
)