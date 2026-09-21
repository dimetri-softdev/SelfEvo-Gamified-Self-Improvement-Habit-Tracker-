package com.example.selfevo.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.data.local.entity.HabitEntity

@Composable
fun HabitItemRow(
    habit: HabitEntity,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val attributeColor = when (habit.attributeType.uppercase()) {
        "PAC", "PACE" -> Color(0xFF00E5FF)
        "PHY", "PHYSICAL" -> Color(0xFFFFA500)
        "SKL", "SKILL" -> Color(0xFFA020F0)
        "DEF", "DEFENDING" -> Color(0xFF00FF00)
        else -> Color(0xFFFFD700)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(20.dp),
        border = if (habit.isCompletedToday) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 1. Completion Circle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            if (habit.isCompletedToday) Color(0xFFFFD700) else Color.Transparent,
                            CircleShape
                        )
                        .border(
                            if (habit.isCompletedToday) 0.dp else 2.dp,
                            if (habit.isCompletedToday) Color.Transparent else Color.Gray,
                            CircleShape
                        )
                        .clickable(enabled = !habit.isCompletedToday) { onCompleteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (habit.isCompletedToday) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // 2. Habit Details
                Column {
                    Text(
                        text = habit.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (habit.isCompletedToday) Color.Gray else Color.White,
                        textDecoration = if (habit.isCompletedToday) TextDecoration.LineThrough else null
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(attributeColor, CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = habit.attributeType.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }
            }

            // 3. Points Badge (+X)
            Box(
                modifier = Modifier
                    .size(56.dp, 40.dp)
                    .background(attributeColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .border(1.dp, attributeColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+2", // Placeholder for dynamic points
                    color = attributeColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
