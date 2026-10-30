package com.example.selfevo.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

/**
 * AuthRepository manages user session and identity using Firebase Authentication.
 * It provides reactive streams for login state and handling credential persistence.
 */
class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val TAG = "SelfEvo_AuthRepo"

    private val _currentUser = MutableStateFlow(firebaseAuth.currentUser)
    val currentUserFlow: StateFlow<FirebaseUser?> = _currentUser

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _currentUser.value = auth.currentUser
    }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    suspend fun login(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw IllegalStateException("Email sign-in returned no user")
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Email sign-in failed: ${e.javaClass.simpleName}")
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser?> {
        return try {
            val clientIdResource = context.resources.getIdentifier(
                "default_web_client_id",
                "string",
                context.packageName
            )
            if (clientIdResource == 0) {
                return Result.failure(IllegalStateException("Google sign-in is not configured for this Firebase project"))
            }
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(clientIdResource))
                .setAutoSelectEnabled(false)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val credential = CredentialManager.create(context)
                .getCredential(context, request)
                .credential
            if (credential !is CustomCredential
                || credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                return Result.failure(IllegalStateException("Google did not return a valid sign-in credential"))
            }

            val googleToken = GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCredential = GoogleAuthProvider.getCredential(googleToken.idToken, null)
            val user = firebaseAuth.signInWithCredential(firebaseCredential).await().user
                ?: return Result.failure(IllegalStateException("Google sign-in returned no user"))
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Google sign-in failed: ${e.javaClass.simpleName}")
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw IllegalStateException("Account creation returned no user")
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Account registration failed: ${e.javaClass.simpleName}")
            Result.failure(e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        _currentUser.value = null
    }
}
