package ivan.gorbunov.backgrounds.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ivan.gorbunov.backgrounds.vmodels.ImageViewModel
import androidx.lifecycle.viewmodel.compose.*
import coil.compose.rememberImagePainter

@Composable
fun ImageScreen(viewModel: ImageViewModel = viewModel()){
    val tops = viewModel.tops.observeAsState()

    LazyColumn(){
        item {
            Column {
                LazyRow(){
                    tops.value?.forEach {bitmap ->
                        item {
                            Image(painter = rememberImagePainter(data = bitmap), contentDescription = bitmap)
                        }
                    }
                }
                Row(modifier = Modifier) {
                    tops.value?.forEach {

                    }
                }
            }
        }


    }
}