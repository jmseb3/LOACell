package com.wonddak.loacell.android

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.di.appModule
import com.wonddak.loacell.android.di.viewModule
import com.wonddak.loacell.di.commonModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class LoaCellApp : Application() {

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this,SharedRes.strings.kakaoKey.getString(this))
        Napier.base(DebugAntilog())

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@LoaCellApp)
            // Load modules
            modules(commonModule() + appModule + viewModule)
        }
    }
}