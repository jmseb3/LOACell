package com.wonddak.loacell

import android.app.Application
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.kakao.sdk.common.KakaoSdk
import com.wonddak.hellogin.core.HelloginContainerProvider
import com.wonddak.loacell.di.AndroidAppGraph
import com.wonddak.loacell.util.FileUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.ActivityKey
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey
@Inject
class MainActivity(
    private val metroViewModelFactory: MetroViewModelFactory,
) : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        HelloginContainerProvider.setContainer(this)
        AppContext.set(this)
        setContent {
            CompositionLocalProvider(LocalActivity provides this) {
                val activity = LocalActivity.current
                val navController = rememberNavController()

                DisposableEffect(activity, navController) {
                    val onNewIntentConsumer = Consumer<Intent> { navController.handleDeepLink(it) }
                    activity.addOnNewIntentListener(onNewIntentConsumer)
                    onDispose { activity.removeOnNewIntentListener(onNewIntentConsumer) }
                }
                App(
                    navController = navController,
                    metroViewModelFactory = metroViewModelFactory,
                    config = (application as LoaCellApplication).appGraph.config,
                    fileHelper = (application as LoaCellApplication).appGraph.fileHelper,
                    lostArkApi = (application as LoaCellApplication).appGraph.lostArkApi,
                )
            }
        }
    }
}

class LoaCellApplication : Application(), MetroApplication {
    internal val appGraph by lazy { createGraphFactory<AndroidAppGraph.Factory>().create(this) }

    override val appComponentProviders: MetroAppComponentProviders
        get() = appGraph

    override fun onCreate() {
        super.onCreate()
        AppContext.set(this)
        KakaoSdk.init(this, "eaad613c8a32160c49991040e94170f9")
        Napier.base(DebugAntilog())
    }
}
