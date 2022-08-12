package dev.sanskar.pokedex

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.text.intl.Locale

fun Modifier.clickWithRipple(bounded: Boolean = true, onClick: () -> Unit) = composed {
    Modifier.clickable(
        indication = rememberRipple(bounded = bounded),
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() }
    )
}

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar { it.uppercase() } }