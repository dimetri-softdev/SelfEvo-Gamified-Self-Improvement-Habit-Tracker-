package com.example.selfevo.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "selfevo_session_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_EMAIL = "user_email"
    }

    fun saveSession(token: String, email: String) {
        prefs.edit().apply {
            putString(KEY_AUTH_TOKEN, token)
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    fun clearSession() {
        prefs.edit().apply {
            remove(KEY_AUTH_TOKEN)
            remove(KEY_USER_EMAIL)
            apply()
        }
    }
}
