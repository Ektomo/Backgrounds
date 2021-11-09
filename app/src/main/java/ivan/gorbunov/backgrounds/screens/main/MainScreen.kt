package ivan.gorbunov.backgrounds.screens.main

import android.graphics.drawable.Drawable
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import ivan.gorbunov.backgrounds.R
import ivan.gorbunov.backgrounds.api.baseUrl
import ivan.gorbunov.backgrounds.pojo.CategoriesAll
import ivan.gorbunov.backgrounds.pojo.FavoritesCat
import ivan.gorbunov.backgrounds.pojo.MainBackgrounds
import ivan.gorbunov.backgrounds.screens.LoadingView
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.*

@ExperimentalSerializationApi
@Composable
fun MainScreen(
    navHostController: NavHostController,
    isShowTop: MutableState<Boolean>,
    isShowBottom: MutableState<Boolean>,
    title: MutableState<Any>
) {
    val viewModel = hiltViewModel<MainViewModel>()
    val curState = viewModel.curState.observeAsState()
    val curStateMainBackground = viewModel.curMainBackground.observeAsState()
    val curRememberListState =
        if (viewModel.rememberListState.value == null) rememberLazyListState() else viewModel.rememberListState.value
    val circularProgressDrawable = CircularProgressDrawable(LocalContext.current)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    title.value = R.drawable.ic_group_7476

    Crossfade(targetState = curState.value) { state ->
        when (state) {
            is MainViewModel.State.Data -> {

                viewModel.curMainBackground.value = state.data
                circularProgressDrawable.start()
                MainListScreen(
                    mainItem = state.data,
                    viewModel = viewModel,
                    circularProgressDrawable
                )
//                LazyColumn(state = curRememberListState!!) {
////                    state.data.forEach {
////                        item {
////                            ScreenLiveItem(item = it, circularProgressDrawable)
////                        }
////                    }
//                    item{
//                        Spacer(modifier = Modifier.padding(bottom = 32.dp))
//                    }
//                }
            }
            is MainViewModel.State.Error -> {
                Text(text = state.error ?: "")
            }
            is MainViewModel.State.Detail -> {
                title.value = state.data.nameCategory.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                isShowTop.value = true
                isShowBottom.value = true
//                viewModel.curStateDetailBackground.value = state.data
//                PreviewLiveList(state, curRememberPreviewState, viewModel)
            }
            MainViewModel.State.Loading -> {
                LoadingView()
            }
            is MainViewModel.State.Choose -> {

                isShowTop.value = false
                isShowBottom.value = false
//                ScreenLive(item = state.url) {
//                    viewModel.curState.value =
//                        ViewModelLive.State.Detail(curStateDetailBackground.value!!)
//                }
            }
            null -> throw IllegalStateException()
        }
    }

}

@ExperimentalSerializationApi
@Composable
fun MainListScreen(
    mainItem: MainBackgrounds,
    viewModel: MainViewModel,
    circularProgressDrawable: CircularProgressDrawable
) {
    val previews = mainItem.categories_all.chunked(3)
    BoxWithConstraints {
        val maxHeight = maxHeight
        val maxWidth = maxWidth
        Column {
            FavoriteHorizontalList(
                favoriteList = mainItem.favorites_cat,
                viewModel = viewModel,
                maxHeight,
                maxWidth
            )
            LazyColumn {
                item {
                    CategoriesView(
                        mainItem.live_category.urlPhoto,
                        mainItem.live_category.nameCategory,
                        mainItem.live_category.link,
                        maxHeight,
                        maxWidth
                    )
                    CategoriesView(
                        mainItem.category3d.urlPhoto,
                        mainItem.category3d.nameCategory,
                        mainItem.category3d.link,
                        maxHeight,
                        maxWidth
                    )
                    CategoriesView(
                        mainItem.category4k.urlPhoto,
                        mainItem.category4k.nameCategory,
                        mainItem.category4k.link,
                        maxHeight,
                        maxWidth
                    )
                }



                item {
                    Spacer(modifier = Modifier.padding(30.dp))
                }
                previews.forEach { list ->
                    item {
                        MainPreviewItem(
                            item = list,
                            circularProgressDrawable = circularProgressDrawable,
                            viewModel = viewModel
                        )
                    }
                }


            }
        }
    }

}

@Composable
private fun CategoriesView(
    url: String,
    title: String,
    link: String,
    maxHeight: Dp,
    maxWidth: Dp
) {
    Box {
        Text(
            text = title, modifier = Modifier
                .fillMaxWidth()
                .align(
                    Alignment.Center
                ), textAlign = TextAlign.Start
        )
        IconButton(onClick = {

        }, modifier = Modifier.align(Alignment.CenterEnd)) {
            Image(Icons.Filled.ArrowForward, contentDescription = "forward")
        }

    }

    Image(
        painter = rememberImagePainter(data = "$baseUrl${url}"),
        contentDescription = null,
        modifier = Modifier
            .height(maxHeight.div(3))
            .width(maxWidth),
        contentScale = ContentScale.Crop
    )
}


@ExperimentalSerializationApi
@Composable
fun FavoriteHorizontalList(
    favoriteList: List<FavoritesCat>,
    viewModel: MainViewModel,
    height: Dp,
    width: Dp
) {

    val stateList =
        if (viewModel.rememberHorizontalList.value == null) rememberLazyListState() else viewModel.rememberHorizontalList.value
    val visibleElement = remember { mutableStateOf(favoriteList.firstOrNull()) }


    Spacer(modifier = Modifier.padding(vertical = 6.dp))
    Column(Modifier.padding(horizontal = 2.dp)) {
        LazyRow(state = stateList!!) {
            favoriteList.forEachIndexed { i, fCat ->
                item {
                    Image(
                        painter = rememberImagePainter(data = "$baseUrl${fCat.urlPhoto}",
                            builder = {
                                transformations(RoundedCornersTransformation(30.0f, 30.0f, 30f, 30f))
                            }),
                        contentDescription = null,
                        modifier = Modifier
                            .height(height.div(3))
                            .width(width),
                    )
                    if (i < favoriteList.size - 1) {
                        Spacer(modifier = Modifier.padding(6.dp))
                    }
                }
            }

        }
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            for (i in favoriteList.indices) {
                if (i == stateList.firstVisibleItemIndex) {
                    Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_ellipse_choose), contentDescription = "choose", modifier = Modifier.size(9.dp))
                } else {
                    Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_ellipse), "ellipse")
                }
                Spacer(modifier = Modifier.padding(horizontal = 1.dp))
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
    }


}


@ExperimentalSerializationApi
@Composable
fun MainPreviewItem(
    item: List<CategoriesAll>,
    circularProgressDrawable: Drawable,
    viewModel: MainViewModel
) {
//    Column {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val int = maxWidth
        Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {

            item.forEach {
                val finalUrl = "$baseUrl${it.urlPhoto}"
                Image(
                    painter = rememberImagePainter(
                        data = finalUrl,
                        builder = {
                            crossfade(true)
                            placeholder(circularProgressDrawable)
                            transformations(
                                RoundedCornersTransformation(
                                    10.0f,
                                    10.0f,
                                    10f,
                                    10f
                                )
                            )
                        }),
                    contentDescription = null,
                    modifier = Modifier
                        .height(220.dp)
                        .width(int / 3),
                    contentScale = ContentScale.Fit
                )

            }
        }
    }
//    }
//    LaunchedEffect(key1 = loadingState.value) {
//        if (viewModel.cur3DBackgroundLink.value.isNotEmpty() && loadingState.value) {
//            viewModel.getPreviews3D(viewModel.cur3DBackgroundLink.value)
//            loadingState.value = false
//        }
//    }

}