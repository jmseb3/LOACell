import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController
import androidx.compose.ui.window.ComposeUIViewController
import com.wonddak.loacell.App
import com.wonddak.loacell.auth.AppleLoginGuide
import com.wonddak.loacell.debugBuild
import com.wonddak.loacell.di.commonModule
import com.wonddak.loacell.util.FileUtil
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import platform.UIKit.UIViewController

fun MainViewController(
    appleSingIn: () -> UIViewController,
    appleLoginGuide: AppleLoginGuide
): UIViewController {
    return ComposeUIViewController {
        App(
            appleLoginGuide = appleLoginGuide,
            appleLogin = {
                UIKitViewController(
                    factory = appleSingIn,
                    modifier = Modifier.fillMaxSize()
                )
            }
        )
    }
}

