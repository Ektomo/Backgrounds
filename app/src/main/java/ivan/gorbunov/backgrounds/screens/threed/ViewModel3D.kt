package ivan.gorbunov.backgrounds.screens.threed

import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.gorbunov.backgrounds.api.ApiService
import ivan.gorbunov.backgrounds.api.FileApi
import ivan.gorbunov.backgrounds.pojo.*
import ivan.gorbunov.backgrounds.screens.live.ViewModelLive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.ResponseBody
import okhttp3.internal.wait
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

@ExperimentalSerializationApi
@HiltViewModel
class ViewModel3D @Inject constructor(): ViewModel() {
    var cur3DBackgroundLink = mutableStateOf("")
    val curState = MutableLiveData<State>(State.Loading)
    val curStateDetailBackground = MutableLiveData<Preview3DList>()
    val curState3DList = MutableLiveData<List<Backgrounds3D>>()

    val nestedStateStack = MutableLiveData<MutableList<State>>(mutableListOf())

    val rememberListState = MutableLiveData<LazyListState>()
    val rememberPreviewState = MutableLiveData<LazyListState>()
    val curPreview3D = MutableLiveData<Preview3D>()
    private val fileApi = FileApi()

    var onBack = {
        nestedStateStack.value!!.removeLast()
        curState.value = nestedStateStack.value!!.last()
    }

    init {
        get3D()
    }



    sealed class State {
        object Loading: State()
        data class Error(val error: String?): State()
        data class Data(val data: List<Backgrounds3D>): State()
        data class Detail(val data: Preview3DList): State()
        data class Choose(val files: List<Layer>): State()
    }




    @ExperimentalSerializationApi
    fun get3D() {
        viewModelScope.launch(Dispatchers.Default) {
            try{
                curState.postValue(State.Loading)
                val apiService = ApiService.getInstance()
                val a = apiService.get3DBackgrounds()
                val state = State.Data(a)
                val stack = nestedStateStack.value
                stack!!.add(state)
                nestedStateStack.postValue(stack)
                curState.postValue(state)
            }catch (e: Exception){
                curState.value = State.Error(e.message)
            }

        }
    }



    fun getPreviews3D(url: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try{
                curState.postValue(State.Loading)
                val apiService = ApiService.getInstance()
                val a = apiService.get3DPreview(url)
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

    fun saveImagesForParallax(source: Preview3D, context: Context){
        viewModelScope.launch(Dispatchers.Default) {
            try {
                curState.postValue(State.Loading)
                val name = source.preview_url.split("/")
                    .dropLast(1).joinToString("")
                    .replace("/", "").replace(" ", "")
                val firstAddress = source.layers[0]
                val firstSplit = firstAddress.split(".")
                val firstExt = firstSplit.last()
                val firstFileName = firstSplit.first().split("/").last()
                val secondAddress = source.layers[1]
                val secondSplit = secondAddress.split(".")
                val secondExt = secondSplit.last()
                val secondFileName = secondSplit.first().split("/").last()
                val thirdAddress = source.layers[2]
                val thirdSplit = thirdAddress.split(".")
                val thirdExt = thirdSplit.last()
                val firstResponse = async(Dispatchers.Default) { fileApi.hop(firstAddress, name, context, firstFileName, firstExt) }
                val secondResponse = async(Dispatchers.Default) { fileApi.hop(secondAddress, name, context, secondFileName, secondExt) }
                val thirdFileName = thirdSplit.first().split("/").last()

                val thirdResponse = async(Dispatchers.Default) { fileApi.hop(thirdAddress, name, context, thirdFileName, thirdExt)}
                val firstFile = firstResponse.await()
                val secondFile =
                    secondResponse.await()
                val thirdFile =
                    thirdResponse.await()
                if (firstFile != null && secondFile != null && thirdFile != null) {
                    val state = State.Choose(
                        listOf(
                            Layer(firstFile, 6),
                            Layer(secondFile, 4),
                            Layer(thirdFile, 2)
                        )
                    )
                    val stack = nestedStateStack.value
                    stack!!.add(state)
                    nestedStateStack.postValue(stack)
                    curState.postValue(state)
                }
            }catch (e: Exception){
                curState.postValue(State.Error(e.localizedMessage))
            }

        }
    }

}