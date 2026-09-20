package com.example.selfevo.ui.evolution

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.selfevo.ui.theme.*

@Composable
fun EvolutionScreen(
    currentOvr: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.your_journey).uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Gold,
            letterSpacing = 1.sp
        )
        Text(
            text = stringResource(R.string.evolution).uppercase(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        Text(
            text = stringResource(R.string.pathway_tier_progress).uppercase(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Current OVR section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceVariant)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, Gold, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = currentOvr.toString(), fontSize = 28.sp, fontWeight = FontWeight.Black, color = Gold)
                    Text(text = "OVR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gold)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "GOLD TIER", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Gold)
                Text(text = "3 ratings to WALKOUT", fontSize = 12.sp, color = Cyan, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 0.7f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = Cyan,
                    trackColor = Black
                )
                Text(text = "82/85 OVR to WALKOUT", fontSize = 10.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.tier_milestones).uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))

        TierItem(name = "BRONZE", range = "50-64 OVR", status = "UNLOCKED", isUnlocked = true)
        TierItem(name = "SILVER", range = "65-74 OVR", status = "UNLOCKED", isUnlocked = true)
        TierItem(name = "GOLD", range = "75-84 OVR", status = "CURRENT", isUnlocked = true, isCurrent = true)
        TierItem(name = "WALKOUT", range = "85+ OVR", status = "LOCKED", isUnlocked = false)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.card_preview).uppercase() + " — GOLD",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Card Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceVariant)
                .border(1.dp, Gold, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Gold, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "82", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Gold)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "GOLD CARD", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text(text = stringResource(R.string.overall_rating, 82), fontSize = 12.sp, color = TextSecondary)
                    Row {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Gold))
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Gold))
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.DarkGray))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun TierItem(name: String, range: String, status: String, isUnlocked: Boolean, isCurrent: Boolean = false) {
    val borderColor = if (isCurrent) Gold else Color.DarkGray
    val opacity = if (isUnlocked) 1f else 0.5f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isCurrent) SurfaceVariant else Color.Transparent),
        border = if (isCurrent) BorderStroke(1.dp, Gold) else BorderStroke(1.dp, Color.DarkGray)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).background(Color.Transparent),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceVariant)
                    .border(1.dp, if (isUnlocked) Gold else Color.Gray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("⭐", color = if (isUnlocked) Gold else Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = name, fontWeight = FontWeight.Black, color = if (isUnlocked) TextPrimary else Color.Gray)
                    if (isCurrent) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Gold.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(text = "CURRENT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Gold)
                        }
                    }
                }
                Text(text = range, fontSize = 12.sp, color = TextSecondary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (isUnlocked) Gold else Color.Gray))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) Gold else Color.Gray
                )
            }
        }
    }
}
