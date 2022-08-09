package dev.sanskar.pokedex.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.pokedex.clickWithRipple
import dev.sanskar.pokedex.ui.theme.PokedexTheme

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
                    ShowPokemons()
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ShowPokemons() {
        val pokemons by viewModel.pokemons.observeAsState()
        if (pokemons != null) {
            LazyColumn(
                modifier = Modifier.padding(8.dp)
            ) {
                stickyHeader {
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
                items(items = pokemons!!, key = { it.name }) {
                    Text(
                        text = it.name,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickWithRipple { }
                    )
                }
            }
        } else ErrorView()
    }

    @Composable
    fun ErrorView() {
        Text(
            text = "Oops, we got a big old error!",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 32.sp
        )
    }
}