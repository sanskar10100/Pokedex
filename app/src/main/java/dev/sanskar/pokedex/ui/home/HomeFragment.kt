package dev.sanskar.pokedex.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
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

    @Composable
    fun ShowPokemons() {
        val pokemons by viewModel.pokemons.observeAsState()
        if (pokemons != null) {
            LazyColumn {
                items(items = pokemons!!, key = { it.name }) {
                    Text(text = it.name)
                    Spacer(modifier = Modifier.height(8.dp))
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