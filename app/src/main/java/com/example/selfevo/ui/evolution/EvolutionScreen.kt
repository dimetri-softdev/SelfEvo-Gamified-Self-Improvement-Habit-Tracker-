package com.example.selfevo.ui.evolution

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.R
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.ui.component.FutPlayerCard

data class EvolutionCardTheme(
    val key: String,
    val name: String,
    val requirementText: String,
    val difficulty: String,
    val isUnlocked: (ovr: Int, habitsCount: Int, streakDays: Int) -> Boolean
)

@Composable
fun EvolutionScreen(
    playerCard: PlayerCard?,
    completedHabitCount: Int = 0,
    currentStreakDays: Int = 14,
    onEquipTheme: (String) -> Unit = {}
) {
    val currentOvr = playerCard?.ovr ?: 50
    val equippedTheme = playerCard?.equippedTheme ?: "BRONZE"

    val cardThemes = listOf(
        EvolutionCardTheme("BRONZE", "Bronze Metallic", "Unlocked by default (OVR 0-64)", "") { _, _, _ -> true },
        EvolutionCardTheme("SILVER", "Silver Chrome", "Reach OVR 65+", "") { _, _, _ -> true },
        EvolutionCardTheme("GOLD", "Lustrous Gold", "Reach OVR 75+", "") { _, _, _ -> true },
        EvolutionCardTheme("ROOKIE_STARTER", "Rookie Starter Emerald", "Complete 5 total habits", "EASY") { _, _, _ -> true },
        EvolutionCardTheme("TOTW", "TOTW In-Form Onyx", "Maintain a 7-Day Habit Streak", "MEDIUM") { _, _, _ -> true },
        EvolutionCardTheme("WALKOUT", "Walkout Sheen Gold", "Reach OVR 85+", "MEDIUM") { _, _, _ -> true },
        EvolutionCardTheme("CYBERPUNK", "Cyberpunk Neon Grid", "Maintain a 15-Day Habit Streak", "MEDIUM") { _, _, _ -> true },
        EvolutionCardTheme("ICON_PRIME", "ICON Prime Marble & Gold", "Maintain a 30-Day Habit Streak", "HARD") { _, _, _ -> true },
        EvolutionCardTheme("TOTS", "TOTS Celestial Sapphire", "Reach OVR 90+", "HARD") { _, _, _ -> true },
        EvolutionCardTheme("DIAMOND_PLATINUM", "Diamond Platinum", "Complete 100 total habits", "END-GAME") { _, _, _ -> true }
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(stringResource(R.string.your_journey), color = Color(0xFFFFA500), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.evolution), fontSize = 32.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
                Text("Unlock rare FUT card themes & equip your active design", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Card
                val (nextTier, nextThreshold) = when {
                    currentOvr < 65 -> "SILVER" to 65
                    currentOvr < 75 -> "GOLD" to 75
                    else -> "WALKOUT" to 85
                }
                val progress = if (currentOvr < 85) {
                    val currentBase = when {
                        currentOvr < 65 -> 50
                        currentOvr < 75 -> 65
                        else -> 75
                    }
                    (currentOvr - currentBase).toFloat() / (nextThreshold - currentBase)
                } else 1.0f

                CurrentProgressCard(ovr = currentOvr, targetTier = nextTier, progress = progress)
            }

            item {
                Text("CARD THEMES GALLERY (10 THEMES)", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            items(cardThemes, key = { it.key }) { theme ->
                val unlocked = theme.isUnlocked(currentOvr, completedHabitCount, currentStreakDays)
                val isEquipped = equippedTheme.equals(theme.key, ignoreCase = true)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = if (isEquipped) 2.dp else 1.dp,
                        color = if (isEquipped) Color(0xFFFFD700) else if (unlocked) Color.White.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(if (unlocked) Color(0xFF262626) else Color(0xFF121212), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (unlocked) Icons.Default.Star else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isEquipped) Color(0xFFFFD700) else if (unlocked) Color(0xFF00E5FF) else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(theme.name, fontWeight = FontWeight.Black, color = if (unlocked) Color.White else Color.Gray, fontSize = 16.sp)
                                    if (theme.difficulty.isNotBlank()) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            color = when(theme.difficulty) {
                                                "EASY" -> Color(0xFF047857)
                                                "MEDIUM" -> Color(0xFFB45309)
                                                "HARD" -> Color(0xFFB91C1C)
                                                else -> Color(0xFF6B21A8)
                                            },
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(theme.difficulty, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                Text(theme.requirementText, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        if (isEquipped) {
                            Button(
                                onClick = { },
                                enabled = false,
                                colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF2C2400), disabledContentColor = Color(0xFFFFD700)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("EQUIPPED", fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        } else if (unlocked) {
                            Button(
                                onClick = { onEquipTheme(theme.key) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("EQUIP", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        } else {
                            Surface(
                                color = Color(0xFF261212),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
                            ) {
                                Text("LOCKED", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                            }
                        }
                    }
                }
            }

            item {
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
