import android.app.Application
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