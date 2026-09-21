package com.example.selfevo.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    onAddHabit: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var habitName by remember { mutableStateOf("") }
    var selectedAttribute by remember { mutableStateOf("PAC") }

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
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "NEW HABIT",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "Each completed habit upgrades your player card stats.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            Text("HABIT NAME", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = habitName,
                onValueChange = { habitName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Morning Run", color = Color.DarkGray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1A1A1A),
                    unfocusedContainerColor = Color(0xFF1A1A1A),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text("LINKED ATTRIBUTE", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(attributes) { (attr, color) ->
                    val isSelected = selectedAttribute == attr
                    AttributeItem(
                        label = attr,
                        color = color,
                        isSelected = isSelected,
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

            Spacer(modifier = Modifier.height(32.dp))

            Text("FREQUENCY", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FrequencyChip("Daily", true)
                FrequencyChip("Mon-Fri", false)
                FrequencyChip("Weekends", false)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Add Habit Button (Cyan Gradient as per design)
            Button(
                onClick = { onAddHabit(habitName, selectedAttribute, "Daily") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF1DE9B6))),
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("+ ADD TO SELFEVO", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AttributeItem(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1.2f)
            .background(Color(0xFF1A1A1A), RoundedCornerShape(12.dp))
            .border(
                2.dp,
                if (isSelected) color else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontWeight = FontWeight.Black, fontSize = 18.sp, color = if (isSelected) color else Color.White)
            Text("+2 pts", fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FrequencyChip(text: String, isSelected: Boolean) {
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
