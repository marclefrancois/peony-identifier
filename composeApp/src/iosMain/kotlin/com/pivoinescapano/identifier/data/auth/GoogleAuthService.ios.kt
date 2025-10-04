package com.pivoinescapano.identifier.data.auth

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

actual class GoogleAuthService {
    private val json = Json { ignoreUnknownKeys = true }
    private val userDefaults = NSUserDefaults.standardUserDefaults
    private var cachedUser: GoogleUser? = null

    companion object {
        private const val USER_DATA_KEY = "google_user_data"
    }

    init {
        // Load cached user from UserDefaults on initialization
        cachedUser = loadUserFromStorage()
    }

    actual suspend fun saveUser(user: GoogleUser) {
        cachedUser = user

        // Persist to UserDefaults (encrypted at OS level on iOS)
        try {
            val userJson = json.encodeToString(user)
            userDefaults.setObject(userJson, USER_DATA_KEY)
            userDefaults.synchronize()
        } catch (e: Exception) {
            // Log error but don't crash
            e.printStackTrace()
        }
    }

    actual suspend fun signOut() {
        cachedUser = null

        // Clear from UserDefaults
        userDefaults.removeObjectForKey(USER_DATA_KEY)
        userDefaults.synchronize()
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
            val userJson = userDefaults.stringForKey(USER_DATA_KEY)
            userJson?.let { json.decodeFromString<GoogleUser>(it) }
        } catch (e: Exception) {
            // Log error but return null
            e.printStackTrace()
            null
        }
    }
}
