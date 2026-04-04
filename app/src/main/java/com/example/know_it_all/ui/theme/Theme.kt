package com.example.know_it_all.ui.theme

import android.app.Activity
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

// ===== LIGHT COLOR SCHEME (Modern, Professional) =====
private val LightColorScheme = lightColorScheme(
    primary = Primary,                          // Vibrant Blue
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    
    secondary = Secondary,                      // Coral Orange
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    
    tertiary = Tertiary,                        // Emerald Green
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    
    background = Background,
    onBackground = OnBackground,
    
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    
    outline = Outline,
    outlineVariant = OutlineVariant
)

// ===== DARK COLOR SCHEME (Modern, Professional) =====
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,                      // Light Blue (dark mode)
    onPrimary = Color(0xFF001A73),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = PrimaryDark,
    
    secondary = SecondaryDark,                  // Light Coral (dark mode)
    onSecondary = Color(0xFF4D1500),
    secondaryContainer = Color(0xFF7A2000),
    onSecondaryContainer = SecondaryDark,
    
    tertiary = TertiaryDark,                    // Light Green (dark mode)
    onTertiary = Color(0xFF003D2E),
    tertiaryContainer = Color(0xFF035841),
    onTertiaryContainer = TertiaryDark,
    
    error = Color(0xFFF87171),
    onError = Color(0xFF7F1D1D),
    errorContainer = Color(0xFF7F1D1D),
    
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF475569)
)

@Composable
fun KnowItAllTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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