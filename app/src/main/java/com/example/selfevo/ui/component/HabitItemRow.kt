package com.example.selfevo.ui.component

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.data.local.entity.HabitEntity
import com.example.selfevo.ui.theme.*

@Composable
fun HabitItemRow(
    habit: HabitEntity,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val attributeColor = when (habit.attributeType.uppercase()) {
        "PACE" -> Cyan
        "PHYSICAL" -> Gold
        "SKILL" -> Color(0xFFE91E63)
        "DEFENDING" -> Color(0xFF4CAF50)
        else -> Gold
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(enabled = !habit.isCompletedToday) { onCompleteClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceVariant
        ),
        border = if (habit.isCompletedToday) null else null // Border if needed
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (habit.isCompletedToday) Gold else Color.Transparent)
                    .border(2.dp, if (habit.isCompletedToday) Gold else Color.DarkGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (habit.isCompletedToday) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (habit.isCompletedToday) TextSecondary else TextPrimary,
                    textDecoration = if (habit.isCompletedToday) TextDecoration.LineThrough else null
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(attributeColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = habit.attributeType,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Points indicator
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(attributeColor.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+2", // Dummy points
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = attributeColor
                )
            }
        }
    }
}
