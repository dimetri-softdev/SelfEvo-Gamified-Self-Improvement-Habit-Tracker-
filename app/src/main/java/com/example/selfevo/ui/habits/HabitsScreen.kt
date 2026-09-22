package com.example.selfevo.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.R
import com.example.selfevo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HabitsScreen(
    onAddHabit: (String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var habitName by remember { mutableStateOf("") }
    var selectedAttribute by remember { mutableStateOf("PAC") }
    var reminderTime by remember { mutableStateOf("07:00") }
    var selectedFrequency by remember { mutableStateOf("Daily") }

    val attributes = listOf(
        "PAC" to Color(0xFF00E5FF),
        "PHY" to Color(0xFFFFA500),
        "SKL" to Color(0xFFA020F0),
        "DEF" to Color(0xFF00FF00),
        "PAS" to Color(0xFFFFD700),
        "SHO" to Color(0xFFFF4500)
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Scrollable Content
            Box(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.new_habit),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.habit_description_sub),
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                    )

                    Text(stringResource(R.string.habit_name_label), color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = habitName,
                        onValueChange = { habitName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.habit_name_hint), color = Color.DarkGray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1A1A1A),
                            unfocusedContainerColor = Color(0xFF1A1A1A),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(stringResource(R.string.linked_attribute_label), color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Attribute Grid using FlowRow for responsiveness
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 3
                    ) {
                        attributes.forEach { (attr, color) ->
                            val isSelected = selectedAttribute == attr
                            AttributeItem(
                                label = attr,
                                color = color,
                                isSelected = isSelected,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedAttribute = attr }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Info bar for selected attribute
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1A1A1A), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).background(attributes.find { it.first == selectedAttribute }?.second ?: Color.Cyan,
                            CircleShape
                        ))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "$selectedAttribute — ${getAttributeFullName(selectedAttribute)}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(stringResource(R.string.frequency_label), color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Daily", "Mon-Fri", "Weekends").forEach { freq ->
                            FrequencyChip(
                                text = freq,
                                isSelected = selectedFrequency == freq,
                                onClick = { selectedFrequency = freq }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(stringResource(R.string.reminder_time_label), color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = reminderTime,
                        onValueChange = { reminderTime = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1A1A1A),
                            unfocusedContainerColor = Color(0xFF1A1A1A),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Fixed Bottom Redesigned Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (habitName.isNotBlank()) {
                            val mappedAttribute = when (selectedAttribute) {
                                "PAC" -> "PACE"
                                "PHY" -> "PHYSICAL"
                                "SKL" -> "SKILL"
                                "DEF" -> "DEFENDING"
                                "PAS" -> "PASSING"
                                "SHO" -> "SHOOTING"
                                else -> selectedAttribute
                            }
                            val mappedFrequency = when (selectedFrequency) {
                                "Mon-Fri" -> "WEEKDAYS"
                                else -> selectedFrequency
                            }
                            onAddHabit(habitName, mappedAttribute, mappedFrequency, reminderTime)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500)) // Premium Gold
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ADD TO MY EVOLUTION PLAN",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttributeItem(
    label: String,
    color: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(72.dp)
            .background(Color(0xFF1A1A1A), RoundedCornerShape(12.dp))
            .border(
                2.dp,
                if (isSelected) color else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontWeight = FontWeight.Black, fontSize = 16.sp, color = if (isSelected) color else Color.White)
            Text("+2 pts", fontSize = 9.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FrequencyChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (isSelected) Color(0xFF2C1F00) else Color(0xFF1A1A1A),
                RoundedCornerShape(50.dp)
            )
            .border(
                1.dp,
                if (isSelected) Color(0xFFFFD700) else Color.Transparent,
                RoundedCornerShape(50.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, color = if (isSelected) Color(0xFFFFD700) else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

fun getAttributeFullName(attr: String): String = when (attr) {
    "PAC" -> "Pace"
    "PHY" -> "Physical"
    "SKL" -> "Skill"
    "DEF" -> "Defending"
    "PAS" -> "Passing"
    "SHO" -> "Shooting"
    else -> ""
}
