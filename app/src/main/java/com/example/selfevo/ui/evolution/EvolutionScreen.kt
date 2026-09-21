package com.example.selfevo.ui.evolution

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EvolutionScreen(currentOvr: Int) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            item {
                Text("YOUR JOURNEY", color = Color(0xFFFFA500), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("EVOLUTION", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
                Text("PATHWAY & TIER PROGRESS", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))

                Spacer(modifier = Modifier.height(48.dp))

                // Current Tier Progress
                CurrentProgressCard(ovr = currentOvr, targetTier = "WALKOUT", progress = (currentOvr - 50) / 35f)

                Spacer(modifier = Modifier.height(48.dp))

                Text("TIER MILESTONES", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                EvolutionMilestone(
                    tier = "BRONZE",
                    range = "50-64 OVR",
                    isUnlocked = currentOvr >= 50,
                    isCurrent = currentOvr in 50..64
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                EvolutionMilestone(
                    tier = "SILVER",
                    range = "65-74 OVR",
                    isUnlocked = currentOvr >= 65,
                    isCurrent = currentOvr in 65..74
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                EvolutionMilestone(
                    tier = "GOLD",
                    range = "75-84 OVR",
                    isUnlocked = currentOvr >= 75,
                    isCurrent = currentOvr in 75..84
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                EvolutionMilestone(
                    tier = "WALKOUT",
                    range = "85+ OVR",
                    isUnlocked = currentOvr >= 85,
                    isCurrent = currentOvr >= 85
                )
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
                Text("CARD PREVIEW — GOLD", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFF1A1A1A), RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("GOLD CARD PREVIEW", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun CurrentProgressCard(ovr: Int, targetTier: String, progress: Float) {
    val currentTierName = when {
        ovr >= 85 -> "WALKOUT TIER"
        ovr >= 75 -> "GOLD TIER"
        ovr >= 65 -> "SILVER TIER"
        else -> "BRONZE TIER"
    }

    val ratingsToNext = when {
        ovr < 65 -> 65 - ovr
        ovr < 75 -> 75 - ovr
        ovr < 85 -> 85 - ovr
        else -> 0
    }

    val displayTargetTier = when {
        ovr < 65 -> "SILVER"
        ovr < 75 -> "GOLD"
        ovr < 85 -> "WALKOUT"
        else -> "MAX"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFF1A1A1A), RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(ovr.toString(), fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD700))
                Text("OVR", fontSize = 10.sp, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(currentTierName, color = Color(0xFFFFD700), fontSize = 16.sp, fontWeight = FontWeight.Black)
            if (ratingsToNext > 0) {
                Text("$ratingsToNext ratings to $displayTargetTier", color = Color.White, fontSize = 14.sp)
            } else {
                Text("LEGENDARY STATUS REACHED", color = Color.White, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = Color(0xFF00E5FF),
                trackColor = Color(0xFF1A1A1A)
            )
        }
    }
}

@Composable
fun EvolutionMilestone(tier: String, range: String, isUnlocked: Boolean, isCurrent: Boolean) {
    val borderColor = if (isCurrent) Color(0xFFFFD700) else Color.White.copy(alpha = 0.1f)
    val textColor = if (isUnlocked) Color.White else Color.Gray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF0A0A0A), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isUnlocked) Icons.Default.Star else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isUnlocked) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(tier, fontWeight = FontWeight.Black, color = textColor, fontSize = 16.sp)
                        if (isCurrent) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFFD700), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("CURRENT", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text(range, color = Color.Gray, fontSize = 12.sp)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(if (isUnlocked) Color(0xFFFFA500) else Color.Gray, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isUnlocked) "UNLOCKED" else "LOCKED", color = if (isUnlocked) Color(0xFFFFA500) else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
