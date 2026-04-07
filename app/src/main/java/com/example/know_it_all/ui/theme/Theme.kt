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

// ===== LIGHT COLOR SCHEME (Modern, Professional, Vibrant) =====
private val LightColorScheme = lightColorScheme(
    primary = Primary,                          // Deep Vibrant Blue
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    
    secondary = Secondary,                      // Hot Pink/Magenta
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    
    tertiary = Tertiary,                        // Teal
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

// ===== DARK COLOR SCHEME (Rich, Premium, Vibrant) =====
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,                      // Bright Blue for dark mode
    onPrimary = Color(0xFF000D2E),              // Very dark text on bright
    primaryContainer = PrimaryContainerDark,    // Deep blue container
    onPrimaryContainer = PrimaryDark,           // Bright blue text in container
    
    secondary = SecondaryDark,                  // Bright Hot Pink for dark mode
    onSecondary = Color(0xFF4A001C),            // Very dark red text
    secondaryContainer = SecondaryContainerDark, // Deep pink container
    onSecondaryContainer = SecondaryDark,       // Bright pink text in container
    
    tertiary = TertiaryDark,                    // Bright Teal for dark mode
    onTertiary = Color(0xFF001E23),             // Very dark teal text
    tertiaryContainer = TertiaryContainerDark,  // Deep teal container
    onTertiaryContainer = TertiaryDark,         // Bright teal text in container
    
    error = Color(0xFFFF8A80),                  // Bright red for dark mode
    onError = Color(0xFF3D0000),
    errorContainer = Color(0xFFD32F2F),
    
    background = BackgroundDark,                // Dark premium blue (#0D1B2A)
    onBackground = OnSurfaceDark,               // Light text
    
    surface = SurfaceDark,                      // Dark blue surface (#1A2332)
    onSurface = OnSurfaceDark,                  // Light text
    surfaceVariant = Color(0xFF232E3F),         // Slightly lighter surface
    onSurfaceVariant = Color(0xFFE0E0E0),       // Light gray text
    
    outline = Color(0xFF8A96A8),                // Light gray borders
    outlineVariant = Color(0xFF3A4B5F)          // Medium borders
)

@Composable
fun KnowItAllTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,  // Changed to false to use our custom vibrant theme
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