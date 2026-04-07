package com.example.know_it_all.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "KnowItAllPrefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_LOCATION_PERMISSION_ASKED = "location_permission_asked"
        private const val KEY_LOCATION_PERMISSION_ENABLED = "location_permission_enabled"
    }

    /**
     * Save JWT token from login/register response
     */
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    /**
     * Retrieve JWT token (used in API Authorization headers)
     */
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    /**
     * Save user ID and basic info (called after successful auth)
     */
    fun saveUserInfo(userId: String, name: String, email: String) {
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
        }.apply()
    }

    /**
     * Retrieve saved user ID
     */
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    /**
     * Retrieve saved user email
     */
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    /**
     * Retrieve saved user name
     */
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    /**
     * Clear all session data (called on logout)
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }

    /**
     * Check if user is currently logged in
     * Returns true only if both token AND userId are present
     */
    fun isLoggedIn(): Boolean {
        return getToken() != null && getUserId() != null
    }

    /**
     * Mark that location permission has been asked
     */
    fun setLocationPermissionAsked() {
        prefs.edit().putBoolean(KEY_LOCATION_PERMISSION_ASKED, true).apply()
    }

    /**
     * Check if location permission has already been asked
     */
    fun hasLocationPermissionBeenAsked(): Boolean {
        return prefs.getBoolean(KEY_LOCATION_PERMISSION_ASKED, false)
    }

    /**
     * Save location permission preference
     */
    fun setLocationPermissionEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LOCATION_PERMISSION_ENABLED, enabled).apply()
    }

    /**
     * Get location permission preference (default: false if not asked)
     */
    fun isLocationPermissionEnabled(): Boolean {
        return prefs.getBoolean(KEY_LOCATION_PERMISSION_ENABLED, false)
    }
}