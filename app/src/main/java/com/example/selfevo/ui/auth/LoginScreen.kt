package com.example.selfevo.ui.auth

import android.util.Patterns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val isFormValid = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 6

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
                    .size(160.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "SELFEVO",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD700),
                letterSpacing = 4.sp
            )

            Text(
                text = "EVOLVE YOUR DAILY STATS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Login/Signup Tab switcher (Simplified UI version)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFF1A1A1A), RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFFFFD700), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("LOGIN", color = Color.Black, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSignUpClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("SIGN UP", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Email Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("EMAIL", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = if (Patterns.EMAIL_ADDRESS.matcher(it).matches()) null else "Invalid email format"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("player@selfevo.app", color = Color.DarkGray) },
                    isError = emailError != null,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1A1A1A),
                        unfocusedContainerColor = Color(0xFF1A1A1A),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        errorContainerColor = Color(0xFF1A1A1A)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                if (emailError != null) {
                    Text(emailError!!, color = MaterialTheme.colorScheme.error, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Password Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("PASSWORD", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = if (it.length >= 6) null else "Password must be at least 6 characters"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("••••••••", color = Color.DarkGray) },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordError != null,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1A1A1A),
                        unfocusedContainerColor = Color(0xFF1A1A1A),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        errorContainerColor = Color(0xFF1A1A1A)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                if (passwordError != null) {
                    Text(passwordError!!, color = MaterialTheme.colorScheme.error, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Gradient Login Button
            Button(
                onClick = onLoginSuccess,
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(
                        if (isFormValid) goldGradient else Brush.linearGradient(listOf(Color.DarkGray, Color.Gray)),
                        RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("LOGIN", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = if (isFormValid) Color.Black else Color.White.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "or",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Google SSO
            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, Color.DarkGray)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Placeholder for G logo
                    Text("G ", color = Color.Red, fontWeight = FontWeight.Black)
                    Text("Sign in with Google", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row {
                Text("Don't have an account? ", color = Color.Gray, fontSize = 14.sp)
                Text(
                    "Sign Up",
                    color = Color(0xFFFFD700),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
        }
    }
}
