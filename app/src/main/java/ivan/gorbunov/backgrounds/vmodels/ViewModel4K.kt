package ivan.gorbunov.backgrounds.vmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ivan.gorbunov.backgrounds.api.ApiService
import ivan.gorbunov.backgrounds.pojo.Backgrounds4K
import kotlinx.coroutines.launch

class ViewModel4K : ViewModel() {


    val items = mutableStateOf(listOf<Backgrounds4K>())
    val errorMessage = mutableStateOf("")
    fun getMovies() {
        viewModelScope.launch {
            try{
                val apiService = ApiService.getInstance()
                items.value = apiService.get4kBackgrounds()
            }catch (e: Exception){
                errorMessage.value = e.localizedMessage
            }

        }
    }


}