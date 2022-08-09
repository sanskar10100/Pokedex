package dev.sanskar.pokedex.network

import dev.sanskar.pokedex.model.PokemonDetail
import dev.sanskar.pokedex.model.Pokemons
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {

    @GET("pokemon?limit=20")
    suspend fun getPokemons(): Response<Pokemons>

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(
        @Path("name") name: String
    ): Response<PokemonDetail>
}