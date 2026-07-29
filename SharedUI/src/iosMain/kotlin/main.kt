import androidx.compose.ui.window.ComposeUIViewController
import com.wonddak.hellogin.core.HelloginContainerProvider
import com.wonddak.loacell.App
import com.wonddak.loacell.di.IosAppGraph
import dev.zacsweers.metro.createGraphFactory
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val appGraph = createGraphFactory<IosAppGraph.Factory>().create()
    return ComposeUIViewController {
        App(
            metroViewModelFactory = appGraph.metroViewModelFactory,
            config = appGraph.config,
            fileHelper = appGraph.fileHelper,
            lostArkApi = appGraph.lostArkApi,
            assetStorage = appGraph.assetStorage,
        )
    }.also {
        HelloginContainerProvider.setContainer(it)
    }
}
