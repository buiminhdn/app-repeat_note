package com.marcus.repeatnote.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    background = Neutral50,
    surface = White,
    surfaceVariant = Neutral100,
    onBackground = Neutral900,
    onSurface = Neutral900,
    onSurfaceVariant = Neutral400,
    primary = Neutral900,
    onPrimary = White,
    secondary = Accent,
    onSecondary = White,
    outline = Neutral200,
    outlineVariant = Neutral150,
)

private val DarkColorScheme = darkColorScheme(
    background = Neutral900,
    surface = Neutral800,
    surfaceVariant = Neutral700,
    onBackground = LightText,
    onSurface = LightText,
    onSurfaceVariant = Neutral400,
    primary = LightText,
    onPrimary = Neutral900,
    secondary = Accent,
    onSecondary = White,
    outline = Neutral600,
    outlineVariant = Neutral600,
)

@Composable
fun RepeatNoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RepeatNoteTypography,
        shapes = RepeatNoteShapes,
        content = content,
    )
}