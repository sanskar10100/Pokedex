package dev.sanskar.pokedex.network

import dev.sanskar.pokedex.model.Pokemons
import retrofit2.Response
import retrofit2.http.GET

interface Api {

    @GET("pokemon?limit=151")
    suspend fun getPokemons(): Response<Pokemons>
}