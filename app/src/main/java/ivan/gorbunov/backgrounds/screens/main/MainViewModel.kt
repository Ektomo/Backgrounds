package ivan.gorbunov.backgrounds.screens.main

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.gorbunov.backgrounds.api.ApiService
import ivan.gorbunov.backgrounds.pojo.LiveBackgroundPreview
import ivan.gorbunov.backgrounds.pojo.LiveBackgrounds
import ivan.gorbunov.backgrounds.pojo.MainBackgrounds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

@ExperimentalSerializationApi
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    val curState = MutableLiveData<State>(State.Loading)
    val curMainBackground = MutableLiveData<MainBackgrounds>()

    val rememberListState = MutableLiveData<LazyListState>()
    val rememberHorizontalList = MutableLiveData<LazyListState>()


    init {
        getMainBackground()
    }



    sealed class State {
        object Loading : State()
        data class Error(val error: String?) : State()
        data class Data(val data: MainBackgrounds) : State()
        data class Detail(val data: LiveBackgroundPreview) : State()
        data class Choose(val url: String) : State()
    }


    private fun getMainBackground() {
        viewModelScope.launch(Dispatchers.Default) {
            curState.postValue(State.Loading)
            val api = ApiService.getInstance()
            val backgrounds = api.getMainBackgrounds()
            curState.postValue(State.Data(backgrounds))
        }
    }

}