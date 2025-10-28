package com.ayaan.sellerdashboard.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Dark Color Scheme - Dark blue aesthetic
private val DarkColorScheme = darkColorScheme(
    primary = SkyBlue,
    onPrimary = White,
    primaryContainer = DarkBlueSurface,
    onPrimaryContainer = LightBlueLight,

    secondary = SkyBlueLight,
    onSecondary = White,
    secondaryContainer = DarkBlue,
    onSecondaryContainer = VeryLightBlue,

    tertiary = AccentOrange,
    onTertiary = White,

    background = DarkBlue,
    onBackground = OffWhite,

    surface = DarkBlueSurface,
    onSurface = OffWhite,

    surfaceVariant = LightBlueDark,
    onSurfaceVariant = LightBlueLight,

    error = ErrorRed,
    onError = White,
)

// Light Color Scheme - Light blue and white aesthetic
private val LightColorScheme = lightColorScheme(
    primary = LightBlue,
    onPrimary = White,
    primaryContainer = VeryLightBlue,
    onPrimaryContainer = LightBlueDark,

    secondary = SkyBlue,
    onSecondary = White,
    secondaryContainer = LightBlueLight,
    onSecondaryContainer = LightBlueDark,

    tertiary = AccentOrange,
    onTertiary = White,
    tertiaryContainer = Color(0xFFFFE0B2),
    onTertiaryContainer = Color(0xFFE65100),

    background = White,
    onBackground = DarkGray,

    surface = OffWhite,
    onSurface = DarkGray,

    surfaceVariant = LightGray,
    onSurfaceVariant = DarkGray,

    surfaceTint = LightBlue,

    inverseSurface = DarkGray,
    inverseOnSurface = White,

    error = ErrorRed,
    onError = White,
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFFC62828),

    outline = MediumGray,
    outlineVariant = LightGray,

    scrim = Color(0xFF000000),
)

@Composable
fun VendorDashboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

