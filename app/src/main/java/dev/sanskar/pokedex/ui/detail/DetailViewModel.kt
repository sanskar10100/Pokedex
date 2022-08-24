package dev.sanskar.pokedex.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.pokedex.db.PokedexDao
import dev.sanskar.pokedex.model.Favorite
import dev.sanskar.pokedex.model.PokemonDetail
import dev.sanskar.pokedex.model.UiState
import dev.sanskar.pokedex.network.Api
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val api: Api,
    private val db: PokedexDao
) : ViewModel() {

    val pokemon = MutableStateFlow<UiState<PokemonDetail>>(UiState.Loading)
    val isFavorite = MutableStateFlow(false)

    fun getPokemonDetail(id: Int) {
        viewModelScope.launch {
            try {
                api.getPokemonDetail(id).let {
                    if (it.isSuccessful && it.body() != null) {
                        pokemon.value = UiState.Success(it.body()!!)
                    } else {
                        pokemon.value = UiState.Error(it.message())
                    }
                }
            } catch (e: Exception) {
                pokemon.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getFavorite(id: Int) =
        viewModelScope.launch {
            isFavorite.value = db.checkIsFavorite(id) != null
        }

    fun toggleFavorite(id: Int, name: String) {
        viewModelScope.launch {
            if (isFavorite.value) {
                db.removeFavorite(id)
            } else {
                db.insert(Favorite(id, name))
            }
            getFavorite(id)
        }
    }
}