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
    val back_default: String?,
    val back_female: String?,
    val back_shiny: String?,
    val back_shiny_female: String?,
    val front_default: String,
    val front_female: String?,
    val front_shiny: String?,
    val front_shiny_female: String?
)

data class Move(
    val move: NameData
)

data class Stat(
    @Json(name = "base_stat") val baseStat: Int,
    val stat: NameData
)
