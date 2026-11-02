package com.example.selfevo.ui.habits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.R
import com.example.selfevo.data.db.entity.CustomAttributeEntity
import com.example.selfevo.data.db.entity.HabitEntity
import com.example.selfevo.ui.component.HabitItemRow

data class AttributeOption(
    val abbr: String,
    val fullName: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HabitsScreen(
    habits: List<HabitEntity> = emptyList(),
    customAttributes: List<CustomAttributeEntity> = emptyList(),
    onAddHabit: (title: String, primaryAttr: String, secondaryAttr: String?, frequency: String, reminderTime: String, daysOfWeek: String, targetDuration: String) -> Unit,
    onEditHabit: (habitId: String, title: String, primaryAttr: String, secondaryAttr: String?, frequency: String, reminderTime: String, daysOfWeek: String, targetDuration: String) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onDeleteHabit: (habitId: String) -> Unit = {},
    onCompleteHabit: (habitId: String) -> Unit = {},
    onCreateCustomAttribute: (title: String, abbr: String, colorHex: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var showCreateHabitDialog by remember { mutableStateOf(false) }
    var habitToEdit by remember { mutableStateOf<HabitEntity?>(null) }
    var habitToDelete by remember { mutableStateOf<HabitEntity?>(null) }
    var showCreateAttrDialog by remember { mutableStateOf(false) }

    val defaultAttributes = remember {
        listOf(
            AttributeOption("PAC", "Pace", Color(0xFF00E5FF)),
            AttributeOption("PHY", "Physical", Color(0xFFFFA500)),
            AttributeOption("SKL", "Skill", Color(0xFFA020F0)),
            AttributeOption("DEF", "Defending", Color(0xFF00FF00)),
            AttributeOption("PAS", "Passing", Color(0xFFFFD700)),
            AttributeOption("SHO", "Shooting", Color(0xFFFF4500))
        )
    }

    val allAttributes = remember(customAttributes, defaultAttributes) {
        val customMapped = customAttributes.map {
            AttributeOption(
                abbr = it.abbreviation,
                fullName = it.title,
                color = try { Color(android.graphics.Color.parseColor(it.colorHex)) } catch (e: Exception) { Color(0xFF00E5FF) }
            )
        }
        defaultAttributes + customMapped
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header with fixed weight so + Custom Stat is never squished (Fixes Issue #2)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HABITS HUB",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${habits.size} Active Habits • Level Up Attributes",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    OutlinedButton(
                        onClick = { showCreateAttrDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF00E5FF))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Custom Stat", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Prominent "+ CREATE NEW HABIT" Action Button (Fixes Issue #4 & #5)
            item {
                Button(
                    onClick = { showCreateHabitDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "+ CREATE NEW HABIT",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            // Active Habits List Section (Fixes Issue #3 & #5)
            item {
                Text(
                    text = "YOUR ACTIVE HABITS",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            if (habits.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "NO HABITS YET",
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap '+ CREATE NEW HABIT' above to set up custom habits, specify target days & duration, and start gaining XP!",
                                color = Color.DarkGray,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(habits, key = { it.id }) { habit ->
                    HabitItemRow(
                        habit = habit,
                        onCompleteClick = { onCompleteHabit(habit.id) },
                        onEditClick = { habitToEdit = habit },
                        onDeleteClick = { habitToDelete = habit }
                    )
                }
            }
        }
    }

    // Create New Habit Dialog
    if (showCreateHabitDialog) {
        CreateHabitDialog(
            allAttributes = allAttributes,
            onDismiss = { showCreateHabitDialog = false },
            onSaveHabit = { title, primAttr, secAttr, freq, time, days, duration ->
                onAddHabit(title, primAttr, secAttr, freq, time, days, duration)
                showCreateHabitDialog = false
            }
        )
    }

    // Edit Habit Dialog
    habitToEdit?.let { habit ->
        CreateHabitDialog(
            allAttributes = allAttributes,
            initialHabit = habit,
            onDismiss = { habitToEdit = null },
            onSaveHabit = { title, primAttr, secAttr, freq, time, days, duration ->
                onEditHabit(habit.id, title, primAttr, secAttr, freq, time, days, duration)
                habitToEdit = null
            }
        )
    }

    // Delete Habit Confirmation Dialog
    habitToDelete?.let { habit ->
        DeleteHabitConfirmationDialog(
            habitTitle = habit.title,
            onDismiss = { habitToDelete = null },
            onConfirmDelete = {
                onDeleteHabit(habit.id)
                habitToDelete = null
            }
        )
    }

    // Create Custom Attribute Dialog
    if (showCreateAttrDialog) {
        CreateCustomAttributeDialog(
            onDismiss = { showCreateAttrDialog = false },
            onCreate = { title, abbr, hex ->
                onCreateCustomAttribute(title, abbr, hex)
                showCreateAttrDialog = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateHabitDialog(
    allAttributes: List<AttributeOption>,
    onDismiss: () -> Unit,
    initialHabit: HabitEntity? = null,
    onSaveHabit: (title: String, primAttr: String, secAttr: String?, frequency: String, reminderTime: String, daysOfWeek: String, targetDuration: String) -> Unit
) {
    var habitName by remember { mutableStateOf(initialHabit?.title ?: "") }
    var selectedPrimaryAttr by remember { mutableStateOf(initialHabit?.attributeType ?: "PAC") }
    var selectedSecondaryAttr by remember { mutableStateOf(initialHabit?.secondaryAttributeType) }
    var reminderTime by remember { mutableStateOf(initialHabit?.reminderTime ?: "07:00") }
    var targetDuration by remember { mutableStateOf(initialHabit?.targetDuration ?: "30 mins") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val daysList = remember { listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun") }
    val initialDays = remember(initialHabit) {
        if (initialHabit == null || initialHabit.daysOfWeek.equals("Daily", ignoreCase = true)) {
            daysList.toSet()
        } else {
            initialHabit.daysOfWeek.split(",").map { it.trim() }.toSet()
        }
    }
    var selectedDays by remember { mutableStateOf(initialDays) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialHabit != null) "Edit Habit" else "Create New Habit",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFFF5252),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Habit Name
                OutlinedTextField(
                    value = habitName,
                    onValueChange = {
                        habitName = it
                        if (it.isNotBlank()) errorMessage = null
                    },
                    label = { Text("Habit Name (e.g. Morning Run)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFFD700)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Target Duration / Goal
                OutlinedTextField(
                    value = targetDuration,
                    onValueChange = { targetDuration = it },
                    label = { Text("Target Duration / Goal (e.g. 30 mins, 10 reps)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFFD700)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Target Days of Week Selector
                Column {
                    Text("TARGET DAYS OF WEEK", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        daysList.forEach { day ->
                            val isSelected = selectedDays.contains(day)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .background(
                                        if (isSelected) Color(0xFFFFD700) else Color(0xFF262626),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedDays = if (isSelected) {
                                            if (selectedDays.size > 1) selectedDays - day else selectedDays
                                        } else {
                                            selectedDays + day
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.take(1),
                                    color = if (isSelected) Color.Black else Color.Gray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextTextButton("All Days") { selectedDays = daysList.toSet() }
                        TextTextButton("Mon-Fri") { selectedDays = setOf("Mon", "Tue", "Wed", "Thu", "Fri") }
                        TextTextButton("Weekends") { selectedDays = setOf("Sat", "Sun") }
                    }
                }

                // Linked Attributes
                Column {
                    Text("LINKED ATTRIBUTES (SELECT UP TO 2)", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("1 selected = +2 XP | 2 selected = +1 / +1 XP", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 3
                    ) {
                        allAttributes.forEach { attr ->
                            val isPrimary = selectedPrimaryAttr == attr.abbr
                            val isSecondary = selectedSecondaryAttr == attr.abbr
                            val isSelected = isPrimary || isSecondary

                            AttributeItem(
                                label = attr.abbr,
                                color = attr.color,
                                isSelected = isSelected,
                                badgeText = if (isPrimary && selectedSecondaryAttr != null) "+1 XP" else if (isSelected) "+2 XP" else "+ XP",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (isPrimary) {
                                        if (selectedSecondaryAttr != null) {
                                            selectedPrimaryAttr = selectedSecondaryAttr!!
                                            selectedSecondaryAttr = null
                                        }
                                    } else if (isSecondary) {
                                        selectedSecondaryAttr = null
                                    } else {
                                        if (selectedSecondaryAttr == null) {
                                            selectedSecondaryAttr = attr.abbr
                                        } else {
                                            selectedPrimaryAttr = attr.abbr
                                            selectedSecondaryAttr = null
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                // Daily Reminder Time
                OutlinedTextField(
                    value = reminderTime,
                    onValueChange = { reminderTime = it },
                    label = { Text("Daily Reminder Time (e.g. 07:00)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFFD700)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (habitName.isBlank()) {
                        errorMessage = "⚠️ Please enter a habit title."
                    } else {
                        val daysFormatted = if (selectedDays.size == 7) "Daily" else selectedDays.joinToString(",")
                        val freqText = if (selectedDays.size == 7) "Daily" else "${selectedDays.size} days/wk"
                        onSaveHabit(
                            habitName,
                            selectedPrimaryAttr,
                            selectedSecondaryAttr,
                            freqText,
                            reminderTime,
                            daysFormatted,
                            targetDuration
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (initialHabit != null) "SAVE CHANGES" else "+ CREATE HABIT",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF1E1E1E)
    )
}

@Composable
fun DeleteHabitConfirmationDialog(
    habitTitle: String,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete Habit", color = Color.White, fontWeight = FontWeight.Black)
        },
        text = {
            Text(
                "Are you sure you want to delete \"$habitTitle\"? This action cannot be undone.",
                color = Color.Gray,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF1E1E1E)
    )
}

@Composable
fun TextTextButton(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = Color(0xFF00E5FF),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
fun CreateCustomAttributeDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, abbr: String, colorHex: String) -> Unit
) {
    var newTitle by remember { mutableStateOf("") }
    var newAbbr by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf("#00E5FF") }

    val colorOptions = listOf("#00E5FF", "#FF007F", "#FFA500", "#A020F0", "#00FF00", "#FFD700")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Custom Attribute", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Custom attributes start at rating 0 and level up as you complete linked habits.", color = Color.Gray, fontSize = 12.sp)

                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("Attribute Name (e.g. Discipline)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )

                OutlinedTextField(
                    value = newAbbr,
                    onValueChange = { if (it.length <= 3) newAbbr = it.uppercase() },
                    label = { Text("3-Letter Acronym (e.g. DIS)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )

                Text("Pick Accent Color:", color = Color.Gray, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorOptions.forEach { hex ->
                        val parsed = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { Color.Cyan }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(parsed, CircleShape)
                                .border(2.dp, if (selectedColorHex == hex) Color.White else Color.Transparent, CircleShape)
                                .clickable { selectedColorHex = hex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newTitle.isNotBlank() && newAbbr.isNotBlank()) {
                        onCreate(newTitle, newAbbr, selectedColorHex)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text("Create Attribute", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF1E1E1E)
    )
}

@Composable
fun AttributeItem(
    label: String,
    color: Color,
    isSelected: Boolean,
    badgeText: String = "+2 pts",
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(64.dp)
            .background(Color(0xFF1A1A1A), RoundedCornerShape(12.dp))
            .border(
                2.dp,
                if (isSelected) color else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontWeight = FontWeight.Black, fontSize = 14.sp, color = if (isSelected) color else Color.White)
            Text(badgeText, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}
