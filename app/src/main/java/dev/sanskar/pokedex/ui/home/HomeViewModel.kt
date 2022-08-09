package dev.sanskar.pokedex.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.pokedex.model.Pokemon
import dev.sanskar.pokedex.model.PokemonDetail
import dev.sanskar.pokedex.model.Pokemons
import dev.sanskar.pokedex.network.Api
import javax.inject.Inject
import kotlinx.coroutines.launch
import logcat.logcat

@HiltViewModel
class HomeViewModel @Inject constructor(private val api: Api) : ViewModel() {
    val pokemonDetails = MutableLiveData<List<PokemonDetail>>()

    init {
        getPokemons()
    }

    private fun getPokemons() {
        viewModelScope.launch {
            val result = api.getPokemons()
            if (result.isSuccessful) {
                result.body()?.results?.forEach {
                    val response = api.getPokemonDetail(it.name)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            pokemonDetails.value = pokemonDetails.value?.plus(it) ?: listOf(it)
                        }
                    }
                }
            } else {
                logcat { "Network call failed with ${result.message()}, ${result.errorBody()}" }
            }
        }
    }
}