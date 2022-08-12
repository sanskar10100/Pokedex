package dev.sanskar.pokedex.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.RootGroupName
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.compose.SubcomposeAsyncImage
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.pokedex.model.PokemonDetail
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
                var expanded by remember { mutableStateOf(false) }
                val radius by animateDpAsState(if (expanded) 0.dp else 32.dp)
                val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        expanded = newState == BottomSheetBehavior.STATE_EXPANDED
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) { }
                }
                (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(bottomSheetCallback)

                PokedexTheme {
                    Surface(
                        shape = RoundedCornerShape(topStart = radius, topEnd = radius)
                    ) {
                        DetailScreen()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as View).setBackgroundColor(android.graphics.Color.TRANSPARENT)
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun DetailScreen(modifier: Modifier = Modifier) {
        val pokemon = viewModel.pokemon.observeAsState(UiState.Loading)
        var loading by remember { mutableStateOf(true) }

        LaunchedEffect(key1 = Unit) {
            viewModel.getPokemonDetail(args.pokemonId)
        }

        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
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
                    SuccessContent(pokemon.data)
                }
                is UiState.Error -> {
                    loading = false
                    Toast.makeText(LocalContext.current, "There was an error while making the API call", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Composable
    private fun SuccessContent(
        pokemon: PokemonDetail,
    ) {
        SubcomposeAsyncImage(
            model = pokemon.sprites.frontDefault,
            contentDescription = "NameData Image",
            loading = {
                CircularProgressIndicator()
            },
            modifier = Modifier.size(128.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = pokemon.name,
            style = MaterialTheme.typography.h4
        )
        Text(
            text = "Height: ${pokemon.height}",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.paddingFromBaseline(top = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        LazyRow( // This was initially a LazyHorizontalGrid but took too much space
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(pokemon.moves.take(3)) { move ->
                Text(
                    text = move.move.name,
                    modifier = Modifier
                        .background(Color(0xFFFFF176), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
            }
        }
    }
}