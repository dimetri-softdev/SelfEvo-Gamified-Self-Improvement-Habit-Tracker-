package com.example.selfevo.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.R
import com.example.selfevo.ui.theme.*

@Composable
fun SettingsScreen(
    onSignOut: () -> Unit
) {
    var amoledMode by remember { mutableStateOf(true) }
    var dailyReminders by remember { mutableStateOf(true) }
    var streakAlerts by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.configure).uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Gold,
            letterSpacing = 1.sp
        )
        Text(
            text = stringResource(R.string.settings).uppercase(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(32.dp))

        SettingsSection(title = stringResource(R.string.language_preference)) {
            LanguageItem("English", "Default system language", true)
            LanguageItem("isiXhosa", "Xhosa — South Africa", false)
            LanguageItem("Afrikaans", "Afrikaans — South Africa", false)
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = stringResource(R.string.appearance)) {
            SwitchItem(
                title = stringResource(R.string.amoled_dark_mode),
                subtitle = stringResource(R.string.amoled_dark_mode_sub),
                checked = amoledMode,
                onCheckedChange = { amoledMode = it }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceVariant)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp)).background(Color.DarkGray))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Dark (AMOLED)", color = TextSecondary, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = stringResource(R.string.push_notifications)) {
            SwitchItem(
                title = stringResource(R.string.daily_reminders),
                subtitle = stringResource(R.string.daily_reminders_sub),
                checked = dailyReminders,
                onCheckedChange = { dailyReminders = it }
            )
            Spacer(modifier = Modifier.height(12.dp))
            SwitchItem(
                title = stringResource(R.string.streak_alerts),
                subtitle = stringResource(R.string.streak_alerts_sub),
                checked = streakAlerts,
                onCheckedChange = { streakAlerts = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = stringResource(R.string.data_cloud)) {
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.dp, Cyan, RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Cyan),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Refresh, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.sync_data_to_cloud), fontWeight = FontWeight.Black)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.last_synced, "Today, 08:42 AM"),
                fontSize = 10.sp,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceVariant)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Black)
                        .border(1.dp, Gold, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", color = Gold)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "MAKHO", fontWeight = FontWeight.Black, color = TextPrimary)
                    Text(text = "makho@selfevo.app", fontSize = 12.sp, color = TextSecondary)
                }
                Box(
                    modifier = Modifier
                        .border(1.dp, Gold, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "GOLD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF311B1B), contentColor = Color.Red),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF552222))
        ) {
            Text(text = stringResource(R.string.sign_out), fontWeight = FontWeight.Black)
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
fun LanguageItem(name: String, sub: String, checked: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = name, fontWeight = FontWeight.Bold, color = if (checked) Gold else TextPrimary)
            Text(text = sub, fontSize = 12.sp, color = TextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = {},
            colors = SwitchDefaults.colors(
                checkedThumbColor = Black,
                checkedTrackColor = Gold,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = SurfaceVariant
            )
        )
    }
}

@Composable
fun SwitchItem(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = subtitle, fontSize = 12.sp, color = TextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Black,
                checkedTrackColor = Gold,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = SurfaceVariant
            )
        )
    }
}
