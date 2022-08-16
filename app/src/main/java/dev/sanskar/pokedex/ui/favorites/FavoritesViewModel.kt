package dev.sanskar.pokedex.ui.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.pokedex.db.PokedexDao
import dev.sanskar.pokedex.model.PokemonDetail
import dev.sanskar.pokedex.model.UiState
import dev.sanskar.pokedex.network.Api
import dev.sanskar.pokedex.repo.Repository
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repo: Repository,
) : ViewModel() {

    val favoritePokemons = MutableLiveData<UiState<List<PokemonDetail>>>(UiState.Loading)

    init {
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