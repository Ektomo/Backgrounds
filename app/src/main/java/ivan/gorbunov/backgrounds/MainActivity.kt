package ivan.gorbunov.backgrounds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import ivan.gorbunov.backgrounds.navigation.BottomNavigationBar
import ivan.gorbunov.backgrounds.navigation.Navigation
import ivan.gorbunov.backgrounds.navigation.TopBar
import ivan.gorbunov.backgrounds.ui.theme.BackgroundsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        setContent {
            BackgroundsTheme {
                val coroutineScope = rememberCoroutineScope()
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold (
        topBar = { TopBar(title = "Title", visibleBack = true) },
        bottomBar = { BottomNavigationBar(navController) }
    ){
        Navigation(navController)
    }
}

