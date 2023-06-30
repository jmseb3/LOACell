package com.wonddak.loacell.android

import android.app.Application
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.KakaoSdk
import com.wonddak.loacell.SharedRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoaCellApp : Application() {

    companion object {
        private var _user: MutableStateFlow<FirebaseUser?> = MutableStateFlow(null)
        val user: StateFlow<FirebaseUser?> get() = _user
    }

    override fun onCreate() {
        super.onCreate()
        Firebase.auth.addAuthStateListener {
            _user.value = it.currentUser
        }
        KakaoSdk.init(this,SharedRes.strings.kakaoKey.getString(this))
    }
}