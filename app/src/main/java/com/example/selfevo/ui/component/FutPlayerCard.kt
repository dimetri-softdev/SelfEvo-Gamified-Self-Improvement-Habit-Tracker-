package com.example.selfevo.ui.component

import android.net.Uri
import java.util.Locale
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.selfevo.data.model.PlayerCard

/**
 * Data class representing color and theme styling for EA FC style Player Cards.
 */
data class CardThemeColors(
    val mainGradient: List<Color>,
    val beamGradient: List<Color>,
    val textColor: Color,
    val textSecondaryColor: Color,
    val borderColors: List<Color>,
    val shieldBorderColor: Color,
    val shieldBgColor: Color,
    val name: String
)

@Composable
fun getCardThemeColors(themeName: String, fallbackTier: String): CardThemeColors {
    val theme = themeName.uppercase()
    return when {
        theme == "DIAMOND_PLATINUM" -> CardThemeColors(
            mainGradient = listOf(Color(0xFFE0F7FA), Color(0xFF80DEEA), Color(0xFF00838F), Color(0xFF4DD0E1)),
            beamGradient = listOf(Color(0xFFFFFFFF), Color(0xFF80DEEA), Color(0xFF0097A7)),
            textColor = Color(0xFF004D40),
            textSecondaryColor = Color(0xFF00695C),
            borderColors = listOf(Color(0xFFFFFFFF), Color(0xFF80DEEA), Color(0xFF00838F)),
            shieldBorderColor = Color(0xFF004D40),
            shieldBgColor = Color(0x1F004D40),
            name = "DIAMOND"
        )
        theme == "TOTS" -> CardThemeColors(
            mainGradient = listOf(Color(0xFF0F2547), Color(0xFF1E3A8A), Color(0xFF0A1128), Color(0xFF132A5E)),
            beamGradient = listOf(Color(0xFF7DD3FC), Color(0xFF38BDF8), Color(0xFFFFD700)),
            textColor = Color(0xFFFFD700),
            textSecondaryColor = Color(0xFF7DD3FC),
            borderColors = listOf(Color(0xFFFFD700), Color(0xFF38BDF8), Color(0xFF1E3A8A), Color(0xFFFFD700)),
            shieldBorderColor = Color(0xFFFFD700),
            shieldBgColor = Color(0x33000000),
            name = "TOTS"
        )
        theme == "ICON_PRIME" -> CardThemeColors(
            mainGradient = listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9), Color(0xFFCBD5E1), Color(0xFFE2E8F0)),
            beamGradient = listOf(Color(0xFFFFF7ED), Color(0xFFFFD700), Color(0xFFB45309)),
            textColor = Color(0xFF1E293B),
            textSecondaryColor = Color(0xFFB45309),
            borderColors = listOf(Color(0xFFFFD700), Color(0xFFB45309), Color(0xFFFFD700)),
            shieldBorderColor = Color(0xFF1E293B),
            shieldBgColor = Color(0x1F000000),
            name = "ICON"
        )
        theme == "CYBERPUNK" -> CardThemeColors(
            mainGradient = listOf(Color(0xFF2E1065), Color(0xFF1E0B3A), Color(0xFF0F051D), Color(0xFF250A52)),
            beamGradient = listOf(Color(0xFF00E5FF), Color(0xFFFF007F), Color(0xFF7C3AED)),
            textColor = Color(0xFF00E5FF),
            textSecondaryColor = Color(0xFFFF007F),
            borderColors = listOf(Color(0xFF00E5FF), Color(0xFFFF007F), Color(0xFF00E5FF)),
            shieldBorderColor = Color(0xFF00E5FF),
            shieldBgColor = Color(0x33000000),
            name = "CYBER"
        )
        theme == "TOTW" -> CardThemeColors(
            mainGradient = listOf(Color(0xFF262626), Color(0xFF171717), Color(0xFF0A0A0A), Color(0xFF1F1F1F)),
            beamGradient = listOf(Color(0xFFFEF08A), Color(0xFFFFD700), Color(0xFFCA8A04)),
            textColor = Color(0xFFFFD700),
            textSecondaryColor = Color(0xFFEAB308),
            borderColors = listOf(Color(0xFFFFD700), Color(0xFFEAB308), Color(0xFFFFD700)),
            shieldBorderColor = Color(0xFFFFD700),
            shieldBgColor = Color(0x33000000),
            name = "TOTW"
        )
        theme == "ROOKIE_STARTER" -> CardThemeColors(
            mainGradient = listOf(Color(0xFF064E3B), Color(0xFF047857), Color(0xFF022C22), Color(0xFF065F46)),
            beamGradient = listOf(Color(0xFFA7F3D0), Color(0xFF34D399), Color(0xFFFFD700)),
            textColor = Color(0xFF34D399),
            textSecondaryColor = Color(0xFFFFD700),
            borderColors = listOf(Color(0xFF34D399), Color(0xFFFFD700), Color(0xFF34D399)),
            shieldBorderColor = Color(0xFF34D399),
            shieldBgColor = Color(0x33000000),
            name = "ROOKIE"
        )
        theme == "WALKOUT" || fallbackTier.equals("Walkout", ignoreCase = true) -> CardThemeColors(
            mainGradient = listOf(Color(0xFF312E81), Color(0xFF1E1B4B), Color(0xFF0F0E2E), Color(0xFF282467)),
            beamGradient = listOf(Color(0xFFE9D5FF), Color(0xFFC084FC), Color(0xFFFFD700)),
            textColor = Color(0xFFFFD700),
            textSecondaryColor = Color(0xFFE9D5FF),
            borderColors = listOf(Color(0xFFFFD700), Color(0xFFA855F7), Color(0xFFFFD700)),
            shieldBorderColor = Color(0xFFFFD700),
            shieldBgColor = Color(0x33000000),
            name = "WALKOUT"
        )
        theme == "GOLD" || fallbackTier.equals("Gold", ignoreCase = true) -> CardThemeColors(
            mainGradient = listOf(Color(0xFFF3E29F), Color(0xFFDCAE47), Color(0xFF9E771C), Color(0xFFCBA038)),
            beamGradient = listOf(Color(0xFFFFF7CB), Color(0xFFE5BE57), Color(0xFF9E771C)),
            textColor = Color(0xFF2A1C04),
            textSecondaryColor = Color(0xFF3D2C0C),
            borderColors = listOf(Color(0xFFFFF5C0), Color(0xFFB88C28), Color(0xFF5A410A), Color(0xFFD8B048)),
            shieldBorderColor = Color(0xFF2A1C04),
            shieldBgColor = Color(0x1F2A1C04),
            name = "GOLD"
        )
        theme == "SILVER" || fallbackTier.equals("Silver", ignoreCase = true) -> CardThemeColors(
            mainGradient = listOf(Color(0xFFF0F4F8), Color(0xFFCBD5E1), Color(0xFF8BA0B2), Color(0xFFBCC7D3)),
            beamGradient = listOf(Color(0xFFFFFFFF), Color(0xFFCBD5E1), Color(0xFF788896)),
            textColor = Color(0xFF222930),
            textSecondaryColor = Color(0xFF333E48),
            borderColors = listOf(Color(0xFFFFFFFF), Color(0xFF94A3B8), Color(0xFF475569), Color(0xFFCBD5E1)),
            shieldBorderColor = Color(0xFF222930),
            shieldBgColor = Color(0x1F222930),
            name = "SILVER"
        )
        else -> CardThemeColors(
            mainGradient = listOf(Color(0xFFE6A67E), Color(0xFFC77E53), Color(0xFF8A4926), Color(0xFFB66C44)),
            beamGradient = listOf(Color(0xFFFFDBC6), Color(0xFFC77E53), Color(0xFF773B1B)),
            textColor = Color(0xFF341708),
            textSecondaryColor = Color(0xFF4A2510),
            borderColors = listOf(Color(0xFFFFDAC4), Color(0xFFA65F37), Color(0xFF54290E), Color(0xFFC77E53)),
            shieldBorderColor = Color(0xFF341708),
            shieldBgColor = Color(0x1F341708),
            name = "BRONZE"
        )
    }
}

/**
 * Authentic EA FC Style Player Card Composable.
 */
@Composable
fun FutPlayerCard(
    playerCard: PlayerCard,
    customStatMap: Map<String, Int> = emptyMap(),
    modifier: Modifier = Modifier
) {
    val themeColors = getCardThemeColors(playerCard.equippedTheme, playerCard.tier)

    val infiniteTransition = rememberInfiniteTransition(label = "shine")
    val shineOffset by infiniteTransition.animateFloat(
        initialValue = -600f,
        targetValue = 1600f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shineOffset"
    )

    Box(
        modifier = modifier
            .width(300.dp)
            .height(490.dp)
            .graphicsLayer {
                shadowElevation = 24f
                shape = FutCardShape
                clip = true
            }
            .border(
                border = BorderStroke(
                    width = 4.dp,
                    brush = Brush.linearGradient(themeColors.borderColors)
                ),
                shape = FutCardShape
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // 1. Background Metallic Beams & Flares Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Base Metallic Background Gradient
            drawRect(
                brush = Brush.linearGradient(
                    colors = themeColors.mainGradient,
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                )
            )

            // Upper Center Spotlight Radial Gradient
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.35f), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.35f),
                    radius = w * 0.55f
                )
            )

            // Main Diagonal Metallic Beam 1 (Wide beam running top-right to lower-left)
            val beamPath1 = Path().apply {
                moveTo(w * 0.3f, 0f)
                lineTo(w * 1.1f, h * 0.55f)
                lineTo(w * 1.1f, h * 0.70f)
                lineTo(w * 0.0f, h * 0.10f)
                close()
            }
            drawPath(
                path = beamPath1,
                brush = Brush.linearGradient(
                    colors = listOf(
                        themeColors.beamGradient.first().copy(alpha = 0.7f),
                        themeColors.beamGradient.last().copy(alpha = 0.2f)
                    ),
                    start = Offset(w * 0.8f, h * 0.2f),
                    end = Offset(w * 0.2f, h * 0.5f)
                )
            )

            // Secondary Diagonal Metallic Beam 2 (Parallel accent stripe)
            val beamPath2 = Path().apply {
                moveTo(w * 0.45f, 0f)
                lineTo(w * 1.1f, h * 0.45f)
                lineTo(w * 1.1f, h * 0.48f)
                lineTo(w * 0.40f, 0f)
                close()
            }
            drawPath(
                path = beamPath2,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.8f),
                        themeColors.beamGradient.first().copy(alpha = 0.3f)
                    )
                )
            )

            // Dot Matrix Texture Grid (Right middle area)
            val dotRadius = 1.8f
            val startX = w * 0.78f
            val startY = h * 0.42f
            for (row in 0..4) {
                for (col in 0..3) {
                    drawCircle(
                        color = themeColors.textColor.copy(alpha = 0.12f),
                        radius = dotRadius,
                        center = Offset(startX + col * 10f, startY + row * 12f)
                    )
                }
            }

            // Lens Flare Sparkle 1 along the beam
            drawSparkle(
                center = Offset(w * 0.65f, h * 0.35f),
                color = Color.White.copy(alpha = 0.9f),
                size = 18f
            )
            // Lens Flare Sparkle 2
            drawSparkle(
                center = Offset(w * 0.42f, h * 0.22f),
                color = Color.White.copy(alpha = 0.75f),
                size = 14f
            )
        }

        // 2. Player Portrait Area (Silhouette or User Photo)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .padding(top = 18.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (!playerCard.avatarUri.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(model = Uri.parse(playerCard.avatarUri)),
                    contentDescription = "Player Portrait",
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            drawContent()
                            // Gradient blend mask at the chest area so user photo smoothly fades into card
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Transparent,
                                        themeColors.mainGradient.first().copy(alpha = 0.85f),
                                        themeColors.mainGradient.first()
                                    ),
                                    startY = size.height * 0.6f,
                                    endY = size.height
                                )
                            )
                        },
                    contentScale = ContentScale.Crop
                )
            } else {
                // EA FC Style Shadow Silhouette Head & Shoulders
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Head
                    drawOval(
                        color = themeColors.textColor.copy(alpha = 0.28f),
                        topLeft = Offset(w * 0.35f, h * 0.18f),
                        size = Size(w * 0.30f, h * 0.32f)
                    )

                    // Shoulders & Chest Silhouette Path
                    val shoulderPath = Path().apply {
                        moveTo(w * 0.12f, h * 0.85f)
                        quadraticBezierTo(w * 0.20f, h * 0.46f, w * 0.36f, h * 0.44f)
                        lineTo(w * 0.64f, h * 0.44f)
                        quadraticBezierTo(w * 0.80f, h * 0.46f, w * 0.88f, h * 0.85f)
                        lineTo(w * 0.88f, h)
                        lineTo(w * 0.12f, h)
                        close()
                    }
                    drawPath(
                        path = shoulderPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                themeColors.textColor.copy(alpha = 0.28f),
                                themeColors.textColor.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
                }
            }
        }

        // 3. Top-Left OVR Shield Badge
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 22.dp, top = 28.dp)
                .size(62.dp, 76.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = createOvrShieldPath(size.width, size.height)

                // Shield background fill
                drawPath(
                    path = path,
                    color = themeColors.shieldBgColor
                )

                // Outer Shield Border
                drawPath(
                    path = path,
                    color = themeColors.shieldBorderColor,
                    style = Stroke(width = 3.5f)
                )

                // Inner Contour Shield Border
                val innerPath = createOvrShieldPath(size.width * 0.88f, size.height * 0.88f)
                drawContext.canvas.save()
                drawContext.canvas.translate(size.width * 0.06f, size.height * 0.06f)
                drawPath(
                    path = innerPath,
                    color = themeColors.shieldBorderColor.copy(alpha = 0.7f),
                    style = Stroke(width = 1.5f)
                )
                drawContext.canvas.restore()
            }

            // OVR Rating Number inside Shield
            Text(
                text = playerCard.ovr.toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = themeColors.textColor,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 6.dp)
            )
        }

        // 4. Lower Info Panel (Player Name, 6 Horizontal Stats, Tier Title)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 12.dp, end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Player Name Text
            Text(
                text = playerCard.playerName.uppercase(),
                fontSize = 21.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = themeColors.textColor,
                letterSpacing = 1.2.sp,
                maxLines = 1,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 6 Stats in One Horizontal Row (PAC SHO PAS DRI DEF PHY)
            val stat1 = getStatValue(playerCard.slot1, playerCard, customStatMap)
            val stat2 = getStatValue(playerCard.slot2, playerCard, customStatMap)
            val stat3 = getStatValue(playerCard.slot3, playerCard, customStatMap)
            val stat4 = getStatValue(playerCard.slot4, playerCard, customStatMap)
            val stat5 = getStatValue(playerCard.slot5, playerCard, customStatMap)
            val stat6 = getStatValue(playerCard.slot6, playerCard, customStatMap)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatColumn(label = formatStatLabel(playerCard.slot1), value = stat1, color = themeColors.textColor)
                StatColumn(label = formatStatLabel(playerCard.slot2), value = stat2, color = themeColors.textColor)
                StatColumn(label = formatStatLabel(playerCard.slot3), value = stat3, color = themeColors.textColor)
                StatColumn(label = formatStatLabel(playerCard.slot4), value = stat4, color = themeColors.textColor)
                StatColumn(label = formatStatLabel(playerCard.slot5), value = stat5, color = themeColors.textColor)
                StatColumn(label = formatStatLabel(playerCard.slot6), value = stat6, color = themeColors.textColor)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Card Tier Title at the bottom (e.g. GOLD, SILVER, BRONZE)
            Text(
                text = themeColors.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = themeColors.textColor,
                letterSpacing = 2.sp
            )
        }

        // 5. Animated Holographic Light Sweep Across Card Surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    val currentShine = shineOffset
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.18f),
                                Color.Transparent
                            ),
                            start = Offset(currentShine, currentShine),
                            end = Offset(currentShine + 220f, currentShine + 220f)
                        )
                    )
                }
        )
    }
}

@Composable
private fun StatColumn(
    label: String,
    value: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            color = color.copy(alpha = 0.85f),
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = if (value > 0) String.format(Locale.getDefault(), "%02d", value) else "--",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = color
        )
    }
}

private fun formatStatLabel(label: String): String {
    return when (label.uppercase()) {
        "SKL", "SKILL" -> "DRI"
        "PACE" -> "PAC"
        "SHOOTING" -> "SHO"
        "PASSING" -> "PAS"
        "DEFENDING" -> "DEF"
        "PHYSICAL" -> "PHY"
        else -> label.uppercase()
    }
}

private fun getStatValue(acronym: String, card: PlayerCard, customMap: Map<String, Int>): Int {
    return when (acronym.uppercase()) {
        "PAC", "PACE" -> card.pace
        "SHO", "SHOOTING" -> card.shooting
        "PAS", "PASSING" -> card.passing
        "SKL", "SKILL", "DRI", "DRIBBLING" -> card.skill
        "DEF", "DEFENDING" -> card.defending
        "PHY", "PHYSICAL" -> card.physical
        else -> customMap[acronym.uppercase()] ?: 0
    }
}

/**
 * Helper to draw 4-point lens flare sparkle lines.
 */
private fun DrawScope.drawSparkle(
    center: Offset,
    color: Color,
    size: Float
) {
    drawLine(
        color = color,
        start = Offset(center.x - size, center.y),
        end = Offset(center.x + size, center.y),
        strokeWidth = 2.5f
    )
    drawLine(
        color = color,
        start = Offset(center.x, center.y - size),
        end = Offset(center.x, center.y + size),
        strokeWidth = 2.5f
    )
    drawCircle(color = color, radius = size * 0.25f, center = center)
}

/**
 * EA FC Card Outer Contour Shape.
 */
val FutCardShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(0f, h * 0.055f)
    lineTo(w * 0.07f, h * 0.04f)
    quadraticBezierTo(w * 0.5f, -h * 0.015f, w * 0.93f, h * 0.04f)
    lineTo(w, h * 0.055f)
    lineTo(w, h * 0.895f)
    quadraticBezierTo(w * 0.85f, h * 0.93f, w * 0.5f, h)
    quadraticBezierTo(w * 0.15f, h * 0.93f, 0f, h * 0.895f)
    close()
}

/**
 * Creates Path for the OVR Shield Badge at top-left.
 */
private fun createOvrShieldPath(w: Float, h: Float): Path {
    return Path().apply {
        moveTo(0f, h * 0.08f)
        quadraticBezierTo(w * 0.5f, 0f, w, h * 0.08f)
        lineTo(w, h * 0.72f)
        quadraticBezierTo(w * 0.85f, h * 0.85f, w * 0.5f, h)
        quadraticBezierTo(w * 0.15f, h * 0.85f, 0f, h * 0.72f)
        close()
    }
}

// ==================== COMPOSE PREVIEWS ====================

@Preview(showBackground = true, name = "Gold EA FC Player Card")
@Composable
fun FutPlayerCardGoldPreview() {
    FutPlayerCard(
        playerCard = PlayerCard(
            id = "preview_gold",
            playerName = "Player Name",
            pace = 88,
            shooting = 90,
            passing = 85,
            skill = 92,
            defending = 45,
            physical = 80,
            equippedTheme = "GOLD"
        )
    )
}

@Preview(showBackground = true, name = "Silver EA FC Player Card")
@Composable
fun FutPlayerCardSilverPreview() {
    FutPlayerCard(
        playerCard = PlayerCard(
            id = "preview_silver",
            playerName = "Player Name",
            pace = 74,
            shooting = 70,
            passing = 72,
            skill = 75,
            defending = 68,
            physical = 71,
            equippedTheme = "SILVER"
        )
    )
}

@Preview(showBackground = true, name = "Bronze EA FC Player Card")
@Composable
fun FutPlayerCardBronzePreview() {
    FutPlayerCard(
        playerCard = PlayerCard(
            id = "preview_bronze",
            playerName = "Player Name",
            pace = 64,
            shooting = 60,
            passing = 58,
            skill = 62,
            defending = 55,
            physical = 65,
            equippedTheme = "BRONZE"
        )
    )
}
