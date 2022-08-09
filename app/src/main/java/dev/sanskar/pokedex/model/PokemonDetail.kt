package dev.sanskar.pokedex.model

import com.squareup.moshi.Json

data class PokemonDetail(
    @Json(name = "base_experience") val baseExperience: Int,
    val height: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val sprites: Sprites
)

data class Sprites(
    @Json(name = "front_default") val frontDefault: String
)
