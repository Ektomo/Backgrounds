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
    val TAG = "ViewModel3D"
    var cur3DBackgroundLink = mutableStateOf("")
    val curState = MutableLiveData<State>(State.Loading)
    val curStateDetailBackground = MutableLiveData<Preview3DList>()
    val curState3DList = MutableLiveData<List<Backgrounds3D>>()

    val rememberListState = MutableLiveData<LazyListState>()
    val rememberPreviewState = MutableLiveData<LazyListState>()
    val curPreview3D = MutableLiveData<Preview3D>()
    val fileApi = FileApi()

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
        viewModelScope.launch {
            try{
                curState.value = State.Loading
                val apiService = ApiService.getInstance()
                val a = apiService.get3DBackgrounds()
                curState.value = State.Data(a)
            }catch (e: Exception){
                curState.value = State.Error(e.message)
            }

        }
    }



    fun getPreviews3D(url: String) {
        viewModelScope.launch {
            try{
                curState.value = State.Loading
                val apiService = ApiService.getInstance()
                val a = apiService.get3DPreview(url)
                curState.value = State.Detail(a)
            }catch (e: Exception){
                curState.value = State.Error(e.message)
            }

        }
    }

    fun saveImagesForParallax(source: Preview3D, context: Context){
        viewModelScope.launch(Dispatchers.Default) {
            try {
                curState.postValue(State.Loading)
                val apiService = ApiService.getInstance()
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
                    curState.postValue( State.Choose(
                        listOf(
                            Layer(firstFile, 6),
                            Layer(secondFile, 4),
                            Layer(thirdFile, 2)
                        )
                    ))
                }
            }catch (e: Exception){
                curState.postValue(State.Error(e.localizedMessage))
            }

        }
    }

//    private fun saveToRoot(context: Context, response: ResponseBody?, name: String, fileName: String, ext: String): File?{
//        if(response == null){
//            return null
//        }
//        var filePath = context.filesDir.toString()
//        val fileFolder = File("$filePath/3dCache/")
//        if (!fileFolder.exists()) fileFolder.mkdir()
//        filePath = "$filePath/3dCache/$name/"
//        val fileConPath = File(filePath)
//        if (!fileConPath.exists()) fileConPath.mkdir()
//        var input: InputStream? = null
//        try {
//            input = response.byteStream()
//            //val file = File(getCacheDir(), "cacheFileAppeal.srl")
//            val file = File("$filePath$fileName.$ext")
//            if (!file.exists()) file.createNewFile()
//            val fos = FileOutputStream("$filePath$fileName.$ext")
//            fos.use { output ->
//                val buffer = ByteArray(4 * 1024) // or other buffer size
//                var read: Int
//                while (input.read(buffer).also { read = it } != -1) {
//                    output.write(buffer, 0, read)
//                }
//                output.flush()
//            }
//            return File("$filePath$fileName.$ext")
//        }catch (e:Exception){
//            Log.e("saveFile",e.toString())
//        }
//        finally {
//            input?.close()
//        }
//        return null
//    }


}