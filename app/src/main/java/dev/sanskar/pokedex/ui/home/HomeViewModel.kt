package dev.sanskar.pokedex.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.pokedex.model.PokemonDetail
import dev.sanskar.pokedex.model.UiState
import dev.sanskar.pokedex.repo.Repository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(private val repo: Repository) : ViewModel() {
    val pokemons = MutableStateFlow<UiState<List<PokemonDetail>>>(UiState.Loading)
    private var nextOffset = 0

    init {
        getPokemons()
        pokemons.value = UiState.Loading
    }

    fun getPokemons() {
        viewModelScope.launch {
            repo.getPokemons(nextOffset).collect {
                withContext(Dispatchers.Main.immediate) {
                    if (it is UiState.Success) {
                        nextOffset += 20
                        pokemons.value = it
                    } else {
                        pokemons.value = it
                    }
                }
            }
        }
    }
}