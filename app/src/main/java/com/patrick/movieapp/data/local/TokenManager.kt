package com.patrick.movieapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenManager(private val context: Context) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    //Guardar tokens
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    //Guardar info de usuario
    suspend fun saveUserInfo(userId: Long, email: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId.toString()
            prefs[USER_EMAIL_KEY] = email
        }
    }

    //Obtener access token
    fun getAccessToken(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[ACCESS_TOKEN_KEY]
        }
    }

    //Obtener refresh token
    fun getRefreshToken(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[REFRESH_TOKEN_KEY]
        }
    }

    //Verificar si está logueado
    fun isLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[ACCESS_TOKEN_KEY] != null
        }
    }

    //Limpiar tokens (logout)
    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}