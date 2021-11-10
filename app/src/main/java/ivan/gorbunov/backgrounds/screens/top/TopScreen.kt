package ivan.gorbunov.backgrounds.screens.top

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import ivan.gorbunov.backgrounds.R
import ivan.gorbunov.backgrounds.api.baseUrl
import ivan.gorbunov.backgrounds.pojo.VideoItem
import ivan.gorbunov.backgrounds.screens.AdMobType
import ivan.gorbunov.backgrounds.screens.AdMobView
import ivan.gorbunov.backgrounds.screens.LoadingView
import ivan.gorbunov.backgrounds.screens.fourk.ButtonSave
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.*
import kotlin.math.abs

@ExperimentalSerializationApi
@Composable
fun TopScreen(
    isShowTop: MutableState<Boolean>,
    isShowBottom: MutableState<Boolean>,
    onBack: () -> Unit
) {

    val viewModel = hiltViewModel<TopViewModel>()
    val curState = viewModel.curState.observeAsState()

    Crossfade(targetState = curState.value) { state ->
        when (state) {
            is TopViewModel.State.List -> {
                isShowBottom.value = true
                isShowTop.value = false
                viewModel.curStateTopBackground.value = state.data
                ExoPlayerColumnAutoplayScreen(viewModel = viewModel)
            }
            is TopViewModel.State.Error -> {
                Text(text = state.error ?: "")
            }

            TopViewModel.State.Loading -> {
                LoadingView()
            }
            null -> throw IllegalStateException()
        }
    }

    BackHandler(true, onBack )

}



@ExperimentalSerializationApi
@Composable
fun ExoPlayerColumnAutoplayScreen(
    viewModel: TopViewModel

) {
    val listState =
        if (viewModel.rememberListState.value == null) rememberLazyListState() else viewModel.rememberListState.value
    if (viewModel.rememberListState.value == null){
        viewModel.rememberListState.value = listState
    }
    val videos = viewModel.curStateTopBackground.observeAsState()


    BoxWithConstraints(modifier = Modifier
        .fillMaxHeight()
        .fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState!!) {
            videos.value?.array?.forEachIndexed { i, video ->
                item {
                    ScreenTop(item = video.url, i, listState, maxWidth, maxHeight)

                }

            }
        }
    }

}

@Composable
fun ScreenTop(item: String, indexItem: Int, lazyList: LazyListState, width: Dp, height: Dp) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val exoPlayer = getExoPlayer(context, item)


    val sound = remember {
        mutableStateOf(100f)

    }
    BoxWithConstraints(
        modifier = Modifier
            .height(height)
            .width(width)
    ) {
        AndroidView(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
            factory = { context ->
                val p = PlayerView(context).apply {
//                    hideController()
                    player = exoPlayer
                    setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                    controllerAutoShow = false
                    hideController()
                    player!!.repeatMode = Player.REPEAT_MODE_ONE
                    player!!.volume = sound.value
                }
                p.setControllerVisibilityListener { visibility ->
                    if (visibility == View.VISIBLE) {
                        p.hideController()
                    }
                }
                p.setOnClickListener {
                    if (p.player!!.isPlaying) {
                        p.player!!.pause()
                    } else {
                        p.player!!.play()
                    }
                }
                p.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                p
            }
        )

//        AdMobView(
//            type = AdMobType.Banner, modifier = Modifier
//                .align(BiasAlignment(1f, 0.8f))
//                .fillMaxWidth()
//        )
        ButtonSave(modifier = Modifier.align(BiasAlignment(0.9f, 0.6f))) {

        }
        IconButton(
            onClick = {
                if (exoPlayer.volume > 0) {
                    exoPlayer.volume = 0f
                } else {
                    exoPlayer.volume = 0.8f
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_volume_off),
                contentDescription = "volume",
                tint = Color.White
            )
        }

        LaunchedEffect(key1 = lazyList.firstVisibleItemIndex){
            if (indexItem == lazyList.firstVisibleItemIndex){
                exoPlayer.play()
            }else{
                exoPlayer.pause()
            }
        }


        DisposableEffect(key1 = exoPlayer) {
            val lifecycleObserver = LifecycleEventObserver { _, event ->
                if (indexItem == -1) return@LifecycleEventObserver
                when (event) {
                    Lifecycle.Event.ON_START -> exoPlayer.play()
                    Lifecycle.Event.ON_STOP -> exoPlayer.pause()
                    else -> exoPlayer.pause()
                }
            }

            lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            }
            onDispose { exoPlayer.release() }
        }

    }
}

@Composable
private fun getExoPlayer(
    context: Context,
    item: String
): ExoPlayer {
    val exoPlayer = remember(context) {

        ExoPlayer.Builder(context).build().apply {
            val dataSourceFactory: DataSource.Factory =
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
    return exoPlayer
}



