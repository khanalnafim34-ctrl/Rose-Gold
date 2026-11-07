package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class UserPreferencesRepository(private val context: Context) {

  private object PreferencesKeys {
    val DARK_MODE = stringPreferencesKey("dark_mode") // "SYSTEM", "LIGHT", "DARK"
    val HIDE_BALANCE = booleanPreferencesKey("hide_balance")
    val IS_BENGALI = booleanPreferencesKey("is_bengali")
    val APP_PIN = stringPreferencesKey("app_pin")
    val IS_DEMO_LOADED = booleanPreferencesKey("is_demo_loaded")
  }

  val darkModeFlow: Flow<String> = context.dataStore.data.map { preferences ->
    preferences[PreferencesKeys.DARK_MODE] ?: "SYSTEM"
  }

  val hideBalanceFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
    preferences[PreferencesKeys.HIDE_BALANCE] ?: false
  }

  val isBengaliFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
    preferences[PreferencesKeys.IS_BENGALI] ?: true
  }

  val appPinFlow: Flow<String?> = context.dataStore.data.map { preferences ->
    preferences[PreferencesKeys.APP_PIN]
  }

  val isDemoLoadedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
    preferences[PreferencesKeys.IS_DEMO_LOADED] ?: false
  }

  suspend fun setDarkMode(mode: String) {
    context.dataStore.edit { preferences ->
      preferences[PreferencesKeys.DARK_MODE] = mode
    }
  }

  suspend fun setHideBalance(hide: Boolean) {
    context.dataStore.edit { preferences ->
      preferences[PreferencesKeys.HIDE_BALANCE] = hide
    }
  }

  suspend fun setIsBengali(isBengali: Boolean) {
    context.dataStore.edit { preferences ->
      preferences[PreferencesKeys.IS_BENGALI] = isBengali
    }
  }

  suspend fun setAppPin(pin: String?) {
    context.dataStore.edit { preferences ->
      if (pin.isNullOrEmpty()) {
        preferences.remove(PreferencesKeys.APP_PIN)
      } else {
        preferences[PreferencesKeys.APP_PIN] = pin
      }
    }
  }

  suspend fun setDemoLoaded(loaded: Boolean) {
    context.dataStore.edit { preferences ->
      preferences[PreferencesKeys.IS_DEMO_LOADED] = loaded
    }
  }
}
