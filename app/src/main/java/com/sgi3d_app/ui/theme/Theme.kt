package com.sgi3d_app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = SGI3DBlue,
    secondary = SGI3DAccent,
    background = LightBackground
)

private val DarkColors = darkColorScheme(
    primary = SGI3DBlueDark,
    secondary = SGI3DAccentDark,
    background = DarkBackground
)

@Composable
fun SGI3DTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}