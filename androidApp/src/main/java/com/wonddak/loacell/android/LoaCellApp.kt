package com.wonddak.loacell.android

import android.app.Application
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoaCellApp : Application() {

    companion object {
        private var _user: MutableStateFlow<FirebaseUser?> = MutableStateFlow(null)
        val user: StateFlow<FirebaseUser?> get() = _user

        fun updateUser(user: FirebaseUser?) {
            _user.value = null
            Log.i("Login",user?.email.toString())
            _user.value = user
        }
    }

}