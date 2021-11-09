package ivan.gorbunov.backgrounds.screens.live

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.gorbunov.backgrounds.api.ApiService
import ivan.gorbunov.backgrounds.pojo.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

@ExperimentalSerializationApi
@HiltViewModel
class ViewModelLive @Inject constructor() : ViewModel() {
    var curLiveLink = mutableStateOf("")
    val curState = MutableLiveData<State>(State.Loading)
    val curStateDetailBackground = MutableLiveData<LiveBackgroundPreview>()
    val curStateLiveList = MutableLiveData<List<LiveBackgrounds>>()

    val rememberListState = MutableLiveData<LazyListState>()
    val rememberPreviewState = MutableLiveData<LazyListState>()

    init {
        getLiveBackgrounds()
    }


    sealed class State {
        object Loading: State()
        data class Error(val error: String?): State()
        data class Data(val data: List<LiveBackgrounds>): State()
        data class Detail(val data: LiveBackgroundPreview): State()
        data class Choose(val url: String): State()
    }


    @ExperimentalSerializationApi
    fun getLiveBackgrounds() {
        viewModelScope.launch(Dispatchers.Default) {
            try{
                curState.postValue(State.Loading)
                val apiService = ApiService.getInstance()
                val a = apiService.getLiveBackgrounds()
                curState.postValue( State.Data(a))
//                _items.value = a
            }catch (e: Exception){
                curState.postValue(State.Error(e.message))
            }

        }
    }



    fun getPreviewLiveBackgrounds(url: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try{
                curState.postValue(State.Loading)
                val apiService = ApiService.getInstance()
                val a = apiService.getLivePreview(url)
                curState.postValue(State.Detail(a))
            }catch (e: Exception){
                curState.postValue(State.Error(e.message))
            }

        }
    }
}