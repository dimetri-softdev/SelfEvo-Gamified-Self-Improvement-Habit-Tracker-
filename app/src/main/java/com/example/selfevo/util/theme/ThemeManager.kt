package com.example.selfevo.util.theme

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

object ThemeManager {
    private const val PREFS_NAME = "settings_prefs"
    private const val KEY_AMOLED_MODE = "amoled_mode"

    private val _isAmoledMode = mutableStateOf(false)
    val isAmoledMode: State<Boolean> = _isAmoledMode

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _isAmoledMode.value = prefs.getBoolean(KEY_AMOLED_MODE, true) // Default to true as per design
    }

    fun setAmoledMode(context: Context, enabled: Boolean) {
        _isAmoledMode.value = enabled
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_AMOLED_MODE, enabled).apply()
    }
}
