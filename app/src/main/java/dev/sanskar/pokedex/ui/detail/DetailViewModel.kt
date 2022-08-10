package dev.sanskar.pokedex.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.pokedex.model.PokemonDetail
import dev.sanskar.pokedex.model.UiState
import dev.sanskar.pokedex.network.Api
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(private val api: Api) : ViewModel() {

    val pokemon = MutableLiveData<UiState<PokemonDetail>>(UiState.Loading)

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
}