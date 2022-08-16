package dev.sanskar.pokedex.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.pokedex.Screen
import dev.sanskar.pokedex.ui.commons.BottomNav
import dev.sanskar.pokedex.ui.commons.PokemonsList
import dev.sanskar.pokedex.ui.theme.PokedexTheme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PokedexTheme {
                    HomeScreen()
                }
            }
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun HomeScreen() {
        var error by remember { mutableStateOf("") }
        val scaffoldState = rememberScaffoldState()
        val coroutineScope = rememberCoroutineScope()
        val pokemonState by viewModel.pokemons.observeAsState()

        if (error.isNotEmpty()) {
            LaunchedEffect(error) {
                coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(error)
                }
            }
        }

        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = { BottomNav(navController = findNavController(), selectedItem = Screen.HOME) }
        ) {
            if (pokemonState != null) {
                PokemonsList(
                    it.calculateBottomPadding(),
                    pokemonState!!,
                    lastItemReached = { viewModel.getPokemons() },
                    onListItemClicked = {
                        val directions = HomeFragmentDirections.actionHomeFragmentToDetailFragment(it)
                        findNavController().navigate(directions)
                    }
                ) {
                    error = it
                }
            }
        }
    }
}