package dev.sanskar.pokedex

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.clickWithRipple(onClick: () -> Unit) = composed {
    Modifier.clickable(
        indication = rememberRipple(bounded = true),
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() }
    )
}