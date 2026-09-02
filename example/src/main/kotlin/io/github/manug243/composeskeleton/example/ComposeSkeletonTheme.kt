package io.github.manug243.composeskeleton.example

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF315DA8),
    secondary = Color(0xFF575E71),
    surface = Color(0xFFFAF8FF),
    background = Color(0xFFFAF8FF),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFAAC7FF),
    secondary = Color(0xFFBFC6DC),
    surface = Color(0xFF121318),
    background = Color(0xFF121318),
)

@Composable
fun ComposeSkeletonTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
