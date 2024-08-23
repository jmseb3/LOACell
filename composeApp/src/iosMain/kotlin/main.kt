import androidx.compose.ui.window.ComposeUIViewController
import com.wonddak.loacell.App
import com.wonddak.loacell.debugBuild
import com.wonddak.loacell.di.commonModule
import com.wonddak.loacell.util.FileUtil
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    debugBuild()
    startKoin{
        // Load modules
        modules(module {
            singleOf(::FileUtil)
        })
        modules(commonModule())
    }
    return ComposeUIViewController { App() }
}
