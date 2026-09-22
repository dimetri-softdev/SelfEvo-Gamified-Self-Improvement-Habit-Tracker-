package com.example.selfevo.data.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
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

    // Reactive flow to observe authentication changes across the app
    private val _currentUser = MutableStateFlow(firebaseAuth.currentUser)
    val currentUserFlow: StateFlow<FirebaseUser?> = _currentUser

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    /**
     * Authenticates existing users via Firebase.
     */
    suspend fun login(email: String, password: String): Result<FirebaseUser?> {
        Log.d(TAG, "Attempting login for: $email")
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            _currentUser.value = result.user
            Log.i(TAG, "Login Successful: UID ${result.user?.uid}")
            Result.success(result.user)
        } catch (e: Exception) {
            Log.e(TAG, "Login Failure: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Creates a new player account in Firebase.
     */
    suspend fun signUp(email: String, password: String): Result<FirebaseUser?> {
        Log.d(TAG, "Registering new account: $email")
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            _currentUser.value = result.user
            Log.i(TAG, "Account Created Successfully: UID ${result.user?.uid}")
            Result.success(result.user)
        } catch (e: Exception) {
            Log.e(TAG, "Registration Error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Ends the user session and clears local reactive state.
     */
    fun signOut() {
        Log.d(TAG, "User signing out.")
        firebaseAuth.signOut()
        _currentUser.value = null
    }
}
