package ivan.gorbunov.backgrounds.screens.live

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.gorbunov.backgrounds.api.ApiService
import ivan.gorbunov.backgrounds.pojo.*
import ivan.gorbunov.backgrounds.screens.fourk.ViewModel4K
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
    val nestedStateStack = MutableLiveData<MutableList<State>>(mutableListOf())

    val rememberListState = MutableLiveData<LazyListState>()
    val rememberPreviewState = MutableLiveData<LazyListState>()

    var onBack = {
        nestedStateStack.value!!.removeLast()
        curState.value = nestedStateStack.value!!.last()
    }

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
                val state = State.Data(a)
                val stack = nestedStateStack.value
                stack!!.add(state)
                nestedStateStack.postValue(stack)
                curState.postValue(state)
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
                val state = State.Detail(a)
                val stack = nestedStateStack.value
                stack!!.add(state)
                nestedStateStack.postValue(stack)
                curState.postValue(state)
            }catch (e: Exception){
                curState.postValue(State.Error(e.message))
            }

        }
    }
}