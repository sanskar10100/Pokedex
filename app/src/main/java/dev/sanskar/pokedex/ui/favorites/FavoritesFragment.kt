package dev.sanskar.pokedex.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.sanskar.pokedex.R
import dev.sanskar.pokedex.getCurrent
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
        val currentDestination = findNavController().getCurrent()
        val scaffoldState = rememberScaffoldState()

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
            Text("This is text!")
        }
    }
}