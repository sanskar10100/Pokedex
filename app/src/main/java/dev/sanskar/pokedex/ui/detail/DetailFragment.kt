package dev.sanskar.pokedex.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.pokedex.capitalizeWords
import dev.sanskar.pokedex.clickWithRipple
import dev.sanskar.pokedex.model.PokemonDetail
import dev.sanskar.pokedex.model.UiState
import dev.sanskar.pokedex.ui.theme.PokedexTheme

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

    @OptIn(ExperimentalLifecycleComposeApi::class)
    @Composable
    fun DetailScreen(modifier: Modifier = Modifier) {
        val pokemon by viewModel.pokemon.collectAsStateWithLifecycle()
        val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
        var loading by remember { mutableStateOf(true) }

        LaunchedEffect(key1 = Unit) {
            viewModel.getPokemonDetail(args.pokemonId)
            viewModel.getFavorite(args.pokemonId)
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

            when (val pokemon = pokemon) {
                is UiState.Loading -> {
                    loading = true
                }
                is UiState.Success -> {
                    loading = false
                    SuccessContent(pokemon.data, isFavorite)
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
        isFavorite: Boolean
    ) {
        val favoriteColor by animateColorAsState(
            if (isFavorite) Color.Red else Color.Gray,
            animationSpec = tween(
                durationMillis = 2000
            )
        )
        val colorsList = remember {
            mutableListOf(
                Color(0xFFB71C1C),
                Color(0xFF4A148C),
                Color(0xFF1A237E),
                Color(0xFF01579B),
                Color(0xFFE65100),
                Color(0xFF1B5E20),
                Color(0xFFF57F17),
            )
        }

        ImagePager(pokemon)
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.h4,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "${if (isFavorite) "Remove" else "Add"} Favorite",
                modifier = Modifier
                    .size(28.dp)
                    .clickWithRipple(true) { viewModel.toggleFavorite(pokemon.id, pokemon.name) }
                    .offset(y = 2.dp),
                tint = favoriteColor
            )
        }
        Text(
            text = "Height: ${pokemon.height}",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.paddingFromBaseline(top = 16.dp)
        )
        Text(
            text = "Weight: ${pokemon.weight}",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.paddingFromBaseline(8.dp)
        )
        Spacer(Modifier.height(16.dp))
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
        Row(
            Modifier
                .padding(16.dp)
                .border(2.dp, Color.Blue, RoundedCornerShape(32.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                pokemon.stats.forEach {
                    Text(
                        text = if (it.stat.name == "hp") "HP" else it.stat.name.replace("-", " ").capitalizeWords(),
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                pokemon.stats.forEachIndexed { i, it ->
                    Text(
                        text = it.baseStat.toString(),
                        style = MaterialTheme.typography.body1,
                        color = colorsList[i],
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun ImagePager(pokemon: PokemonDetail, modifier: Modifier = Modifier) {
        val sprites = pokemon.sprites.getNonNullItems()
        val pagerState = rememberPagerState()
        var loading by remember { mutableStateOf(true) }
        HorizontalPager(
            count = sprites.size,
            modifier = Modifier.size(192.dp),
            state = pagerState
        ) { page ->
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = sprites[page],
                    contentDescription = "Pokemon ${pokemon.name}",
                    modifier = Modifier.fillMaxSize(),
                    onLoading = {
                        loading = true
                    },
                    onSuccess = {
                        loading = false
                    }
                )
                AnimatedVisibility(visible = loading) {
                    CircularProgressIndicator()
                }
            }
        }
        HorizontalPagerIndicator(pagerState = pagerState)
    }
}