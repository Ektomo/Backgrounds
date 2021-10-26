package ivan.gorbunov.backgrounds.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ivan.gorbunov.backgrounds.screens.HomeScreen
import ivan.gorbunov.backgrounds.screens.ImageScreen
import ivan.gorbunov.backgrounds.screens.Screen4kList
import ivan.gorbunov.backgrounds.vmodels.ViewModel4K

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Home.route) {
        composable(NavigationItem.Home.route) {
            HomeScreen()
        }
        composable(NavigationItem.Image.route) {
            ImageScreen()
        }
        composable(NavigationItem.Maximise.route) {
            val viewModel4K: ViewModel4K = viewModel()
            Screen4kList(screens = viewModel4K.items.value)
            viewModel4K.getMovies()
        }
//        composable(NavigationItem.Books.route) {
//            BooksScreen()
//        }
//        composable(NavigationItem.Profile.route) {
//            ProfileScreen()
//        }
    }
}