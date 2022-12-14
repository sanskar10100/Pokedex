package dev.sanskar.pokedex.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.pokedex.model.PokemonDetail
import dev.sanskar.pokedex.model.UiState
import dev.sanskar.pokedex.repo.Repository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repo: Repository,
) : ViewModel() {

    val favoritePokemons = MutableStateFlow<UiState<List<PokemonDetail>>>(UiState.Loading)

    init {
        getFavoritePokemons()
    }

    fun getFavoritePokemons() {
        viewModelScope.launch {
            repo.getFavoritePokemons().let {
                if (it.isEmpty()) {
                    favoritePokemons.value = UiState.Error("No favorites found or load failed")
                } else {
                    favoritePokemons.value = UiState.Success(it)
                }
            }
        }
    }


}