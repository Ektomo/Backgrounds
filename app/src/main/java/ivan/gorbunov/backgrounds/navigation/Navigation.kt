package ivan.gorbunov.backgrounds.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ivan.gorbunov.backgrounds.R
import ivan.gorbunov.backgrounds.screens.ImageScreen
import ivan.gorbunov.backgrounds.screens.fourk.Screen4kList
import ivan.gorbunov.backgrounds.screens.live.ScreenLiveList
import ivan.gorbunov.backgrounds.screens.main.MainScreen
import ivan.gorbunov.backgrounds.screens.threed.Screen3DList
import ivan.gorbunov.backgrounds.screens.top.TopScreen
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Composable
fun Navigation(
    navController: NavHostController,
    title: MutableState<Any>,
    isShowTop: MutableState<Boolean>,
    isShowBottom: MutableState<Boolean>
) {
    NavHost(navController, startDestination = NavigationItem.Home.route) {
        composable(NavigationItem.Home.route) {
            isShowBottom.value = true
            isShowTop.value = false

            TopScreen(
                navController, isShowTop, isShowBottom, title
            )
        }
        composable(NavigationItem.Image.route) {
            title.value = R.drawable.ic_group_7476
            isShowBottom.value = true
            isShowTop.value = true
            MainScreen(navController, isShowTop, isShowBottom, title)
        }
        composable(NavigationItem.Maximise.route) {
            isShowBottom.value = true
            isShowTop.value = true
            Screen4kList(navController, isShowTop, isShowBottom, title)

        }
        composable(NavigationItem.Layers.route) {
            isShowBottom.value = true
            isShowTop.value = true
            ScreenLiveList(
                navHostController = navController,
                isShowTop = isShowTop,
                isShowBottom = isShowBottom,
                title = title
            )
        }
        composable(NavigationItem.Box.route) {
            Screen3DList(
                navHostController = navController,
                isShowTop = isShowTop,
                isShowBottom = isShowBottom,
                title = title
            )
        }
//        composable(NavigationItem.Profile.route) {
//            ProfileScreen()
//        }
    }
}