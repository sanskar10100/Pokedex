package dev.sanskar.pokedex.model

import com.squareup.moshi.Json

data class PokemonDetail(
    @Json(name = "base_experience") val baseExperience: Int,
    val height: Int,
    val id: Int,
    val name: String,
    val moves: List<Move>,
    val order: Int,
    val sprites: Sprites,
    val stats: List<Stat>,
    val weight: Int
)

data class Sprites(
    @Json(name = "front_default") val frontDefault: String
)

data class Move(
    val move: NameData
)

data class Stat(
    @Json(name = "base_stat") val baseStat: Int,
    val stat: NameData
)
