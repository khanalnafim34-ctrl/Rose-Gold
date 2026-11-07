package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = DeepBlue,
  onPrimary = Color.White,
  primaryContainer = LightBlueBg,
  onPrimaryContainer = NavyPrimary,
  secondary = DeepBlueAccent,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFDBEAFE),
  onSecondaryContainer = Color(0xFF1E3A8A),
  tertiary = MoneyGreen,
  onTertiary = Color.White,
  background = BackgroundLight,
  onBackground = TextPrimaryLight,
  surface = SurfaceLight,
  onSurface = TextPrimaryLight,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = TextSecondaryLight,
  outline = CardBorderLight,
  error = DebtRed,
  onError = Color.White,
  errorContainer = DebtRedLight,
  onErrorContainer = DebtRedDark
)

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFF60A5FA),
  onPrimary = Color(0xFF0F172A),
  primaryContainer = Color(0xFF1E3A8A),
  onPrimaryContainer = Color(0xFFDBEAFE),
  secondary = Color(0xFF93C5FD),
  onSecondary = Color(0xFF0F172A),
  secondaryContainer = Color(0xFF1E293B),
  onSecondaryContainer = Color(0xFFE2E8F0),
  tertiary = MoneyGreen,
  onTertiary = Color.White,
  background = BackgroundDark,
  onBackground = TextPrimaryDark,
  surface = SurfaceDark,
  onSurface = TextPrimaryDark,
  surfaceVariant = CardSurfaceDark,
  onSurfaceVariant = TextSecondaryDark,
  outline = CardBorderDark,
  error = Color(0xFFF87171),
  onError = Color(0xFF450A0A),
  errorContainer = Color(0xFF7F1D1D),
  onErrorContainer = Color(0xFFFECACA)
)

@Composable
fun MonethakaTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
