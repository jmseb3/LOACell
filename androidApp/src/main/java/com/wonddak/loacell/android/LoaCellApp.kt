package com.wonddak.loacell.android

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.auth.LoginHelper
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class LoaCellApp : Application() {


    companion object {
        lateinit var loginHelper: LoginHelper
    }

    override fun onCreate() {
        super.onCreate()
        loginHelper = LoginHelper(this)
        KakaoSdk.init(this,SharedRes.strings.kakaoKey.getString(this))
        Napier.base(DebugAntilog())
    }
}