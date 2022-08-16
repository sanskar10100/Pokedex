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
) {
    fun getNonNullCount() = getNonNullItems().size

    fun getNonNullItems(): List<String> {
        val list = mutableListOf<String>()
        if (back_default != null) list.add(back_default)
        if (back_female != null) list.add(back_female)
        if (back_shiny != null) list.add(back_shiny)
        if (back_shiny_female != null) list.add(back_shiny_female)
        if (front_default != null) list.add(front_default)
        if (front_female != null) list.add(front_female)
        if (front_shiny != null) list.add(front_shiny)
        if (front_shiny_female != null) list.add(front_shiny_female)
        return list
    }
}

data class Move(
    val move: NameData
)

data class Stat(
    @Json(name = "base_stat") val baseStat: Int,
    val stat: NameData
)
