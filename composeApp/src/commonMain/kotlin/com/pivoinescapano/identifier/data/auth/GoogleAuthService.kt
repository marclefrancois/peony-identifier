package com.pivoinescapano.identifier.data.auth

expect class GoogleAuthService {
    suspend fun saveUser(user: GoogleUser)

    suspend fun signOut()

    suspend fun getAccessToken(): String?

    suspend fun getCurrentUser(): GoogleUser?
}
