package com.example.selfevo.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.ui.theme.*

@Composable
fun FutPlayerCard(
    playerCard: PlayerCard,
    modifier: Modifier = Modifier
) {
    val tierColor = when (playerCard.tier) {
        "Walkout" -> GoldGradientStart
        "Gold" -> GoldGradientStart
        "Silver" -> Color(0xFFC0C0C0)
        else -> Color(0xFFCD7F32)
    }

    // Outer glow for Gold/Walkout cards
    val glowBrush = if (playerCard.ovr >= 75) {
        Brush.radialGradient(
            colors = listOf(Gold.copy(alpha = 0.3f), Color.Transparent),
            radius = 600f
        )
    } else null

    Box(
        modifier = modifier
            .padding(16.dp)
            .then(if (glowBrush != null) Modifier.background(glowBrush) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(260.dp)
                .height(340.dp),
            shape = HexagonShape,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(DarkGrey, Black)
                        )
                    )
                    .border(2.dp, tierColor, HexagonShape)
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top: OVR and Rating
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = playerCard.ovr.toString(),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                color = tierColor
                            )
                            Text(
                                text = playerCard.tier.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = tierColor
                            )
                        }

                        // Stars for rating? Design 3 shows 4 dots/stars
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            repeat(4) {
                                Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(tierColor))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Avatar Placeholder
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(HexagonShape)
                            .background(SurfaceVariant)
                            .border(1.dp, Color.DarkGray, HexagonShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // User icon placeholder
                        Text(
                            text = "👤",
                            fontSize = 40.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Player Name
                    Text(
                        text = playerCard.playerName.uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = tierColor,
                        letterSpacing = 2.sp
                    )

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(tierColor.copy(alpha = 0.5f)))

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats Grid
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        StatLabelValue(label = "PAC", value = playerCard.pace, color = TextPrimary)
                        StatLabelValue(label = "SHO", value = playerCard.shooting, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        StatLabelValue(label = "PAS", value = playerCard.passing, color = TextPrimary)
                        StatLabelValue(label = "PHY", value = playerCard.physical, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        StatLabelValue(label = "SKL", value = playerCard.skill, color = TextPrimary)
                        StatLabelValue(label = "DEF", value = playerCard.defending, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "SELFEVO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = tierColor.copy(alpha = 0.7f),
                        letterSpacing = 4.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatLabelValue(label: String, value: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color.copy(alpha = 0.6f)
        )
        Text(
            text = value.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = color
        )
    }
}
