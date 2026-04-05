package com.example.know_it_all.data.repository

import com.example.know_it_all.data.local.db.KnowItAllDatabase
import com.example.know_it_all.data.model.AuthData
import com.example.know_it_all.data.model.User
import com.example.know_it_all.data.model.dto.UserDTO
import com.example.know_it_all.data.model.dto.UserLoginRequest
import com.example.know_it_all.data.model.dto.UserRegisterRequest
import com.example.know_it_all.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class UserRepository(private val database: KnowItAllDatabase) {
    private val userService = RetrofitClient.createUserService()
    private val userDao = database.userDao()

    suspend fun login(email: String, password: String): Result<AuthData> {
        return MockDataSource.login(email, password)
    }

    suspend fun register(name: String, email: String, password: String): Result<AuthData> {
        return MockDataSource.register(name, email, password)
    }

    suspend fun getNearbyUsers(
        token: String,
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 5.0
    ): Result<List<UserDTO>> {
        return Result.success(MockDataSource.getNearbyUsers())
    }

    suspend fun getUserProfile(token: String): Result<UserDTO> {
        return try {
            val response = userService.getUserProfile("Bearer $token")
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to fetch profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveUserLocally(user: User) {
        userDao.insertUser(user)
    }

    fun getLocalUser(uid: String): Flow<User?> {
        return userDao.getUserByIdFlow(uid)
    }

}
