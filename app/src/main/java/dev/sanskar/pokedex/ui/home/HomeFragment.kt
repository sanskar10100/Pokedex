package dev.sanskar.pokedex.ui.home

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import coil.compose.SubcomposeAsyncImage
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.pokedex.R
import dev.sanskar.pokedex.getCurrent
import dev.sanskar.pokedex.isOnScreen
import dev.sanskar.pokedex.model.PokemonDetail
import dev.sanskar.pokedex.model.UiState
import dev.sanskar.pokedex.ui.theme.PokedexTheme
import kotlinx.coroutines.delay
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
                    PokedexApp()
                }
            }
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun PokedexApp() {
        var error by remember { mutableStateOf("") }
        val scaffoldState = rememberScaffoldState()
        val coroutineScope = rememberCoroutineScope()
        val currentDestination = findNavController().getCurrent()

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
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    elevation = 5.dp
                ) {
                    BottomNavigationItem(
                        selected = currentDestination.value == R.id.homeFragment,
                        onClick = { findNavController().navigate(R.id.action_global_homeFragment) },
                        icon = {
                            Icon(Icons.Filled.Home, contentDescription = "Home")
                        },
                        label = { Text("Home") }
                    )

                    BottomNavigationItem(
                        selected = currentDestination.value == R.id.favoritesFragment,
                        onClick = { findNavController().navigate(R.id.action_global_favoritesFragment) },
                        icon = {
                            Icon(Icons.Filled.Favorite, contentDescription = "Favorite")
                        },
                        label = { Text("Favorite") }
                    )
                }
            }
        ) {
            ShowPokemons {
                error = it
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
    @Composable
    fun ShowPokemons(modifier: Modifier = Modifier, onError: (String) -> Unit) {
        val pokemonState = viewModel.pokemons.observeAsState()
        var loading by rememberSaveable { mutableStateOf(true) }

        AnimatedVisibility(
            visible = loading,
            exit = scaleOut(animationSpec = tween(1000))
        ) {
            ShowLoader()
        }

        when (val currentState = pokemonState.value) {
            null -> {
                onError("Pokemons not found")
                loading = false
            }
            is UiState.Loading -> {
                loading = true
            }
            is UiState.Error -> {
                loading = false
                onError(currentState.message)
            }
            is UiState.Success -> {
                AnimatedVisibility(visible = !loading, enter = slideInVertically(
                    animationSpec = tween(
                        durationMillis = 500,
                        500,
                        easing = LinearOutSlowInEasing
                    ),
                    initialOffsetY = { it }
                )) {
                    LazyColumn(
                        modifier = modifier.padding(8.dp),
                    ) {
                        stickyHeader {
                            ListHeader()
                        }
                        items(items = currentState.data, key = { it.name }) {
                            ListItem(it)
                        }
                        item {
                            LaunchedEffect(Unit) {
                                viewModel.getPokemons()
                            }
                        }
                    }
                }
                loading = false
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun ListItem(it: PokemonDetail) {
        Card(
            modifier = Modifier.padding(8.dp),
            onClick = {
                val navDirections = HomeFragmentDirections.actionHomeFragmentToDetailFragment(it.id)
                findNavController().navigate(navDirections)
            },
            backgroundColor = Color(0xFF81D4FA),
            elevation = 3.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = it.name.replaceFirstChar { it.uppercase() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .fillMaxWidth(),
                )
                Box(
                    Modifier.background(Color(0xFFE6EE9C))
                ) {
                    SubcomposeAsyncImage(
                        model = it.sprites.frontDefault,
                        contentDescription = "Image of ${it.name}",
                        loading = {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        },
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
        }
    }

    @Composable
    fun ShowLoader() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(200.dp)
            )
        }
    }

    @Composable
    private fun ListHeader() {
        Column(
            Modifier.background(MaterialTheme.colors.surface)
        ) {
            Text(
                "Tap on a pokemon to know more about it!",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    @Preview
    @Composable
    fun ShowPokemonsPreview() {
        PokedexTheme {
            ShowPokemons(onError = {})
        }
    }
}