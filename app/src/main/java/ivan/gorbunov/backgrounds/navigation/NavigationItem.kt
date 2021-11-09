package ivan.gorbunov.backgrounds.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ivan.gorbunov.backgrounds.R
import ivan.gorbunov.backgrounds.ui.theme.backgroundColor


sealed class NavigationItem(var route: String, var icon: Int) {
    object Home : NavigationItem("home", R.drawable.home)
    object Image : NavigationItem("image", R.drawable.image)
    object Layers : NavigationItem("layers", R.drawable.layers)
    object Box : NavigationItem("box", R.drawable.box)
    object Maximise : NavigationItem("maximise", R.drawable.maximise)
    object Detail4K : NavigationItem("detail4K", 0)
}

@Composable
fun TopBar(
    title: Any,
    onBack: (() -> Unit)?
) {
    val appBarHorizontalPadding = 4.dp
    val titleIconModifier = Modifier
        .fillMaxHeight()
        .width(72.dp - appBarHorizontalPadding)

    TopAppBar(
        backgroundColor = backgroundColor,
        contentColor = Color.Blue
    ) {
        Box(Modifier.height(32.dp)) {

            //Navigation Icon
            Row(titleIconModifier, verticalAlignment = Alignment.CenterVertically) {
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.high,
                ) {
                    IconButton(
                        onClick = {
                            if (onBack != null) {
                                onBack()
                            }
                        },
                        enabled = true,
                    ) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back")
                    }
                }
            }

            //Title
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                ProvideTextStyle(value = MaterialTheme.typography.h6) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                    ) {
                        when (title) {
                            is Int -> {
                                Image(
                                    modifier = Modifier.fillMaxWidth(),
                                    imageVector = ImageVector.vectorResource(id = title),
                                    contentDescription = "mainScreenTitle",
                                    alignment = Alignment.Center
                                )
                            }
                            is String -> {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    text = title
                                )
                            }
                            else -> {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    text = "Backgrounds"
                                )
                            }
                        }

                    }
                }
            }
        }
    }

//    BackHandler(true, onBack)
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

                icon = {
                    Icon(
                        painterResource(id = item.icon),
                        modifier = Modifier.requiredSize(24.dp),
                        contentDescription = item.route
                    )
                },
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
