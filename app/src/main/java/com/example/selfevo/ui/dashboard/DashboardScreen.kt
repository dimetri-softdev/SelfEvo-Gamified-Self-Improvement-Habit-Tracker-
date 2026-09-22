package com.example.selfevo.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.R
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.ui.component.FutPlayerCard
import com.example.selfevo.ui.component.HabitItemRow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val playerCard by viewModel.playerCard.collectAsState()
    val habits by viewModel.habits.collectAsState()

    var showWalkoutOvr by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(key1 = viewModel) {
        viewModel.levelUpEvent.collectLatest { newOvr ->
            showWalkoutOvr = newOvr
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))

                    // 1. Personalized Header Section
                    HeaderSection(playerName = playerCard?.playerName ?: "Player")

                    Spacer(modifier = Modifier.height(32.dp))

                    // 2. Daily Progress Section
                    ProgressSection(completedCount = habits.count { it.isCompletedToday }, totalCount = habits.size)

                    Spacer(modifier = Modifier.height(32.dp))

                    // 3. Creative Card Placeholder
                    if (playerCard != null) {
                        FutPlayerCard(playerCard = playerCard!!)
                    } else {
                        SkeletonCard()
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = stringResource(R.string.todays_habits),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (habits.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.no_habits),
                            color = Color.DarkGray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 32.dp)
                        )
                    }
                } else {
                    items(habits, key = { it.id }) { habit ->
                        HabitItemRow(
                            habit = habit,
                            onCompleteClick = { viewModel.completeHabit(habit.id) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            // High-Impact Walkout Overlay
            AnimatedVisibility(
                visible = showWalkoutOvr != null,
                enter = fadeIn() + scaleIn(initialScale = 0.8f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                exit = fadeOut() + scaleOut(targetScale = 1.2f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.95f))
                        .clickable(enabled = true) { showWalkoutOvr = null },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "⭐ WALKOUT ⭐",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD700),
                            letterSpacing = 4.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.walkout_evolved),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        playerCard?.let { card ->
                            FutPlayerCard(playerCard = card)
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "NEW OVR: ${showWalkoutOvr ?: ""}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFD700)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { showWalkoutOvr = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(stringResource(R.string.continue_journey), color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkeletonCard() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .alpha(alpha)
            .background(Color(0xFF1A1A1A), RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(100.dp).background(Color(0xFF2C2C2C), CircleShape))
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.width(150.dp).height(20.dp).background(Color(0xFF2C2C2C)))
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.width(100.dp).height(10.dp).background(Color(0xFF2C2C2C)))
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
                    .size(56.dp)
                    .background(Color(0xFF1A1A1A), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFFFD700))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(stringResource(R.string.good_morning), color = Color.Gray, fontSize = 12.sp)
                Text(playerName.uppercase(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
        }

        // Streak Flame
        Row(
            modifier = Modifier
                .background(Color(0xFF1A1A1A), RoundedCornerShape(50.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔥", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text("14 day", color = Color(0xFFFFD700), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProgressSection(completedCount: Int, totalCount: Int) {
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(R.string.daily_progress), color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("$completedCount/$totalCount", color = Color(0xFFFFD700), fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(Color(0xFF1A1A1A), RoundedCornerShape(50.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(listOf(Color(0xFFFFA500), Color(0xFFFFD700))),
                        shape = RoundedCornerShape(50.dp)
                    )
            )
        }
    }
}
