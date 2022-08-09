package dev.sanskar.pokedex.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.SubcomposeAsyncImage
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.pokedex.model.PokemonDetail
import dev.sanskar.pokedex.model.UiState
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
                    PokedexApp()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PokedexApp() {
        var error by remember { mutableStateOf("") }

        Scaffold {
            ShowPokemons(Modifier.padding(it)) {
                error = it
            }
            if (error.isNotEmpty()) {
                Toast.makeText(LocalContext.current, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ShowPokemons(modifier: Modifier = Modifier, onError: (String) -> Unit) {
        val pokemonState = viewModel.pokemons.observeAsState()
        var loading by remember { mutableStateOf(true) }
        ShowLoader(showLoader = loading)

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
                loading  = false
                AnimatedVisibility(visible = !loading) {
                    LazyColumn(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        stickyHeader {
                            ListHeader()
                        }
                        items(items = currentState.data, key = { it.name }) {
                            ListItem(it)
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ListItem(it: PokemonDetail) {
        Card(
            modifier = Modifier.padding(8.dp),
            onClick = {
                // Todo
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = it.name,
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
    fun ShowLoader(showLoader: Boolean) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showLoader) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(200.dp)
                )
            }
        }
    }

    @Composable
    private fun ListHeader() {
        Column(
            Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            Text(
                "Tap on a pokemon to know more about it!",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}