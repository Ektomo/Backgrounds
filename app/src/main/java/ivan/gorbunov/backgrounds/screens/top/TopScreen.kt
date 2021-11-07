package ivan.gorbunov.backgrounds.screens.top

import android.net.Uri
import android.view.View
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
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
import java.util.*
import kotlin.math.abs

@Composable
fun TopScreen(
    navHostController: NavHostController,
    isShowTop: MutableState<Boolean>,
    isShowBottom: MutableState<Boolean>,
    title: MutableState<Any>
) {
    val viewModel = hiltViewModel<TopViewModel>()
    val curState = viewModel.curState.observeAsState()
    val curRememberListState =
        if (viewModel.rememberListState.value == null) rememberLazyListState() else viewModel.rememberListState.value

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



}



@Composable
fun ExoPlayerColumnAutoplayScreen(
    viewModel: TopViewModel

) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val listState =
        if (viewModel.rememberListState.value == null) rememberLazyListState() else viewModel.rememberListState.value
    val exoPlayer = remember { SimpleExoPlayer.Builder(context).build() }
    val videos = viewModel.curStateTopBackground.observeAsState()
    val playingVideoItem = remember { mutableStateOf(videos.value?.array?.firstOrNull()) }

    LaunchedEffect(listState!!.firstVisibleItemIndex) {

        if (listState.firstVisibleItemIndex > videos.value!!.array.size - 1) {
            playingVideoItem.value = videos.value!!.array[listState.firstVisibleItemIndex]
        } else {
            playingVideoItem.value = null
        }
    }

    LaunchedEffect(playingVideoItem.value) {
        // is null only upon entering the screen
        if (playingVideoItem.value == null) {
            exoPlayer.pause()
        } else {
            // move playWhenReady to exoPlayer initialization if you don't
            // want to play next video automatically
            exoPlayer.setMediaItem(MediaItem.fromUri(playingVideoItem.value!!.url))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (playingVideoItem.value == null) return@LifecycleEventObserver
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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(bottom = 8.dp, start = 16.dp, end = 16.dp)) {
        videos.value?.array?.forEach { video ->
            item {
                Spacer(modifier = Modifier.height(16.dp))

                ScreenTop(item = video.url)
            }

        }
    }
}

@Composable
fun ScreenTop(item: String) {

    val context = LocalContext.current
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


    val sound = remember {
        mutableStateOf(100f)

    }
    BoxWithConstraints(
        modifier = Modifier
            .height(700.dp)
            .fillMaxWidth()
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
//                    this.player!!.playWhenReady = true
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

        AdMobView(
            type = AdMobType.Banner, modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
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
                contentDescription = "volume"
            )
        }


        DisposableEffect(key1 = exoPlayer.playbackState) {
            onDispose { exoPlayer.release() }
        }

    }
}



