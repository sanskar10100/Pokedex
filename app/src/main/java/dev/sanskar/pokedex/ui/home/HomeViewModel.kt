package dev.sanskar.pokedex.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.pokedex.model.PokemonDetail
import dev.sanskar.pokedex.model.UiState
import dev.sanskar.pokedex.network.Api
import java.lang.Exception
import javax.inject.Inject
import kotlinx.coroutines.launch
import logcat.logcat

@HiltViewModel
class HomeViewModel @Inject constructor(private val api: Api) : ViewModel() {
    val pokemons = MutableLiveData<UiState<List<PokemonDetail>>>(UiState.Loading)

    init {
        getPokemons()
    }

    fun getPokemons() {
        pokemons.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = api.getPokemons()
                if (result.isSuccessful) {
                    val pokemonList = mutableListOf<PokemonDetail>()
                    result.body()?.results?.forEach {
                        val response = api.getPokemonDetail(it.name)
                        if (response.isSuccessful) {
                            response.body()?.let {
                                pokemonList += it
                            }
                        }
                    }
                    if (pokemonList.isNotEmpty()) pokemons.value = UiState.Success(pokemonList)
                    else pokemons.value = UiState.Error("No pokemons found")
                } else {
                    logcat { "Network call failed with ${result.message()}, ${result.errorBody()}" }
                    pokemons.value =
                        UiState.Error("Network call failed with ${result.message()}, ${result.errorBody()}")
                }
            } catch (e: Exception) {
                pokemons.value = UiState.Error("Network call failed with ${e.message}")
            }
        }
    }
}