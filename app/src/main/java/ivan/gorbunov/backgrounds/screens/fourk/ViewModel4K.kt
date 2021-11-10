package ivan.gorbunov.backgrounds.screens.fourk

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.gorbunov.backgrounds.api.ApiService
import ivan.gorbunov.backgrounds.pojo.Backgrounds4K
import ivan.gorbunov.backgrounds.pojo.Preview4KBackGrounds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

@ExperimentalSerializationApi
@HiltViewModel
class ViewModel4K @Inject constructor() : ViewModel() {

    var cur4KBackgroundLink = mutableStateOf("")
    val curState = MutableLiveData<State>(State.Loading)
    val curStateDetailBackground = MutableLiveData<Preview4KBackGrounds>()
    val curState4KList = MutableLiveData<List<Backgrounds4K>>()
    val nestedStateStack = MutableLiveData<MutableList<State>>(mutableListOf())

    val rememberListState = MutableLiveData<LazyListState>()
    val rememberPreviewState = MutableLiveData<LazyListState>()

    var onBack = {
        nestedStateStack.value!!.removeLast()
        curState.value = nestedStateStack.value!!.last()
    }



    sealed class State {
        object Loading: State()
        data class Error(val error: String?): State()
        data class Data(val data: List<Backgrounds4K>): State()
        data class Detail(val data: Preview4KBackGrounds): State()
        data class Choose(val url: String): State()
    }

    init {
        get4KBackgrounds()
    }


    @ExperimentalSerializationApi
    fun get4KBackgrounds() {

        viewModelScope.launch(Dispatchers.Default) {
            try{
                curState.postValue( State.Loading)
                val apiService = ApiService.getInstance()
                val a = apiService.get4kBackgrounds()
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



    fun getDetail4KBackgrounds(url: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try{
                curState.postValue( State.Loading)
                val apiService = ApiService.getInstance()
                val a = apiService.get4Preview(url)
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