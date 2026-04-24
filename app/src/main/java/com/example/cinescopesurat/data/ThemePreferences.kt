package com.example.cinescopesurat.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

class ThemePreferences(private val dataStore: DataStore<Preferences>) {
    private val THEME_KEY = stringPreferencesKey("app_theme")

    val themeFlow: Flow<AppTheme> = dataStore.data
        .map { preferences ->
            val themeName = preferences[THEME_KEY] ?: AppTheme.SYSTEM.name
            try {
                AppTheme.valueOf(themeName)
            } catch (e: Exception) {
                AppTheme.SYSTEM
            }
        }

    suspend fun saveTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
}
