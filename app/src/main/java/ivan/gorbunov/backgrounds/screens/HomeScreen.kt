package ivan.gorbunov.backgrounds.screens

import android.net.Uri
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.*
import coil.compose.rememberImagePainter
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import ivan.gorbunov.backgrounds.R
import ivan.gorbunov.backgrounds.api.baseUrl
import ivan.gorbunov.backgrounds.pojo.VideoItem
import ivan.gorbunov.backgrounds.ui.theme.Shapes
import ivan.gorbunov.backgrounds.vmodels.HomeViewModel
import kotlin.math.abs


//@Composable
//fun VideosScreen(viewModel: HomeViewModel = viewModel()) {
//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    val exoPlayer = remember(context) { SimpleExoPlayer.Builder(context).build() }
//    val listState = rememberLazyListState()
//
//    val videos by viewModel.videos.observeAsState(listOf())
//    val playingItemIndex by viewModel.currentlyPlayingIndex.observeAsState()
//    val isCurrentItemVisible = remember { mutableStateOf(false) }
//    val playingVideoItem = remember { mutableStateOf(videos.firstOrNull()) }
//
//    LaunchedEffect(Unit) {
//        snapshotFlow {
//            listState.playingItem(videos)
//        }
//    }
//
//    LaunchedEffect(playingItemIndex) {
//        if (playingItemIndex == null) {
//            exoPlayer.pause()
//        } else {
//            val video = videos[playingItemIndex!!]
//            exoPlayer.setMediaItem(MediaItem.fromUri(video.mediaUrl), video.lastPlayedPosition)
//            exoPlayer.prepare()
//            exoPlayer.playWhenReady = true
//        }
//    }
//
//    LaunchedEffect(isCurrentItemVisible.value) {
//        if (!isCurrentItemVisible.value && playingItemIndex != null) {
//            viewModel.onPlayVideoClick(exoPlayer.currentPosition, playingItemIndex!!)
//        }
//    }
//
//    DisposableEffect(exoPlayer) {
//        val lifecycleObserver = LifecycleEventObserver { _, event ->
//            if (playingItemIndex == null) return@LifecycleEventObserver
//            when (event) {
//                Lifecycle.Event.ON_START -> exoPlayer.play()
//                Lifecycle.Event.ON_STOP -> exoPlayer.pause()
//            }
//        }
//
//        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
//            exoPlayer.release()
//        }
//    }
//
////    LazyColumn(
////        state = listState,
////        modifier = Modifier.fillMaxSize(),
////        contentPadding = rememberInsetsPaddingValues(
////            insets = LocalWindowInsets.current.systemBars,
////            applyTop = true,
////            applyBottom = true,
////            additionalStart = 16.dp,
////            additionalEnd = 16.dp,
////            additionalBottom = 8.dp
////        )
////    ) {
////        itemsIndexed(videos, { _, video -> video.id }) { index, video ->
////            Spacer(modifier = Modifier.height(16.dp))
////            VideoCard(
////                videoItem = video,
////                exoPlayer = exoPlayer,
////                isPlaying = index == playingItemIndex,
////                onClick = {
////                    viewModel.onPlayVideoClick(exoPlayer.currentPosition, index)
////                }
////            )
////        }
////    }
//}
//
//
//private fun LazyListState.visibleAreaContainsItem(
//    currentlyPlayedIndex: Int?,
//    videos: List<VideoItem>
//): Boolean {
//    return when {
//        currentlyPlayedIndex == null -> false
//        videos.isEmpty() -> false
//        else -> {
//            layoutInfo.visibleItemsInfo.map { videos[it.index] }
//                .contains(videos[currentlyPlayedIndex])
//        }
//    }
//}
//
//private fun LazyListState.playingItem(videos: List<VideoItem>): VideoItem? {
//    if (layoutInfo.visibleItemsInfo.isNullOrEmpty() || videos.isEmpty()) return null
//    val layoutInfo = layoutInfo
//    val visibleItems = layoutInfo.visibleItemsInfo
//    val lastItem = visibleItems.last()
//    val firstItemVisible = firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0
//    val itemSize = lastItem.size
//    val itemOffset = lastItem.offset
//    val totalOffset = layoutInfo.viewportEndOffset
//    val lastItemVisible = lastItem.index == videos.size - 1 && totalOffset - itemOffset >= itemSize
//    val midPoint = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
//    val centerItems = visibleItems.sortedBy { abs((it.offset + it.size / 2) - midPoint) }
//
//    return when {
//        firstItemVisible -> videos.first()
//        lastItemVisible -> videos.last()
//        else -> centerItems.firstNotNullOf { videos[it.index] }
//    }
//}

//@Composable
//fun VideoCard(
//    modifier: Modifier = Modifier,
//    videoItem: VideoItem,
//    isPlaying: Boolean,
//    exoPlayer: SimpleExoPlayer,
//    onClick: () -> Unit
//) {
//    val isPlayerUiVisible = remember { mutableStateOf(false) }
//    val isPlayButtonVisible = if (isPlayerUiVisible.value) true else !isPlaying
//
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .background(Color.Black, Shapes.medium)
//            .clip(Shapes.medium),
//        contentAlignment = Alignment.Center
//    ) {
//        if (isPlaying) {
//            VideoPlayer(exoPlayer) { uiVisible ->
//                if (isPlayerUiVisible.value) {
//                    isPlayerUiVisible.value = uiVisible
//                } else {
//                    isPlayerUiVisible.value = true
//                }
//            }
//        } else {
//            VideoThumbnail(videoItem.thumbnail)
//        }
//        if (isPlayButtonVisible) {
//            Icon(
//                painter = painterResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
//                contentDescription = "",
//                modifier = Modifier
//                    .align(Alignment.Center)
//                    .size(72.dp)
//                    .clip(RoundedCornerShape(percent = 50))
//                    .clickable { onClick() })
//        }
//    }
//}


@Composable
fun HomeScreenasd(viewModel: HomeViewModel = viewModel()) {

    val context = LocalContext.current
    val addresses = viewModel.urls.observeAsState(listOf())
    val videoItems by viewModel.videos.observeAsState()
    val playingItemIndex by viewModel.currentlyPlayingIndex.observeAsState()
    val exoPlayer = remember { SimpleExoPlayer.Builder(context).build() }



    LazyColumn {
        if (addresses.value.isNotEmpty()) {
            addresses.value.forEach { address ->
                item {

                    exoPlayer.apply {
                        val dataSourceFactory: com.google.android.exoplayer2.upstream.DataSource.Factory =
                            DefaultDataSourceFactory(
                                context,
                                Util.getUserAgent(context, context.packageName)
                            )

                        val mediaItem = MediaItem.fromUri(
                            Uri.parse(
                                address
//                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                            )
                        )

                        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(
                                mediaItem
                            )
                        setMediaSource(source)
                        this.prepare()
                    }


                    val sound = remember {
                        mutableStateOf(100)

                    }
                    Box(modifier = Modifier.fillMaxSize()) {

                        AndroidView(
                            factory = { context ->
                                PlayerView(context).apply {
                                    player = exoPlayer
                                    playSoundEffect(sound.value)
                                }
                            }
                        )
                    }
                }
            }
        } else {

//            item {
//
//                exoPlayer.apply {
//                    val dataSourceFactory: com.google.android.exoplayer2.upstream.DataSource.Factory =
//                        DefaultDataSourceFactory(
//                            context,
//                            Util.getUserAgent(context, context.packageName)
//                        )
//
//                    val mediaItem = MediaItem.fromUri(
//                        Uri.parse(
//                            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
//                        )
//                    )
//
//                    val source = ProgressiveMediaSource.Factory(dataSourceFactory)
//                        .createMediaSource(
//                            mediaItem
//                        )
//                    setMediaSource(source)
//                    this.prepare()
//                }
//
//                val sound = remember {
//                    mutableStateOf(100)
//
//                }
//                Box(modifier = Modifier.fillMaxSize()) {
//                    AndroidView(modifier = Modifier.fillMaxSize(),
//                        factory = { context ->
//                            PlayerView(context).apply {
//                                hideController()
//                                player = exoPlayer
//                                controllerAutoShow = false
//
//                                this.player!!.playWhenReady = true
//                                playSoundEffect(sound.value)
//                            }
//                        }
//                    )
//                    AdMobView(type = AdMobType.Banner, modifier = Modifier.align(Alignment.Center))
//                }
//                Spacer(modifier = Modifier.padding(20.dp))
//
//            }

//            item {
//
//                exoPlayer.apply {
//                    val dataSourceFactory: com.google.android.exoplayer2.upstream.DataSource.Factory =
//                        DefaultDataSourceFactory(
//                            context,
//                            Util.getUserAgent(context, context.packageName)
//                        )
//
//                    val mediaItem = MediaItem.fromUri(
//                        Uri.parse(
//                            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
//                        )
//                    )
//
//                    val source = ProgressiveMediaSource.Factory(dataSourceFactory)
//                        .createMediaSource(
//                            mediaItem
//                        )
//                    setMediaSource(source)
//                    this.prepare()
//                }
//
////                val exoPlayer = remember(context) {
////                    SimpleExoPlayer.Builder(context).build().apply {
////                        val dataSourceFactory: com.google.android.exoplayer2.upstream.DataSource.Factory =
////                            DefaultDataSourceFactory(
////                                context,
////                                Util.getUserAgent(context, context.packageName)
////                            )
////
////                        val mediaItem = MediaItem.fromUri(
////                            Uri.parse(
////                                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
////                            )
////                        )
////
////                        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
////                            .createMediaSource(
////                                mediaItem
////                            )
////                        setMediaSource(source)
////                        this.prepare()
////                    }
////                }
//
//                val sound = remember {
//                    mutableStateOf(100)
//
//                }
//                Box(modifier = Modifier.height(200.dp)) {
////                    AdMobView(type = AdMobType.Banner)
//                    AndroidView(modifier = Modifier.fillMaxHeight(),
//                        factory = { context ->
//                            PlayerView(context).apply {
//                                hideController()
//                                player = exoPlayer
//                                controllerAutoShow = false
//
//                                this.player!!.playWhenReady = true
//                                playSoundEffect(sound.value)
//                            }
//                        }
//                    )
//                    AdMobView(type = AdMobType.Native, modifier = Modifier.align(Alignment.Center))
//                }
//                Spacer(modifier = Modifier.padding(20.dp))
//
//            }
//
            item {

                val exoPlayer = remember(context) {
                    SimpleExoPlayer.Builder(context).build().apply {
                        val dataSourceFactory: com.google.android.exoplayer2.upstream.DataSource.Factory =
                            DefaultDataSourceFactory(
                                context,
                                Util.getUserAgent(context, context.packageName)
                            )

                        val mediaItem = MediaItem.fromUri(
                            Uri.parse(
                                "$baseUrl/live/top/1.mp4"
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
                    mutableStateOf(100)

                }
                BoxWithConstraints {

                    AndroidView(modifier = Modifier.height(512.dp).width(maxWidth),
                        factory = { context ->
                            PlayerView(context).apply {
                                hideController()
                                player = exoPlayer
                                controllerAutoShow = false

                                this.player!!.playWhenReady = true
                                playSoundEffect(sound.value)
                            }
                        }
                    )
                    AdMobView(type = AdMobType.Banner, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(modifier = Modifier.padding(20.dp))

            }

//            item {
//
//                val exoPlayer = remember(context) {
//                    SimpleExoPlayer.Builder(context).build().apply {
//                        val dataSourceFactory: com.google.android.exoplayer2.upstream.DataSource.Factory =
//                            DefaultDataSourceFactory(
//                                context,
//                                Util.getUserAgent(context, context.packageName)
//                            )
//
//                        val mediaItem = MediaItem.fromUri(
//                            Uri.parse(
//                                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
//                            )
//                        )
//
//                        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
//                            .createMediaSource(
//                                mediaItem
//                            )
//                        setMediaSource(source)
//                        this.prepare()
//                    }
//                }
//
//                val sound = remember {
//                    mutableStateOf(100)
//
//                }
//                Box(modifier = Modifier.height(200.dp)) {
//                    AdMobView(type = AdMobType.Banner)
//                    AndroidView(
//                        factory = { context ->
//                            PlayerView(context).apply {
//                                player = exoPlayer
//                                playSoundEffect(sound.value)
//                            }
//                        }
//                    )
//                }
//                Spacer(modifier = Modifier.padding(20.dp))
//
//            }
//
//            item {
//
//                val exoPlayer = remember(context) {
//                    SimpleExoPlayer.Builder(context).build().apply {
//                        val dataSourceFactory: com.google.android.exoplayer2.upstream.DataSource.Factory =
//                            DefaultDataSourceFactory(
//                                context,
//                                Util.getUserAgent(context, context.packageName)
//                            )
//
//                        val mediaItem = MediaItem.fromUri(
//                            Uri.parse(
//                                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
//                            )
//                        )
//
//                        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
//                            .createMediaSource(
//                                mediaItem
//                            )
//                        setMediaSource(source)
//                        this.prepare()
//                    }
//                }
//
//                val sound = remember {
//                    mutableStateOf(100)
//
//                }
//                Box(modifier = Modifier.fillMaxHeight()) {
//                    AdMobView(type = AdMobType.Banner)
//                    AndroidView(
//                        factory = { context ->
//                            PlayerView(context).apply {
//                                player = exoPlayer
//                                playSoundEffect(sound.value)
//                            }
//                        }
//                    )
//                }
//
//            }
        }


    }

}

@Composable
fun VideoBackgroundItem(){

}


@Composable
fun VideoPlayer(
    exoPlayer: SimpleExoPlayer,
    onControllerVisibilityChanged: (uiVisible: Boolean) -> Unit
) {
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                setControllerVisibilityListener { onControllerVisibilityChanged(it == View.VISIBLE) }
                player = exoPlayer
            }
        },
        Modifier
            .height(256.dp)
            .background(Color.Black)
    )
}

@Composable
fun VideoThumbnail(url: String) {
    Image(
        painter = rememberImagePainter(data = url, builder = {
            crossfade(true)
            size(512, 512)
        }),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .size(256.dp),
        contentScale = ContentScale.Crop
    )
}

