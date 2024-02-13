package com.wonddak.loacell.android

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.auth.LoginHelper

class LoaCellApp : Application() {


    companion object {
        lateinit var loginHelper: LoginHelper
    }

    override fun onCreate() {
        super.onCreate()
        loginHelper = LoginHelper(this)
        KakaoSdk.init(this,SharedRes.strings.kakaoKey.getString(this))
    }
}