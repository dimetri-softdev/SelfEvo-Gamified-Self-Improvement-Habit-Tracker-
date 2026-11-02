package com.example.selfevo.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfevo.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val currentUserFlow: StateFlow<FirebaseUser?> = authRepository.currentUserFlow

    fun login(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.login(email, pass)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                onSuccess()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Login failed"
                _uiState.update { it.copy(isLoading = false, errorMessage = error) }
                onError(error)
            }
        }
    }

    fun signUp(email: String, pass: String, name: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.signUp(email, pass)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                onSuccess(name)
            } else {
                val error = result.exceptionOrNull()?.message ?: "Sign up failed"
                _uiState.update { it.copy(isLoading = false, errorMessage = error) }
                onError(error)
            }
        }
    }

    fun signInWithGoogle(context: Context, onSuccess: (String?) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.signInWithGoogle(context)
            if (result.isSuccess) {
                val displayName = result.getOrNull()?.displayName
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                onSuccess(displayName)
            } else {
                val error = result.exceptionOrNull()?.localizedMessage ?: "Google Sign-In failed"
                _uiState.update { it.copy(isLoading = false, errorMessage = error) }
                onError(error)
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState()
    }
}
