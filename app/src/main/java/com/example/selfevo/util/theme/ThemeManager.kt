package com.example.selfevo.util.theme

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

object ThemeManager {
    private const val PREFS_NAME = "settings_prefs"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_AMOLED_MODE = "amoled_mode"

    private val _isDarkMode = mutableStateOf(true)
    val isDarkMode: State<Boolean> = _isDarkMode

    private val _isAmoledMode = mutableStateOf(true)
    val isAmoledMode: State<Boolean> = _isAmoledMode

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _isDarkMode.value = prefs.getBoolean(KEY_DARK_MODE, true)
        _isAmoledMode.value = prefs.getBoolean(KEY_AMOLED_MODE, true)
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        _isDarkMode.value = enabled
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun setAmoledMode(context: Context, enabled: Boolean) {
        _isAmoledMode.value = enabled
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_AMOLED_MODE, enabled).apply()
    }
}
