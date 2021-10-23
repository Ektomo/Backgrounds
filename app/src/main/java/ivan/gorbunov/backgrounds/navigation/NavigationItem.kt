package ivan.gorbunov.backgrounds.navigation

import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ivan.gorbunov.backgrounds.R
import ivan.gorbunov.backgrounds.ui.theme.backgroundColor


sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Home : NavigationItem("home", R.drawable.home, "Home")
    object Image : NavigationItem("image", R.drawable.image, "Image")
    object Layers : NavigationItem("layers", R.drawable.layers, "Layers")
    object Box : NavigationItem("box", R.drawable.box, "Box")
    object Maximise : NavigationItem("maximise", R.drawable.maximise, "Maximise")
}

@Composable
fun TopBar(title: String, visibleBack: Boolean) {
    if (visibleBack) {
        TopAppBar(
            title = { Text(text = (title), fontSize = 18.sp) },
            navigationIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back")
                }
            },
            backgroundColor = backgroundColor,
            contentColor = Color.Blue
        )
    } else {
        TopAppBar(
            title = { Text(text = (title), fontSize = 18.sp) },
            backgroundColor =backgroundColor,
            contentColor = backgroundColor
        )
    }

}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Image,
        NavigationItem.Layers,
        NavigationItem.Box,
        NavigationItem.Maximise
    )
    BottomNavigation(
        backgroundColor = backgroundColor,
        contentColor = Color.Blue
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(

                icon = { Icon(painterResource(id = item.icon), modifier = Modifier.requiredSize(24.dp), contentDescription = item.title) },
                label = { Text(text = item.title) },
                selectedContentColor = Color.Blue,
                unselectedContentColor = Color.Blue.copy(0.4f),
                alwaysShowLabel = true,
                selected = false,
                onClick = {
                    /* Add code later */
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}
