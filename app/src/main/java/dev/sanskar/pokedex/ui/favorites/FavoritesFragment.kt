package dev.sanskar.pokedex.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.pokedex.Screen
import dev.sanskar.pokedex.ui.commons.BottomNav
import dev.sanskar.pokedex.ui.commons.PokemonsList
import dev.sanskar.pokedex.ui.commons.ShowErrorSnackbar
import dev.sanskar.pokedex.ui.theme.PokedexTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private val viewModel by viewModels<FavoritesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PokedexTheme {
                    FavoritesScreen()
                }
            }
        }
    }

    @Composable
    fun FavoritesScreen() {
        val scaffoldState = rememberScaffoldState()
        val pokemonState by viewModel.favoritePokemons.observeAsState()
        var error by remember { mutableStateOf("") }
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)
        val coroutineScope = rememberCoroutineScope()
        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = { BottomNav(navController = findNavController(), selectedItem = Screen.FAVORITES) }
        ) {
            if (error.isNotEmpty()) { scaffoldState.ShowErrorSnackbar(error) }

            if (pokemonState != null) {
                SwipeRefresh(state = swipeRefreshState, {
                    viewModel.getFavoritePokemons()
                    coroutineScope.launch {
                        swipeRefreshState.isRefreshing = true
                        delay(500)
                        swipeRefreshState.isRefreshing = false
                    }
                }) {
                    PokemonsList(padding = it.calculateBottomPadding(),
                        pokemonState = pokemonState!!,
                        lastItemReached = { },
                        showLoader = false,
                        headerText = "Here are your favorite pokemons!",
                        onListItemClicked = {
                            val directions = FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(it)
                            findNavController().navigate(directions)
                        },
                        onRemove = {
                            false
                        }
                    ) {
                        error = it
                    }
                }
            }
        }
    }
}