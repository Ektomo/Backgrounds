package ivan.gorbunov.backgrounds.screens

import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import ivan.gorbunov.backgrounds.api.baseUrl
import ivan.gorbunov.backgrounds.pojo.Backgrounds4K
import ivan.gorbunov.backgrounds.vmodels.ViewModel4K

@Composable
fun Screen4kList(screens: List<Backgrounds4K>, viewModel4K: ViewModel4K = viewModel()){
    LazyColumn{
        screens.forEach {
            item {
                Screen4kItem(item = it)
            }
        }
    }
}

@Composable
fun Screen4kItem(item: Backgrounds4K){
    Row {
       Text(text = item.nameCategory)
       Spacer(modifier = Modifier.padding(horizontal = 20.dp))
       IconButton(onClick = { /*TODO*/ }) {
           Image(Icons.Filled.ArrowForward, contentDescription = "forward")
       }
    }
    Row {
        item.array.forEachIndexed{i, url ->
            Image(painter = rememberImagePainter(data = "$baseUrl$url"), contentDescription = "image")
        }
    }
}