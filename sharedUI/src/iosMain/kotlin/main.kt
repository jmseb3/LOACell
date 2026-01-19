import androidx.compose.ui.window.ComposeUIViewController
import com.wonddak.hellogin.core.HelloginContainerProvider
import com.wonddak.loacell.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        App()
    }.also {
        HelloginContainerProvider.setContainer(it)
    }
}

