package dev.sanskar.pokedex.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Favorite(
    @PrimaryKey val id: Int,
    val name: String,
)