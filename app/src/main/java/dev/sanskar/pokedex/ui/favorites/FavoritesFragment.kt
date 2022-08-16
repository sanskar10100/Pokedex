package dev.sanskar.pokedex.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.sanskar.pokedex.Screen
import dev.sanskar.pokedex.ui.commons.BottomNav
import dev.sanskar.pokedex.ui.theme.PokedexTheme

class FavoritesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PokedexTheme {
                    FavoritesScreen()
                }
            }
        }
    }

    @Composable
    fun FavoritesScreen(modifier: Modifier = Modifier) {
        val scaffoldState = rememberScaffoldState()
        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = {
                BottomNav(navController = findNavController(), selectedItem = Screen.FAVORITES)
            }
        ) {
            Text(text = "Hello")
        }
    }
}