import androidx.compose.ui.window.ComposeUIViewController
import com.wonddak.loacell.App
import com.wonddak.loacell.di.commonModule
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    startKoin{
        // Load modules
        modules(commonModule())
    }
    return ComposeUIViewController { App() }
}
