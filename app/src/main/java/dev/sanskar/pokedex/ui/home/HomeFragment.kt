package dev.sanskar.pokedex.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.compose.SubcomposeAsyncImage
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.pokedex.R
import dev.sanskar.pokedex.getCurrent
import dev.sanskar.pokedex.model.PokemonDetail
import dev.sanskar.pokedex.model.UiState
import dev.sanskar.pokedex.ui.commons.PokemonsList
import dev.sanskar.pokedex.ui.commons.ShowLoader
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
            bottomBar = {
                BottomNavigation(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    elevation = 5.dp
                ) {
                    BottomNavigationItem(
                        selected = true,
                        onClick = { },
                        icon = {
                            Icon(Icons.Filled.Home, contentDescription = "Home")
                        },
                        label = { Text("Home") }
                    )

                    BottomNavigationItem(
                        selected = false,
                        onClick = { findNavController().navigate(R.id.action_global_favoritesFragment) },
                        icon = {
                            Icon(Icons.Filled.Favorite, contentDescription = "Favorites")
                        },
                        label = { Text("Favorites") }
                    )
                }
            }
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