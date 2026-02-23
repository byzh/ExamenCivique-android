package com.examencivique.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.examencivique.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    data class Success(val user: FirebaseUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(context)

    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    val isLoggedIn: Boolean get() = auth.currentUser != null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuthResult.Error("Unknown error")
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e))
        }
    }

    suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuthResult.Error("Unknown error")
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e))
        }
    }

    suspend fun signInWithGoogle(activityContext: Context): AuthResult {
        return try {
            val webClientId = context.getString(R.string.default_web_client_id)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialResponse = credentialManager.getCredential(activityContext, request)
            val credential = credentialResponse.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val result = auth.signInWithCredential(firebaseCredential).await()
                val user = result.user ?: return AuthResult.Error("Unknown error")
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Unexpected credential type")
            }
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e))
        }
    }

    suspend fun resetPassword(email: String): AuthResult {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success(auth.currentUser ?: return AuthResult.Error("Email sent"))
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e))
        }
    }

    fun signOut() {
        auth.signOut()
    }

    private fun mapFirebaseError(e: Exception): String {
        val msg = e.message ?: return "Unknown error"
        return when {
            "email address is badly formatted" in msg -> "INVALID_EMAIL"
            "password is invalid" in msg || "INVALID_LOGIN_CREDENTIALS" in msg -> "WRONG_PASSWORD"
            "no user record" in msg || "USER_NOT_FOUND" in msg -> "USER_NOT_FOUND"
            "email address is already in use" in msg -> "EMAIL_ALREADY_IN_USE"
            "password should be at least 6 characters" in msg -> "WEAK_PASSWORD"
            "network error" in msg.lowercase() -> "NETWORK_ERROR"
            "blocked" in msg.lowercase() -> "TOO_MANY_REQUESTS"
            "cancelled" in msg.lowercase() || "canceled" in msg.lowercase() -> "CANCELLED"
            else -> msg
        }
    }
}
