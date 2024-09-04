package com.wonddak.loacell

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.kakao.sdk.common.KakaoSdk
import com.wonddak.loacell.di.commonModule
import com.wonddak.loacell.util.FileUtil
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.lang.ref.WeakReference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        AppContext.set(this@MainActivity)
        setContent { App() }
    }
}

@Preview
@Composable
fun AppPreview() { App() }


class LoaCellApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "eaad613c8a32160c49991040e94170f9")
        startKoin{
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@LoaCellApplication)
            // Load modules
            modules(module {
                singleOf(::FileUtil)
            })
            modules(commonModule())
        }
        Napier.base(DebugAntilog())
    }
}

object AppContext {
    private var value: WeakReference<Context?>? = null
    fun set(context: Context) {
        value = WeakReference(context)
    }

    internal fun get(): Context {
        return value?.get() ?: throw RuntimeException("Context Error")
    }
}