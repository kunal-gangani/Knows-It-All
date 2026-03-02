package com.example.know_it_all.util

import android.content.SharedPreferences

class TokenManager(private val sharedPreferences: SharedPreferences) {
    companion object {
        private const val TOKEN_KEY = "auth_token"
        private const val USER_ID_KEY = "user_id"
        private const val EXPIRY_TIME_KEY = "token_expiry"
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().apply {
            putString(TOKEN_KEY, token)
            putLong(EXPIRY_TIME_KEY, System.currentTimeMillis() + 24 * 60 * 60 * 1000) // 24 hours
            apply()
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(USER_ID_KEY, userId).apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(USER_ID_KEY, null)
    }

    fun isTokenValid(): Boolean {
        val expiryTime = sharedPreferences.getLong(EXPIRY_TIME_KEY, 0)
        return System.currentTimeMillis() < expiryTime
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
