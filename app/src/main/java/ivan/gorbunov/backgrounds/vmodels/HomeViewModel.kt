package ivan.gorbunov.backgrounds.vmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ivan.gorbunov.backgrounds.pojo.VideoItem

class HomeViewModel: ViewModel() {
    var videos = MutableLiveData<List<VideoItem>>()
    val currentlyPlayingIndex = MutableLiveData<Int?>()
    var urls = MutableLiveData<List<String>>()



    fun onPlayVideoClick(playbackPosition: Long, videoIndex: Int) {
        when (currentlyPlayingIndex.value) {
            null -> currentlyPlayingIndex.postValue(videoIndex)
            videoIndex -> {
                currentlyPlayingIndex.postValue(null)
                videos.value = videos.value!!.toMutableList().also { list ->
                    list[videoIndex] = list[videoIndex].copy(lastPlayedPosition = playbackPosition)
                }
            }
            else -> {
                videos.value = videos.value!!.toMutableList().also { list ->
                    list[currentlyPlayingIndex.value!!] = list[currentlyPlayingIndex.value!!].copy(lastPlayedPosition = playbackPosition)
                }
                currentlyPlayingIndex.postValue(videoIndex)
            }
        }
    }
}