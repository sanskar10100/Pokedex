package dev.sanskar.pokedex.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanskar.pokedex.db.PokedexDao
import dev.sanskar.pokedex.network.Api
import dev.sanskar.pokedex.repo.Repository
import javax.inject.Inject
import kotlinx.coroutines.launch

class FavoritesViewModel @Inject constructor(
    private val repo: Repository,
) : ViewModel() {

    init {

    }


}