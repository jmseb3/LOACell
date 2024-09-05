package com.wonddak.loacell.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wonddak.loacell.auth.FBUser
import com.wonddak.loacell.auth.LoginHelper
import com.wonddak.loacell.auth.delete
import com.wonddak.loacell.auth.signOut
import kotlinx.coroutines.launch

class AuthViewModel(
    val loginHelper: LoginHelper,
) : ViewModel() {

    //현재 로그인된 유저 정보
    var user: FBUser? by mutableStateOf(null)
        private set

    //user 정보가 최초 1번 불러와졌는지 여부
    var initSuccess: Boolean by mutableStateOf(false)
        private set

    //로그인 중인지 여부
    var loginIn by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            loginHelper.loginIn.collect {
                loginIn = it
            }
        }
        viewModelScope.launch {
            loginHelper.auth.user.collect {
                initSuccess = true
                user = it
            }
        }
    }

    fun updateName(name: String) {
        loginHelper.auth.updateDisplayName(name)
    }

    fun outOrSignOut() {
        user?.let {
            if (it.isAnonymous) {
                loginHelper.delete()
            } else {
                loginHelper.signOut()
            }
        }
    }

    fun deleteAccount() {
        loginHelper.delete()
    }
}