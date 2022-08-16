package dev.sanskar.pokedex.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.pokedex.model.PokemonDetail
import dev.sanskar.pokedex.model.UiState
import dev.sanskar.pokedex.network.Api
import java.lang.Exception
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.logcat
import kotlin.io.path.createTempDirectory

data class Page(
    val offset: Int = 0,
    val limit: Int = 20
) {
    fun nextPage() =
        this.copy(
            offset = this.offset + 20,
            limit = this.limit + 20
        )
}

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(private val api: Api) : ViewModel() {
    val pokemons = MutableLiveData<UiState<List<PokemonDetail>>>(UiState.Loading)
    var currentPage = Page()

    init {
        getPokemons()
        pokemons.value = UiState.Loading
    }

    fun getPokemons() {
        Log.d(TAG, "Fetching pokemons, page: $currentPage")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = api.getPokemons(currentPage.offset, currentPage.limit)
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
                    withContext(Dispatchers.Main.immediate) {
                        if (pokemonList.isNotEmpty()) {
                            try {
                                val currentData = (pokemons.value as UiState.Success).data
                                pokemons.value = UiState.Success(currentData + pokemonList)
                            } catch (e: ClassCastException) {
                                pokemons.value = UiState.Success(pokemonList)
                            }
                            currentPage = currentPage.nextPage()
                        }
                        else pokemons.value = UiState.Error("No pokemons found")
                    }
                } else {
                    logcat { "Network call failed with ${result.message()}, ${result.errorBody()}" }
                    withContext(Dispatchers.Main.immediate) {
                        pokemons.value =
                            UiState.Error("Network call failed with ${result.message()}, ${result.errorBody()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main.immediate) {
                    pokemons.value = UiState.Error("Network call failed with ${e.message}")
                }
            }
        }
    }
}