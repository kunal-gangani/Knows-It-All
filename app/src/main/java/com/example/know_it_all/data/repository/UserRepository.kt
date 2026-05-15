package com.example.know_it_all.data.repository

import com.example.know_it_all.data.local.dao.UserDao
import com.example.know_it_all.data.model.AuthData
import com.example.know_it_all.data.model.User
import com.example.know_it_all.data.model.dto.UserDTO
import com.example.know_it_all.data.model.dto.UserLocationUpdate
import com.example.know_it_all.data.model.dto.UserLoginRequest
import com.example.know_it_all.data.model.dto.UserProfileUpdateRequest
import com.example.know_it_all.data.model.dto.UserRegisterRequest
import com.example.know_it_all.data.remote.MockDataSource
import com.example.know_it_all.data.remote.api.UserService
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao,
    private val userService: UserService
) {

    private val USE_MOCK = true

    fun getLocalUser(uid: String): Flow<User?> =
        userDao.getUserByIdFlow(uid)

    fun getTokenBalance(uid: String): Flow<Long?> =
        userDao.getTokenBalance(uid)

    suspend fun saveUserLocally(user: User) =
        userDao.insertUser(user)

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

    suspend fun getUserProfile(token: String, userId: String): Result<UserDTO> {
        return try {
            val response = userService.getUserProfile("Bearer $token", userId)
            if (response.success && response.data != null) {
                userDao.insertUser(response.data.toEntity())
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to fetch profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(
        token: String,
        userId: String,
        request: UserProfileUpdateRequest
    ): Result<UserDTO> {
        return try {
            val response = userService.updateProfile("Bearer $token", userId, request)
            if (response.success && response.data != null) {
                userDao.insertUser(response.data.toEntity())
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to update profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNearbyUsers(
        token: String,
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 5.0
    ): Result<List<UserDTO>> {
        return if (USE_MOCK) {
            val dtos = MockDataSource.getNearbyUsers()
            userDao.insertUsers(dtos.map { it.toEntity() })
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
                    userDao.insertUsers(response.data.map { it.toEntity() })
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.error ?: "Failed to load nearby users"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateLocation(
        token: String,
        userId: String,
        latitude: Double,
        longitude: Double
    ): Result<Unit> {
        return try {
            val response = userService.updateLocation(
                "Bearer $token",
                userId,
                UserLocationUpdate(latitude, longitude)
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

    suspend fun logout() {
        userDao.clearAllUsers()
    }
}

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
