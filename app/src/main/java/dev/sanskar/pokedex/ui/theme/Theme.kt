package dev.sanskar.pokedex.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal

private val LightColorScheme = lightColors(
    primary = Purple40,
    secondary = PurpleGrey40,
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun PokedexTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = LightColorScheme,
        content = content
    )
}