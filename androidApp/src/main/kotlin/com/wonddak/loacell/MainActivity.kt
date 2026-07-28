package com.wonddak.loacell

import android.app.Application
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
import com.wonddak.loacell.di.commonModule
import com.wonddak.loacell.util.FileUtil
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class MainActivity : ComponentActivity() {
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
                App(navController)
            }
        }
    }
}

class LoaCellApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "eaad613c8a32160c49991040e94170f9")
        startKoin {
            androidLogger()
            androidContext(this@LoaCellApplication)
            modules(module { singleOf(::FileUtil) })
            modules(commonModule())
        }
        Napier.base(DebugAntilog())
    }
}
