package com.example.selfevo.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
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
import com.example.selfevo.ui.component.SelfEvoButton
import com.example.selfevo.ui.component.SelfEvoTextField
import com.example.selfevo.ui.theme.*

@Composable
fun HabitsScreen(
    onAddHabit: (String, String, String, String) -> Unit
) {
    var habitName by remember { mutableStateOf("") }
    var selectedAttribute by remember { mutableStateOf("PAC") }
    var selectedFrequency by remember { mutableStateOf("Daily") }
    var reminderTime by remember { mutableStateOf("07:00") }

    val attributes = listOf("PAC", "PHY", "SKL", "DEF", "PAS", "SHO")
    val frequencies = listOf("Daily", "Mon-Fri", "Mon/Wed/Fri", "Weekends", "Custom")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.new_habit),
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        Text(
            text = stringResource(R.string.habit_description_sub),
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        SelfEvoTextField(
            value = habitName,
            onValueChange = { habitName = it },
            label = stringResource(R.string.habit_name_label),
            placeholder = stringResource(R.string.habit_name_hint)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.linked_attribute_label),
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(160.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(attributes) { attr ->
                val isSelected = selectedAttribute == attr
                val color = when(attr) {
                    "PAC" -> Cyan
                    "PHY" -> Gold
                    "SKL" -> Color(0xFFE91E63)
                    else -> Cyan
                }

                Box(
                    modifier = Modifier
                        .height(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) color.copy(alpha = 0.1f) else SurfaceVariant)
                        .border(
                            2.dp,
                            if (isSelected) color else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedAttribute = attr },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = attr,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) color else TextPrimary,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "+2 pts",
                            fontSize = 10.sp,
                            color = if (isSelected) color else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Cyan.copy(alpha = 0.1f))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Cyan))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$selectedAttribute — ${getAttributeFullName(selectedAttribute)}",
                    color = Cyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.frequency_label),
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            frequencies.take(4).forEach { freq ->
                val isSelected = selectedFrequency == freq
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Gold.copy(alpha = 0.2f) else SurfaceVariant)
                        .border(1.dp, if (isSelected) Gold else Color.DarkGray, RoundedCornerShape(12.dp))
                        .clickable { selectedFrequency = freq }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = freq,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Gold else TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SelfEvoTextField(
            value = reminderTime,
            onValueChange = { reminderTime = it },
            label = stringResource(R.string.reminder_time_label),
            placeholder = "07:00"
        )

        Spacer(modifier = Modifier.height(32.dp))

        SelfEvoButton(
            text = stringResource(R.string.add_to_selfevo),
            onClick = { onAddHabit(habitName, selectedAttribute, selectedFrequency, reminderTime) },
            containerColor = Cyan,
            contentColor = Black
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}

fun getAttributeFullName(attr: String): String = when(attr) {
    "PAC" -> "Pace"
    "PHY" -> "Physical"
    "SKL" -> "Skill"
    "DEF" -> "Defending"
    "PAS" -> "Passing"
    "SHO" -> "Shooting"
    else -> attr
}
