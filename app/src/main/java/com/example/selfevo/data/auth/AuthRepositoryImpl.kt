package com.example.selfevo.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import com.example.selfevo.BuildConfig
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.example.selfevo.domain.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    private val TAG = "SelfEvo_AuthRepo"
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableStateFlow(firebaseAuth.currentUser)
    override val currentUserFlow: StateFlow<FirebaseUser?> = _currentUser

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _currentUser.value = auth.currentUser
    }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun login(email: String, password: String): Result<FirebaseUser?> {
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

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Password reset email request failed: ${e.javaClass.simpleName}")
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(context: Context): Result<FirebaseUser?> {
        return try {
            val clientIdResource = context.resources.getIdentifier(
                "default_web_client_id",
                "string",
                context.packageName
            )
            val generatedClientId = clientIdResource
                .takeIf { it != 0 }
                ?.let(context::getString)
            val serverClientId = generatedClientId
                ?.takeIf { it.isNotBlank() }
                ?: BuildConfig.GOOGLE_WEB_CLIENT_ID.takeIf { it.isNotBlank() }
            if (serverClientId == null) {
                return Result.failure(IllegalStateException("Google sign-in is not configured for this Firebase project"))
            }
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val credentialManager = CredentialManager.create(context)
            val credential = try {
                credentialManager.getCredential(context, request).credential
            } catch (_: NoCredentialException) {
                // Offer the explicit Google button flow when there is no eligible saved credential.
                val signInOption = GetSignInWithGoogleOption.Builder(serverClientId).build()
                val signInRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(signInOption)
                    .build()
                credentialManager.getCredential(context, signInRequest).credential
            }
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

    override suspend fun signUp(email: String, password: String): Result<FirebaseUser?> {
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

    override fun signOut() {
        firebaseAuth.signOut()
        _currentUser.value = null
    }
}
