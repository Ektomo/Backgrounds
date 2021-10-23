package ivan.gorbunov.backgrounds.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ivan.gorbunov.backgrounds.screens.HomeScreen

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Home.route) {
        composable(NavigationItem.Home.route) {
            HomeScreen()
        }
//        composable(NavigationItem.Music.route) {
//            MusicScreen()
//        }
//        composable(NavigationItem.Movies.route) {
//            MoviesScreen()
//        }
//        composable(NavigationItem.Books.route) {
//            BooksScreen()
//        }
//        composable(NavigationItem.Profile.route) {
//            ProfileScreen()
//        }
    }
}