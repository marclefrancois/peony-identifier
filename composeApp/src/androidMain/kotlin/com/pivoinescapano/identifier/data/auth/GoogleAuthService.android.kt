package com.pivoinescapano.identifier.data.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual class GoogleAuthService(
    private val context: Context,
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val json = Json { ignoreUnknownKeys = true }
    private var cachedUser: GoogleUser? = null

    companion object {
        private const val KEY_USER_DATA = "google_user_data"
    }

    init {
        // Load cached user from secure storage on initialization
        cachedUser = loadUserFromStorage()
    }

    actual suspend fun saveUser(user: GoogleUser) {
        cachedUser = user

        // Persist to secure storage
        try {
            val userJson = json.encodeToString(user)
            sharedPreferences.edit().putString(KEY_USER_DATA, userJson).apply()
        } catch (e: Exception) {
            // Log error but don't crash
            e.printStackTrace()
        }
    }

    actual suspend fun signOut() {
        cachedUser = null

        // Clear from secure storage
        sharedPreferences.edit().remove(KEY_USER_DATA).apply()
    }

    actual suspend fun getAccessToken(): String? {
        return getCurrentUser()?.accessToken
    }

    actual suspend fun getCurrentUser(): GoogleUser? {
        // Return cached user or try to load from storage
        return cachedUser ?: loadUserFromStorage()?.also {
            cachedUser = it
        }
    }

    private fun loadUserFromStorage(): GoogleUser? {
        return try {
            val userJson = sharedPreferences.getString(KEY_USER_DATA, null)
            userJson?.let { json.decodeFromString<GoogleUser>(it) }
        } catch (e: Exception) {
            // Log error but return null
            e.printStackTrace()
            null
        }
    }
}
