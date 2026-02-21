package com.example.allinone.ui.theme

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// This is the color scheme you already defined
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00C9A7),    // A vibrant teal for primary actions
    background = Color(0xFF0F111A), // A very dark blue, almost black
    surface = Color(0xFF1C1F2E)     // A slightly lighter dark blue for cards and surfaces
)

// You can also define a LightColorScheme for light mode, but it's optional
private val LightColorScheme = darkColorScheme(
    primary = Color(0xFF008577),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF2F2F2)
    /* Other default colors to override */
)


// This is the main Theme function you need to add or update
@Composable
fun AllinoneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Automatically detect system theme
    content: @Composable () -> Unit
) {
    // 1. Select the correct color scheme based on the system setting
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        // You can use a LightColorScheme here if you've made one
        DarkColorScheme // Using DarkColorScheme for both for now
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Use background color for status bar
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // 2. Apply the chosen color scheme to the MaterialTheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assumes you have a Typography.kt file
        content = content
    )
}
