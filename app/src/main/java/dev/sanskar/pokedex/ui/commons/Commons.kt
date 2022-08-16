package dev.sanskar.pokedex.ui.commons

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
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import dev.sanskar.pokedex.R
import dev.sanskar.pokedex.Screen
import dev.sanskar.pokedex.model.PokemonDetail
import dev.sanskar.pokedex.model.UiState
import kotlinx.coroutines.launch

@Composable
private fun ListHeader(header: String = "Tap on a pokemon to know more about it!") {
    Column(
        Modifier.background(MaterialTheme.colors.surface)
    ) {
        Text(
            header,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Divider()
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ShowLoader(loading: Boolean) {
    AnimatedVisibility(
        visible = loading,
        exit = scaleOut(animationSpec = tween(500))
    ) {
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
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ListItem(it: PokemonDetail, onClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(8.dp),
        onClick = {
            onClick()
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
                    model = it.sprites.front_default,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PokemonsList(
    padding: Dp,
    pokemonState: UiState<List<PokemonDetail>>,
    showLoader: Boolean = true,
    headerText: String = "",
    lastItemReached: () -> Unit,
    onListItemClicked: (Int) -> Unit,
    onError: (String) -> Unit
) {
    var loading by rememberSaveable { mutableStateOf(true) }
    if (showLoader) ShowLoader(loading)

    when (pokemonState) {
        null -> {
            onError("Pokemons not found")
            loading = false
        }
        is UiState.Loading -> {
            loading = true
        }
        is UiState.Error -> {
            loading = false
            onError(pokemonState.message)
        }
        is UiState.Success -> {
            AnimatedVisibility(visible = !loading, enter = slideInVertically(
                animationSpec = tween(
                    durationMillis = 500,
                    delayMillis = if (showLoader) 500 else 0,
                    easing = LinearOutSlowInEasing
                ),
                initialOffsetY = { it }
            )) {
                LazyColumn(
                    modifier = Modifier.padding(8.dp),
                ) {
                    stickyHeader {
                        if (headerText.isNotEmpty()) ListHeader(headerText) else ListHeader()
                    }
                    items(items = pokemonState.data, key = { it.name }) {
                        ListItem(it) { onListItemClicked(it.id) }
                    }
                    item {
                        LaunchedEffect(Unit) {
                            lastItemReached()
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(padding))
                    }
                }
            }
            loading = false
        }
    }
}

@Composable
fun BottomNav(
    navController: NavController,
    selectedItem: Screen,
) {
    BottomNavigation(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(16.dp)),
        elevation = 5.dp
    ) {
        BottomNavigationItem(
            selected = selectedItem == Screen.HOME,
            onClick = {
                if (selectedItem != Screen.HOME) navController.popBackStack(R.id.homeFragment, inclusive = false)
            },
            icon = {
                Icon(Icons.Filled.Home, contentDescription = "Home")
            },
            label = { Text("Home") }
        )

        BottomNavigationItem(
            selected = selectedItem == Screen.FAVORITES,
            onClick = { if (selectedItem != Screen.FAVORITES) navController.navigate(R.id.favoritesFragment) },
            icon = {
                Icon(Icons.Filled.Favorite, contentDescription = "Favorites")
            },
            label = { Text("Favorites") }
        )
    }
}

@Composable
fun ScaffoldState.ShowErrorSnackbar(message: String) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(message) {
        coroutineScope.launch {
            this@ShowErrorSnackbar.snackbarHostState.showSnackbar(message)
        }
    }
}