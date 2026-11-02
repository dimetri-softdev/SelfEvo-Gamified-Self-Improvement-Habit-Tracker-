package com.example.selfevo.ui.auth

import com.google.firebase.auth.FirebaseUser

data class AuthUiState(
    val currentUser: FirebaseUser? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
