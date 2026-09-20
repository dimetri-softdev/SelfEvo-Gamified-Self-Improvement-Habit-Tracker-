package com.example.selfevo.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.data.model.PlayerCard

@Composable
fun FutPlayerCard(
    playerCard: PlayerCard,
    modifier: Modifier = Modifier
) {
    val goldColor = Color(0xFFFFD700)
    val amoledBlack = Color(0xFF000000)

    // Animated Shine effect for Gold/Walkout cards
    val infiniteTransition = rememberInfiniteTransition(label = "shine")
    val shineOffset by infiniteTransition.animateFloat(
        initialValue = -500f,
        targetValue = 1500f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shineOffset"
    )

    Box(
        modifier = modifier
            .width(300.dp)
            .height(420.dp)
            .graphicsLayer {
                shadowElevation = 20f
                shape = FutCardShape
                clip = true
            }
            .background(amoledBlack)
            .border(3.dp, goldColor, FutCardShape),
        contentAlignment = Alignment.Center
    ) {
        // Shine Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Transparent, goldColor.copy(alpha = 0.15f), Color.Transparent),
                        start = Offset(shineOffset, shineOffset),
                        end = Offset(shineOffset + 200f, shineOffset + 200f)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Row: OVR and Tier
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = playerCard.ovr.toString(),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Black,
                        color = goldColor
                    )
                    Text(
                        text = "HAB", // HABIT RATING
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = goldColor
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row {
                        repeat(4) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = goldColor, modifier = Modifier.size(14.dp))
                        }
                    }
                    Text(
                        text = playerCard.tier.uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = goldColor,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Player Avatar (Hexagon Style)
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(PlayerAvatarHexShape)
                    .background(Color(0xFF1A1A1A))
                    .border(2.dp, goldColor.copy(alpha = 0.5f), PlayerAvatarHexShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = goldColor, modifier = Modifier.size(80.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Player Name
            Text(
                text = playerCard.playerName.uppercase(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = goldColor,
                letterSpacing = 2.sp
            )

            Box(modifier = Modifier.width(60.dp).height(2.dp).background(goldColor).padding(top = 8.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Grid
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatBox(label = "PAC", value = playerCard.pace, color = goldColor)
                    StatBox(label = "SHO", value = playerCard.shooting, color = goldColor)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatBox(label = "PAS", value = playerCard.passing, color = goldColor)
                    StatBox(label = "PHY", value = playerCard.physical, color = goldColor)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatBox(label = "SKL", value = playerCard.skill, color = goldColor)
                    StatBox(label = "DEF", value = playerCard.defending, color = goldColor)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "SELFEVO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = goldColor.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun StatBox(label: String, value: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.width(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = color
        )
    }
}

val FutCardShape = GenericShape { size, _ ->
    val width = size.width
    val height = size.height
    moveTo(width * 0.5f, 0f)
    lineTo(width, height * 0.1f)
    lineTo(width, height * 0.9f)
    lineTo(width * 0.5f, height)
    lineTo(0f, height * 0.9f)
    lineTo(0f, height * 0.1f)
    close()
}

val PlayerAvatarHexShape = GenericShape { size, _ ->
    val width = size.width
    val height = size.height
    moveTo(width * 0.5f, 0f)
    lineTo(width, height * 0.25f)
    lineTo(width, height * 0.75f)
    lineTo(width * 0.5f, height)
    lineTo(0f, height * 0.75f)
    lineTo(0f, height * 0.25f)
    close()
}
