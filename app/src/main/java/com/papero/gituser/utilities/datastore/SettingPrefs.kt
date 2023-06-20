package com.papero.gituser.utilities.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SettingPrefs private constructor(private val dataStore: DataStore<Preferences>) {

    private val THEME_KEY = booleanPreferencesKey("theme_setting")

    fun getThemeSetting(): Flow<Boolean>{
        return dataStore.data.map { preferences ->
                preferences[THEME_KEY] ?: false
            }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean){
        dataStore.edit { prefs ->
            prefs[THEME_KEY] = isDarkModeActive
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: SettingPrefs? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPrefs{
            return INSTANCE ?: synchronized(this){
                val instance = SettingPrefs(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}