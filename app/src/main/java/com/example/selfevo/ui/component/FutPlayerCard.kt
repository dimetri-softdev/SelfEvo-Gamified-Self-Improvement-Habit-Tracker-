package com.example.selfevo.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.data.model.PlayerCard

@Composable
fun FutPlayerCard(
    playerCard: PlayerCard,
    modifier: Modifier = Modifier
) {
    val tierColors = when (playerCard.tier) {
        "Walkout" -> listOf(Color(0xFF6A0DAD), Color(0xFF8A2BE2), Color(0xFFDDA0DD))
        "Gold" -> listOf(Color(0xFFD4AF37), Color(0xFFFFD700), Color(0xFFF0E68C))
        "Silver" -> listOf(Color(0xFFC0C0C0), Color(0xFFE6E6E6), Color(0xFFDCDCDC))
        else -> listOf(Color(0xFFCD7F32), Color(0xFFD2691E), Color(0xFF8B4513))
    }

    val textColor = if (playerCard.tier == "Walkout") Color.White else Color(0xFF1C1C1C)

    Card(
        modifier = modifier
            .width(280.dp)
            .height(380.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(colors = tierColors),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(3.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top header: OVR & Tier Label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = playerCard.ovr.toString(),
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = textColor
                    )
                    Text(
                        text = playerCard.tier.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor.copy(alpha = 0.8f),
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Placeholder avatar circle or silhouette
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp)
                        .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(50.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = playerCard.playerName.take(1).uppercase(),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Player name
                Text(
                    text = playerCard.playerName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.weight(1f))

                // Stats Grid
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        StatLabelValue(label = "PAC", value = playerCard.pace, textColor = textColor)
                        StatLabelValue(label = "SHO", value = playerCard.shooting, textColor = textColor)
                        StatLabelValue(label = "PAS", value = playerCard.passing, textColor = textColor)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        StatLabelValue(label = "DRI", value = playerCard.dribbling, textColor = textColor)
                        StatLabelValue(label = "DEF", value = playerCard.defending, textColor = textColor)
                        StatLabelValue(label = "PHY", value = playerCard.physical, textColor = textColor)
                    }
                }
            }
        }
    }
}

@Composable
fun StatLabelValue(label: String, value: Int, textColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor.copy(alpha = 0.7f)
        )
    }
}
