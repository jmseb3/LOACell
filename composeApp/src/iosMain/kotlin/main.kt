import androidx.compose.ui.window.ComposeUIViewController
import com.wonddak.loacell.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        App()
    }
}

