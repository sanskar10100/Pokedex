package dev.sanskar.pokedex.repo

import android.util.Log
import dev.sanskar.pokedex.db.PokedexDao
import dev.sanskar.pokedex.model.Favorite
import dev.sanskar.pokedex.model.PokemonDetail
import dev.sanskar.pokedex.model.UiState
import dev.sanskar.pokedex.network.Api
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import logcat.logcat

private const val TAG = "Repository"

@Singleton
class Repository @Inject constructor(
    private val api: Api,
    private val dao: PokedexDao
) {
    val pokemonsTillNow = mutableListOf<PokemonDetail>()

    fun getPokemons(offset: Int) = flow {
        logcat { "getPokemons: $offset" }
        try {
            val result = api.getPokemons(offset, 20)
            if (result.isSuccessful) {
                result.body()?.results?.forEach {
                    val response = api.getPokemonDetail(it.name)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            pokemonsTillNow += it
                        }
                    }
                }
                if (pokemonsTillNow.isNotEmpty()) {
                    emit(UiState.Success(pokemonsTillNow.toList()))
                }
                else emit(UiState.Error("No pokemons found"))
            } else {
                logcat { "Network call failed with ${result.message()}, ${result.errorBody()}" }
                emit(UiState.Error("Network call failed with ${result.message()}, ${result.errorBody()}"))
            }
        } catch (e: Exception) {
            emit(UiState.Error("Network call failed with ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getFavoritePokemons(): List<PokemonDetail> {
        val favoritePokemonDetails = mutableListOf<PokemonDetail>()
        val favoritePokemons = dao.getAllFavorites()
        favoritePokemons.forEach { favoritePokemon ->
            val findResult = pokemonsTillNow.find { it.name == favoritePokemon.name }
            if (findResult != null) {
                favoritePokemonDetails += findResult
            } else {
                val response = api.getPokemonDetail(favoritePokemon.name)
                if (response.isSuccessful) {
                    response.body()?.let {
                        favoritePokemonDetails += it
                    }
                }
            }
        }
        return favoritePokemonDetails
    }
}