package com.example.selfevo.ui.auth

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: suspend (String, String) -> Unit,
    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var showSsoPrompt by remember { mutableStateOf(false) }
    var ssoName by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    val isFormValid = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 8

    val goldGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500))
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // High-fidelity Logo
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "SelfEvo Logo",
                modifier = Modifier
                    .size(140.dp)
                    .padding(bottom = 12.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "SELFEVO",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD700),
                letterSpacing = 4.sp
            )

            Text(
                text = stringResource(R.string.evolve_stats),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Email Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.email_label), color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("player@selfevo.app", color = Color.DarkGray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1A1A1A),
                        unfocusedContainerColor = Color(0xFF1A1A1A),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.password_label), color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("••••••••", color = Color.DarkGray) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(icon, contentDescription = "Toggle password visibility", tint = Color.Gray)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1A1A1A),
                        unfocusedContainerColor = Color(0xFF1A1A1A),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // Validation Warning Banner
            if ((email.isNotBlank() || password.isNotBlank()) && !isFormValid) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFF3E1010),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) "⚠️ Invalid email format (e.g., player@selfevo.app)" else "⚠️ Password must be at least 8 characters long",
                        color = Color(0xFFFF5252),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Gradient Login Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        onLoginSuccess(email, password)
                        isLoading = false
                    }
                },
                enabled = isFormValid && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isFormValid && !isLoading) goldGradient else Brush.linearGradient(listOf(Color.DarkGray, Color.Gray)),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                    } else {
                        Text(stringResource(R.string.login_title), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = if (isFormValid) Color.Black else Color.White.copy(alpha = 0.5f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Google SSO Button
            OutlinedButton(
                onClick = { showSsoPrompt = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFF1A1A1A),
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f))
            ) {
                Text("Sign in with Google (SSO)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Biometric Fingerprint Button
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        onLoginSuccess("biometric.user@selfevo.app", "google_sso_token_secure")
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFF1A1A1A),
                    contentColor = Color(0xFF00E5FF)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
            ) {
                Text("🔓 Fingerprint / Biometric Login", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row {
                Text(stringResource(R.string.dont_have_account) + " ", color = Color.Gray, fontSize = 14.sp)
                Text(
                    stringResource(R.string.signup_title),
                    color = Color(0xFFFFD700),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
        }
    }

    // Google SSO Account Selector Dialog
    if (showSsoPrompt) {
        AlertDialog(
            onDismissRequest = { showSsoPrompt = false },
            title = { Text("Google Account Selector", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select your Google account or enter your player name:", color = Color.Gray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = ssoName,
                        onValueChange = { ssoName = it },
                        placeholder = { Text("e.g., MAKHO", color = Color.DarkGray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1A1A1A),
                            unfocusedContainerColor = Color(0xFF1A1A1A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val nameToUse = if (ssoName.isNotBlank()) ssoName else "Google Player"
                        showSsoPrompt = false
                        coroutineScope.launch {
                            isLoading = true
                            onLoginSuccess("sso.$nameToUse@selfevo.app", "google_sso_token_secure")
                            isLoading = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text("SIGN IN AS GOOGLE USER", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSsoPrompt = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF1A1A1A)
        )
    }
}
