package com.examencivique.data.repository

import android.content.Context
import com.examencivique.ui.i18n.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LanguageManager(context: Context) {

    private val prefs = context.getSharedPreferences("ec_settings", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(loadLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    val current: AppLanguage get() = _language.value

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        prefs.edit().putString("language", lang.name).apply()
    }

    private fun loadLanguage(): AppLanguage {
        val saved = prefs.getString("language", null) ?: return AppLanguage.FR
        return try {
            AppLanguage.valueOf(saved)
        } catch (_: Exception) {
            AppLanguage.FR
        }
    }
}
