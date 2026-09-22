package com.example.selfevo.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.selfevo.R
import com.example.selfevo.ui.dashboard.DashboardViewModel
import com.example.selfevo.util.lang.LocaleHelper
import com.example.selfevo.util.theme.ThemeManager

@Composable
fun SettingsScreen(
    viewModel: DashboardViewModel,
    onSignOut: () -> Unit,
    onSyncClick: () -> Unit
) {
    val context = LocalContext.current
    val currentLang = remember { mutableStateOf(LocaleHelper.getLocale(context)) }
    val isAmoled = ThemeManager.isAmoledMode.value
    val lastSync by viewModel.lastSyncTime.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            item {
                Text(stringResource(R.string.configure), color = Color(0xFFFFA500), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.settings), fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
            }

            // Language Preference
            item {
                SettingsSection(title = stringResource(R.string.language_preference)) {
                    LanguageItem("English", "Default system language", currentLang.value == "en") {
                        LocaleHelper.setLocale(context, "en")
                        currentLang.value = "en"
                    }
                    LanguageItem("isiXhosa", "Xhosa — South Africa", currentLang.value == "xh") {
                        LocaleHelper.setLocale(context, "xh")
                        currentLang.value = "xh"
                    }
                    LanguageItem("Afrikaans", "Afrikaans — South Africa", currentLang.value == "af") {
                        LocaleHelper.setLocale(context, "af")
                        currentLang.value = "af"
                    }
                }
            }

            // Appearance
            item {
                SettingsSection(title = stringResource(R.string.appearance)) {
                    ToggleItem(
                        name = stringResource(R.string.amoled_dark_mode),
                        description = stringResource(R.string.amoled_dark_mode_sub),
                        isEnabled = isAmoled
                    ) { enabled ->
                        ThemeManager.setAmoledMode(context, enabled)
                    }
                }
            }

            // Push Notifications
            item {
                SettingsSection(title = stringResource(R.string.push_notifications)) {
                    ToggleItem(stringResource(R.string.daily_reminders), stringResource(R.string.daily_reminders_sub), true) {}
                    ToggleItem(stringResource(R.string.streak_alerts), stringResource(R.string.streak_alerts_sub), true) {}
                }
            }

            // Data & Cloud
            item {
                Column {
                    Text(stringResource(R.string.data_cloud), color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                            Text(stringResource(R.string.sync_data_to_cloud), color = Color(0xFF00E5FF), fontWeight = FontWeight.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.last_synced, lastSync),
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
                    Text(stringResource(R.string.sign_out), color = Color.Red, fontWeight = FontWeight.Bold)
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
fun LanguageItem(name: String, description: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(name, color = if (isSelected) Color(0xFFFFD700) else Color.White, fontWeight = FontWeight.Bold)
            Text(description, color = Color.Gray, fontSize = 12.sp)
        }
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFFFFD700),
                unselectedColor = Color.Gray
            )
        )
    }
}

@Composable
fun ToggleItem(name: String, description: String, isEnabled: Boolean, onToggle: (Boolean) -> Unit) {
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
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = Color(0xFFFFD700),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFF0A0A0A)
            )
        )
    }
}
