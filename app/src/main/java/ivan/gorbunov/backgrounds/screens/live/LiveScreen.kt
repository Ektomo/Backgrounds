package ivan.gorbunov.backgrounds.screens.live

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.PlayerView.SHOW_BUFFERING_ALWAYS
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import ivan.gorbunov.backgrounds.R
import ivan.gorbunov.backgrounds.api.baseUrl
import ivan.gorbunov.backgrounds.pojo.LiveBackgrounds
import ivan.gorbunov.backgrounds.screens.AdMobType
import ivan.gorbunov.backgrounds.screens.AdMobView
import ivan.gorbunov.backgrounds.screens.fourk.ButtonSave
import ivan.gorbunov.backgrounds.screens.LoadingView
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.*

@ExperimentalSerializationApi
@Composable
fun ScreenLiveList(
    navHostController: NavHostController,
    isShowTop: MutableState<Boolean>,
    isShowBottom: MutableState<Boolean>,
    title: MutableState<Any>
) {
    val viewModel = hiltViewModel<ViewModelLive>()
    val curState = viewModel.curState.observeAsState()
    val curStateDetailBackground = viewModel.curStateDetailBackground.observeAsState()
    val curStateLiveList = viewModel.curStateLiveList.observeAsState()
    val curRememberListState =
        if (viewModel.rememberListState.value == null) rememberLazyListState() else viewModel.rememberListState.value
    val curRememberPreviewState =
        if (viewModel.rememberPreviewState.value == null) rememberLazyListState() else viewModel.rememberPreviewState.value
    val circularProgressDrawable = CircularProgressDrawable(LocalContext.current)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f


    Crossfade(targetState = curState.value) { state ->
        when (state) {
            is ViewModelLive.State.Data -> {
                title.value = "Live Categories"
                viewModel.curStateLiveList.value = state.data
                circularProgressDrawable.start()
                LazyColumn(state = curRememberListState!!) {
                    state.data.forEach {
                        item {
                            ScreenLiveItem(item = it, circularProgressDrawable)
                        }
                    }
                    item{
                        Spacer(modifier = Modifier.padding(bottom = 32.dp))
                    }
                }
            }
            is ViewModelLive.State.Error -> {
                Text(text = state.error ?: "")
            }
            is ViewModelLive.State.Detail -> {
                title.value = state.data.nameCategory.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                isShowTop.value = true
                isShowBottom.value = true
                viewModel.curStateDetailBackground.value = state.data
                PreviewLiveList(state, curRememberPreviewState, viewModel)
            }
            ViewModelLive.State.Loading -> {
                LoadingView()
            }
            is ViewModelLive.State.Choose -> {

                isShowTop.value = false
                isShowBottom.value = false
                ScreenLive(item = state.url) {
                    viewModel.curState.value =
                        ViewModelLive.State.Detail(curStateDetailBackground.value!!)
                }
            }
            null -> throw IllegalStateException()
        }
    }


    if (curState.value is ViewModelLive.State.Detail) {
        BackHandler(true) {
            isShowTop.value = true
            isShowBottom.value = true
            when (curState.value) {
                is ViewModelLive.State.Detail -> {
                    viewModel.curState.value = ViewModelLive.State.Data(curStateLiveList.value!!)
                }
                else -> {
                    navHostController.popBackStack()
                }
            }
        }
    }

}

@Composable
private fun PreviewLiveList(
    state: ViewModelLive.State.Detail,
    curRememberPreviewState: LazyListState?,
    viewModel: ViewModelLive
) {
    val (o, t) = state.data.array.partition { state.data.array.indexOf(it) % 2 == 0 }
    BoxWithConstraints {
        val maxWidth = maxWidth
        val maxHeight = maxHeight
        LazyColumn(state = curRememberPreviewState!!, contentPadding = PaddingValues(2.dp)) {
            o.forEachIndexed { i, oV ->
                item {
                    Row {
                        PreviewLiveScreenItem(
                            modifier = Modifier.clickable {
                                viewModel.curState.value =
                                    ViewModelLive.State.Choose(oV.url)
                            },
                            url = oV.preview_url,
                            int = maxWidth / 2,
                            height = maxHeight / 2
                        )
                        Spacer(modifier = Modifier.padding(1.dp))
                        if (t.size - 1 >= i) {
                            PreviewLiveScreenItem(
                                modifier = Modifier.clickable {
                                    viewModel.curState.value =
                                        ViewModelLive.State.Choose(t[i].url)
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
fun PreviewLiveScreenItem(modifier: Modifier = Modifier, url: String, int: Dp, height: Dp) {
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
fun ScreenLiveItem(
    item: LiveBackgrounds,
    circularProgressDrawable: Drawable,
    viewModelLive: ViewModelLive = viewModel()
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
                viewModelLive.curLiveLink.value = item.link
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
        if (!viewModelLive.curLiveLink.value.isNullOrEmpty() && loadingState.value) {
            viewModelLive.getPreviewLiveBackgrounds(viewModelLive.curLiveLink.value)
            loadingState.value = false
        }
    }

}

@Composable
fun ScreenLive(item: String, onBack: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val playPause = remember { mutableStateOf(true) }
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            val dataSourceFactory: com.google.android.exoplayer2.upstream.DataSource.Factory =
                DefaultDataSourceFactory(
                    context,
                    Util.getUserAgent(context, context.packageName)
                )


            val mediaItem = MediaItem.fromUri(
                Uri.parse(
                    "$baseUrl$item"
                )
            )

            val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(
                    mediaItem
                )
            setMediaSource(source)
            this.prepare()
        }
    }


    val sound = remember {
        mutableStateOf(100f)

    }
    BoxWithConstraints {
        AndroidView(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
            factory = { context ->
                val p = PlayerView(context).apply {
//                    hideController()
                    player = exoPlayer
                    setShowBuffering(SHOW_BUFFERING_ALWAYS)
                    controllerAutoShow = false
                    hideController()

                    player!!.repeatMode = REPEAT_MODE_ONE
                    this.player!!.playWhenReady = true
                    player!!.volume = sound.value


//                    if(playPause.value){
//                        player!!.play()
//
//                    }else{
//                        player!!.stop()
////                        player!!.pause()
//                    }
                }
                p.setControllerVisibilityListener { visibility ->
                    if (visibility == View.VISIBLE) {
                        p.hideController()
                    }
                }
                p.setOnClickListener{
                    if (p.player!!.isPlaying){
                        p.player!!.pause()
                    }else{
                        p.player!!.play()
                    }
                }
                p.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                p
            }
        )

        AdMobView(
            type = AdMobType.Banner, modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
        ButtonSave(modifier = Modifier.align(BiasAlignment(0.9f, 0.6f))) {

        }
        IconButton(onClick = onBack) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back")
        }

        IconButton(
            onClick = {
            if (exoPlayer.volume > 0){
                exoPlayer.volume = 0f
            }else{
                exoPlayer.volume = 0.8f
            }
            },
        modifier = Modifier
            .align(Alignment.TopEnd).padding(top = 4.dp)) {
            Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_volume_off), contentDescription = "volume")
        }

        BackHandler(true, onBack)


        DisposableEffect(exoPlayer) {
            val lifecycleObserver = LifecycleEventObserver { _, event ->
//                if ( == null) return@LifecycleEventObserver
                when (event) {
                    Lifecycle.Event.ON_START -> exoPlayer.play()
                    Lifecycle.Event.ON_STOP -> exoPlayer.pause()
                }
            }

            lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
                exoPlayer.release()
            }
        }
    }
}

