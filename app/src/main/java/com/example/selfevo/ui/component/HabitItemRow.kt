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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.data.db.entity.HabitEntity

@Composable
fun HabitItemRow(
    habit: HabitEntity,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    val primColor = getAttrColor(habit.attributeType)
    val secColor = habit.secondaryAttributeType?.let { getAttrColor(it) }

    val hasSecondary = !habit.secondaryAttributeType.isNullOrBlank()
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = if (habit.isCompletedToday) null else BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            if (habit.isCompletedToday) MaterialTheme.colorScheme.primary else Color.Transparent,
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
                        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = habit.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (habit.isCompletedToday) Color.Gray else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (habit.isCompletedToday) TextDecoration.LineThrough else null,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(primColor, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = habit.attributeType.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        if (hasSecondary) {
                            Text(" | ", color = Color.Gray, fontSize = 11.sp)
                            Box(modifier = Modifier.size(8.dp).background(secColor ?: Color.Cyan, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = habit.secondaryAttributeType!!.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                        Text(
                            text = "📅 ${habit.daysOfWeek}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        if (habit.targetDuration.isNotBlank()) {
                            Text(
                                text = " • ⏱️ ${habit.targetDuration}",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .background(primColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .border(1.dp, primColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (hasSecondary) "+1/+1" else "+2",
                        color = primColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                if (onEditClick != null || onDeleteClick != null) {
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Habit Options",
                                tint = Color.Gray
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color(0xFF262626))
                        ) {
                            if (onEditClick != null) {
                                DropdownMenuItem(
                                    text = { Text("Edit Habit", color = Color.White) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF00E5FF))
                                    },
                                    onClick = {
                                        showMenu = false
                                        onEditClick()
                                    }
                                )
                            }
                            if (onDeleteClick != null) {
                                DropdownMenuItem(
                                    text = { Text("Delete Habit", color = Color(0xFFFF5252)) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF5252))
                                    },
                                    onClick = {
                                        showMenu = false
                                        onDeleteClick()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getAttrColor(attr: String): Color {
    return when (attr.uppercase()) {
        "PAC", "PACE" -> Color(0xFF00E5FF)
        "PHY", "PHYSICAL" -> Color(0xFFFFA500)
        "SKL", "SKILL" -> Color(0xFFA020F0)
        "DEF", "DEFENDING" -> Color(0xFF00FF00)
        "PAS", "PASSING" -> Color(0xFFFFD700)
        "SHO", "SHOOTING" -> Color(0xFFFF4500)
        else -> Color(0xFFFFD700)
    }
}
