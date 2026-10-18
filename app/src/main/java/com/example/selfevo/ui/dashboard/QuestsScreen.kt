package com.example.selfevo.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class QuestItem(
    val id: String,
    val title: String,
    val description: String,
    val rewardXp: Int,
    val isCompleted: Boolean
)

@Composable
fun QuestsScreen() {
    val quests = remember {
        mutableStateListOf(
            QuestItem("q1", "Consistency King", "Complete 5 habits this week", 250, true),
            QuestItem("q2", "Cardio Conditioning", "Log Pace attribute 3 times", 150, false),
            QuestItem("q3", "Elite Evolution", "Reach 75+ Gold Card Tier", 500, false)
        )
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
            item {
                Text(text = "WEEKLY QUESTS", color = Color(0xFFFFD700), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = "Challenges & Rewards", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
            }

            items(quests) { quest ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (quest.isCompleted) Color(0xFF00E5FF).copy(alpha = 0.5f) else Color(0xFFFFD700).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = quest.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = Color(0xFF2C1F00),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "+${quest.rewardXp} XP",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        color = Color(0xFFFFD700),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = quest.description, color = Color.Gray, fontSize = 13.sp)
                        }

                        Icon(
                            imageVector = if (quest.isCompleted) Icons.Default.CheckCircle else Icons.Default.Star,
                            contentDescription = null,
                            tint = if (quest.isCompleted) Color(0xFF00E5FF) else Color(0xFFFFD700)
                        )
                    }
                }
            }
        }
    }
}
