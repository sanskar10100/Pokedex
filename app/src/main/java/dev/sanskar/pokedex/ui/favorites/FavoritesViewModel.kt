package dev.sanskar.pokedex.ui.favorites

import androidx.lifecycle.ViewModel
import dev.sanskar.pokedex.db.PokedexDao
import dev.sanskar.pokedex.network.Api
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    private val dao: PokedexDao,
    private val api: Api
) : ViewModel() {

    init {

    }

}