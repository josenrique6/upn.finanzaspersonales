// Theme.kt
package com.example.upnfinanzaspersonales.ui.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.ViewCompat

private val DarkColorScheme = darkColorScheme(
    primary        = PrimaryDark,
    onPrimary      = Color.White,
    secondary      = Info,
    onSecondary    = Color.White,
    tertiary       = Accent,
    onTertiary     = Color.White,
    background     = Color(0xFF121212),
    onBackground   = Color.White,
    surface        = Color(0xFF121212),
    onSurface      = Color.White,
    error          = ErrorColor,
    onError        = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary        = Primary,
    onPrimary      = Color.White,
    secondary      = Info,
    onSecondary    = Color.White,
    tertiary       = Accent,
    onTertiary     = Color.White,
    background     = Background,
    onBackground   = TextPrimary,
    surface        = Surface,
    onSurface      = TextPrimary,
    error          = ErrorColor,
    onError        = Color.White
)

@SuppressLint("ContextCastToActivity")
@Composable
fun UpnfinanzaspersonalesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    // opcional: adaptar la barra de estado en Android 12+
    val view = (LocalContext.current as Activity).window
    view.statusBarColor = colorScheme.primary.toArgb()
    ViewCompat.getWindowInsetsController(view.decorView)?.isAppearanceLightStatusBars = !darkTheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
