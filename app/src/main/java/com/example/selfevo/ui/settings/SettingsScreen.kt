package com.example.selfevo.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onSignOut: () -> Unit,
    onSyncClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            item {
                Text("CONFIGURE", color = Color(0xFFFFA500), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("SETTINGS", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
            }

            // Language Preference
            item {
                SettingsSection(title = "LANGUAGE PREFERENCE") {
                    LanguageItem("English", "Default system language", true)
                    LanguageItem("isiXhosa", "Xhosa — South Africa", false)
                    LanguageItem("Afrikaans", "Afrikaans — South Africa", false)
                }
            }

            // Appearance
            item {
                SettingsSection(title = "APPEARANCE") {
                    ToggleItem("AMOLED Dark Mode", "Pure pitch black — saves battery", true)
                }
            }

            // Push Notifications
            item {
                SettingsSection(title = "PUSH NOTIFICATIONS") {
                    ToggleItem("Daily Reminders", "Get notified at your scheduled times", true)
                    ToggleItem("Streak Alerts", "Warning when streak is at risk", true)
                }
            }

            // Data & Cloud
            item {
                Column {
                    Text("DATA & CLOUD", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onSyncClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E5FF))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("SYNC DATA TO CLOUD", color = Color(0xFF00E5FF), fontWeight = FontWeight.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Last synced: Today, 08:42 AM",
                        color = Color.DarkGray,
                        fontSize = 11.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            // User Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF2C1F00), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFFFD700))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("MAKHO", color = Color.White, fontWeight = FontWeight.Black)
                            Text("makho@selfevo.app", color = Color.Gray, fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("GOLD", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Sign Out
            item {
                Button(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A0A0A)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.2f))
                ) {
                    Text("Sign Out", color = Color.Red, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1A), RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            content()
        }
    }
}

@Composable
fun LanguageItem(name: String, description: String, isSelected: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(name, color = if (isSelected) Color(0xFFFFD700) else Color.White, fontWeight = FontWeight.Bold)
            Text(description, color = Color.Gray, fontSize = 12.sp)
        }
        Switch(
            checked = isSelected,
            onCheckedChange = {},
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = Color(0xFFFFD700),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFF0A0A0A)
            )
        )
    }
}

@Composable
fun ToggleItem(name: String, description: String, isEnabled: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(name, color = Color.White, fontWeight = FontWeight.Bold)
            Text(description, color = Color.Gray, fontSize = 12.sp)
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = {},
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = Color(0xFFFFD700),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFF0A0A0A)
            )
        )
    }
}
