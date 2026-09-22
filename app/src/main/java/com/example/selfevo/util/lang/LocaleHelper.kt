package com.example.selfevo.util.lang

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleHelper {

    fun setLocale(context: Context, languageCode: String) {
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)

        // Save to preferences for manual handling if needed
        val prefs = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("selected_lang", languageCode).apply()
    }

    fun getLocale(context: Context): String {
        val prefs = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        if (!prefs.contains("selected_lang")) {
            // First time user detection: Force English
            prefs.edit().putString("selected_lang", "en").apply()
            return "en"
        }
        return prefs.getString("selected_lang", "en") ?: "en"
    }

    fun applyLocale(context: Context) {
        val languageCode = getLocale(context)
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
