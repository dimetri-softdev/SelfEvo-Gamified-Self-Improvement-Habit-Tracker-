package com.example.selfevo.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.R
import com.example.selfevo.ui.component.FutPlayerCard
import com.example.selfevo.ui.component.HabitItemRow
import com.example.selfevo.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val playerCard by viewModel.playerCard.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val completedCount = habits.count { it.isCompletedToday }
    val totalCount = habits.size

    Scaffold(
        containerColor = Black,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                HeaderSection(playerName = playerCard?.playerName ?: "User")
                Spacer(modifier = Modifier.height(24.dp))

                ProgressSection(completedCount = completedCount, totalCount = totalCount)

                Spacer(modifier = Modifier.height(24.dp))

                playerCard?.let { card ->
                    FutPlayerCard(playerCard = card)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.todays_habits),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Cyan.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.in_progress).uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Cyan
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(habits) { habit ->
                HabitItemRow(
                    habit = habit,
                    onCompleteClick = { viewModel.completeHabit(habit.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom nav
            }
        }
    }
}

@Composable
fun HeaderSection(playerName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.good_morning),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = playerName.uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Streak
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🔥", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.streak_days, 14),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            // Language
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("EN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Cyan)
                    Icon(Icons.Default.KeyboardArrowDown, null, modifier = Modifier.size(14.dp), tint = Cyan)
                }
            }
        }
    }
}

@Composable
fun ProgressSection(completedCount: Int, totalCount: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.daily_progress),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Text(
                text = "$completedCount/$totalCount",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Gold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { if (totalCount > 0) completedCount.toFloat() / totalCount else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = Gold,
            trackColor = SurfaceVariant
        )
    }
}
