package ivan.gorbunov.backgrounds.screens.fourk

import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import ivan.gorbunov.backgrounds.R
import ivan.gorbunov.backgrounds.api.baseUrl
import ivan.gorbunov.backgrounds.pojo.Backgrounds4K
import ivan.gorbunov.backgrounds.screens.AdMobType
import ivan.gorbunov.backgrounds.screens.AdMobView
import ivan.gorbunov.backgrounds.screens.LoadingView
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.*


@ExperimentalSerializationApi
@Composable
fun Screen4kList(
    navHostController: NavHostController,
    isShowTop: MutableState<Boolean>,
    isShowBottom: MutableState<Boolean>,
    title: MutableState<Any>
) {
    val viewModel = hiltViewModel<ViewModel4K>()
    val curState = viewModel.curState.observeAsState()
    val curStateDetailBackground = viewModel.curStateDetailBackground.observeAsState()
    val curState4KList = viewModel.curState4KList.observeAsState()
    val curRememberListState =
        if (viewModel.rememberListState.value == null) rememberLazyListState() else viewModel.rememberListState.value
    val curRememberPreviewState =
        if (viewModel.rememberPreviewState.value == null) rememberLazyListState() else viewModel.rememberPreviewState.value
    val circularProgressDrawable = CircularProgressDrawable(LocalContext.current)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f


    Crossfade(targetState = curState.value) { state ->
        when (state) {
            is ViewModel4K.State.Data -> {
                title.value = "4k Categories"
                viewModel.curState4KList.value = state.data
                circularProgressDrawable.start()
                LazyColumn(state = curRememberListState!!) {
                    state.data.forEach {
                        item {
                            Screen4kItem(item = it, circularProgressDrawable)
                        }
                    }
                    item{
                        Spacer(Modifier.padding(32.dp))
                    }
                }

            }
            is ViewModel4K.State.Error -> {
                Text(text = state.error ?: "")
            }
            is ViewModel4K.State.Detail -> {
                title.value = state.data.nameCategory.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                isShowTop.value = true
                isShowBottom.value = true
                viewModel.curStateDetailBackground.value = state.data
                Preview4KList(state, curRememberPreviewState, viewModel)

            }
            ViewModel4K.State.Loading -> {
                LoadingView()
            }
            is ViewModel4K.State.Choose -> {
                isShowTop.value = false
                isShowBottom.value = false
                Screen4K(item = state.url, circularProgressDrawable) {
                    viewModel.curState.value =
                        ViewModel4K.State.Detail(curStateDetailBackground.value!!)
                }

            }
            null -> throw IllegalStateException()
        }
    }


    if (curState.value is ViewModel4K.State.Detail) {
        BackHandler(true) {
            isShowTop.value = true
            isShowBottom.value = true
            when (curState.value) {
                is ViewModel4K.State.Detail -> {
                    viewModel.curState.value = ViewModel4K.State.Data(curState4KList.value!!)
                }
                else -> {
                    navHostController.popBackStack()
                }
            }
        }
    }

}

@ExperimentalSerializationApi
@Composable
private fun Preview4KList(
    state: ViewModel4K.State.Detail,
    curRememberPreviewState: LazyListState?,
    viewModel: ViewModel4K,
) {
    val (o, t) = state.data.array.partition { state.data.array.indexOf(it) % 2 == 0 }
    BoxWithConstraints {
        val maxWidth = maxWidth
        val maxHeight = maxHeight
        LazyColumn(state = curRememberPreviewState!!, contentPadding = PaddingValues(2.dp)) {
            o.forEachIndexed { i, oV ->
                item {
                    Row {
                        Preview4KScreenItem(
                            modifier = Modifier.clickable {
                                viewModel.curState.value =
                                    ViewModel4K.State.Choose(oV.url)
                            },
                            url = oV.preview_url,
                            int = maxWidth / 2,
                            height = maxHeight / 2
                        )
                        if (t.size - 1 >= i) {
                            Spacer(modifier = Modifier.padding(1.dp))
                            Preview4KScreenItem(
                                modifier = Modifier.clickable {
                                    viewModel.curState.value =
                                        ViewModel4K.State.Choose(t[i].url)
                                },
                                url = t[i].preview_url,
                                int = maxWidth / 2,
                                maxHeight / 2
                            )
                        }
                    }

                    Spacer(modifier = Modifier.padding(1.dp))
                }
            }
            item {
                Spacer(modifier = Modifier.padding(bottom = 32.dp))
            }
        }
    }
}

@Composable
fun Preview4KScreenItem(modifier: Modifier = Modifier, url: String, int: Dp, height: Dp) {
    Row(
        modifier = modifier
    ) {
        Image(
            painter = rememberImagePainter(data = "$baseUrl${url}",
                builder = {
                    transformations(RoundedCornersTransformation(10.0f, 10.0f, 10f, 10f))
                }),
            contentDescription = null,
            modifier = modifier
                .height(height)
                .width(int),
        )
    }

}

@Composable
fun Screen4kItem(
    item: Backgrounds4K,
    circularProgressDrawable: Drawable,
    viewModel4K: ViewModel4K = viewModel()
) {

    val loadingState = remember {
        mutableStateOf(false)
    }
    val fontEastMan = FontFamily(
        Font(R.font.eastman_regular, FontWeight.Normal)
    )
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = item.nameCategory,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = fontEastMan,

                )
            IconButton(onClick = {
                viewModel4K.cur4KBackgroundLink.value = item.link
                loadingState.value = true
            }) {
                Image(Icons.Filled.ArrowForward, contentDescription = "forward")
            }
        }
        BoxWithConstraints(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .fillMaxSize(),
        ) {
            val int = maxWidth
            Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                item.array.forEach { url ->

                    val finalUrl = "$baseUrl${url.preview_url}"
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
                            .height(180.dp)
                            .width(int / 4),
                        contentScale = ContentScale.Crop
                    )
                }
            }

        }
    }
    LaunchedEffect(key1 = loadingState.value) {
        if (!viewModel4K.cur4KBackgroundLink.value.isNullOrEmpty() && loadingState.value) {
            viewModel4K.getDetail4KBackgrounds(viewModel4K.cur4KBackgroundLink.value)
            loadingState.value = false
        }
    }

}

@Composable
fun Screen4K(item: String,circularProgressDrawable: Drawable, onBack: () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val width = maxWidth
        val height = maxHeight


        Image(
            painter = rememberImagePainter(data = "$baseUrl$item", builder = {
                crossfade(true)
                placeholder(circularProgressDrawable)
            }),
            contentDescription = null,
            modifier = Modifier
                .height(height)
                .width(width),
            contentScale = ContentScale.Crop
        )
        AdMobView(type = AdMobType.Banner, modifier = Modifier.align(Alignment.BottomCenter))
        ButtonSave(modifier = Modifier.align(BiasAlignment(0.9f, 0.6f))) {

        }
        IconButton(onClick = onBack) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back")
        }
        BackHandler(true, onBack)
    }
}

@Composable
fun ButtonSave(modifier: Modifier = Modifier, onClickAction: () -> Unit) {
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFEBEFFB),
                        Color.Transparent
                    )
                )
            )
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(Color(0xFFEBEFFB)),
            modifier = modifier
                .size(74.dp)
                .clip(RoundedCornerShape(25.dp))
            , onClick = onClickAction
        ) {
            Column() {
                Image(
                    modifier = modifier.size(40.dp),
                    alignment = Alignment.Center,
                    painter = painterResource(R.drawable.download),
                    contentDescription = "Download Button"
                )
                Text(text = "Save", fontSize = 16.sp, color = Color(0xFF2329D6))
            }

        }
    }
}

@Preview
@Composable
fun PreviewButton() {
    ButtonSave {

    }
}