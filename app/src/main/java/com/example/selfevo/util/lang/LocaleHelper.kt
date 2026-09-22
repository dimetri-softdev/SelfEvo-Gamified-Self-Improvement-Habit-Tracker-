package com.example.selfevo.util.lang

import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

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
        return prefs.getString("selected_lang", "en") ?: "en"
    }

    fun applyLocale(context: Context) {
        val languageCode = getLocale(context)
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
