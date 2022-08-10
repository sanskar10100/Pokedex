package dev.sanskar.pokedex.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.compose.SubcomposeAsyncImage
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.pokedex.model.UiState
import dev.sanskar.pokedex.ui.theme.PokedexTheme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : BottomSheetDialogFragment() {
    private val viewModel by viewModels<DetailViewModel>()
    private val args by navArgs<DetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val scaffoldState = rememberScaffoldState()
                PokedexTheme {
                    Scaffold(
                        scaffoldState = scaffoldState
                    ) {
                        DetailScreen(scaffoldState, Modifier.padding(it))
                    }
                }
            }
        }
    }

    @Composable
    fun DetailScreen(scaffoldState: ScaffoldState, modifier: Modifier = Modifier) {
        val pokemon = viewModel.pokemon.observeAsState(UiState.Loading)
        var loading by remember { mutableStateOf(true) }

        LaunchedEffect(key1 = Unit) {
            viewModel.getPokemonDetail(args.pokemonId)
        }

        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = loading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }

            when (val pokemon = pokemon.value) {
                is UiState.Loading -> {
                    loading = true
                }
                is UiState.Success -> {
                    loading = false
                    SubcomposeAsyncImage(
                        model = pokemon.data.sprites.frontDefault,
                        contentDescription = "Pokemon Image",
                        loading = {
                            CircularProgressIndicator()
                        },
                        modifier = Modifier.size(128.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = pokemon.data.name,
                        style = MaterialTheme.typography.h3
                    )
                }
                is UiState.Error -> {
                    loading = false
                    val scope = rememberCoroutineScope()
                    LaunchedEffect(key1 = Unit) {
                        scope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(pokemon.message)
                        }
                    }
                }
            }
        }
    }
}