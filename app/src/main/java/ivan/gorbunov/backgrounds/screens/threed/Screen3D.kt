package ivan.gorbunov.backgrounds.screens.threed

import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import ivan.gorbunov.backgrounds.R
import ivan.gorbunov.backgrounds.api.baseUrl
import ivan.gorbunov.backgrounds.pojo.Backgrounds3D
import ivan.gorbunov.backgrounds.pojo.Layer
import ivan.gorbunov.backgrounds.screens.AdMobType
import ivan.gorbunov.backgrounds.screens.AdMobView
import ivan.gorbunov.backgrounds.screens.fourk.ButtonSave
import ivan.gorbunov.backgrounds.screens.LoadingView
import ivan.gorbunov.backgrounds.screens.threed.opengl.GlView
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.*


@ExperimentalSerializationApi
@Composable
fun Screen3DList(
    navHostController: NavHostController,
    isShowTop: MutableState<Boolean>,
    isShowBottom: MutableState<Boolean>,
    title: MutableState<Any>
) {
    val viewModel = hiltViewModel<ViewModel3D>()
    val curState = viewModel.curState.observeAsState()
    val curStateDetailBackground = viewModel.curStateDetailBackground.observeAsState()
    val curState3DList = viewModel.curState3DList.observeAsState()
    val curRememberListState =
        if (viewModel.rememberListState.value == null) rememberLazyListState() else viewModel.rememberListState.value
    val curRememberPreviewState =
        if (viewModel.rememberPreviewState.value == null) rememberLazyListState() else viewModel.rememberPreviewState.value
    val circularProgressDrawable = CircularProgressDrawable(LocalContext.current)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f


    Crossfade(targetState = curState.value) { state ->
        when (state) {
            is ViewModel3D.State.Data -> {
                title.value = "3D Categories"
                viewModel.curState3DList.value = state.data
                circularProgressDrawable.start()
                LazyColumn(state = curRememberListState!!) {
                    state.data.forEach {
                        item {
                            Screen3DItem(item = it, circularProgressDrawable)
                        }
                    }
                    item{
                        Spacer(modifier = Modifier.padding(bottom = 32.dp))
                    }
                }
            }
            is ViewModel3D.State.Error -> {
                Text(text = state.error ?: "")
            }
            is ViewModel3D.State.Detail -> {
                title.value = state.data.nameCategory.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                isShowTop.value = true
                isShowBottom.value = true
                viewModel.curStateDetailBackground.value = state.data
                Preview3DList(state, curRememberPreviewState, viewModel)
            }
            ViewModel3D.State.Loading -> {
                LoadingView()
            }
            is ViewModel3D.State.Choose -> {
                isShowTop.value = false
                isShowBottom.value = false
                Screen3D(list = state.files) {
                    viewModel.curState.value = ViewModel3D.State.Detail(curStateDetailBackground.value!!)
                }
            }
            null -> throw IllegalStateException()
        }
    }


    if (curState.value is ViewModel3D.State.Detail) {
        BackHandler(true) {
            isShowTop.value = true
            isShowBottom.value = true
            when (curState.value) {
                is ViewModel3D.State.Detail -> {
                    viewModel.curState.value = ViewModel3D.State.Data(curState3DList.value!!)
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
private fun Preview3DList(
    state: ViewModel3D.State.Detail,
    curRememberPreviewState: LazyListState?,
    viewModel: ViewModel3D
) {
    val context = LocalContext.current
    val needLoad = remember {
        mutableStateOf(false)
    }
    val curPreview3D = viewModel.curPreview3D.observeAsState()
    val (o, t) = state.data.array.partition { state.data.array.indexOf(it) % 2 == 0 }
    BoxWithConstraints {
        val maxWidth = maxWidth
        val maxHeight = maxHeight
        LazyColumn(state = curRememberPreviewState!!, contentPadding = PaddingValues(2.dp)) {
            o.forEachIndexed { i, oV ->
                item {
                    Row {
                        Preview3DScreenItem(
                            modifier = Modifier.clickable {
                                needLoad.value = true
                                viewModel.curPreview3D.value = oV
                            },
                            url = oV.preview_url,
                            int = maxWidth / 2,
                            height = maxHeight / 2
                        )
                        Spacer(modifier = Modifier.padding(1.dp))
                        if (t.size - 1 >= i) {
                            Preview3DScreenItem(
                                modifier = Modifier.clickable {
                                    needLoad.value = true
                                    viewModel.curPreview3D.value = t[i]
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

    LaunchedEffect(key1 = needLoad.value) {
        if (needLoad.value && viewModel.curPreview3D.value != null) {
            viewModel.saveImagesForParallax(viewModel.curPreview3D.value!!, context)
            needLoad.value = false
        }
    }
}

@Composable
fun Preview3DScreenItem(modifier: Modifier = Modifier, url: String, int: Dp, height: Dp) {
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

@ExperimentalSerializationApi
@Composable
fun Screen3DItem(
    item: Backgrounds3D,
    circularProgressDrawable: Drawable,
    viewModel: ViewModel3D = viewModel()
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
                viewModel.cur3DBackgroundLink.value = item.link
                loadingState.value = true
            }) {
                Image(Icons.Filled.ArrowForward, contentDescription = "forward")
            }
        }
        BoxWithConstraints(
            modifier = Modifier
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
        if (viewModel.cur3DBackgroundLink.value.isNotEmpty() && loadingState.value) {
            viewModel.getPreviews3D(viewModel.cur3DBackgroundLink.value)
            loadingState.value = false
        }
    }

}

@Composable
fun Screen3D(list: List<Layer>, onBack: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var view: GlView? = null
    BoxWithConstraints(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .fillMaxSize(),
    ) {
        val width = maxWidth
        val height = maxHeight
        AndroidView(
            factory = { context ->
                view = GlView(context = context).apply {
                    init(list)
//                    start()
                }
                view!!
            },
            modifier = Modifier
                .width(width)
                .height(height)
        )

        AdMobView(type = AdMobType.Banner, modifier = Modifier.align(Alignment.BottomCenter))
        ButtonSave(modifier = Modifier.align(BiasAlignment(0.9f, 0.6f))) {

        }
        IconButton(onClick = onBack) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back")
        }
        BackHandler(true, onBack)
    }

    DisposableEffect(view) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (view == null) return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_START -> view?.start()
                Lifecycle.Event.ON_STOP -> view?.stop()
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            view!!.stop()
        }
    }

}
