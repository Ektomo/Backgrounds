package ivan.gorbunov.backgrounds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import ivan.gorbunov.backgrounds.navigation.BottomNavigationBar
import ivan.gorbunov.backgrounds.navigation.Navigation
import ivan.gorbunov.backgrounds.navigation.TopBar
import ivan.gorbunov.backgrounds.ui.theme.BackgroundsTheme
import kotlinx.serialization.ExperimentalSerializationApi

@AndroidEntryPoint
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

@ExperimentalSerializationApi
@Composable
fun MainScreen() {
    val onBack: (() -> Unit)? = null
    val navController = rememberNavController()
    val isShowTopBar = remember {
        mutableStateOf(true)
    }

    val isShoBottomBar = remember {
        mutableStateOf(true)
    }

    val title = remember {
        mutableStateOf(Any())
    }

    Scaffold(
        topBar = {
            if (isShowTopBar.value) {
                TopBar(title = title.value, onBack)
            }
        },
        bottomBar = {
            if (isShoBottomBar.value) {
                BottomNavigationBar(navController)
            }
        }
    ) {
        Navigation(navController, title, isShowTopBar, isShoBottomBar)
    }


}

//@Composable
//fun rememberMyAppState(
//    scaffoldState: ScaffoldState = rememberScaffoldState(),
//    navController: NavHostController = rememberNavController(),
//    resources: Resources = LocalContext.current.resources,
//    /* ... */
//) = remember(scaffoldState, navController, resources, /* ... */) {
//    MainScreen(scaffoldState, navController, resources, /* ... */)
//}

