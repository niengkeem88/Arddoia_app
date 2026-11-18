package com.ar.arddoia.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object ArddoiaColors {
    val Background = Color(0xFF0F120E)
    val SurfaceCard = Color(0xFF181B18)
    val SurfaceElevated = Color(0xFF212521)
    val BottomBar = Color(0xFF131613)
    val HeroGreen = Color(0xFF336339)
    val HeroTrack = Color(0xFF47774D)
    val MintAccent = Color(0xFF9ED5A2)
    val MintButton = Color(0xFFA6D9A8)
    val MintButtonText = Color(0xFF0E2912)
    val FabGreen = Color(0xFF37653C)
    val NavIndicator = Color(0xFF334434)
    val GoalSelectedBg = Color(0xFF283D2A)
    val EmptyCell = Color(0xFF252925)
    val TextPrimary = Color(0xFFF3F5F2)
    val TextSecondary = Color(0xFFA2A8A1)
    val OutlineMuted = Color(0xFF323732)
}

private val ArddoiaDarkColorScheme = darkColorScheme(
    primary = ArddoiaColors.MintAccent,
    onPrimary = ArddoiaColors.MintButtonText,
    primaryContainer = ArddoiaColors.HeroGreen,
    onPrimaryContainer = ArddoiaColors.TextPrimary,
    secondary = ArddoiaColors.FabGreen,
    onSecondary = ArddoiaColors.TextPrimary,
    secondaryContainer = ArddoiaColors.NavIndicator,
    onSecondaryContainer = ArddoiaColors.TextPrimary,
    background = ArddoiaColors.Background,
    onBackground = ArddoiaColors.TextPrimary,
    surface = ArddoiaColors.SurfaceCard,
    onSurface = ArddoiaColors.TextPrimary,
    surfaceVariant = ArddoiaColors.SurfaceElevated,
    onSurfaceVariant = ArddoiaColors.TextSecondary,
    outline = ArddoiaColors.OutlineMuted
)

private val ArddoiaTypography = Typography(
    headlineLarge = TextStyle(
        fontSize = 32.sp,
        lineHeight = 38.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp,
        color = ArddoiaColors.TextPrimary
    ),
    headlineSmall = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Bold,
        color = ArddoiaColors.TextPrimary
    ),
    titleLarge = TextStyle(
        fontSize = 20.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.SemiBold,
        color = ArddoiaColors.TextPrimary
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.SemiBold,
        color = ArddoiaColors.TextPrimary
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
        color = ArddoiaColors.TextSecondary
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
        color = ArddoiaColors.TextSecondary
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
        color = ArddoiaColors.TextPrimary
    )
)

@Composable
fun ArddoiaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ArddoiaDarkColorScheme,
        typography = ArddoiaTypography,
        content = content
    )
}
