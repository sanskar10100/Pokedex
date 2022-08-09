package dev.sanskar.pokedex.network

import retrofit2.http.GET

interface Api {

    @GET("pokemon?limit=151")
    suspend fun getPokemons()
}