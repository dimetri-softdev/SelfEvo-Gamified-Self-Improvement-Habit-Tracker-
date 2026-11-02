package com.example.selfevo.domain.repository

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUserFlow: StateFlow<FirebaseUser?>
    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Result<FirebaseUser?>
    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser?>
    suspend fun signUp(email: String, password: String): Result<FirebaseUser?>
    fun signOut()
}
