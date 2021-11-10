package ivan.gorbunov.backgrounds.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.gorbunov.backgrounds.screens.fourk.ViewModel4K
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {

    val stateStack =
        MutableLiveData<MutableList<NavigationItem>>(mutableListOf(NavigationItem.Home))
    val curState = MutableLiveData<NavigationItem>(NavigationItem.Home)
    var onBack = {
        stateStack.value!!.removeLast()
        curState.value = stateStack.value!!.last()
    }


}