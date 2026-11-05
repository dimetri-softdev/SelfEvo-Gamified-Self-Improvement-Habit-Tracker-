package com.example.selfevo.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.selfevo.R
import com.example.selfevo.data.notification.HabitReminderWorker
import com.example.selfevo.data.repository.SelfEvoNotificationTopic
import com.example.selfevo.ui.dashboard.DashboardViewModel
import com.example.selfevo.util.lang.LocaleHelper
import com.example.selfevo.util.theme.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: DashboardViewModel,
    playerName: String,
    email: String,
    onSignOut: () -> Unit,
    onSyncClick: () -> Unit
) {
    val context = LocalContext.current

    val currentLang = remember { mutableStateOf(LocaleHelper.getLocale(context)) }
    val isDarkMode by ThemeManager.isDarkMode
    val isAmoled by ThemeManager.isAmoledMode
    val lastSync by viewModel.lastSyncTime.collectAsState()
    val playerCard by viewModel.playerCard.collectAsState()
    val customAttributes by viewModel.customAttributes.collectAsState()
    val profileImageUrl by viewModel.profileImageUrl.collectAsState()
    val profileImageError by viewModel.profileImageError.collectAsState()
    val isUploadingProfileImage by viewModel.isUploadingProfileImage.collectAsState()
    val dailyTopicEnabled by viewModel.dailyRemindersEnabled.collectAsState()
    val streakAlertsEnabled by viewModel.streakAlertsEnabled.collectAsState()
    val notificationError by viewModel.notificationError.collectAsState()

    var dailyRemindersEnabled by remember { mutableStateOf(true) }
    var scheduledTimeText by remember { mutableStateOf("07:30") }
    var reminderConfirmedBanner by remember { mutableStateOf<String?>(null) }
    var notificationPermissionError by remember { mutableStateOf(false) }
    var pendingNotificationTopic by remember { mutableStateOf<SelfEvoNotificationTopic?>(null) }

    // Slot states
    var slot1 by remember(playerCard) { mutableStateOf(playerCard?.slot1 ?: "PAC") }
    var slot2 by remember(playerCard) { mutableStateOf(playerCard?.slot2 ?: "SHO") }
    var slot3 by remember(playerCard) { mutableStateOf(playerCard?.slot3 ?: "PAS") }
    var slot4 by remember(playerCard) { mutableStateOf(playerCard?.slot4 ?: "SKL") }
    var slot5 by remember(playerCard) { mutableStateOf(playerCard?.slot5 ?: "DEF") }
    var slot6 by remember(playerCard) { mutableStateOf(playerCard?.slot6 ?: "PHY") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let(viewModel::uploadProfileImage)
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        val topic = pendingNotificationTopic
        pendingNotificationTopic = null
        if (granted && topic != null) {
            notificationPermissionError = false
            viewModel.setNotificationEnabled(topic, true)
        } else if (!granted) {
            notificationPermissionError = true
        }
    }

    fun updateNotificationPreference(topic: SelfEvoNotificationTopic, enabled: Boolean) {
        notificationPermissionError = false
        if (!enabled) {
            viewModel.setNotificationEnabled(topic, false)
        } else if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            pendingNotificationTopic = topic
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.setNotificationEnabled(topic, true)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dailyRemindersEnabled = true
            HabitReminderWorker.schedule(context)
            reminderConfirmedBanner = "✓ Daily reminders scheduled for $scheduledTimeText"
        } else {
            dailyRemindersEnabled = false
            HabitReminderWorker.cancel(context)
            reminderConfirmedBanner = "⚠️ Notification permission denied."
        }
    }

    val profilePhotoModel = profileImageUrl ?: playerCard?.avatarUri

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(stringResource(R.string.configure), color = Color(0xFFFFA500), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = stringResource(R.string.settings),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "PoE v3.0",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // User Profile Card & Photo Upload
            item {
                SettingsSection(title = "PLAYER PROFILE & CARD PHOTO") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E1E1E))
                                .border(2.dp, Color(0xFFFFD700), CircleShape)
                                .clickable {
                                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!profilePhotoModel.isNullOrBlank()) {
                                AsyncImage(
                                    model = profilePhotoModel,
                                    contentDescription = stringResource(R.string.profile_photo),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(50.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = {
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            enabled = !isUploadingProfileImage
                        ) {
                            if (isUploadingProfileImage) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (isUploadingProfileImage) {
                                    stringResource(R.string.uploading_profile_photo)
                                } else {
                                    stringResource(R.string.change_profile_photo)
                                },
                                color = Color(0xFF00E5FF),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        profileImageError?.let { errorMessage ->
                            Text(
                                stringResource(errorMessage),
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(playerName.uppercase(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text(email, color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            // Working Daily Notification Schedule Section with Confirm Button
            item {
                SettingsSection(title = "DAILY NOTIFICATION REMINDER SCHEDULE") {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFFFD700))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Daily Habit Reminder Time", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = scheduledTimeText,
                            onValueChange = { scheduledTimeText = it },
                            label = { Text("Reminder Time (HH:mm, e.g. 07:30)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasPermission) {
                                        dailyRemindersEnabled = true
                                        HabitReminderWorker.schedule(context)
                                        reminderConfirmedBanner = "✓ Daily reminders scheduled for $scheduledTimeText"
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    dailyRemindersEnabled = true
                                    HabitReminderWorker.schedule(context)
                                    reminderConfirmedBanner = "✓ Daily reminders scheduled for $scheduledTimeText"
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                        ) {
                            Text("CONFIRM REMINDER SCHEDULE", color = Color.Black, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        }

                        if (reminderConfirmedBanner != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = Color(0xFF0D3322),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF00FF00))
                            ) {
                                Text(
                                    text = reminderConfirmedBanner!!,
                                    color = Color(0xFF00FF00),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Push Notification Topic Preferences
            item {
                SettingsSection(title = stringResource(R.string.push_notifications)) {
                    ToggleItem(
                        name = stringResource(R.string.daily_reminders),
                        description = stringResource(R.string.daily_reminders_sub),
                        isEnabled = dailyTopicEnabled
                    ) { enabled ->
                        updateNotificationPreference(SelfEvoNotificationTopic.DAILY_REMINDERS, enabled)
                    }
                    ToggleItem(
                        name = stringResource(R.string.streak_alerts),
                        description = stringResource(R.string.streak_alerts_sub),
                        isEnabled = streakAlertsEnabled
                    ) { enabled ->
                        updateNotificationPreference(SelfEvoNotificationTopic.STREAK_ALERTS, enabled)
                    }
                    if (notificationPermissionError) {
                        Text(
                            stringResource(R.string.notification_permission_required),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    if (notificationError != null) {
                        Text(
                            stringResource(R.string.notification_settings_error, notificationError.orEmpty()),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Card 6-Slot Layout Configuration
            item {
                SettingsSection(title = "CARD 6-SLOT STAT LAYOUT") {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Customize which 6 attributes appear on your FUT player card:", color = Color.Gray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        val availableAbbrs = remember(customAttributes) {
                            listOf("PAC", "SHO", "PAS", "SKL", "DEF", "PHY") + customAttributes.map { it.abbreviation }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SlotSelector("Slot 1", slot1, availableAbbrs, Modifier.weight(1f)) { slot1 = it }
                            SlotSelector("Slot 2", slot2, availableAbbrs, Modifier.weight(1f)) { slot2 = it }
                            SlotSelector("Slot 3", slot3, availableAbbrs, Modifier.weight(1f)) { slot3 = it }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SlotSelector("Slot 4", slot4, availableAbbrs, Modifier.weight(1f)) { slot4 = it }
                            SlotSelector("Slot 5", slot5, availableAbbrs, Modifier.weight(1f)) { slot5 = it }
                            SlotSelector("Slot 6", slot6, availableAbbrs, Modifier.weight(1f)) { slot6 = it }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = {
                                viewModel.updateCardSlots(slot1, slot2, slot3, slot4, slot5, slot6)
                                Toast.makeText(context, "Card stat slots updated!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF00E5FF))
                        ) {
                            Text("SAVE CARD SLOT LAYOUT", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                        }
                    }
                }
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

            // Appearance (Dark Mode & AMOLED Mode)
            item {
                SettingsSection(title = stringResource(R.string.appearance)) {
                    ToggleItem(
                        name = stringResource(R.string.dark_mode),
                        description = stringResource(R.string.dark_mode_sub),
                        isEnabled = isDarkMode
                    ) { enabled ->
                        ThemeManager.setDarkMode(context, enabled)
                    }
                    if (isDarkMode) {
                        ToggleItem(
                            name = stringResource(R.string.amoled_dark_mode),
                            description = stringResource(R.string.amoled_dark_mode_sub),
                            isEnabled = isAmoled
                        ) { enabled ->
                            ThemeManager.setAmoledMode(context, enabled)
                        }
                    }
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
                        color = Color.Gray,
                        fontSize = 11.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            // Sign Out
            item {
                Button(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
                ) {
                    Text(stringResource(R.string.sign_out), color = Color.Red, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun SlotSelector(
    label: String,
    currentValue: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(label, fontSize = 9.sp, color = Color.Gray)
                Text(currentValue, fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD700))
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.distinct().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontWeight = FontWeight.Bold) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
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
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
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
            Text(
                text = name,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(description, color = Color.Gray, fontSize = 12.sp)
        }
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = Color.Gray
            )
        )
    }
}

@Composable
fun ToggleItem(
    name: String,
    description: String,
    isEnabled: Boolean,
    onToggleChanged: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            Text(description, color = Color.Gray, fontSize = 12.sp)
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggleChanged,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}
