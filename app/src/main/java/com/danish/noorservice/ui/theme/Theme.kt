package com.danish.noorservice.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val NoorLightColorScheme = lightColorScheme(
    primary          = NoorBlue,
    onPrimary        = NoorSurface,
    primaryContainer = NoorBlueLight,
    onPrimaryContainer = NoorBlueDark,

    secondary        = NoorGreen,
    onSecondary      = NoorSurface,
    secondaryContainer = NoorGreenLight,
    onSecondaryContainer = NoorGreenDark,

    tertiary         = NoorOrange,
    onTertiary       = NoorSurface,
    tertiaryContainer = NoorOrangeLight,

    error            = NoorRed,
    onError          = NoorSurface,
    errorContainer   = NoorRedLight,

    background       = NoorBackground,
    onBackground     = NoorTextPrimary,

    surface          = NoorSurface,
    onSurface        = NoorTextPrimary,
    surfaceVariant   = NoorBlueSurface,
    onSurfaceVariant = NoorTextSecondary,

    outline          = NoorBorder,
    outlineVariant   = NoorDivider,
)

@Composable
fun NoorServiceTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = NoorLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NoorBlue.toArgb()
            window.navigationBarColor = NoorBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            view.setBackgroundColor(NoorBackground.toArgb())
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}