package ivan.gorbunov.backgrounds.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ivan.gorbunov.backgrounds.R
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
    val viewModel = hiltViewModel<NavigationViewModel>()
    val curState = viewModel.curState.observeAsState()

    Crossfade(targetState = curState.value) { state ->
        when (state) {
            is NavigationItem.Home -> {
                isShowBottom.value = true
                isShowTop.value = false

                TopScreen(isShowTop, isShowBottom) {
                    viewModel.stateStack.value!!.removeLast()
                    viewModel.curState.value = viewModel.stateStack.value!!.last()
                }
            }
            is NavigationItem.Image -> {
                title.value = R.drawable.ic_group_7476
                isShowBottom.value = true
                isShowTop.value = true
                MainScreen(isShowTop, isShowBottom, title) {
                    viewModel.stateStack.value!!.removeLast()
                    viewModel.curState.value = viewModel.stateStack.value!!.last()
                }
            }
            is NavigationItem.Maximise -> {
                isShowBottom.value = true
                isShowTop.value = true
                Screen4kList(viewModel, isShowTop, isShowBottom, title){
                    viewModel.stateStack.value!!.removeLast()
                    viewModel.curState.value = viewModel.stateStack.value!!.last()
                }
            }
            is NavigationItem.Layers -> {
                isShowBottom.value = true
                isShowTop.value = true
                ScreenLiveList(
                    viewModel,
                    isShowTop = isShowTop,
                    isShowBottom = isShowBottom,
                    title = title
                ) {
                    viewModel.stateStack.value!!.removeLast()
                    viewModel.curState.value = viewModel.stateStack.value!!.last()
                }
            }
            is NavigationItem.Box -> {
                isShowBottom.value = true
                isShowTop.value = true
                Screen3DList(
                    viewModel,
                    isShowTop = isShowTop,
                    isShowBottom = isShowBottom,
                    title = title
                ) {
                    viewModel.stateStack.value!!.removeLast()
                    viewModel.curState.value = viewModel.stateStack.value!!.last()
                }
            }
        }

    }


//    NavHost(navController, startDestination = NavigationItem.Home.route) {
//        composable(NavigationItem.Home.route) {
//            isShowBottom.value = true
//            isShowTop.value = false
//
//            TopScreen(
//                navController, isShowTop, isShowBottom, title
//            )
//        }
//        composable(NavigationItem.Image.route) {
//            title.value = R.drawable.ic_group_7476
//            isShowBottom.value = true
//            isShowTop.value = true
//            MainScreen(navController, isShowTop, isShowBottom, title)
//        }
//        composable(NavigationItem.Maximise.route) {
//            isShowBottom.value = true
//            isShowTop.value = true
//            Screen4kList(navController, isShowTop, isShowBottom, title)
//
//        }
//        composable(NavigationItem.Layers.route) {
//            isShowBottom.value = true
//            isShowTop.value = true
//            ScreenLiveList(
//                navHostController = navController,
//                isShowTop = isShowTop,
//                isShowBottom = isShowBottom,
//                title = title
//            )
//        }
//        composable(NavigationItem.Box.route) {
//            Screen3DList(
//                navHostController = navController,
//                isShowTop = isShowTop,
//                isShowBottom = isShowBottom,
//                title = title
//            )
//        }
////        composable(NavigationItem.Profile.route) {
////            ProfileScreen()
////        }
//    }
}