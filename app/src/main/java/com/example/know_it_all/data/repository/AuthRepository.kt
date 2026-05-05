package com.example.know_it_all.data.repository

import com.example.know_it_all.data.local.prefs.PreferenceManager
import com.example.know_it_all.data.model.AuthData
import com.example.know_it_all.data.remote.api.UserService
import com.example.know_it_all.data.model.dto.UserLoginRequest
import com.example.know_it_all.data.model.dto.UserRegisterRequest

/**
 * AuthRepository
 *
 * Bridge between UserService (remote) and PreferenceManager (local).
 * Orchestrates the login/register flow:
 *   1. Call UserService.login() with credentials
 *   2. Extract token and userId from response
 *   3. Store securely in PreferenceManager
 *   4. Return AuthData for the ViewModel to display
 *
 * On any network error, the token is NOT saved and login fails.
 */
class AuthRepository(
    private val userService: UserService,
    private val prefManager: PreferenceManager
) {
    /**
     * Attempts to log in with email + password.
     *
     * Success Flow:
     *   → ApiResponse.success = true
     *   → Extract token, userId, name from response.data
     *   → Save token + userId to secure storage
     *   → Set isLoggedIn = true
     *   → Return AuthData
     *
     * Failure Flow:
     *   → Network error, invalid credentials, or success=false
     *   → Do NOT modify stored credentials
     *   → Return failure with error message
     */
    suspend fun login(email: String, password: String): Result<AuthData> {
        return try {
            val response = userService.login(
                UserLoginRequest(email = email, password = password)
            )

            if (response.success && response.data != null) {
                val authData = response.data
                // ✅ Store the token and userId
                prefManager.saveAuthToken(authData.token)
                prefManager.saveUserId(authData.userId)
                prefManager.setLoggedIn(true)
                Result.success(authData)
            } else {
                Result.failure(
                    Exception(response.error ?: response.message ?: "Invalid credentials")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Attempts to register a new account.
     *
     * Success Flow:
     *   → ApiResponse.success = true
     *   → Extract token, userId, name from response.data
     *   → Save token + userId to secure storage
     *   → Set isLoggedIn = true
     *   → Return AuthData
     *
     * Failure Flow:
     *   → Network error, email already taken, or success=false
     *   → Do NOT modify stored credentials
     *   → Return failure with error message
     */
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<AuthData> {
        return try {
            val response = userService.register(
                UserRegisterRequest(name = name, email = email, password = password)
            )

            if (response.success && response.data != null) {
                val authData = response.data
                // ✅ Store the token and userId
                prefManager.saveAuthToken(authData.token)
                prefManager.saveUserId(authData.userId)
                prefManager.setLoggedIn(true)
                Result.success(authData)
            } else {
                Result.failure(
                    Exception(response.error ?: response.message ?: "Registration failed")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logs out the user by clearing all stored session data.
     * After this, the app will route to Login screen on next launch.
     */
    suspend fun logout() {
        prefManager.clearSession()
    }

    /**
     * Checks if the user has a valid token.
     * Used to determine initial navigation route (Login vs MainHub).
     */
    fun isLoggedIn(): Boolean {
        return !prefManager.getAuthToken().isNullOrEmpty()
    }
}