import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.wonddak.loacell.App
import com.wonddak.loacell.initProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		WindowCompat.setDecorFitsSystemWindows(window, false)
        initProvider(activity = this)
        setContent {
            val activity = LocalActivity.current as ComponentActivity
            val navController = rememberNavController()

            DisposableEffect(activity, navController) {
                val onNewIntentConsumer = Consumer<Intent> {
                    navController.handleDeepLink(it)
                }

                activity.addOnNewIntentListener(onNewIntentConsumer)

                onDispose { activity.removeOnNewIntentListener(onNewIntentConsumer) }
            }
            App(navController)
		}
    }
}