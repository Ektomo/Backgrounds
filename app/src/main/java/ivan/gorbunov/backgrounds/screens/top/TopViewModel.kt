package ivan.gorbunov.backgrounds.screens.top

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.gorbunov.backgrounds.api.ApiService
import ivan.gorbunov.backgrounds.pojo.LiveBackgroundPreview
import ivan.gorbunov.backgrounds.pojo.LiveBackgrounds
import ivan.gorbunov.backgrounds.pojo.TopBackgrounds
import ivan.gorbunov.backgrounds.pojo.VideoItem
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

@ExperimentalSerializationApi
@HiltViewModel
class TopViewModel @Inject constructor(): ViewModel() {
    val curState = MutableLiveData<State>(State.Loading)
    val curStateTopBackground = MutableLiveData<TopBackgrounds>()



    val rememberListState = MutableLiveData<LazyListState>()


    init {
        getTopBackgrounds()
    }


    sealed class State {
        object Loading: State()
        data class Error(val error: String?): State()
        data class List(val data: TopBackgrounds): State()
    }


    @ExperimentalSerializationApi
    fun getTopBackgrounds() {
        viewModelScope.launch {
            try{
                curState.value = State.Loading
                val apiService = ApiService.getInstance()
                val a = apiService.getTopBackgrounds()
                curState.value = State.List(a)
            }catch (e: Exception){
                curState.value = State.Error(e.message)
            }

        }
    }



}