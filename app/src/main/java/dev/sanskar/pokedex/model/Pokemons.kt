package dev.sanskar.pokedex.model

data class Pokemons(
    val count: Int,
    val next: String,
    val previous: String?,
    val results: List<NameData>
)